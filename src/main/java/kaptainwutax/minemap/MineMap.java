package kaptainwutax.minemap;

import com.formdev.flatlaf.*;
import kaptainwutax.featureutils.misc.SlimeChunk;
import kaptainwutax.featureutils.structure.Mineshaft;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.minemap.feature.chests.Chests;
import kaptainwutax.minemap.init.*;
import kaptainwutax.minemap.ui.component.WorldTabs;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.MapSettings;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.minemap.ui.menubar.MenuBar;
import kaptainwutax.minemap.util.data.Assets;
import kaptainwutax.minemap.util.data.DrawInfo;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.minemap.util.ui.ModalPopup;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.function.Supplier;

import static kaptainwutax.mcutils.state.Dimension.OVERWORLD;


public class MineMap extends JFrame {
    public static final String version = "d1.35";
    public final static String ROOT_DIR = System.getProperty("user.home") + File.separatorChar + ".minemap";
    public final static String LOG_DIR = ROOT_DIR + File.separatorChar + "logs";
    public final static String SETTINGS_DIR = ROOT_DIR + File.separatorChar + "configs";
    public final static String DOWNLOAD_DIR = ROOT_DIR + File.separatorChar + "downloads";
    public static MineMap INSTANCE;
    public static LookType lookType = LookType.DARCULA;
    public MenuBar toolbarPane;
    public WorldTabs worldTabs;

    public MineMap() {
        applyStyle();
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);
        this.initComponents();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setSize(screenSize.width / 2, screenSize.height / 2);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("MineMap " + MineMap.version);
        this.setIconImage(Icons.get(this.getClass()));
    }

    public static void main(String[] args) throws IOException {
        createDirs();
        Logger.registerLogger();
        Pair<Pair<String, String>, String> updateInfo = Assets.shouldUpdate();
        boolean noUpdate = Arrays.asList(args).contains("--no-update");
        boolean update = Arrays.asList(args).contains("--update");
        boolean screenshot = Arrays.asList(args).contains("--screenshot");
        if (!screenshot && updateInfo != null && !noUpdate) {
            updateMinemap(updateInfo.getFirst(), updateInfo.getSecond(), !update);
        }
        doRegister();
        if (screenshot) {
            doScreenshot(args);
            return;
        }
        INSTANCE = new MineMap();
        INSTANCE.setVisible(true);
        doDelayedRegister();
    }

    private static void doScreenshot(String[] args) throws IOException {
        // FIXME prettify
        long seed;
        MCVersion version;
        int blockX;
        int blockZ;
        int size;
        if (Arrays.asList(args).contains("--seed")) {
            int idx = Arrays.asList(args).indexOf("--seed");
            if (idx + 1 >= args.length) {
                System.err.println("Error no seed provided");
                return;
            }
            try {
                seed = Long.parseLong(args[idx + 1]);
            } catch (NumberFormatException ignored) {
                System.err.println("Invalid seed provided, should be numeric only for now");
                return;
            }
        } else {
            System.out.println("No seed argument provided, command is --screenshot --seed <seed> --version <version> --pos <x> <z> --size <size>");
            return;
        }
        if (Arrays.asList(args).contains("--version")) {
            int idx = Arrays.asList(args).indexOf("--version");
            if (idx + 1 >= args.length) {
                System.err.println("Error no version provided");
                return;
            }
            version = MCVersion.fromString(args[idx + 1]);
            if (version == null) {
                System.err.println("Invalid version provided");
                return;
            }
        } else {
            System.out.println("No version argument provided, command is --screenshot --seed <seed> --version <version> --pos <x> <z> --size <size>");
            return;
        }
        if (Arrays.asList(args).contains("--pos")) {
            int idx = Arrays.asList(args).indexOf("--pos");
            if (idx + 2 > args.length) {
                System.err.println("Error no pos provided");
                return;
            }
            try {
                blockX = Integer.parseInt(args[idx + 1]);
                blockZ = Integer.parseInt(args[idx + 2]);
            } catch (NumberFormatException ignored) {
                System.err.println("Invalid pos provided, should be numeric");
                return;
            }
        } else {
            System.out.println("No pos argument provided, command is --screenshot --seed <seed> --version <version> --pos <x> <z> --size <size>");
            return;
        }
        if (Arrays.asList(args).contains("--size")) {
            int idx = Arrays.asList(args).indexOf("--size");
            if (idx + 1 > args.length) {
                System.err.println("Error no size provided");
                return;
            }
            try {
                size = Integer.parseInt(args[idx + 1]);
            } catch (NumberFormatException ignored) {
                System.err.println("Invalid size provided, should be numeric");
                return;
            }
        } else {
            System.out.println("No size argument provided, command is --screenshot --seed <seed> --version <version> --pos <x> <z> --size <size>");
            return;
        }
        MapSettings settings = new MapSettings(version, OVERWORLD).refresh();
        MapContext context = new MapContext(seed, settings);
        settings.hide(SlimeChunk.class, Mineshaft.class);
        Fragment fragment = new Fragment(blockX, blockZ, size, context);
        BufferedImage screenshot = getScreenShot(fragment, size, size);
        ImageIO.write(screenshot, "png", new File(context.worldSeed + ".png"));
        System.out.println("Done!");
    }

    private static BufferedImage getScreenShot(Fragment fragment, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        DrawInfo info = new DrawInfo(0, 0, width, height);
        fragment.drawBiomes(image.getGraphics(), info);
        fragment.drawFeatures(image.getGraphics(), info);
        return image;
    }

    private static void updateMinemap(Pair<String, String> versionUrlFilename, String tagName, boolean shouldAsk) {
        if (shouldAsk) {
            int dialogResult = JOptionPane.showConfirmDialog(
                    null,
                    String.format("Would you like to update to the version %s of Minemap?", tagName),
                    "Update available for Minemap " + MineMap.version,
                    JOptionPane.YES_NO_OPTION
            );
            if (dialogResult != JOptionPane.YES_OPTION) {
                return;
            }
        }
        JDialog downloadPopup = new ModalPopup(null, "Downloading new MineMap version");
        downloadPopup.setSize(new Dimension(300, 50));
        downloadPopup.setShape(new RoundRectangle2D.Double(0, 0, 300, 50, 50, 50));
        applyStyle();
        SwingWorker<String, Void> downloadWorker = getDownloadWorker(downloadPopup, versionUrlFilename);
        downloadWorker.execute();
        downloadPopup.setVisible(true);
        String newVersion = null;
        try {
            newVersion = downloadWorker.get(); // blocking wait (intended)
        } catch (Exception e) {
            Logger.LOGGER.severe(String.format("Failed to use the download worker, error %s", e));
        }
        downloadPopup.setVisible(false);
        downloadPopup.dispose();
        if (newVersion != null) {
            Process ps;
            try {
                ps = Runtime.getRuntime().exec(new String[] {"java", "-jar", newVersion, "--no-update"});
                Logger.LOGGER.info(String.format("Process exited with %s", ps.waitFor()));
            } catch (Exception e) {
                Logger.LOGGER.severe(String.format("Failed to spawn the new process, error %s", e));
                return;
            }
            int exitVal = ps.exitValue();
            if (exitVal != 0) {
                Logger.LOGGER.severe("Failed to execute jar, " + Arrays.toString(new BufferedReader(new InputStreamReader(ps.getErrorStream())).lines().toArray()));
            } else {
                Logger.LOGGER.warning(String.format("UPDATING TO %s", newVersion));
                System.exit(0);
            }
        }
    }

    private static SwingWorker<String, Void> getDownloadWorker(JDialog parent, Pair<String, String> newVersion) {
        return new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return Assets.downloadLatestMinemap(newVersion.getFirst(), newVersion.getSecond());
            }

            @Override
            protected void done() {
                super.done();
                parent.dispose();
            }
        };
    }

    public static void createDirs() {
        try {
            String[] dirs = {LOG_DIR, SETTINGS_DIR, DOWNLOAD_DIR};
            for (String dir : dirs) {
                Files.createDirectories(Paths.get(dir));
            }
            Assets.createDirs();
        } catch (IOException e) {
            Logger.LOGGER.severe(String.format("Failed to create a directory, error: %s", e));
        }

    }

    public static void doRegister() {
        Features.registerFeatures();
        Chests.registerChests();
        Configs.registerConfigs();
        Icons.registerIcons(); // this depends on config
    }

    public static void doDelayedRegister() {
        // register keyboard event after the menus creation (very important)
        Configs.registerDelayedConfigs();
        KeyShortcuts.registerShortcuts();
        INSTANCE.doDelayedInitTasks();
        Icons.registerDelayedIcons(INSTANCE);
    }

    public static void applyStyle() {
        try {
            lookType.setLookAndFeel();
        } catch (Exception e) {
            lookType = LookType.DARCULA;
            try {
                lookType.setLookAndFeel();
            } catch (Exception impossibleError) {
                Logger.LOGGER.severe(impossibleError.toString());
                impossibleError.printStackTrace();
            }
        }
    }

    private void initComponents() {
        this.toolbarPane = new MenuBar();
        this.add(this.toolbarPane, BorderLayout.NORTH);

        this.worldTabs = new WorldTabs();
        this.add(this.worldTabs);
    }

    public void doDelayedInitTasks() {
        this.toolbarPane.doDelayedLabels();
    }

    public enum LookType {
        DARK("Dark", FlatDarkLaf::new),
        LIGHT("Light", FlatLightLaf::new),
        INTELLIJ("Intellij", FlatIntelliJLaf::new),
        DARCULA("Darcula", FlatDarculaLaf::new);

        private final String name;
        private final Supplier<FlatLaf> supplier;

        LookType(String name, Supplier<FlatLaf> supplier) {
            this.name = name;
            this.supplier = supplier;
        }

        public void setLookAndFeel() throws UnsupportedLookAndFeelException {
            UIManager.setLookAndFeel(supplier.get());
            for (Window window : JFrame.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
        }

        public String getName() {
            return name;
        }

        public boolean isDark() {
            return supplier.get().isDark();
        }
    }

}

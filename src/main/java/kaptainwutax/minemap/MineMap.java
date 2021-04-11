package kaptainwutax.minemap;

import com.formdev.flatlaf.*;
import kaptainwutax.minemap.config.Config;
import kaptainwutax.minemap.feature.chests.Chests;
import kaptainwutax.minemap.init.*;
import kaptainwutax.minemap.ui.component.WorldTabs;
import kaptainwutax.minemap.ui.menubar.MenuBar;
import kaptainwutax.minemap.util.data.Assets;
import kaptainwutax.minemap.util.data.Pair;
import kaptainwutax.minemap.util.ui.ModalPopup;
import kaptainwutax.seedutils.mc.MCVersion;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MineMap extends JFrame {
    public static final String version="1.34";
    public static MineMap INSTANCE;
    public static LookType lookType = LookType.DARCULA;
    public final static String ROOT_DIR = System.getProperty("user.home") + File.separatorChar + ".minemap";
    public final static String LOG_DIR = ROOT_DIR + File.separatorChar + "logs";
    public final static String SETTINGS_DIR = ROOT_DIR + File.separatorChar + "configs";
    public final static String DOWNLOAD_DIR = ROOT_DIR + File.separatorChar + "downloads";
    public MenuBar toolbarPane;
    public WorldTabs worldTabs;

    public static void main(String[] args) {
        Logger.registerLogger();
        updateMinemap();
        createDirs();
        doRegister();
        INSTANCE = new MineMap();
        INSTANCE.setVisible(true);
        doDelayedRegister();
    }

    private static void updateMinemap(){
        JDialog downloadPopup = new ModalPopup(null, "Downloading new MineMap version");
        downloadPopup.setSize(new Dimension(300,50));
        downloadPopup.setShape(new RoundRectangle2D.Double(0,0,300,50,50,50));
        applyStyle();
        SwingWorker<String, Void> downloadWorker = getDownloadWorker(downloadPopup);
        downloadWorker.execute();
        downloadPopup.setVisible(true);
        String newVersion=null;
        try {
            newVersion = downloadWorker.get(); // blocking wait (intended)
        }catch (Exception e){
            Logger.LOGGER.severe(String.format("Failed to use the download worker, error %s",e));
        }
        downloadPopup.setVisible(false);
        downloadPopup.dispose();
        if (newVersion!=null){
            Process ps;
            try{
                ps =Runtime.getRuntime().exec(new String[]{"java","-jar",newVersion});
                ps.waitFor();
            }catch (Exception e){
                Logger.LOGGER.severe(String.format("Failed to spawn the new process, error %s",e));
                return;
            }
            int exitVal = ps.exitValue();
            if (exitVal != 0) {
                Logger.LOGGER.severe("Failed to execute jar, " + Arrays.toString(new BufferedReader(new InputStreamReader(ps.getErrorStream())).lines().toArray()));
            }else{
                Logger.LOGGER.warning(String.format("UPDATING TO %s",newVersion));
                System.exit(0);
            }
        }
    }

    private static SwingWorker<String, Void> getDownloadWorker(JDialog parent) {
        return new SwingWorker<String, Void>() {
            @Override
            protected String doInBackground() {
                return Assets.downloadLatestMinemap();
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
        }catch (IOException e){
            Logger.LOGGER.severe(String.format("Failed to create a directory, error: %s",e));
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

    public MineMap() {
        applyStyle();
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);
        this.initComponents();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setSize(screenSize.width / 2, screenSize.height / 2);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("MineMap");
        this.setIconImage(Icons.get(this.getClass()));
        System.out.println("Hello, its me");
        System.out.println("General Kenobi");
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

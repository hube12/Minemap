package com.seedfinding.minemap;

import com.formdev.flatlaf.*;
import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.misc.SlimeChunk;
import com.seedfinding.mcfeature.structure.Mineshaft;
import com.seedfinding.minemap.feature.chests.Chests;
import com.seedfinding.minemap.init.*;
import com.seedfinding.minemap.ui.component.TabGroup;
import com.seedfinding.minemap.ui.component.WorldTabs;
import com.seedfinding.minemap.ui.map.MapContext;
import com.seedfinding.minemap.ui.map.MapPanel;
import com.seedfinding.minemap.ui.map.MapSettings;
import com.seedfinding.minemap.ui.map.fragment.Fragment;
import com.seedfinding.minemap.ui.menubar.MenuBar;
import com.seedfinding.minemap.util.data.Assets;
import com.seedfinding.minemap.util.data.DrawInfo;
import com.seedfinding.minemap.util.ui.interactive.ModalPopup;
import com.vdurmont.semver4j.Semver;

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
import java.util.List;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.seedfinding.mccore.state.Dimension.OVERWORLD;
import static com.seedfinding.minemap.config.UserProfileConfig.MAX_SIZE;


public class MineMap extends JFrame {
    static {
        // https://bugs.openjdk.java.net/browse/JDK-8235363
        if (System.getProperty("os.name").startsWith("Mac OS"))
            System.setProperty("apple.awt.application.appearance", "system");

    }


    public static final String version = "@VERSION@"; //using SemVer
    public static final Semver semver = new Semver(version);
    public final static String ROOT_DIR = System.getProperty("user.home") + File.separatorChar + ".minemap";
    public final static String LOG_DIR = ROOT_DIR + File.separatorChar + "logs";
    public final static String SETTINGS_DIR = ROOT_DIR + File.separatorChar + "configs";
    public final static String DOWNLOAD_DIR = ROOT_DIR + File.separatorChar + "downloads";
    public final static String SCREENSHOTS_DIR = ROOT_DIR + File.separatorChar + "screenshots";
    public final static boolean DEBUG = false;
    public static MineMap INSTANCE;
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

    @SuppressWarnings("ConstantConditions")
    public static void main(String[] args) throws IOException {
        Properties props = System.getProperties();
        // props.setProperty("sun.java2d.opengl", "True");
//        props.setProperty("sun.java2d.trace", "count");
//        props.setProperty("sun.java2d.pmoffscreen", "false");

        if (MineMap.version.startsWith("@VER") && MineMap.version.endsWith("SION@")) {
            throw new UnsupportedOperationException("The version was not replaced manually or by gradle");
        }
        createDirs();
        Logger.registerLogger();
        HashMap<String, Pair<Pair<String, String>, String>> updateInfo = Assets.shouldUpdate();
        boolean noUpdate = Arrays.asList(args).contains("--no-update");
        boolean update = Arrays.asList(args).contains("--update");
        boolean screenshot = Arrays.asList(args).contains("--screenshot");
        doRegister();
        if (!screenshot && updateInfo != null && !noUpdate) {
            updateMinemap(updateInfo, !update);
        }
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
            System.err.println("No seed argument provided, command is --screenshot --seed <seed> --version <version> --pos <x> <z> --size <size>");
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
            System.err.println("No version argument provided, command is --screenshot --seed <seed> --version <version> --pos <x> <z> --size <size>");
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
            System.err.println("No size argument provided, command is --screenshot --seed <seed> --version <version> --pos <x> <z> --size <size>");
            return;
        }
        com.seedfinding.mccore.state.Dimension dimension = OVERWORLD;
        if (Arrays.asList(args).contains("--dimension")) {
            int idx = Arrays.asList(args).indexOf("--dimension");
            if (idx + 1 > args.length) {
                System.err.println("Error no dimension provided");
                return;
            }
                dimension = com.seedfinding.mccore.state.Dimension.fromString(args[idx + 1]);
            if (dimension == null) {
                System.err.println("Invalid dimension provided, should be: " + Arrays.toString(com.seedfinding.mccore.state.Dimension.values()));
                return;
            }
        }
        MapSettings settings = new MapSettings(version, dimension).refresh();
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

    private static void updateMinemap(HashMap<String, Pair<Pair<String, String>, String>> updateInfo, boolean shouldAsk) {
        Pair<Pair<String, String>, String> release = updateInfo.get("jar");
        if (release == null) {
            Logger.LOGGER.severe("Missing jar Entry");
            return;
        }
        String OS = System.getProperty("os.name").toLowerCase();
        boolean isWindows = (OS.contains("win"));
        boolean isMax = (OS.contains("mac"));
        boolean isUnix = (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
        boolean isSolaris = (OS.contains("sunos"));
        boolean shouldUseVersion = false;
        Pair<Pair<String, String>, String> exeRelease = updateInfo.get("exe");
        if (exeRelease != null && isWindows) {
            release = exeRelease;
            shouldUseVersion = true;
        }
        // TODO add more build options here
        if (shouldAsk) {
            int dialogResult = JOptionPane.showConfirmDialog(
                null,
                String.format("Would you like to update to the version %s of Minemap?", release.getSecond()),
                "Update available for Minemap " + MineMap.version,
                JOptionPane.YES_NO_OPTION
            );
            if (dialogResult != 0) {
                return;
            }
        }
        JDialog downloadPopup = new ModalPopup(null, "Downloading new MineMap version");
        downloadPopup.setSize(new Dimension(300, 50));
        downloadPopup.setShape(new RoundRectangle2D.Double(0, 0, 300, 50, 50, 50));
        applyStyle();
        SwingWorker<String, Void> downloadWorker = getDownloadWorker(downloadPopup, release.getFirst());
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
                if (!shouldUseVersion) {
                    ps = Runtime.getRuntime().exec(new String[] {"java", "-jar", newVersion, "--no-update"});
                } else {
                    ps = Runtime.getRuntime().exec(new String[] {"./" + newVersion, "--no-update"});
                }

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
            String[] dirs = {LOG_DIR, SETTINGS_DIR, DOWNLOAD_DIR, SCREENSHOTS_DIR};
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
        INSTANCE.loadPinnedSeeds();
    }

    public static void applyStyle() {
        try {
            if (Configs.USER_PROFILE != null && Configs.USER_PROFILE.getUserSettings() != null) {
                Configs.USER_PROFILE.getUserSettings().look.setLookAndFeel();
            } else {
                throw new UnsatisfiedLinkError("Missing settings");
            }
        } catch (Exception e) {
            Logger.LOGGER.warning(e.toString());
            try {
                LookType.DARCULA.setLookAndFeel();
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

    public void loadPinnedSeeds() {
        int cores = Runtime.getRuntime().availableProcessors();
        Object[] pinned = Configs.USER_PROFILE.getPinnedSeeds().toArray();
        int len = Math.min(MAX_SIZE, pinned.length);
        List<Pair<Pair<MCVersion, String>, com.seedfinding.mccore.state.Dimension>> list = new ArrayList<>();
        for (int i = 1; i <= len; i++) {
            String config = (String) pinned[len - i];
            String[] split = config.split("::");
            if (split.length == 3) {
                String seed = split[0];
                String version = split[1];
                MCVersion mcVersion = MCVersion.fromString(version);
                int integer = Integer.MAX_VALUE;
                try {
                    integer = Integer.parseInt(split[2]);
                } catch (NumberFormatException e) {
                    Logger.LOGGER.severe(e.getMessage());
                }
                com.seedfinding.mccore.state.Dimension dimension = com.seedfinding.mccore.state.Dimension.fromId(integer);
                if (mcVersion != null && dimension != null) {
                    list.add(new Pair<>(new Pair<>(mcVersion, seed), dimension));
                } else {
                    Logger.LOGGER.severe("Saved seed is not possible to use " + dimension + " " + mcVersion + " " + seed);
                }
            } else {
                Logger.LOGGER.severe("Saved seed is not in the proper format");
            }
        }
        Map<Pair<MCVersion, String>, List<com.seedfinding.mccore.state.Dimension>> pinnedSeeds = list.stream()
            .collect(Collectors.groupingBy(Pair::getFirst, Collectors.mapping(Pair::getSecond, Collectors.toList())));
        for (Map.Entry<Pair<MCVersion, String>, List<com.seedfinding.mccore.state.Dimension>> pinnedSeed : pinnedSeeds.entrySet()) {
            pinnedSeed.getValue().sort(com.seedfinding.mccore.state.Dimension::compareTo);
            TabGroup tabGroup = MineMap.INSTANCE.worldTabs.load(
                pinnedSeed.getKey().getFirst(),
                pinnedSeed.getKey().getSecond(),
                Configs.USER_PROFILE.getThreadCount(cores),
                pinnedSeed.getValue()
            );
            if (tabGroup != null) {
                tabGroup.getMapPanels().forEach(e -> e.getHeader().setSaved(true, false));
                tabGroup.getMapPanels().forEach(e -> Arrays.stream(e.manager.popup.getComponents()).forEach(c -> {
                        if (c instanceof JMenuItem) {
                            JMenuItem item = ((JMenuItem) c);
                            if (item.getText().equals("Save tab")) {
                                item.setText("Unsave tab");
                            }
                        }
                    }

                ));
            }
        }
    }

    public static boolean isDarkTheme() {
        if (Configs.USER_PROFILE != null && Configs.USER_PROFILE.getUserSettings() != null) {
            return Configs.USER_PROFILE.getUserSettings().look.isDark();
        }
        return true;
    }

    public enum LookType {

        DARK("Dark", FlatDarkLaf::new),
        LIGHT("Light", FlatLightLaf::new),
        INTELLIJ("Intellij", FlatIntelliJLaf::new),
        DARCULA("Darcula", FlatDarculaLaf::new),
        ARC("Arc", com.formdev.flatlaf.intellijthemes.FlatArcIJTheme::new),
        ARC_ORANGE("Arc - Orange", com.formdev.flatlaf.intellijthemes.FlatArcOrangeIJTheme::new),
        ARC_DARK("Arc Dark", com.formdev.flatlaf.intellijthemes.FlatArcDarkIJTheme::new),
        ARC_DARK_ORANGE("Arc Dark - Orange", com.formdev.flatlaf.intellijthemes.FlatArcDarkOrangeIJTheme::new),
        CARBON("Carbon", com.formdev.flatlaf.intellijthemes.FlatCarbonIJTheme::new),
        COBALT_2("Cobalt 2", com.formdev.flatlaf.intellijthemes.FlatCobalt2IJTheme::new),
        CYAN_LIGHT("Cyan light", com.formdev.flatlaf.intellijthemes.FlatCyanLightIJTheme::new),
        DARK_FLAT("Dark Flat", com.formdev.flatlaf.intellijthemes.FlatDarkFlatIJTheme::new),
        DARK_PURPLE("Dark purple", com.formdev.flatlaf.intellijthemes.FlatDarkPurpleIJTheme::new),
        DRACULA("Dracula", com.formdev.flatlaf.intellijthemes.FlatDraculaIJTheme::new),
        GRADIANTO_DARK_FUCHSIA("Gradianto Dark Fuchsia", com.formdev.flatlaf.intellijthemes.FlatGradiantoDarkFuchsiaIJTheme::new),
        GRADIANTO_DEEP_OCEAN("Gradianto Deep Ocean", com.formdev.flatlaf.intellijthemes.FlatGradiantoDeepOceanIJTheme::new),
        GRADIANTO_MIDNIGHT_BLUE("Gradianto Midnight Blue", com.formdev.flatlaf.intellijthemes.FlatGradiantoMidnightBlueIJTheme::new),
        GRADIANTO_NATURE_GREEN("Gradianto Nature Green", com.formdev.flatlaf.intellijthemes.FlatGradiantoNatureGreenIJTheme::new),
        GRAY("Gray", com.formdev.flatlaf.intellijthemes.FlatGrayIJTheme::new),
        GRUVBOX_DARK_HARD("Gruvbox Dark Hard", com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkHardIJTheme::new),
        GRUVBOX_DARK_MEDIUM("Gruvbox Dark Medium", com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkMediumIJTheme::new),
        GRUVBOX_DARK_SOFT("Gruvbox Dark Soft", com.formdev.flatlaf.intellijthemes.FlatGruvboxDarkSoftIJTheme::new),
        HIBERBEE_DARK("Hiberbee Dark", com.formdev.flatlaf.intellijthemes.FlatHiberbeeDarkIJTheme::new),
        HIGH_CONTRAST("High contrast", com.formdev.flatlaf.intellijthemes.FlatHighContrastIJTheme::new),
        LIGHT_FLAT("Light Flat", com.formdev.flatlaf.intellijthemes.FlatLightFlatIJTheme::new),
        MATERIAL_DESIGN_DARK("Material Design Dark", com.formdev.flatlaf.intellijthemes.FlatMaterialDesignDarkIJTheme::new),
        MONOCAI("Monocai", com.formdev.flatlaf.intellijthemes.FlatMonocaiIJTheme::new),
        MONOKAI_PRO("Monokai Pro", com.formdev.flatlaf.intellijthemes.FlatMonokaiProIJTheme::new),
        NORD("Nord", com.formdev.flatlaf.intellijthemes.FlatNordIJTheme::new),
        ONE_DARK("One Dark", com.formdev.flatlaf.intellijthemes.FlatOneDarkIJTheme::new),
        SOLARIZED_DARK("Solarized Dark", com.formdev.flatlaf.intellijthemes.FlatSolarizedDarkIJTheme::new),
        SOLARIZED_LIGHT("Solarized Light", com.formdev.flatlaf.intellijthemes.FlatSolarizedLightIJTheme::new),
        SPACEGRAY("Spacegray", com.formdev.flatlaf.intellijthemes.FlatSpacegrayIJTheme::new),
        VUESION("Vuesion", com.formdev.flatlaf.intellijthemes.FlatVuesionIJTheme::new),
        XCODE_DARK("Xcode-Dark", com.formdev.flatlaf.intellijthemes.FlatXcodeDarkIJTheme::new),
        ARC_DARK__MATERIAL_("Arc Dark (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatArcDarkIJTheme::new),
        ARC_DARK_CONTRAST__MATERIAL_("Arc Dark Contrast (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatArcDarkContrastIJTheme::new),
        ATOM_ONE_DARK__MATERIAL_("Atom One Dark (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkIJTheme::new),
        ATOM_ONE_DARK_CONTRAST__MATERIAL_("Atom One Dark Contrast (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneDarkContrastIJTheme::new),
        ATOM_ONE_LIGHT__MATERIAL_("Atom One Light (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightIJTheme::new),
        ATOM_ONE_LIGHT_CONTRAST__MATERIAL_("Atom One Light Contrast (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatAtomOneLightContrastIJTheme::new),
        DRACULA__MATERIAL_("Dracula (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaIJTheme::new),
        DRACULA_CONTRAST__MATERIAL_("Dracula Contrast (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatDraculaContrastIJTheme::new),
        GITHUB__MATERIAL_("GitHub (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubIJTheme::new),
        GITHUB_CONTRAST__MATERIAL_("GitHub Contrast (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubContrastIJTheme::new),
        GITHUB_DARK__MATERIAL_("GitHub Dark (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkIJTheme::new),
        GITHUB_DARK_CONTRAST__MATERIAL_("GitHub Dark Contrast (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatGitHubDarkContrastIJTheme::new),
        LIGHT_OWL__MATERIAL_("Light Owl (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatLightOwlIJTheme::new),
        LIGHT_OWL_CONTRAST__MATERIAL_("Light Owl Contrast (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatLightOwlContrastIJTheme::new),
        MATERIAL_DARKER__MATERIAL_("Material Darker (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerIJTheme::new),
        MATERIAL_DARKER_CONTRAST__MATERIAL_("Material Darker Contrast (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDarkerContrastIJTheme::new),
        MATERIAL_DEEP_OCEAN__MATERIAL_("Material Deep Ocean (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDeepOceanIJTheme::new),
        MATERIAL_DEEP_OCEAN_CONTRAST__MATERIAL_("Material Deep Ocean Contrast (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialDeepOceanContrastIJTheme::new),
        MATERIAL_LIGHTER__MATERIAL_("Material Lighter (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterIJTheme::new),
        MATERIAL_LIGHTER_CONTRAST__MATERIAL_("Material Lighter Contrast (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialLighterContrastIJTheme::new),
        MATERIAL_OCEANIC__MATERIAL_("Material Oceanic (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialOceanicIJTheme::new),
        MATERIAL_OCEANIC_CONTRAST__MATERIAL_("Material Oceanic Contrast (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialOceanicContrastIJTheme::new),
        MATERIAL_PALENIGHT__MATERIAL_("Material Palenight (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialPalenightIJTheme::new),
        MATERIAL_PALENIGHT_CONTRAST__MATERIAL_("Material Palenight Contrast (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMaterialPalenightContrastIJTheme::new),
        MONOKAI_PRO__MATERIAL_("Monokai Pro (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMonokaiProIJTheme::new),
        MONOKAI_PRO_CONTRAST__MATERIAL_("Monokai Pro Contrast (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMonokaiProContrastIJTheme::new),
        MOONLIGHT__MATERIAL_("Moonlight (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMoonlightIJTheme::new),
        MOONLIGHT_CONTRAST__MATERIAL_("Moonlight Contrast (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatMoonlightContrastIJTheme::new),
        NIGHT_OWL__MATERIAL_("Night Owl (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatNightOwlIJTheme::new),
        NIGHT_OWL_CONTRAST__MATERIAL_("Night Owl Contrast (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatNightOwlContrastIJTheme::new),
        SOLARIZED_DARK__MATERIAL_("Solarized Dark (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedDarkIJTheme::new),
        SOLARIZED_DARK_CONTRAST__MATERIAL_("Solarized Dark Contrast (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedDarkContrastIJTheme::new),
        SOLARIZED_LIGHT__MATERIAL_("Solarized Light (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedLightIJTheme::new),
        SOLARIZED_LIGHT_CONTRAST__MATERIAL_("Solarized Light Contrast (Material)", com.formdev.flatlaf.intellijthemes.materialthemeuilite.FlatSolarizedLightContrastIJTheme::new),


        ;

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
            if (MineMap.INSTANCE != null && MineMap.INSTANCE.worldTabs != null && MineMap.INSTANCE.worldTabs.getSelectedMapPanel() != null) {
                MapPanel mapPanel = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
                if (isDarkTheme()) {
                    mapPanel.setBackground(WorldTabs.BACKGROUND_COLOR.darker());
                } else {
                    mapPanel.setBackground(WorldTabs.BACKGROUND_COLOR_LIGHT.darker());
                }
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

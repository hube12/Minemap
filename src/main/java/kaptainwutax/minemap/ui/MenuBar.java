package kaptainwutax.minemap.ui;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.feature.SpawnPoint;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.dialog.*;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.ui.map.MapSettings;
import kaptainwutax.minemap.ui.map.icon.IconRenderer;
import kaptainwutax.minemap.ui.map.icon.SpawnIcon;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.seed.WorldSeed;
import kaptainwutax.seedutils.util.math.DistanceMetric;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;

public class MenuBar extends JMenuBar {

    public MenuBar() {
        this.addFileMenu();
        this.addWorldMenu();
        this.addUtilityMenu();
        this.addSettingsMenu();
    }

    private void addSettingsMenu() {
        JMenu settingsMenu = new JMenu("Settings");

        JMenu lookMenu = new JMenu("UI Look");
        ButtonGroup lookButtons = new ButtonGroup();

        for (MineMap.LookType look : MineMap.LookType.values()) {
            JRadioButtonMenuItem button = new JRadioButtonMenuItem(look.getName());

            button.addMouseListener(Events.Mouse.onPressed(e -> {
                if (!button.isEnabled()) return;
                for (Component c : lookMenu.getMenuComponents()) {
                    c.setEnabled(true);
                }
                button.setEnabled(false);
                Configs.USER_PROFILE.getUserSettings().look = look;
                MineMap.lookType = look;
                MineMap.applyStyle();
                Configs.USER_PROFILE.flush();
            }));

            if (Configs.USER_PROFILE.getUserSettings().look.equals(look)) {
                button.setEnabled(false);
            }

            lookButtons.add(button);
            lookMenu.add(button);
        }

        JMenu styleMenu = new JMenu("Biome Style");
        ButtonGroup styleButtons = new ButtonGroup();

        for (String style : Configs.BIOME_COLORS.getStyles()) {
            JRadioButtonMenuItem button = new JRadioButtonMenuItem(style);

            button.addMouseListener(Events.Mouse.onPressed(e -> {
                if (!button.isEnabled()) return;

                for (Component c : styleMenu.getMenuComponents()) {
                    c.setEnabled(true);
                }

                button.setEnabled(false);
                Configs.USER_PROFILE.getUserSettings().style = style;
                MineMap.INSTANCE.worldTabs.invalidateAll();
                Configs.USER_PROFILE.flush();
            }));

            if (Configs.USER_PROFILE.getUserSettings().style.equals(style)) {
                button.setEnabled(false);
            }

            styleButtons.add(button);
            styleMenu.add(button);
        }


        JCheckBoxMenuItem zoom = new JCheckBoxMenuItem("Restrict Maximum Zoom");

        zoom.addChangeListener(e -> {
            Configs.USER_PROFILE.getUserSettings().restrictMaximumZoom = zoom.getState();
            Configs.USER_PROFILE.flush();
        });

        settingsMenu.addMenuListener(Events.Menu.onSelected(e -> {
            zoom.setState(Configs.USER_PROFILE.getUserSettings().restrictMaximumZoom);
        }));

        JMenu metric = new JMenu("Fragment Metric");
        ButtonGroup group = new ButtonGroup();
        JRadioButtonMenuItem metric1 = new JRadioButtonMenuItem("Euclidean");
        JRadioButtonMenuItem metric2 = new JRadioButtonMenuItem("Manhattan");
        JRadioButtonMenuItem metric3 = new JRadioButtonMenuItem("Chebyshev");
        metric.add(metric1);
        metric.add(metric2);
        metric.add(metric3);
        group.add(metric1);
        group.add(metric2);
        group.add(metric3);

        DistanceMetric m = Configs.USER_PROFILE.getUserSettings().getFragmentMetric();
        if (m == DistanceMetric.EUCLIDEAN_SQ) metric1.setSelected(true);
        else if (m == DistanceMetric.MANHATTAN) metric2.setSelected(true);
        else if (m == DistanceMetric.CHEBYSHEV) metric3.setSelected(true);

        metric1.addMouseListener(Events.Mouse.onPressed(mouseEvent -> {
            Configs.USER_PROFILE.getUserSettings().fragmentMetric = metric1.getText();
            Configs.USER_PROFILE.flush();
        }));

        metric2.addMouseListener(Events.Mouse.onPressed(mouseEvent -> {
            Configs.USER_PROFILE.getUserSettings().fragmentMetric = metric2.getText();
            Configs.USER_PROFILE.flush();
        }));

        metric3.addMouseListener(Events.Mouse.onPressed(mouseEvent -> {
            Configs.USER_PROFILE.getUserSettings().fragmentMetric = metric3.getText();
            Configs.USER_PROFILE.flush();
        }));

        settingsMenu.add(lookMenu);
        settingsMenu.add(styleMenu);
        settingsMenu.add(metric);
        settingsMenu.add(zoom);
        this.add(settingsMenu);
    }

    private void addFileMenu() {
        JMenu fileMenu = new JMenu("Home");

        JMenuItem loadSeed = new JMenuItem("New From Seed...");

        loadSeed.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(() -> {
            EnterSeedDialog dialog = new EnterSeedDialog();
            dialog.setVisible(true);
        })));

        JMenuItem screenshot = new JMenuItem("Screenshot...");

        screenshot.addMouseListener(Events.Mouse.onPressed(mouseEvent -> {
            if (!screenshot.isEnabled()) return;

            MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            if (map == null) return;
            BufferedImage image = map.getScreenshot();

            String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            File dir = new File("screenshots/");
            File file = new File("screenshots/" + fileName + ".png");
            if (!dir.exists() && !dir.mkdirs()) return;

            try {
                ImageIO.write(image, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));

        JMenuItem close = new JMenuItem("Close");
        close.addMouseListener(Events.Mouse.onPressed(mouseEvent -> {
                    for (Frame frame : JFrame.getFrames()) {
                        frame.dispose();
                    }
                    System.exit(0);
                }
        ));
        fileMenu.addMenuListener(Events.Menu.onSelected(e -> screenshot.setEnabled(MineMap.INSTANCE.worldTabs.getSelectedMapPanel() != null)));

        fileMenu.add(loadSeed);
        fileMenu.add(screenshot);
        fileMenu.add(close);
        this.add(fileMenu);
    }


    private void addUtilityMenu() {
        JMenu utilityMenu = new JMenu("Utilities");

        JMenuItem listStructure = new JMenuItem("List N Structures");

        listStructure.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(() -> {
            if (!listStructure.isEnabled()) return;
            JDialog jumpDialogue = new StructureListDialog();
            jumpDialogue.setVisible(true);
        })));

        JMenuItem structureSeedMode = new JCheckBoxMenuItem("Structure Seed Mode");

        structureSeedMode.addActionListener(e -> {
            if (!structureSeedMode.isEnabled()) return;
            Configs.USER_PROFILE.getUserSettings().structureMode = structureSeedMode.isSelected();
            Configs.USER_PROFILE.flush();
            MineMap.INSTANCE.worldTabs.invalidateAll();
        });

        structureSeedMode.setSelected(Configs.USER_PROFILE.getUserSettings().structureMode);

        utilityMenu.addMenuListener(Events.Menu.onSelected(e -> {
            MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            listStructure.setEnabled(map != null);
            structureSeedMode.setEnabled(map != null);
        }));

        utilityMenu.add(listStructure);
        utilityMenu.add(structureSeedMode);
        this.add(utilityMenu);
    }

    private void addWorldMenu() {
        JMenu worldMenu = new JMenu("World");

        JMenuItem goToCoords = new JMenuItem("Go to Coordinates");

        goToCoords.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(() -> {
            if (!goToCoords.isEnabled()) return;
            JDialog jumpDialogue = new CoordHopperDialog();
            jumpDialogue.setVisible(true);
        })));

        JMenuItem goToSpawn = new JMenuItem("Go to Spawn");

        goToSpawn.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(() -> {
            if (!goToSpawn.isEnabled()) return;
            BPos pos = this.getActiveSpawn();
            if (pos != null) {
                MineMap.INSTANCE.worldTabs.getSelectedMapPanel().getManager().setCenterPos(pos.getX(), pos.getZ());
            }
        })));

        JMenuItem loadShadowSeed = new JMenuItem("Load Shadow Seed");

        loadShadowSeed.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(() -> {
            if (!loadShadowSeed.isEnabled()) return;
            MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            MineMap.INSTANCE.worldTabs.load(
                    map.getContext().version,
                    String.valueOf(WorldSeed.getShadowSeed(map.getContext().worldSeed)),
                    map.threadCount, Collections.singletonList(map.getContext().dimension));
        })));


        JMenuItem goToStructure = new JMenuItem("Go to Structure");

        goToStructure.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(() -> {
            if (!goToStructure.isEnabled()) return;
            JDialog jumpDialogue = new StructureHopperDialog();
            jumpDialogue.setVisible(true);
        })));

        worldMenu.addMenuListener(Events.Menu.onSelected(e -> {
            MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            goToCoords.setEnabled(map != null);
            goToSpawn.setEnabled(map != null && this.getActiveSpawn() != null);
            loadShadowSeed.setEnabled(map != null && map.getContext().dimension == Dimension.OVERWORLD);
            goToStructure.setEnabled(map != null);
        }));

        JMenuItem changeSalts = new JMenuItem("Change Salts");

        changeSalts.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(() -> {
            SaltDialog dialog = null;
            try {
                dialog = new SaltDialog();
                dialog.setVisible(true);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        })));

        worldMenu.add(goToCoords);
        worldMenu.add(goToSpawn);
        worldMenu.add(goToStructure);
        worldMenu.add(loadShadowSeed);
        worldMenu.add(changeSalts);
        this.add(worldMenu);
    }


    private BPos getActiveSpawn() {
        MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
        IconRenderer icon = map.getContext().getIconManager().getFor(SpawnPoint.class);
        return icon instanceof SpawnIcon ? ((SpawnIcon) icon).getPos() : null;
    }

}

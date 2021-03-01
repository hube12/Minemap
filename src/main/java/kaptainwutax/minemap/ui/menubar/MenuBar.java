package kaptainwutax.minemap.ui.menubar;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.feature.SpawnPoint;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.dialog.*;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.ui.map.icon.IconRenderer;
import kaptainwutax.minemap.ui.map.icon.SpawnIcon;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.seed.WorldSeed;
import kaptainwutax.seedutils.util.math.DistanceMetric;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.function.Consumer;

public class MenuBar extends JMenuBar {
    public FileMenu fileMenu;
    public JMenu worldMenu;
    public JMenu utilitiesMenu;
    public SettingsMenu settingsMenu;
    public JButton structureSeedModePopup;

    public MenuBar() {
        fileMenu = new FileMenu();
        worldMenu = this.addWorldMenu();
        utilitiesMenu = this.addUtilitiesMenu();
        settingsMenu = new SettingsMenu();
        structureSeedModePopup = this.addStructureModePopup();

        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridheight = 1; // 1 row
        gbc.gridwidth = 5; // 5 columns
        gbc.fill = GridBagConstraints.NONE; // no expanding of component
        // no insets (external padding) nor internal padding (ipadx/y)
        gbc.anchor = GridBagConstraints.LINE_START;// put it at the start

        this.add(fileMenu.getMenu(), gbc);
        this.add(worldMenu, gbc);
        this.add(utilitiesMenu, gbc);
        gbc.weightx=1; // give all the space before to that component (needed to offcenter the first part)
        this.add(settingsMenu.getMenu(), gbc);
        gbc.anchor = GridBagConstraints.LINE_END; // put it at the end
        this.add(structureSeedModePopup, gbc);
    }

    public boolean isActive(){
        return fileMenu.isActive();
    }

    private JMenu addUtilitiesMenu() {
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
            if (this.structureSeedModePopup == null) {
                MineMap.INSTANCE.toolbarPane = new MenuBar();
                System.out.println("This should not happen");
                return;
            }
            this.structureSeedModePopup.setVisible(structureSeedMode.isSelected());
        });

        structureSeedMode.setSelected(Configs.USER_PROFILE.getUserSettings().structureMode);

        utilityMenu.addMenuListener(Events.Menu.onSelected(e -> {
            MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            listStructure.setEnabled(map != null);
        }));

        utilityMenu.add(listStructure);
        utilityMenu.add(structureSeedMode);
        return utilityMenu;
    }

    private JMenu addWorldMenu() {
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
            SaltDialog dialog;
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
        return worldMenu;
    }


    private BPos getActiveSpawn() {
        MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
        IconRenderer icon = map.getContext().getIconManager().getFor(SpawnPoint.class);
        return icon instanceof SpawnIcon ? ((SpawnIcon) icon).getPos() : null;
    }

    private JButton addStructureModePopup() {
        structureSeedModePopup = new JButton("Sister seeds info");
        structureSeedModePopup.addActionListener(a -> {
            MapPanel map=MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            if (map==null)return;

        });
        if (!Configs.USER_PROFILE.getUserSettings().structureMode) {
            structureSeedModePopup.setVisible(false);
        }
        return structureSeedModePopup;
    }

}

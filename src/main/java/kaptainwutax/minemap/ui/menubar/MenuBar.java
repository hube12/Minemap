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
    public WorldMenu worldMenu;
    public JMenu utilitiesMenu;
    public SettingsMenu settingsMenu;
    public JButton structureSeedModePopup;

    public MenuBar() {
        fileMenu = new FileMenu();
        worldMenu = new WorldMenu();
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
        this.add(worldMenu.getMenu(), gbc);
        this.add(utilitiesMenu, gbc);
        gbc.weightx=1; // give all the space before to that component (needed to offcenter the first part)
        this.add(settingsMenu.getMenu(), gbc);
        gbc.anchor = GridBagConstraints.LINE_END; // put it at the end
        this.add(structureSeedModePopup, gbc);
    }

    public boolean isActive(){
        return fileMenu.isActive() || worldMenu.isActive();
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



    private JButton addStructureModePopup() {
        structureSeedModePopup = new JButton("Sister seeds info");
        structureSeedModePopup.addActionListener(a -> {
            MapPanel map=MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            if (map==null)return;
            // TODO implement
        });
        if (!Configs.USER_PROFILE.getUserSettings().structureMode) {
            structureSeedModePopup.setVisible(false);
        }
        return structureSeedModePopup;
    }

}

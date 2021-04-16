package kaptainwutax.minemap.ui.menubar;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.ui.map.MapPanel;

import javax.swing.*;
import java.awt.*;

public class MenuBar extends JMenuBar {
    public FileMenu fileMenu;
    public WorldMenu worldMenu;
    public UtilitiesMenu utilitiesMenu;
    public SettingsMenu settingsMenu;
    public JButton structureSeedModePopup;

    public MenuBar() {
        this.fileMenu = new FileMenu();
        this.worldMenu = new WorldMenu();
        this.utilitiesMenu = new UtilitiesMenu();
        this.settingsMenu = new SettingsMenu();
        this.structureSeedModePopup = this.addStructureModePopup();

        this.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridheight = 1; // 1 row
        gbc.gridwidth = 5; // 5 columns
        gbc.fill = GridBagConstraints.NONE; // no expanding of component
        // no insets (external padding) nor internal padding (ipadx/y)
        gbc.anchor = GridBagConstraints.LINE_START;// put it at the start

        this.add(this.fileMenu.getMenu(), gbc);
        this.add(this.worldMenu.getMenu(), gbc);
        this.add(this.utilitiesMenu.getMenu(), gbc);
        gbc.weightx = 1; // give all the space before to that component (needed to offcenter the first part)
        this.add(this.settingsMenu.getMenu(), gbc);
        gbc.anchor = GridBagConstraints.LINE_END; // put it at the end
        this.add(this.structureSeedModePopup, gbc);
    }

    public boolean isActive() {
        return this.fileMenu.isActive() || this.worldMenu.isActive() || this.utilitiesMenu.isActive() || this.settingsMenu.isActive();
    }


    private JButton addStructureModePopup() {
        this.structureSeedModePopup = new JButton("Sister seeds info");
        this.structureSeedModePopup.addActionListener(a -> {
            MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            if (map == null) return;
            // TODO implement
        });
        if (!Configs.USER_PROFILE.getUserSettings().structureMode) {
            this.structureSeedModePopup.setVisible(false);
        }
        return this.structureSeedModePopup;
    }


    public void doDelayedLabels() {
        this.fileMenu.doDelayedLabels();
        this.worldMenu.doDelayedLabels();
        this.utilitiesMenu.doDelayedLabels();
        this.settingsMenu.doDelayedLabels();
    }

}

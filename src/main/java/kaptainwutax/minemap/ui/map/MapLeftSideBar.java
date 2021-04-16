package kaptainwutax.minemap.ui.map;

import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.map.sidebar.SettingsSidebar;
import kaptainwutax.minemap.ui.map.sidebar.TooltipSidebar;

import javax.swing.*;
import java.awt.*;

public class MapLeftSideBar extends JPanel {

    public final TooltipSidebar tooltip;
    public final SettingsSidebar settings;
    private final MapPanel map;

    public MapLeftSideBar(MapPanel map) {
        this.map = map;
        this.tooltip = new TooltipSidebar(this.map);
        this.settings = new SettingsSidebar(this.map);

        this.tooltip.setVisible(true);
        this.settings.setVisible(false);
        this.settings.closeButton.addMouseListener(Events.Mouse.onPressed(e -> this.settings.setVisible(false)));

        this.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.fill = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        this.add(this.settings, gridBagConstraints);
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        this.add(this.tooltip, gridBagConstraints);
        this.setBackground(new Color(0, 0, 0, 0));
        this.setOpaque(false);
    }

}

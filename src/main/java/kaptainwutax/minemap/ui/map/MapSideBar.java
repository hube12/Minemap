package kaptainwutax.minemap.ui.map;

import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.map.sidebar.SettingsSidebar;
import kaptainwutax.minemap.ui.map.sidebar.TooltipSidebar;

import javax.swing.*;
import java.awt.*;

public class MapSideBar extends JPanel {

    private final MapPanel map;

    public final TooltipSidebar tooltip;
    public final SettingsSidebar settings;

    public MapSideBar(MapPanel map) {
        this.map = map;
        this.tooltip = new TooltipSidebar(this.map);
        this.settings = new SettingsSidebar(this.map);

        this.tooltip.setVisible(true);
        this.settings.setVisible(false);

        this.tooltip.settingsButton.addMouseListener(Events.Mouse.onPressed(e -> {
            this.tooltip.setVisible(false);
            this.settings.setVisible(true);
        }));

        this.settings.closeButton.addMouseListener(Events.Mouse.onPressed(e -> {
            this.tooltip.setVisible(true);
            this.settings.setVisible(false);
        }));

        this.add(this.tooltip);
        this.add(this.settings);
        this.setBackground(new Color(0, 0, 0, 0));
    }

}

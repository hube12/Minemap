package kaptainwutax.minemap.ui.map;

import kaptainwutax.minemap.ui.map.sidebar.TooltipTools;

import javax.swing.*;
import java.awt.*;

public class MapRightSideBar extends JPanel {

    public final TooltipTools tooltip;
    private final MapPanel map;

    public MapRightSideBar(MapPanel map) {
        this.map = map;
        this.tooltip = new TooltipTools(this.map);
        this.tooltip.setVisible(true);
        this.add(this.tooltip, new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(new Color(0, 0, 0, 0));
        this.setOpaque(false);
    }

}

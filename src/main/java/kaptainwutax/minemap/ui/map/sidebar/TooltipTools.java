package kaptainwutax.minemap.ui.map.sidebar;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.ui.map.tool.ColorChooserButton;
import kaptainwutax.minemap.ui.map.tool.Tool;
import kaptainwutax.minemap.util.Str;
import kaptainwutax.seedutils.mc.pos.BPos;

import javax.swing.*;
import javax.swing.plaf.metal.MetalIconFactory;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.function.Consumer;

public class TooltipTools extends JPanel {

    private final MapPanel map;
    public boolean isHiddenForSize = false;

    public TooltipTools(MapPanel map) {
        this.map = map;
        this.setLayout(new GridLayout(0, 1, 2, 2));
        this.setBackground(new Color(0, 0, 0, 0));
    }

    public void updateToolsMetrics(ArrayList<Tool> tools) {
        this.removeAll();
        for (Tool tool : tools) {
            this.add(new Entry(tool, e -> { map.manager.removeTool(tool);}));
        }
    }

    public static class Entry extends JPanel {
        private final JComponent iconView;
        private final JLabel positionText;
        private final ColorChooserButton colorChoser;
        private final JButton closeButton;

        public Entry(Tool tool, Consumer<MouseEvent> onClose) {
            this.iconView = new JComponent() {
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(30, 30);
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    BufferedImage icon = Icons.REGISTRY.get(tool.getClass());
                    int iconSizeX, iconSizeZ;
                    int defaultValue = 20;
                    float factor = 1.5F;
                    if (icon.getRaster().getWidth() > icon.getRaster().getHeight()) {
                        iconSizeX = defaultValue;
                        iconSizeZ = (int) (defaultValue * (float) icon.getRaster().getHeight() / icon.getRaster().getWidth());
                    } else {
                        iconSizeZ = defaultValue;
                        iconSizeX = (int) (defaultValue * (float) icon.getRaster().getWidth() / icon.getRaster().getHeight());
                    }
                    g.drawImage(icon, (defaultValue - iconSizeX) / 2, (defaultValue - iconSizeZ) / 2, (int) (iconSizeX * factor), (int) (iconSizeZ * factor), null);
                }
            };

            this.positionText = new JLabel(tool.getMetricString());
            this.positionText.setFont(new Font(this.positionText.getFont().getName(), Font.PLAIN, 18));
            this.positionText.setBackground(new Color(0, 0, 0, 0));
            this.positionText.setFocusable(false);
            this.positionText.setOpaque(true);
            this.positionText.setForeground(Color.WHITE);

            this.colorChoser = new ColorChooserButton(tool.getColor());
            this.colorChoser.addColorChangedListener(tool::setColor);

            this.closeButton = new JButton(MetalIconFactory.getInternalFrameCloseIcon(14));
            this.closeButton.addMouseListener(Events.Mouse.onPressed(onClose));

            this.add(this.iconView);
            this.add(this.colorChoser);
            this.add(this.positionText);
            this.add(this.closeButton);

            this.setBackground(new Color(0, 0, 0, 180));
        }
    }

}

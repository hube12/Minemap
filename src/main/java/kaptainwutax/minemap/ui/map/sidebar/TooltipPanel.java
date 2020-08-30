package kaptainwutax.minemap.ui.map.sidebar;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.util.Str;
import kaptainwutax.seedutils.mc.pos.BPos;

import javax.swing.*;
import java.awt.*;

public class TooltipPanel extends JPanel {

    private final MapPanel map;

    public TooltipPanel(MapPanel map) {
        this.map = map;
        this.setLayout(new GridLayout(0, 1));
        this.setBackground(new Color(0, 0, 0, 0));
   }

    @Override
    public void repaint() {
        if(this.map != null && this.map.scheduler != null) {
            this.removeAll();

            int size = (int)this.map.getManager().pixelsPerFragment;

            this.map.scheduler.forEachFragment(fragment -> {
                fragment.getHoveredFeatures(size, size).forEach((feature, positions) -> {
                    positions.forEach(pos -> this.add(new Entry(feature, pos)));
                });
            });
        }

        super.repaint();
    }

    public static class Entry extends JPanel {
        private final JComponent iconView;
        private final JLabel positionText;

        public Entry(Feature<?, ?> feature, BPos pos) {
            this.iconView = new JComponent() {
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(30, 30);
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Image icon = Icons.REGISTRY.get(feature.getClass());
                    g.drawImage(icon, 0, 0, 30, 30, null);
                }
            };

            this.positionText = new JLabel(" [" + pos.getX() + ", " + pos.getZ() + "] " + Str.formatName(feature.getName()));

            this.add(this.iconView);
            this.add(this.positionText);

            Color c = this.getBackground();
            this.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue(), 250));
        }
    }

}

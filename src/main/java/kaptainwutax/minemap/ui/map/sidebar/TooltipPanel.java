package kaptainwutax.minemap.ui.map.sidebar;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.ui.DrawInfo;
import kaptainwutax.minemap.ui.map.MapPanel;
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
            DrawInfo info = new DrawInfo(0, 0, size, size);

            this.map.scheduler.forEachFragment(fragment -> {
                fragment.getHoveredFeatures(info).forEach((feature, positions) -> {
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

            this.positionText = new JLabel(feature.getName() + " at (" + pos.getX() + ", " + pos.getZ() + ")");

            this.add(this.iconView);
            this.add(this.positionText);

            Color c = this.getBackground();
            this.setBackground(new Color(c.getRed(), c.getGreen(), c.getBlue(), 250));
        }
    }

}

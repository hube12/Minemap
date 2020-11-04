package kaptainwutax.minemap.ui.map.sidebar;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.util.Str;
import kaptainwutax.seedutils.mc.pos.BPos;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TooltipPanel extends JPanel {

    private final MapPanel map;

    public TooltipPanel(MapPanel map) {
        this.map = map;
        this.setLayout(new GridLayout(0, 1, 2, 2));
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
                    BufferedImage icon = Icons.REGISTRY.get(feature.getClass());
                    int iconSizeX,iconSizeZ;
                    int defaultValue=20;
                    float factor=1.5F;
                    if (icon.getRaster().getWidth() > icon.getRaster().getHeight()) {
                        iconSizeX = defaultValue;
                        iconSizeZ=(int)(defaultValue*(float)icon.getRaster().getHeight()/icon.getRaster().getWidth());
                    }else{
                        iconSizeZ = defaultValue;
                        iconSizeX=(int)(defaultValue*(float)icon.getRaster().getWidth()/icon.getRaster().getHeight());
                    }
                    g.drawImage(icon, (defaultValue-iconSizeX)/2, (defaultValue-iconSizeZ)/2, (int) (iconSizeX*factor),  (int) (iconSizeZ*factor), null);
                }
            };

            this.positionText = new JLabel(" [" + pos.getX() + ", " + pos.getZ() + "] " + Str.formatName(feature.getName()));
            this.positionText.setFont(new Font(this.positionText.getFont().getName(), Font.PLAIN, 18));
            this.positionText.setBackground(new Color(0, 0, 0, 0));
            this.positionText.setFocusable(false);
            this.positionText.setOpaque(true);
            this.positionText.setForeground(Color.WHITE);

            this.add(this.iconView);
            this.add(this.positionText);

            this.setBackground(new Color(0, 0, 0, 180));
        }
    }

}

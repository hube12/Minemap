package kaptainwutax.minemap.ui.component;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.util.data.Str;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class FeatureEntry extends JPanel {

    private final JCheckBox checkBox;
    private final JComponent iconView;

    public FeatureEntry(Feature<?, ?> feature) {
        this.checkBox = new JCheckBox(Str.formatName(feature.getName()));

        this.iconView = new JComponent() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(20, 20);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                BufferedImage icon = Icons.get(feature.getClass());
                if (icon == null) return;
                int iconSizeX, iconSizeZ;
                int defaultValue = 20;
                if (icon.getRaster().getWidth() > icon.getRaster().getHeight()) {
                    iconSizeX = defaultValue;
                    iconSizeZ = (int) (defaultValue * (float) icon.getRaster().getHeight() / icon.getRaster().getWidth());
                } else {
                    iconSizeZ = defaultValue;
                    iconSizeX = (int) (defaultValue * (float) icon.getRaster().getWidth() / icon.getRaster().getHeight());
                }
                g.drawImage(icon, (defaultValue - iconSizeX) / 2, (defaultValue - iconSizeZ) / 2, iconSizeX, iconSizeZ, null);
            }
        };

        this.add(this.iconView);
        this.add(this.checkBox);
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    }

    public JCheckBox getCheckBox() {
        return this.checkBox;
    }

}

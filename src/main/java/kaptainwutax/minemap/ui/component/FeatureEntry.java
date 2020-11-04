package kaptainwutax.minemap.ui.component;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.util.Str;

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
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                BufferedImage icon = Icons.REGISTRY.get(feature.getClass());
                g.drawImage(icon, 0, 0, icon.getWidth(), icon.getHeight(), null);
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

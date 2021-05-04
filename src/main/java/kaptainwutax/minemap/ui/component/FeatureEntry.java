package kaptainwutax.minemap.ui.component;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.util.data.Str;

import javax.swing.*;
import java.awt.*;

import static kaptainwutax.minemap.util.ui.graphics.Icon.paintImage;

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
                paintImage(Icons.get(feature.getClass()), g, 20, 1.0F);
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

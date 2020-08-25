package kaptainwutax.minemap.ui.component;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Icons;

import javax.swing.*;
import java.awt.*;

public class FeatureEntry extends JPanel {

    private final JCheckBox checkBox;
    private final JComponent iconView;

    public FeatureEntry(Feature<?, ?> feature) {
        this.checkBox = new JCheckBox(feature.getName());

        this.iconView = new JComponent() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(20, 20);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image icon = Icons.REGISTRY.get(feature.getClass());
                g.drawImage(icon, 0, 0, 20, 20, null);
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

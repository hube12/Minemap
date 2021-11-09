package com.seedfinding.minemap.ui.component;

import com.seedfinding.mcfeature.Feature;
import com.seedfinding.minemap.init.Icons;
import com.seedfinding.minemap.util.data.Str;
import com.seedfinding.minemap.util.ui.graphics.Icon;

import javax.swing.*;
import java.awt.*;

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
                Icon.paintImage(Icons.get(feature.getClass()), g, 20, 1.0F);
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

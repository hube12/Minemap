package kaptainwutax.minemap.ui.component;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.util.data.Str;

import javax.swing.*;
import java.awt.*;

public class BiomeEntry extends JPanel {

    private final JCheckBox checkBox;
    private final JComponent colorView;

    public BiomeEntry(Biome biome) {
        this.checkBox = new JCheckBox(Str.formatName(biome.getName()));

        this.colorView = new JComponent() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(20, 20);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Color color = Configs.BIOME_COLORS.get(Configs.USER_PROFILE.getUserSettings().style, biome);
                g.setColor(color == null ? Color.BLACK : color);
                g.fillRect(0, 0, 20, 20);
            }
        };

        this.add(this.colorView);
        this.add(this.checkBox);
        this.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
    }

    public JCheckBox getCheckBox() {
        return this.checkBox;
    }

}

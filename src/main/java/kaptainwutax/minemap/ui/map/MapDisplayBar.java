package kaptainwutax.minemap.ui.map;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.minemap.ui.component.Dropdown;

import javax.swing.*;
import java.awt.*;
import java.util.stream.IntStream;

public class MapDisplayBar extends JPanel {

    private final MapPanel panel;

    private JLabel biomeDisplay;
    private Dropdown<Integer> layerDropdown;

    public MapDisplayBar(MapPanel panel) {
        this.panel = panel;
        this.addBiomeDisplay();
        this.addLayerDropdown();
    }

    private void addBiomeDisplay() {
        this.biomeDisplay = new JLabel();
        this.biomeDisplay.setFocusable(false);
        this.biomeDisplay.setOpaque(true);
        this.biomeDisplay.setVerticalAlignment(SwingConstants.TOP);
        this.biomeDisplay.setHorizontalAlignment(SwingConstants.LEFT);
        //this.biomeDisplay.setBackground(new Color(0, 0, 0, 127));
        this.biomeDisplay.setForeground(Color.WHITE);
        this.biomeDisplay.setHorizontalTextPosition(SwingConstants.LEFT);
        //this.biomeDisplay.setFont(new Font(".SF NS Text", Font.BOLD, 14));
        this.add(this.biomeDisplay);
    }

    private void addLayerDropdown() {
        BiomeSource source = this.panel.info.getBiomeSource();
        this.layerDropdown = new Dropdown<>(i -> "[" + i + "] " + source.getLayer(i).getClass().getSimpleName() + " " + source.getLayer(i).getScale() + ":1", IntStream.range(0, source.getLayerCount()).boxed());
        this.layerDropdown.selectIfPresent(this.panel.info.layerId);

        this.layerDropdown.addActionListener(e1 -> {
            this.panel.info.layerId = this.layerDropdown.getSelected();
            this.panel.restart();
        });

        this.add(this.layerDropdown);
    }

    public void setBiomeDisplay(int x, int z, String biomeName) {
        this.biomeDisplay.setText(String.format("Seed %d at (%d, %d): %s", this.panel.info.worldSeed, x, z, biomeName));
    }

}

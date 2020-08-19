package kaptainwutax.minemap.ui.map;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.layer.BiomeLayer;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.minemap.ui.component.Dropdown;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.RPos;

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
        BiomeSource source = this.panel.getContext().getBiomeSource();
        this.layerDropdown = new Dropdown<>(i -> "[" + i + "] " + source.getLayer(i).getClass().getSimpleName() + " " + source.getLayer(i).getScale() + ":1", IntStream.range(0, source.getLayerCount()).boxed());
        this.layerDropdown.selectIfPresent(this.panel.getContext().getLayerId());

        this.layerDropdown.addActionListener(e1 -> {
            this.panel.getContext().setLayerId(this.layerDropdown.getSelected());
            this.panel.restart();
        });

        this.add(this.layerDropdown);
    }

    public void updateBiomeDisplay(int blockX, int blockZ) {
        Biome biome = this.getBiome(blockX, blockZ);
        String name = biome == null ? "UNKNOWN" : biome.getName().toUpperCase();
        this.biomeDisplay.setText(String.format("Seed %d at (%d, %d): %s", this.panel.getContext().worldSeed, blockX, blockZ, name));
    }

    private Biome getBiome(int blockX, int blockZ) {
        BiomeLayer layer = this.panel.getContext().getBiomeLayer();
        RPos pos = new BPos(blockX, 0, blockZ).toRegionPos(layer.getScale());
        return Biome.REGISTRY.get(layer.get(pos.getX(), 0, pos.getZ()));
    }

}

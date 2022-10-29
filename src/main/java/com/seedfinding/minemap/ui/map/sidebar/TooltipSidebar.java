package com.seedfinding.minemap.ui.map.sidebar;

import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mcbiome.biome.Biomes;
import com.seedfinding.mcbiome.layer.BiomeLayer;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.RPos;
import com.seedfinding.mcterrain.TerrainGenerator;
import com.seedfinding.minemap.init.Configs;
import com.seedfinding.minemap.ui.map.MapPanel;
import com.seedfinding.minemap.util.data.Str;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TooltipSidebar extends JPanel {

    private final MapPanel map;
    public TooltipPanel tooltip;
    private JLabel biomeDisplay;

    public TooltipSidebar(MapPanel map) {
        this.map = map;
        this.addBiomeDisplay();
        this.addTooltip();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(new Color(0, 0, 0, 0));
        this.setOpaque(false);
    }

    public static int getBiome(MapPanel map, int blockX, int blockZ) {
        BiomeLayer layer = map.getContext().getBiomeLayer();
        RPos pos = new BPos(blockX, 0, blockZ).toRegionPos(layer.getScale());
        return layer.getBiome(pos.getX(), 0, pos.getZ());
    }

    public static Integer getHeight(MapPanel map, int blockX, int blockZ) {
        TerrainGenerator generator = map.getContext().getTerrainGenerator();
        if (generator == null) return null;
        return generator.getHeightOnGround(blockX, blockZ);
    }

    private void addBiomeDisplay() {
        this.biomeDisplay = new JLabel();
        this.biomeDisplay.setFocusable(false);
        this.biomeDisplay.setOpaque(true);
        this.biomeDisplay.setVerticalAlignment(SwingConstants.TOP);
        this.biomeDisplay.setHorizontalAlignment(SwingConstants.LEFT);
        this.biomeDisplay.setBackground(new Color(0, 0, 0, 180));
        this.biomeDisplay.setForeground(Color.WHITE);
        this.biomeDisplay.setFont(new Font(this.biomeDisplay.getFont().getName(), Font.PLAIN, 20));
        this.biomeDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.biomeDisplay.setBorder(new EmptyBorder(5, 10, 5, 10));
        this.add(this.biomeDisplay);
    }

    private void addTooltip() {
        this.tooltip = new TooltipPanel(this.map);

        this.tooltip.setFocusable(false);
        this.tooltip.setOpaque(false);
        this.tooltip.setBorder(new EmptyBorder(5, 0, 5, 0));
        this.add(this.tooltip);
    }

    public void updateBiomeDisplay(int blockX, int blockZ) {
        int biomeId = getBiome(this.map, blockX, blockZ);
        Biome biome = Biomes.REGISTRY.get(biomeId);
        String name = biome == null ? "Unknown" : Str.formatName(biome.getName());

        String text = String.format("[%d, %d] %s - ID %d (0x%s)", blockX, blockZ, name,
            biomeId, Integer.toHexString(biomeId).toUpperCase());
        if (this.map.getContext().getSettings().showFlowers){

        }
        this.biomeDisplay.setText(text);
        if (Configs.USER_PROFILE.getUserSettings().doHeightmap) {
            Integer height = getHeight(this.map, blockX, blockZ);
            String heightText;
            if (height==null){
                heightText = "No height";
            }else{
                heightText = String.format("[%d, %d] height: %d", blockX, blockZ, height);
            }
            this.biomeDisplay.setText("<html>" + text + "<br>" + heightText + "</html>");
        }
    }

}

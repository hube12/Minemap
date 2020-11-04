package kaptainwutax.minemap.ui.map.sidebar;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.layer.BiomeLayer;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.util.Str;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.RPos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class TooltipSidebar extends JPanel {

    private final MapPanel map;

    private JLabel biomeDisplay;
    public TooltipPanel tooltip;

    public TooltipSidebar(MapPanel map) {
        this.map = map;
        this.addBiomeDisplay();
        this.addTooltip();
        this.setLayout(new BoxLayout(this,BoxLayout.Y_AXIS));
        this.setBackground(new Color(0, 0, 0, 0));
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
        this.tooltip.setBorder(new EmptyBorder(5, 0, 5, 0));
        this.add(this.tooltip);
    }

    public void updateBiomeDisplay(int blockX, int blockZ) {
        int biomeId = this.getBiome(blockX, blockZ);
        Biome biome = Biome.REGISTRY.get(biomeId);
        String name = biome == null ? "Unknown" : Str.formatName(biome.getName());

        String text = String.format("[%d, %d] %s - ID %d (0x%s)", blockX, blockZ, name,
                biomeId, Integer.toHexString(biomeId).toUpperCase());
        this.biomeDisplay.setText(text);
    }

    private int getBiome(int blockX, int blockZ) {
        BiomeLayer layer = this.map.getContext().getBiomeLayer();
        RPos pos = new BPos(blockX, 0, blockZ).toRegionPos(layer.getScale());
        return layer.get(pos.getX(), 0, pos.getZ());
    }

}

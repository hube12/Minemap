package kaptainwutax.minemap.ui.map.sidebar;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.layer.BiomeLayer;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.RPos;

import javax.swing.*;
import java.awt.*;

public class TooltipSidebar extends JPanel {

    private final MapPanel map;

    private JLabel biomeDisplay;
    private TooltipPanel tooltip;
    private JButton pinButton;
    public JButton settingsButton;

    public TooltipSidebar(MapPanel map) {
        this.map = map;
        this.addBiomeDisplay();
        this.addTooltip();
        this.addPinSettingsButton();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        //this.setBackground(new Color(0, 0, 0, 0));
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
        this.biomeDisplay.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(this.biomeDisplay);
    }

    private void addTooltip() {
        this.tooltip = new TooltipPanel(this.map);
        this.add(this.tooltip);
    }

    public void updateBiomeDisplay(int blockX, int blockZ) {
        int biomeId = this.getBiome(blockX, blockZ);
        Biome biome = Biome.REGISTRY.get(biomeId);
        String name = biome == null ? "UNKNOWN" : biome.getName().toUpperCase();

        String text = String.format("(%d, %d): %s with ID %d (0x%s)", blockX, blockZ, name,
                biomeId, Integer.toHexString(biomeId).toUpperCase());
        this.biomeDisplay.setText(text);
        this.tooltip.repaint();
    }

    private int getBiome(int blockX, int blockZ) {
        BiomeLayer layer = this.map.getContext().getBiomeLayer();
        RPos pos = new BPos(blockX, 0, blockZ).toRegionPos(layer.getScale());
        return layer.get(pos.getX(), 0, pos.getZ());
    }

    private void addPinSettingsButton() {
        this.settingsButton = new JButton("Settings");

        this.pinButton = new JButton("Pin");

        this.pinButton.addMouseListener(Events.Mouse.onPressed(e -> {
            boolean newState = !MineMap.INSTANCE.worldTabs.getSelectedHeader().isPinned();
            MineMap.INSTANCE.worldTabs.getSelectedHeader().setPinned(newState);
            this.pinButton.setText(newState ? "Unpin" : "Pin");
        }));

        this.pinButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel duo = new JPanel();
        duo.add(this.pinButton);
        duo.add(this.settingsButton);
        duo.setLayout(new BoxLayout(duo, BoxLayout.X_AXIS));
        duo.setBackground(new Color(0, 0, 0, 0));
        this.add(duo);
    }

}

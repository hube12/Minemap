package kaptainwutax.minemap.ui.map;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.layer.BiomeLayer;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.component.BiomeEntry;
import kaptainwutax.minemap.ui.component.Dropdown;
import kaptainwutax.minemap.ui.component.FeatureEntry;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.RPos;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.stream.IntStream;

public class MapDisplayBar extends JPanel {

    private final MapPanel panel;

    private JLabel biomeDisplay;
    private Dropdown<Integer> layerDropdown;
    private JButton pinButton;

    public MapDisplayBar(MapPanel panel) {
        this.panel = panel;
        //this.addPinButton();
        this.addBiomeDisplay();
        this.addLayerDropdown();
        this.addFeatureToggles();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    private void addFeatureToggles() {
        JPanel toggles = new JPanel();
        toggles.setLayout(new GridLayout(0, 1));

        JScrollPane panelPane = new JScrollPane(toggles);
        panelPane.getVerticalScrollBar().setUnitIncrement(20);
        panelPane.setPreferredSize(new Dimension(300, 300));

        MapSettings settings = this.panel.getContext().getSettings();

        //=====================================================================================

        JCheckBox showBiomes = new JCheckBox("Show Biomes") {
            @Override
            protected void paintComponent(Graphics g) {
                this.setSelected(settings.showBiomes);
                super.paintComponent(g);
            }
        };

        JCheckBox showFeatures = new JCheckBox("Show Features") {
            @Override
            protected void paintComponent(Graphics g) {
                this.setSelected(settings.showFeatures);
                super.paintComponent(g);
            }
        };

        JCheckBox showGrid = new JCheckBox("Show Grid") {
            @Override
            protected void paintComponent(Graphics g) {
                this.setSelected(settings.showGrid);
                super.paintComponent(g);
            }
        };

        showBiomes.addItemListener(e -> {
            settings.showBiomes = showBiomes.isSelected();
            this.panel.repaint();
        });

        showFeatures.addItemListener(e -> {
            settings.showFeatures = showFeatures.isSelected();
            this.panel.repaint();
        });

        showGrid.addItemListener(e -> {
            settings.showGrid = showGrid.isSelected();
            this.panel.repaint();
        });

        toggles.add(showBiomes);
        toggles.add(showFeatures);
        toggles.add(showGrid);

        //=====================================================================================

        for(Feature<?, ?> feature: settings.getAllFeatures()) {
            FeatureEntry entry = new FeatureEntry(feature) {
                @Override
                public void paintComponent(Graphics g) {
                    this.getCheckBox().setSelected(settings.isActive(feature));
                    super.paintComponent(g);
                }
            };

            entry.getCheckBox().setSelected(settings.isActive(feature));

            entry.getCheckBox().addItemListener(e -> {
                settings.setState(feature, entry.getCheckBox().isSelected());
                this.panel.repaint();
            });

            toggles.add(entry);
        }

        for(Biome biome: settings.getAllBiomes()) {
            BiomeEntry entry = new BiomeEntry(biome) {
                @Override
                public void paintComponent(Graphics g) {
                    this.getCheckBox().setSelected(settings.isActive(biome));
                    super.paintComponent(g);
                }
            };

            entry.getCheckBox().setSelected(settings.isActive(biome));

            entry.getCheckBox().addItemListener(e -> {
                settings.setState(biome, entry.getCheckBox().isSelected());
                this.panel.repaint();
            });

            toggles.add(entry);
        }

        //=====================================================================================

        JButton hideAll = new JButton("Hide All");
        JButton showAll = new JButton("Show All");

        hideAll.addMouseListener(Events.Mouse.onPressed(e -> {
            settings.getAllBiomes().forEach(settings::hide);
            settings.getAllFeatures().forEach(settings::hide);
            Arrays.stream(toggles.getComponents()).filter(c -> c instanceof FeatureEntry)
                    .map(c -> (FeatureEntry)c).forEach(c -> c.getCheckBox().setSelected(false));
            Arrays.stream(toggles.getComponents()).filter(c -> c instanceof BiomeEntry)
                    .map(c -> (BiomeEntry)c).forEach(c -> c.getCheckBox().setSelected(false));
            this.panel.repaint();
        }));

        showAll.addMouseListener(Events.Mouse.onPressed(e -> {
            settings.getAllBiomes().forEach(settings::show);
            settings.getAllFeatures().forEach(settings::show);
            Arrays.stream(toggles.getComponents()).filter(c -> c instanceof FeatureEntry)
                    .map(c -> (FeatureEntry)c).forEach(c -> c.getCheckBox().setSelected(true));
            Arrays.stream(toggles.getComponents()).filter(c -> c instanceof BiomeEntry)
                    .map(c -> (BiomeEntry)c).forEach(c -> c.getCheckBox().setSelected(true));
            this.panel.repaint();
        }));

        //=====================================================================================

        JButton set = new JButton("Set as Default");
        JButton reset = new JButton("Reset to Default");

        set.addMouseListener(Events.Mouse.onPressed(e -> {
            Configs.USER_PROFILE.setDefaultSettings(this.panel.getContext().dimension, settings);
        }));

        reset.addMouseListener(Events.Mouse.onPressed(e -> {
            settings.set(Configs.USER_PROFILE.getSettingsCopy(this.panel.getContext().version, this.panel.getContext().dimension));
            this.panel.repaint();
            toggles.repaint();
        }));

        //=====================================================================================
        JPanel duo = new JPanel();
        duo.add(showAll);
        duo.add(hideAll);
        duo.setLayout(new BoxLayout(duo, BoxLayout.X_AXIS));

        JPanel duo2 = new JPanel();
        duo2.add(set);
        duo2.add(reset);
        duo2.setLayout(new BoxLayout(duo2, BoxLayout.X_AXIS));
        //=====================================================================================

        this.add(panelPane);
        this.add(duo);
        this.add(duo2);
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

    private void addLayerDropdown() {
        BiomeSource source = this.panel.getContext().getBiomeSource();
        this.layerDropdown = new Dropdown<>(i -> "[" + i + "] " + source.getLayer(i).getClass().getSimpleName() + " " + source.getLayer(i).getScale() + ":1", IntStream.range(0, source.getLayerCount()).boxed());
        this.layerDropdown.selectIfPresent(this.panel.getContext().getLayerId());

        this.layerDropdown.addActionListener(e1 -> {
            this.panel.getContext().setLayerId(this.layerDropdown.getSelected());
            this.panel.restart();
        });

        this.layerDropdown.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(this.layerDropdown);
    }

    private void addPinButton() {
        this.pinButton = new JButton("Pin");

        this.pinButton.addMouseListener(Events.Mouse.onPressed(e -> {
            boolean newState = !MineMap.INSTANCE.worldTabs.getSelectedHeader().isPinned();
            MineMap.INSTANCE.worldTabs.getSelectedHeader().setPinned(newState);
            this.pinButton.setText(newState ? "Unpin" : "Pin");
        }));

        this.pinButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(this.pinButton);
    }

    public void updateBiomeDisplay(int blockX, int blockZ) {
        int biomeId = this.getBiome(blockX, blockZ);
        Biome biome = Biome.REGISTRY.get(biomeId);
        String name = biome == null ? "UNKNOWN" : biome.getName().toUpperCase();

        String text = String.format("(%d, %d): %s with ID %d (0x%s)", blockX, blockZ, name,
                biomeId, Integer.toHexString(biomeId).toUpperCase());
        this.biomeDisplay.setText(text);
    }

    private int getBiome(int blockX, int blockZ) {
        BiomeLayer layer = this.panel.getContext().getBiomeLayer();
        RPos pos = new BPos(blockX, 0, blockZ).toRegionPos(layer.getScale());
        return layer.get(pos.getX(), 0, pos.getZ());
    }

}

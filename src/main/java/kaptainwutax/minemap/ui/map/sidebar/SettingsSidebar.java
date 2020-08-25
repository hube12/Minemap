package kaptainwutax.minemap.ui.map.sidebar;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.component.BiomeEntry;
import kaptainwutax.minemap.ui.component.FeatureEntry;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.ui.map.MapSettings;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class SettingsSidebar extends JPanel {

    private final MapPanel map;
    private final MapSettings settings;

    private final JPanel toggles = new JPanel();
    public JButton closeButton;

    public SettingsSidebar(MapPanel map) {
        this.map = map;
        this.settings = this.map.getContext().getSettings();

        this.initialize();
        this.addGlobalToggles();
        this.addFeatureToggles();
        this.addBiomeToggles();
        this.addHideShowButtons();
        this.addSetResetButtons();
        this.addCloseButton();
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    }

    private void initialize() {
        this.toggles.setLayout(new GridLayout(0, 1));
        JScrollPane scrollPane = new JScrollPane(this.toggles);
        scrollPane.getVerticalScrollBar().setUnitIncrement(20);
        scrollPane.setPreferredSize(new Dimension(300, 600));
        this.add(scrollPane);
    }

    private void addGlobalToggles() {
        JCheckBox showBiomes = new JCheckBox("Show Biomes") {
            @Override
            protected void paintComponent(Graphics g) {
                this.setSelected(SettingsSidebar.this.settings.showBiomes);
                super.paintComponent(g);
            }
        };

        JCheckBox showFeatures = new JCheckBox("Show Features") {
            @Override
            protected void paintComponent(Graphics g) {
                this.setSelected(SettingsSidebar.this.settings.showFeatures);
                super.paintComponent(g);
            }
        };

        JCheckBox showGrid = new JCheckBox("Show Grid") {
            @Override
            protected void paintComponent(Graphics g) {
                this.setSelected(SettingsSidebar.this.settings.showGrid);
                super.paintComponent(g);
            }
        };

        showBiomes.addItemListener(e -> {
            this.settings.showBiomes = showBiomes.isSelected();
            this.map.repaint();
        });

        showFeatures.addItemListener(e -> {
            this.settings.showFeatures = showFeatures.isSelected();
            this.map.repaint();
        });

        showGrid.addItemListener(e -> {
            this.settings.showGrid = showGrid.isSelected();
            this.map.repaint();
        });

        this.toggles.add(showBiomes);
        this.toggles.add(showFeatures);
        this.toggles.add(showGrid);
    }

    private void addFeatureToggles() {
        for(Feature<?, ?> feature: this.settings.getAllFeatures()) {
            FeatureEntry entry = new FeatureEntry(feature) {
                @Override
                public void paintComponent(Graphics g) {
                    this.getCheckBox().setSelected(SettingsSidebar.this.settings.isActive(feature));
                    super.paintComponent(g);
                }
            };

            entry.getCheckBox().setSelected(this.settings.isActive(feature));

            entry.getCheckBox().addItemListener(e -> {
                this.settings.setState(feature, entry.getCheckBox().isSelected());
                this.map.repaint();
            });

            this.toggles.add(entry);
        }
    }

    private void addBiomeToggles() {
        for(Biome biome: this.settings.getAllBiomes()) {
            BiomeEntry entry = new BiomeEntry(biome) {
                @Override
                public void paintComponent(Graphics g) {
                    this.getCheckBox().setSelected(SettingsSidebar.this.settings.isActive(biome));
                    super.paintComponent(g);
                }
            };

            entry.getCheckBox().setSelected(this.settings.isActive(biome));

            entry.getCheckBox().addItemListener(e -> {
                this.settings.setState(biome, entry.getCheckBox().isSelected());
                this.map.repaint();
            });

            this.toggles.add(entry);
        }
    }

    private void addHideShowButtons() {
        JButton hideAll = new JButton("Hide All");
        JButton showAll = new JButton("Show All");

        hideAll.addMouseListener(Events.Mouse.onPressed(e -> {
            this.settings.getAllBiomes().forEach(this.settings::hide);
            this.settings.getAllFeatures().forEach(this.settings::hide);

            Arrays.stream(this.toggles.getComponents()).filter(c -> c instanceof FeatureEntry)
                    .map(c -> (FeatureEntry)c).forEach(c -> c.getCheckBox().setSelected(false));
            Arrays.stream(this.toggles.getComponents()).filter(c -> c instanceof BiomeEntry)
                    .map(c -> (BiomeEntry)c).forEach(c -> c.getCheckBox().setSelected(false));
            this.map.repaint();
        }));

        showAll.addMouseListener(Events.Mouse.onPressed(e -> {
            this.settings.getAllBiomes().forEach(this.settings::show);
            this.settings.getAllFeatures().forEach(this.settings::show);

            Arrays.stream(this.toggles.getComponents()).filter(c -> c instanceof FeatureEntry)
                    .map(c -> (FeatureEntry)c).forEach(c -> c.getCheckBox().setSelected(true));
            Arrays.stream(this.toggles.getComponents()).filter(c -> c instanceof BiomeEntry)
                    .map(c -> (BiomeEntry)c).forEach(c -> c.getCheckBox().setSelected(true));
            this.map.repaint();
        }));

        JPanel duo = new JPanel();
        duo.add(showAll);
        duo.add(hideAll);
        duo.setLayout(new BoxLayout(duo, BoxLayout.X_AXIS));
        this.add(duo);
    }

    private void addSetResetButtons() {
        JButton set = new JButton("Set as Default");
        JButton reset = new JButton("Reset to Default");

        set.addMouseListener(Events.Mouse.onPressed(e -> {
            Configs.USER_PROFILE.setDefaultSettings(this.map.getContext().dimension, this.settings);
        }));

        reset.addMouseListener(Events.Mouse.onPressed(e -> {
            this.settings.set(Configs.USER_PROFILE.getSettingsCopy(this.map.getContext().version, this.map.getContext().dimension));
            this.map.repaint();
            this.toggles.repaint();
        }));

        JPanel duo = new JPanel();
        duo.add(set);
        duo.add(reset);
        duo.setLayout(new BoxLayout(duo, BoxLayout.X_AXIS));
        this.add(duo);
    }

    private void addCloseButton() {
        this.closeButton = new JButton("Close");
        this.closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.add(closeButton);
    }

}

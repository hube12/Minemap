package com.seedfinding.minemap.ui.component;

import com.seedfinding.mcfeature.Feature;
import com.seedfinding.minemap.init.Configs;
import com.seedfinding.minemap.init.Icons;
import com.seedfinding.minemap.ui.map.MapPanel;
import com.seedfinding.minemap.ui.map.MapSettings;
import com.seedfinding.minemap.ui.map.tool.Area;
import com.seedfinding.minemap.ui.map.tool.Circle;
import com.seedfinding.minemap.ui.map.tool.Polyline;
import com.seedfinding.minemap.ui.map.tool.Ruler;
import com.seedfinding.minemap.ui.map.tool.Tool;
import com.seedfinding.minemap.util.data.Str;
import com.seedfinding.minemap.util.ui.graphics.Icon;
import com.seedfinding.minemap.util.ui.graphics.VectorIcon;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Compact ribbon above the dimension tabs: quick toggles for the map settings of the selected map
 * (display options, measurement tools, then one icon per feature/structure).
 */
public class RibbonBar extends JPanel {
    private static final int ICON_SIZE = 18;
    private static final Insets TEXT_MARGIN = new Insets(0, 4, 0, 4);
    private static final Insets ICON_MARGIN = new Insets(1, 1, 1, 1);
    private static final List<Supplier<Tool>> TOOLS = List.of(Ruler::new, Area::new, Circle::new, Polyline::new);
    private final WorldTabs worldTabs;
    private final JPanel leading = new JPanel(new GridLayout(1, 0, 1, 0));
    private final JPanel row = new JPanel(new FlowLayout(FlowLayout.LEADING, 1, 1));
    private MapPanel shownMap = null;

    public RibbonBar(WorldTabs worldTabs) {
        super(new BorderLayout());
        this.worldTabs = worldTabs;
        JScrollPane scroll = new JScrollPane(row, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getHorizontalScrollBar().setUnitIncrement(20);
        scroll.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 6));
        this.add(scroll, BorderLayout.CENTER);
        leading.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 0));
        this.add(leading, BorderLayout.WEST);
        // bar at the bottom to visually separate the ribbon from the map
        this.add(new JSeparator(SwingConstants.HORIZONTAL), BorderLayout.SOUTH);

        // rebuild when the selected map (tab / dimension / seed) changes
        worldTabs.getJTabbedPane().addChangeListener(e -> rebuild());
        // keep the pressed states in sync with changes made elsewhere (e.g. the settings sidebar)
        new Timer(300, e -> {
            if (worldTabs.getSelectedMapPanel() != shownMap) rebuild();
            else this.repaint();
        }).start();
        rebuild();
    }

    private void rebuild() {
        MapPanel map = worldTabs.getSelectedMapPanel();
        shownMap = map;
        row.removeAll();
        leading.removeAll();
        if (map != null) {
            MapSettings s = map.getContext().getSettings();
            addGlobal(map, "Biomes", "Show biomes", st -> st.showBiomes, (st, v) -> st.showBiomes = v);
            addGlobal(map, "Features", "Show features", st -> st.showFeatures, (st, v) -> st.showFeatures = v);
            addGlobal(map, "Grid", "Show grid", st -> st.showGrid, (st, v) -> st.showGrid = v);
            while (row.getComponentCount() > 0) leading.add(row.getComponent(0)); // moves the 3 toggles out of the row
            addSeparator();
            for (Supplier<Tool> tool : TOOLS) {
                addTool(map, tool);
            }
            addSeparator();
            for (Feature<?, ?> feature : s.getAllFeatures()) {
                addFeature(map, s, feature);
            }
        }
        this.setVisible(map != null);
        this.revalidate();
        this.repaint();
    }

    /**
     * Persist the map settings as the default for this dimension (same as "Set as Default" in the sidebar).
     */
    private static void save(MapPanel map) {
        Configs.USER_PROFILE.setDefaultSettings(map.getContext().dimension, map.getContext().getSettings());
    }

    private void addSeparator() {
        JSeparator separator = new JSeparator(SwingConstants.VERTICAL);
        separator.setPreferredSize(new Dimension(6, 22));
        row.add(separator);
    }

    private void addGlobal(MapPanel map, String text, String tip, Function<MapSettings, Boolean> getter, BiConsumer<MapSettings, Boolean> setter) {
        JToggleButton button = new JToggleButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                this.setSelected(Boolean.TRUE.equals(getter.apply(map.getContext().getSettings())));
                super.paintComponent(g);
            }
        };
        compact(button, TEXT_MARGIN, tip);
        button.addActionListener(e -> {
            setter.accept(map.getContext().getSettings(), button.isSelected());
            save(map);
            map.repaint();
        });
        row.add(button);
    }

    private void addTool(MapPanel map, Supplier<Tool> supplier) {
        Tool prototype = supplier.get();
        VectorIcon vector = Icons.getVector(prototype.getClass());
        JToggleButton button = new JToggleButton(vector == null ? prototype.getName() : null, vector == null ? null : new javax.swing.Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                vector.paint(g, x, y, ICON_SIZE, ICON_SIZE);
            }

            @Override
            public int getIconWidth() {
                return ICON_SIZE;
            }

            @Override
            public int getIconHeight() {
                return ICON_SIZE;
            }
        }) {
            @Override
            protected void paintComponent(Graphics g) {
                this.setSelected(map.manager.isToolSelected(prototype.getClass()));
                super.paintComponent(g);
            }
        };
        compact(button, vector == null ? TEXT_MARGIN : ICON_MARGIN, "Measure: " + prototype.getName());
        button.addActionListener(e -> {
            map.manager.toggleTool(supplier);
            map.repaint();
        });
        row.add(button);
    }

    private void addFeature(MapPanel map, MapSettings settings, Feature<?, ?> feature) {
        ImageIcon icon = Icon.getIcon(feature.getClass(), ICON_SIZE, ICON_SIZE, null);
        JToggleButton button = new JToggleButton(icon) {
            @Override
            protected void paintComponent(Graphics g) {
                this.setSelected(settings.isActive(feature));
                super.paintComponent(g);
            }
        };
        if (icon == null) button.setText(Str.prettifyDashed(feature.getName()));
        compact(button, icon == null ? TEXT_MARGIN : ICON_MARGIN, Str.prettifyDashed(feature.getName()));
        button.addActionListener(e -> {
            settings.setState(feature, button.isSelected());
            save(map);
            map.repaint();
        });
        row.add(button);
    }

    private static void compact(AbstractButton button, Insets margin, String tip) {
        button.setMargin(margin);
        button.setFocusable(false);
        button.setToolTipText(tip);
        button.setFont(button.getFont().deriveFont(button.getFont().getSize2D() - 1f));
    }
}

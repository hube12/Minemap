package kaptainwutax.minemap.ui.component;

import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.ui.map.MapPanel;

import javax.swing.*;
import java.util.*;

public class TabGroup extends JTabbedPane {

    private final MCVersion version;
    protected Map<Dimension, MapPanel> mapPanels = new LinkedHashMap<>();
    private long worldSeed;
    private boolean lazyLoaded;
    private int threadCount;
    private Collection<Dimension> dimensions;

    public TabGroup(MCVersion version, String worldSeed, int threadCount) {
        this(version, worldSeed, threadCount, Arrays.asList(Dimension.values()));
    }

    public TabGroup(MCVersion version, String worldSeed, int threadCount, Dimension... dimensions) {
        this(version, worldSeed, threadCount, Arrays.asList(dimensions));
    }

    public TabGroup(MCVersion version, String worldSeed, int threadCount, Collection<Dimension> dimensions) {
        this(version,worldSeed,threadCount,dimensions,false);
    }

    public TabGroup(MCVersion version, String worldSeed, int threadCount, Collection<Dimension> dimensions, boolean lazyLoaded) {
        this.version = version;
        this.lazyLoaded = lazyLoaded;

        if (worldSeed.isEmpty()) {
            this.loadSeed(new Random().nextLong(), threadCount, dimensions);
        } else {
            try {
                this.loadSeed(Long.parseLong(worldSeed), threadCount, dimensions);
            } catch (NumberFormatException e) {
                this.loadSeed(worldSeed.hashCode(), threadCount, dimensions);
            }
        }
    }

    public boolean isLazyLoaded() {
        return lazyLoaded;
    }

    public MCVersion getVersion() {
        return this.version;
    }

    public long getWorldSeed() {
        return this.worldSeed;
    }

    public Collection<MapPanel> getMapPanels() {
        return this.mapPanels.values();
    }

    public Map<Dimension, MapPanel> getPanels() {
        return this.mapPanels;
    }

    private void loadSeed(long worldSeed, int threadCount, Collection<Dimension> dimensions) {
        this.worldSeed = worldSeed;
        this.threadCount=threadCount;
        this.dimensions=dimensions;
        if (!this.isLazyLoaded()){
            loadEffectively();
        }
    }

    public void loadEffectively() {
        Configs.USER_PROFILE.addRecentSeed(worldSeed, this.version);
        for (Dimension dimension : dimensions) {
            MapPanel mapPanel = new MapPanel(this.getVersion(), dimension, this.worldSeed, threadCount);
            this.mapPanels.put(dimension, mapPanel);
        }
        this.lazyLoaded =false;
    }

    public void invalidateAll() {
        this.mapPanels.values().forEach(MapPanel::restart);
    }

    public void removeIfPresent(MapPanel mapPanel) {
        this.mapPanels.entrySet().removeIf(e -> e.getValue() == mapPanel);
    }

    public boolean contains(MapPanel mapPanel) {
        return this.mapPanels.containsValue(mapPanel);
    }

    public boolean isEmpty() {
        return this.mapPanels.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TabGroup)) return false;
        TabGroup tabGroup = (TabGroup) o;
        return worldSeed == tabGroup.worldSeed && version == tabGroup.version && mapPanels.equals(tabGroup.mapPanels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, mapPanels, worldSeed);
    }
}

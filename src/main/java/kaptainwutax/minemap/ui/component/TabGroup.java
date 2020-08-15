package kaptainwutax.minemap.ui.component;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.minemap.ui.MapPanel;
import kaptainwutax.minemap.util.WorldInfo;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.MCVersion;

import java.util.*;

public class TabGroup {

    private final MCVersion version;
    private long worldSeed;

    protected Map<Dimension, MapPanel> mapPanels = new LinkedHashMap<>();

    public TabGroup(MCVersion version, String worldSeed, int threadCount) {
        this(version, worldSeed, threadCount, Arrays.asList(Dimension.values()));
    }

    public TabGroup(MCVersion version, String worldSeed, int threadCount, Dimension... dimensions) {
        this(version, worldSeed, threadCount, Arrays.asList(dimensions));
    }

    public TabGroup(MCVersion version, String worldSeed, int threadCount, Collection<Dimension> dimensions) {
        this.version = version;

        if(worldSeed.isEmpty()) {
            this.loadSeed(new Random().nextLong(), threadCount, dimensions);
        } else {
            try {this.loadSeed(Long.parseLong(worldSeed), threadCount, dimensions);}
            catch(NumberFormatException e) {this.loadSeed(worldSeed.hashCode(), threadCount, dimensions);}
        }
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

    private void loadSeed(long worldSeed, int threadCount, Collection<Dimension> dimensions) {
        this.worldSeed = worldSeed;

        for(Dimension dimension: dimensions) {
            WorldInfo worldInfo = new WorldInfo(this.getVersion(), this.worldSeed, BiomeSource.factory(dimension));
            MapPanel mapPanel = new MapPanel(worldInfo, threadCount);
            this.mapPanels.put(dimension, mapPanel);
        }
    }

    public void add(WorldTabs tabs) {
        String prefix = "[" + this.version + "] ";

        this.mapPanels.forEach((dimension, mapPanel) -> {
            String s = dimension.name.substring(0, 1).toUpperCase() + dimension.name.substring(1);
            tabs.addMapTab(prefix + s + " " + this.worldSeed, this, mapPanel);
        });
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

}

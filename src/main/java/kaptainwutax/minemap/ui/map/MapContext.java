package kaptainwutax.minemap.ui.map;

import kaptainwutax.biomeutils.layer.BiomeLayer;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.MCVersion;

import java.util.HashMap;
import java.util.Map;

public class MapContext {

    public final MCVersion version;
    public final Dimension dimension;
    public final long worldSeed;

    private final MapSettings settings;
    private final IconManager iconManager;

    private final ThreadLocal<Map<Dimension, BiomeSource>> biomeSource;
    private int layerId;

    public MapContext(long worldSeed, MapSettings settings) {
        this.version = settings.getVersion();
        this.dimension = settings.getDimension();
        this.worldSeed = worldSeed;
        this.settings = settings;

        this.biomeSource = ThreadLocal.withInitial(() -> {
            Map<Dimension, BiomeSource> map = new HashMap<>();
            for(Dimension dim: Dimension.values()) map.put(dim, BiomeSource.of(dim, this.version, worldSeed));
            return map;
        });

        this.layerId = this.getBiomeSource().getLayerCount() - 2;

        this.iconManager = new IconManager(this);
    }

    public MapContext(MCVersion version, Dimension dimension, long worldSeed) {
        this(worldSeed, Configs.USER_PROFILE.getMapSettingsCopy(version, dimension));
    }

    public IconManager getIconManager() {
        return this.iconManager;
    }

    public MapSettings getSettings() {
        return this.settings;
    }

    public int getLayerId() {
        return this.layerId;
    }

    public BiomeSource getBiomeSource() {
        return this.getBiomeSource(this.dimension);
    }

    public BiomeSource getBiomeSource(Dimension dimension) {
        return this.biomeSource.get().get(dimension);
    }

    public BiomeLayer getBiomeLayer() {
        return this.getBiomeSource().getLayer(this.layerId);
    }

    public MapContext setLayerId(int layerId) {
        this.layerId = layerId;
        return this;
    }

}

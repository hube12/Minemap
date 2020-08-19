package kaptainwutax.minemap.ui.map;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.layer.BiomeLayer;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Features;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.MCVersion;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class MapContext {

    public final MCVersion version;
    public final Dimension dimension;
    public final long worldSeed;

    private final Map<Class<? extends Feature<?, ?>>, Feature<?, ?>> features;

    private final Map<Class<? extends Feature<?, ?>>, Boolean> featureStates;
    private final Map<Biome, Boolean> biomeStates;

    private final ThreadLocal<BiomeSource> biomeSource;
    private int layerId;

    private final IconManager iconManager;

    public MapContext(MCVersion version, Dimension dimension, long worldSeed) {
        this.version = version;
        this.dimension = dimension;
        this.worldSeed = worldSeed;

        this.features = Features.getForVersion(version);
        this.features.values().removeIf(f -> !f.isValidDimension(dimension));

        this.featureStates = this.features.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> Boolean.TRUE));
        this.biomeStates = Biome.REGISTRY.values().stream().collect(Collectors.toMap(e -> e, e -> Boolean.TRUE));

        this.biomeSource = ThreadLocal.withInitial(() -> BiomeSource.of(dimension, version, worldSeed));
        this.layerId = this.getBiomeSource().getLayerCount() - 2;

        this.iconManager = new IconManager(this);
    }

    public <T extends Feature<?, ?>> T getFeatureOfType(Class<? extends T> feature) {
        return (T)this.features.get(feature);
    }

    public Set<Feature<?, ?>> getFeatures() {
        return new HashSet<>(this.features.values());
    }

    public Set<Biome> getBiomes() {
        return this.biomeStates.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey).collect(Collectors.toSet());
    }

    public BiomeSource getBiomeSource() {
        return this.biomeSource.get();
    }

    public BiomeLayer getBiomeLayer() {
        return this.getBiomeSource().getLayer(this.layerId);
    }

    public int getLayerId() {
        return this.layerId;
    }

    public IconManager getIconManager() {
        return this.iconManager;
    }

    public MapContext setLayerId(int layerId) {
        this.layerId = layerId;
        return this;
    }

    public boolean isActive(Feature<?, ?> feature) {
        return this.featureStates.getOrDefault(feature.getClass(), false)
                && this.features.containsKey(feature.getClass());
    }

    public boolean isActive(Class<? extends Feature<?, ?>> feature) {
        return this.featureStates.getOrDefault(feature, false)
                && this.features.containsKey(feature);
    }

    public boolean isActive(Biome biome) {
        return this.biomeStates.getOrDefault(biome, false);
    }

    @SafeVarargs
    public final MapContext hide(Class<? extends Feature<?, ?>>... features) {
        for(Class<? extends Feature<?, ?>> feature: features) {
            this.featureStates.replace(feature, false);
        }

        return this;
    }

    @SafeVarargs
    public final MapContext show(Class<? extends Feature<?, ?>>... features) {
        for(Class<? extends Feature<?, ?>> feature: features) {
            this.featureStates.replace(feature, true);
        }

        return this;
    }

    public final MapContext hide(Biome... biomes) {
        for(Biome biome: biomes) {
            this.biomeStates.replace(biome, false);
        }

        return this;
    }

    public final MapContext show(Biome... biomes) {
        for(Biome biome: biomes) {
            this.biomeStates.replace(biome, true);
        }

        return this;
    }

}

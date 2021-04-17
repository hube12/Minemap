package kaptainwutax.minemap.ui.map;

import kaptainwutax.biomeutils.layer.BiomeLayer;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.mcutils.version.UnsupportedVersion;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.terrainutils.ChunkGenerator;

import java.util.HashMap;
import java.util.Map;

public class MapContext {

    public final MCVersion version;
    public final Dimension dimension;
    public final long worldSeed;

    private final MapSettings settings;
    private final IconManager iconManager;

    private final ThreadLocal<Map<Dimension, BiomeSource>> biomeSource;
    private final ThreadLocal<Map<Dimension, ChunkGenerator>> chunkGenerators;

    private int layerId;

    public MapContext(long worldSeed, MapSettings settings) {
        this.version = settings.getVersion();
        this.dimension = settings.getDimension();
        this.worldSeed = worldSeed;
        this.settings = settings;

        this.biomeSource = ThreadLocal.withInitial(() -> {
            Map<Dimension, BiomeSource> map = new HashMap<>();
            for (Dimension dim : Dimension.values()) {
                try {
                    BiomeSource biomeSource = BiomeSource.of(dim, this.version, worldSeed);
                    map.put(dim, biomeSource);
                } catch (UnsupportedVersion e) {
                    System.out.printf("Biome source for the %s for version %s could not be initialized%n", dim.getName(), this.version.toString());
                    throw e;
                }
            }
            return map;
        });

        this.chunkGenerators = ThreadLocal.withInitial(() -> {
            Map<Dimension, ChunkGenerator> map = new HashMap<>();
            for (Dimension dim : Dimension.values()) {
                try {
                    ChunkGenerator chunkGenerator = ChunkGenerator.of(dim, this.biomeSource.get().get(dim));
                    map.put(dim, chunkGenerator);
                } catch (UnsupportedVersion e) {
                    System.err.printf("Chunk generator for the %s for version %s could not be initialized%n", dim.getName(), this.version.toString());
                    map.put(dim, null);
                }
            }
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

    public MapContext setLayerId(int layerId) {
        this.layerId = layerId;
        return this;
    }

    public ChunkGenerator getChunkGenerator() {
        return this.getChunkGenerator(this.dimension);
    }

    public ChunkGenerator getChunkGenerator(Dimension dimension) {
        return this.chunkGenerators.get().get(dimension);
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

}

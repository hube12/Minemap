package kaptainwutax.minemap.ui.map;

import kaptainwutax.biomeutils.layer.BiomeLayer;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.biomeutils.source.LayeredBiomeSource;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.RuinedPortal;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.mcutils.version.UnsupportedVersion;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.terrainutils.ChunkGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MapContext {

    public final MCVersion version;
    public final Dimension dimension;
    public final long worldSeed;

    private final MapSettings settings;
    private final IconManager iconManager;

    private final ThreadLocal<Map<Dimension, LayeredBiomeSource<BiomeLayer>>> biomeSource;
    private final ThreadLocal<Map<Dimension, ChunkGenerator>> chunkGenerators;

    private int layerId;

    public MapContext(long worldSeed, MapSettings settings) {
        this.version = settings.getVersion();
        this.dimension = settings.getDimension();
        this.worldSeed = worldSeed;
        this.settings = settings;

        this.biomeSource = ThreadLocal.withInitial(() -> {
            Map<Dimension, LayeredBiomeSource<BiomeLayer>> map = new HashMap<>();
            for (Dimension dim : Dimension.values()) {
                try {
                    @SuppressWarnings("unchecked")
                    LayeredBiomeSource<BiomeLayer> biomeSource = (LayeredBiomeSource<BiomeLayer>) BiomeSource.of(dim, this.version, worldSeed);
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

    public long getWorldSeed() {
        return worldSeed;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public MCVersion getVersion() {
        return version;
    }

    public ThreadLocal<Map<Dimension, ChunkGenerator>> getChunkGenerators() {
        return chunkGenerators;
    }

    public ChunkGenerator getChunkGenerator() {
        return this.getChunkGenerator(this.dimension);
    }

    public ChunkGenerator getChunkGenerator(Dimension dimension) {
        return this.chunkGenerators.get().get(dimension);
    }

    public Pair<ChunkGenerator, Function<CPos, CPos>> getChunkGenerator(Feature<?, ?> feature) {
        ChunkGenerator generator = null;
        Function<CPos, CPos> f = e -> e;
        if (feature instanceof RuinedPortal) {
            RuinedPortal ruinedPortal = (RuinedPortal) feature;
            Dimension dimension = ruinedPortal.getDimension();
            return new Pair<>(this.getChunkGenerator(dimension), getDimensionFunction(dimension));
        }
        if (feature.isValidDimension(this.getDimension())) {
            generator = this.getChunkGenerator();
        } else {
            for (Dimension dimension : Dimension.values()) {
                if (feature.isValidDimension(dimension)) {
                    generator = this.getChunkGenerator(dimension);
                    f = getDimensionFunction(dimension);
                    break;
                }
            }
        }
        return new Pair<>(generator, f);
    }

    public Function<CPos, CPos> getDimensionFunction(Dimension dimension) {
        Function<CPos, CPos> f = e -> e;
        if (dimension == Dimension.NETHER && this.getDimension() == Dimension.OVERWORLD) {
            f = e -> e.shr(3);
        } else if (dimension == Dimension.OVERWORLD && this.getDimension() == Dimension.NETHER) {
            f = e -> e.shl(3);
        }
        return f;
    }

    public LayeredBiomeSource<BiomeLayer> getBiomeSource() {
        return this.getBiomeSource(this.dimension);
    }

    public LayeredBiomeSource<BiomeLayer> getBiomeSource(Dimension dimension) {
        return this.biomeSource.get().get(dimension);
    }

    public BiomeLayer getBiomeLayer() {
        return this.getBiomeSource().getLayer(this.layerId);
    }

}

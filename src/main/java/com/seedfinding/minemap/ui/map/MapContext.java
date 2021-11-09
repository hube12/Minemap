package com.seedfinding.minemap.ui.map;

import com.seedfinding.mcbiome.layer.BiomeLayer;
import com.seedfinding.mcbiome.source.BiomeSource;
import com.seedfinding.mcbiome.source.LayeredBiomeSource;
import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mccore.version.UnsupportedVersion;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.structure.RuinedPortal;
import com.seedfinding.mcfeature.structure.Stronghold;
import com.seedfinding.mcseed.rand.JRand;
import com.seedfinding.mcterrain.TerrainGenerator;
import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.feature.NEStronghold;
import com.seedfinding.minemap.init.Configs;
import com.seedfinding.minemap.init.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

public class MapContext {

    public final MCVersion version;
    public final Dimension dimension;
    public final long worldSeed;

    private final MapSettings settings;
    private final IconManager iconManager;

    private final ThreadLocal<Map<Dimension, LayeredBiomeSource<? extends BiomeLayer>>> biomeSource;
    private final ThreadLocal<Map<Dimension, TerrainGenerator>> chunkGenerators;
    private CPos[] starts = null;

    private int layerId;

    public MapContext(long worldSeed, MapSettings settings) {
        this.version = settings.getVersion();
        this.dimension = settings.getDimension();
        this.worldSeed = worldSeed;
        this.settings = settings;

        this.biomeSource = ThreadLocal.withInitial(() -> {
            Map<Dimension, LayeredBiomeSource<?>> map = new HashMap<>();
            for (Dimension dim : Dimension.values()) {
                try {

                    LayeredBiomeSource<? extends BiomeLayer> biomeSource;

                    if (dim == Dimension.OVERWORLD) {
                        biomeSource = new OverworldBiomeSource(this.version, this.worldSeed, settings.getBiomeSize(), settings.getRiverSize());
                    } else {
                        biomeSource = (LayeredBiomeSource<? extends BiomeLayer>) BiomeSource.of(dim, this.version, worldSeed);
                    }

                    map.put(dim, biomeSource);
                } catch (UnsupportedVersion e) {
                    System.out.printf("Biome source for the %s for version %s could not be initialized%n", dim.getName(), this.version.toString());
                    throw e;
                }
            }
            return map;
        });

        this.chunkGenerators = ThreadLocal.withInitial(() -> {
            Map<Dimension, TerrainGenerator> map = new HashMap<>();
            for (Dimension dim : Dimension.values()) {
                try {
                    TerrainGenerator chunkGenerator = TerrainGenerator.of(dim, this.biomeSource.get().get(dim));
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

    public CPos[] getStarts() {
        return this.starts;
    }

    public void calculateStarts(MapPanel mapPanel) {
        Stronghold stronghold = this.getSettings().getFeatureOfType(this.dimension == Dimension.OVERWORLD ? Stronghold.class : NEStronghold.class);

        if (stronghold != null && !Configs.USER_PROFILE.getUserSettings().disableStronghold) {
            BiomeSource biomeSource = this.getBiomeSource(Dimension.OVERWORLD);
            if (biomeSource != null) {
                if (this.dimension == Dimension.OVERWORLD || this.dimension == Dimension.NETHER) {
                    synchronized (MineMap.version) {
                        Thread t = new Thread(
                            () -> {
                                this.starts = stronghold.getStarts(biomeSource, 128, new JRand(0L));
                                if (Configs.USER_PROFILE.getUserSettings().allowFlashing) mapPanel.restart();
                            }
                        );
                        t.start();
                        if (!Configs.USER_PROFILE.getUserSettings().allowFlashing) {
                            try {
                                t.join();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                                Logger.LOGGER.severe("thread didn't work " + e);
                            }
                        }
                    }
                }
            }
        }
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

    public ThreadLocal<Map<Dimension, TerrainGenerator>> getTerrainGenerators() {
        return chunkGenerators;
    }

    public TerrainGenerator getTerrainGenerator() {
        return this.getTerrainGenerator(this.dimension);
    }

    public TerrainGenerator getTerrainGenerator(Dimension dimension) {
        return this.chunkGenerators.get().get(dimension);
    }

    public Pair<TerrainGenerator, Function<CPos, CPos>> getTerrainGenerator(Feature<?, ?> feature) {
        TerrainGenerator generator = null;
        Function<CPos, CPos> f = e -> e;
        if (feature instanceof RuinedPortal) {
            RuinedPortal ruinedPortal = (RuinedPortal) feature;
            Dimension dimension = ruinedPortal.getValidDimension();
            return new Pair<>(this.getTerrainGenerator(dimension), getDimensionFunction(dimension));
        }
        if (feature.isValidDimension(this.getDimension())) {
            generator = this.getTerrainGenerator();
        } else {
            for (Dimension dimension : Dimension.values()) {
                if (feature.isValidDimension(dimension)) {
                    generator = this.getTerrainGenerator(dimension);
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

    public LayeredBiomeSource<? extends BiomeLayer> getBiomeSource() {
        return this.getBiomeSource(this.dimension);
    }

    public LayeredBiomeSource<? extends BiomeLayer> getBiomeSource(Dimension dimension) {
        return this.biomeSource.get().get(dimension);
    }

    @SuppressWarnings("unchecked")
    public <T extends BiomeLayer> T getBiomeLayer() {
        return (T) this.getBiomeSource().getLayer(this.layerId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MapContext)) return false;
        MapContext that = (MapContext) o;
        return worldSeed == that.worldSeed && layerId == that.layerId && version == that.version && dimension == that.dimension && settings.equals(that.settings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, dimension, worldSeed, settings, layerId);
    }
}

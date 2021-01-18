package kaptainwutax.minemap.feature;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.pos.BPos;

public class SpawnPoint extends Feature<Feature.Config, SpawnPoint.Data> {

    public SpawnPoint() {
        super(new Config(), null);
    }

    @Override
    public String getName() {
        return "spawn";
    }

    @Override
    public boolean canStart(SpawnPoint.Data data, long structureSeed, ChunkRand rand) {
        throw new UnsupportedOperationException("Spawn depends on biomes!");
    }

    @Override
    public boolean canSpawn(SpawnPoint.Data data, BiomeSource source) {
        if(source instanceof OverworldBiomeSource) {
            BPos spawn = ((OverworldBiomeSource)source).getSpawnPoint();
            return data.blockX == spawn.getX() && data.blockZ == spawn.getZ();
        }

        return false;
    }

    @Override
    public boolean isValidDimension(Dimension dimension) {
        return dimension == Dimension.OVERWORLD;
    }

    public BPos get(BiomeSource source) {
        return source instanceof OverworldBiomeSource ? ((OverworldBiomeSource)source).getSpawnPoint() : null;
    }

    public static class Data extends Feature.Data<SpawnPoint> {
        public final int blockX;
        public final int blockZ;

        public Data(SpawnPoint feature, int blockX, int blockZ) {
            super(feature, blockX >> 4, blockZ >> 4);
            this.blockX = blockX;
            this.blockZ = blockZ;
        }
    }

}

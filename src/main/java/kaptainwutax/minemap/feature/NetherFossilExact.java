package kaptainwutax.minemap.feature;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.featureutils.structure.NetherFossil;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.MCVersion;

public class NetherFossilExact extends NetherFossil {
    public NetherFossilExact(MCVersion version) {
        super(version);
    }

    public NetherFossilExact(Config config, MCVersion version) {
        super(config, version);
    }

    @Override
    public boolean isValidBiome(Biome biome) {
        boolean b=super.isValidBiome(biome);
        if (b){

        }
        return b;
    }

    @Override
    public boolean isValidDimension(Dimension dimension) {
        return dimension == Dimension.OVERWORLD;
    }
}

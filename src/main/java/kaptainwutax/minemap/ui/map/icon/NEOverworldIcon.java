package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.fragment.Fragment;

import java.util.List;
import java.util.function.Function;

public class NEOverworldIcon extends RegionIcon {

    public NEOverworldIcon(MapContext context) {
        super(context);
    }

    @Override
    public boolean isValidFeature(Feature<?, ?> feature) {
        return false;
    }

    @Override
    public Dimension getDimension() {
        return Dimension.OVERWORLD;
    }

    @Override
    public Function<BPos, BPos> blockPosTranslation() {
        return e->e.shr(3);
    }

    @Override
    public Function<Integer, Integer> integerTranslation() {
        return e->e<<3;
    }
}

package com.seedfinding.minemap.ui.map.icon;

import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.minemap.ui.map.MapContext;

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
        return e -> e.shr(3);
    }

    @Override
    public Function<Integer, Integer> integerTranslation() {
        return e -> e << 3;
    }
}

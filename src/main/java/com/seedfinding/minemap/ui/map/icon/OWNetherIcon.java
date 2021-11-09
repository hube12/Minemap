package com.seedfinding.minemap.ui.map.icon;

import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.minemap.feature.OWBastionRemnant;
import com.seedfinding.minemap.feature.OWFortress;
import com.seedfinding.minemap.feature.OWNERuinedPortal;
import com.seedfinding.minemap.ui.map.MapContext;

import java.util.function.Function;

public class OWNetherIcon extends RegionIcon {

    public OWNetherIcon(MapContext context) {
        super(context);
    }

    @Override
    public boolean isValidFeature(Feature<?, ?> feature) {
        return feature instanceof OWBastionRemnant || feature instanceof OWFortress || feature instanceof OWNERuinedPortal;
    }

    @Override
    public Dimension getDimension() {
        return Dimension.NETHER;
    }

    @Override
    public Function<Integer, Integer> integerTranslation() {
        return e -> e >> 3;
    }

    @Override
    public Function<BPos, BPos> blockPosTranslation() {
        return e -> e.shl(3);
    }
}

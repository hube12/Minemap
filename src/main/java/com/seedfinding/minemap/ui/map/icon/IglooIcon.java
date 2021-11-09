package com.seedfinding.minemap.ui.map.icon;

import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.structure.Igloo;
import com.seedfinding.minemap.init.Icons;
import com.seedfinding.minemap.ui.map.MapContext;
import com.seedfinding.minemap.util.ui.special_icons.IglooLabIcon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Function;

public class IglooIcon extends RegionIcon {

    public IglooIcon(MapContext context) {
        super(context);
    }

    @Override
    public boolean isValidFeature(Feature<?, ?> feature) {
        return feature instanceof Igloo;
    }

    @Override
    public Function<BPos, Pair<Color, BufferedImage>> getExtraIcon() {
        return bPos -> {
            Igloo igloo = new Igloo(this.getContext().getVersion());
            return igloo.hasBasement(this.getContext().worldSeed, bPos.toChunkPos(), new ChunkRand()) ?
                new Pair<>(new Color(0x338ED5), Icons.get(IglooLabIcon.class)) : null;
        };
    }
}

package com.seedfinding.minemap.ui.map.icon;

import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.structure.Village;
import com.seedfinding.minemap.init.Icons;
import com.seedfinding.minemap.ui.map.MapContext;
import com.seedfinding.minemap.util.ui.special_icons.ZombieVillageIcon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Function;

public class VillageIcon extends RegionIcon {

    public VillageIcon(MapContext context) {
        super(context);
    }

    @Override
    public boolean isValidFeature(Feature<?, ?> feature) {
        return feature instanceof Village;
    }

    @Override
    public Function<BPos, Pair<Color, BufferedImage>> getExtraIcon() {
        return bPos -> {
            Village village = new Village(this.getContext().getVersion());
            village.canSpawn(bPos.toChunkPos(), this.getContext().getBiomeSource());
            return village.isZombieVillage(this.getContext().worldSeed, bPos.toChunkPos(), new ChunkRand()) ?
                new Pair<>(new Color(0x2A5324), Icons.get(ZombieVillageIcon.class)) : null;
        };
    }
}

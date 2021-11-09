package com.seedfinding.minemap.ui.map.icon;

import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.structure.EndCity;
import com.seedfinding.mcfeature.structure.generator.structure.EndCityGenerator;
import com.seedfinding.minemap.init.Icons;
import com.seedfinding.minemap.ui.map.MapContext;
import com.seedfinding.minemap.util.ui.special_icons.EndShipIcon;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Function;

public class EndCityIcon extends RegionIcon {

    public EndCityIcon(MapContext context) {
        super(context);
    }

    @Override
    public boolean isValidFeature(Feature<?, ?> feature) {
        return feature instanceof EndCity;
    }

    @Override
    public Function<BPos, Pair<Color, BufferedImage>> getExtraIcon() {
        return bPos -> {
            EndCityGenerator endCityGenerator = new EndCityGenerator(this.getContext().getVersion());
            if (!endCityGenerator.generate(this.getContext().getTerrainGenerator(), bPos.toChunkPos())) return null;
            return endCityGenerator.hasShip() ? new Pair<>(Color.BLACK, Icons.get(EndShipIcon.class)) : null;
        };
    }
}

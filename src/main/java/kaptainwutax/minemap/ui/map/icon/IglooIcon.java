package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.Igloo;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.util.ui.special_icons.IglooLabIcon;

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
    public Function<BPos, Pair<Color,BufferedImage>> getExtraIcon() {
        return bPos -> {
            Igloo igloo = new Igloo(this.getContext().getVersion());
            return igloo.hasBasement(this.getContext().worldSeed, bPos.toChunkPos(), new ChunkRand()) ?
                new Pair<>(new Color(0x338ED5),Icons.get(IglooLabIcon.class)) : null;
        };
    }
}

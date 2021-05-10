package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.EndCity;
import kaptainwutax.featureutils.structure.generator.structure.EndCityGenerator;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.util.ui.special_icons.EndShipIcon;

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
    public Function<BPos, Pair<Color,BufferedImage>> getExtraIcon() {
        return bPos -> {
            EndCityGenerator endCityGenerator = new EndCityGenerator(this.getContext().getVersion());
            if (!endCityGenerator.generate(this.getContext().getChunkGenerator(), bPos.toChunkPos())) return null;
            return endCityGenerator.hasShip() ? new Pair<>(Color.BLACK,Icons.get(EndShipIcon.class)) : null;
        };
    }
}

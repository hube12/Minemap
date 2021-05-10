package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.Igloo;
import kaptainwutax.featureutils.structure.Village;
import kaptainwutax.featureutils.structure.generator.EndCityGenerator;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.util.ui.feature_icons.EndShipIcon;

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
    public Function<BPos, BufferedImage> getExtraIcon() {
        return bPos -> {
            MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            if (map == null) return null;
            EndCityGenerator endCityGenerator = new EndCityGenerator(map.context.getVersion());
            if (!endCityGenerator.generate(map.context.getChunkGenerator(), bPos.toChunkPos())) return null;
            return endCityGenerator.hasShip() ? Icons.get(EndShipIcon.class) : null;
        };
    }
}

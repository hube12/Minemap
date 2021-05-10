package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.Village;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.util.ui.special_icons.ZombieVillageIcon;

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
    public Function<BPos, BufferedImage> getExtraIcon() {
        return bPos -> {
            Village village = new Village(this.getContext().getVersion());
            return village.isZombieVillage(this.getContext().worldSeed, bPos.toChunkPos(), new ChunkRand()) ?
                Icons.get(ZombieVillageIcon.class) : null;
        };
    }
}

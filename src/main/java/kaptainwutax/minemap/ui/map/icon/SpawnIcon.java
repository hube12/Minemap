package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.feature.SpawnPoint;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.mcutils.util.pos.BPos;

import java.util.List;

public class SpawnIcon extends StaticIcon {

    private final BPos pos;

    public SpawnIcon(MapContext context) {
        super(context);
        BiomeSource source = this.getContext().getBiomeSource();
        this.pos = source instanceof OverworldBiomeSource ? ((OverworldBiomeSource) source).getSpawnPoint() : null;
    }

    public BPos getPos() {
        return this.pos;
    }

    @Override
    public boolean isValidFeature(Feature<?, ?> feature) {
        return feature instanceof SpawnPoint;
    }

    @Override
    public void addPositions(Feature<?, ?> feature, Fragment fragment, List<BPos> positions) {
        if (this.getPos() != null) {
            positions.add(this.getPos());
        }
    }

}

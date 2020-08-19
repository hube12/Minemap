package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.feature.SpawnPoint;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.seedutils.mc.pos.BPos;

import java.util.List;

public class SpawnIcon extends StaticIcon {

    public SpawnIcon(MapContext context) {
        super(context);
    }

    @Override
    public boolean isValidFeature(Feature<?, ?> feature) {
        return feature instanceof SpawnPoint;
    }

    @Override
    public void addPositions(Feature<?, ?> feature, Fragment fragment, List<BPos> positions) {
        positions.add(((SpawnPoint)feature).get(this.getContext().getBiomeSource()));
    }

}

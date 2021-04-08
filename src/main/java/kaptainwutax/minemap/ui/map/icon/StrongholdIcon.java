package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.Stronghold;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.seedutils.lcg.rand.JRand;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.CPos;

import java.util.List;

public class StrongholdIcon extends StaticIcon {

    private CPos[] starts;

    public StrongholdIcon(MapContext context, int count) {
        super(context);
        Stronghold stronghold = context.getSettings().getFeatureOfType(Stronghold.class);

        if(stronghold != null) {
            starts = stronghold.getStarts(this.getContext().getBiomeSource(), count, new JRand(0L));
        }
    }

    @Override
    public boolean isValidFeature(Feature<?, ?> feature) {
        return feature instanceof Stronghold;
    }

    @Override
    public void addPositions(Feature<?, ?> feature, Fragment fragment, List<BPos> positions) {
        if(this.starts == null)return;

        for(CPos start: this.starts) {
            positions.add(start.toBlockPos().add(8, 0, 8)); // TODO check for old version 1.15+ ok
        }
    }

}

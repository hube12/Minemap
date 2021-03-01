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

    public StrongholdIcon(MapContext context) {
        super(context);
        Stronghold stronghold = context.getSettings().getFeatureOfType(Stronghold.class);

        if(stronghold != null) {
            this.starts = stronghold.getStarts(this.getContext().getBiomeSource(), stronghold.getCount(), new JRand(0L));
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
            positions.add(start.toBlockPos().add(9, 0, 9));
        }
    }

}

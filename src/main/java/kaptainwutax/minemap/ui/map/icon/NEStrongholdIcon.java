package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.Stronghold;
import kaptainwutax.minemap.feature.NEStronghold;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.seedutils.lcg.rand.JRand;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.CPos;

import java.util.List;

public class NEStrongholdIcon extends StaticIcon {

    private CPos[] starts;

    public NEStrongholdIcon(MapContext context, int count) {
        super(context);
        NEStronghold stronghold = context.getSettings().getFeatureOfType(NEStronghold.class);

        if(stronghold != null) {
            starts = stronghold.getStarts(this.getContext().getBiomeSource(Dimension.OVERWORLD), count, new JRand(0L));
        }
    }

    @Override
    public boolean isValidFeature(Feature<?, ?> feature) {
        return feature instanceof NEStronghold;
    }

    @Override
    public void addPositions(Feature<?, ?> feature, Fragment fragment, List<BPos> positions) {
        if(this.starts == null)return;
        for(CPos start: this.starts) {
            positions.add(new BPos(start.toBlockPos().getX()>>3,start.toBlockPos().getY(),start.toBlockPos().getZ()>>3));
        }
    }

}

package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.Stronghold;
import kaptainwutax.minemap.feature.NEStronghold;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.seedutils.rand.JRand;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.stream.IntStream;

public class StrongholdIcon extends StaticIcon {

    protected CPos[] starts;

    public StrongholdIcon(MapContext context, int count) {
        super(context);
        Stronghold stronghold = context.getSettings().getFeatureOfType(this.getContext().dimension == Dimension.OVERWORLD ? Stronghold.class : NEStronghold.class);

        if (stronghold != null) {
            BiomeSource biomeSource = this.getContext().getBiomeSource(Dimension.OVERWORLD);
            if (biomeSource != null) {
                if (this.getContext().dimension == Dimension.OVERWORLD || this.getContext().dimension == Dimension.NETHER) {
                    starts = stronghold.getStarts(biomeSource, count, new JRand(0L));
                }
            }
        }
    }

    public CPos[] getStarts() {
        return starts;
    }

    @Override
    public Function<Object, String> getExtraInfo() {
        return (input) -> {
            BPos bPos = (BPos) input;
            CPos cPos = new CPos(bPos.getX() >> (this.getContext().dimension == Dimension.OVERWORLD ? 4 : 1), bPos.getZ() >> (this.getContext().dimension == Dimension.OVERWORLD ? 4 : 1));
            ;
            CPos[] starts = this.getStarts();
            OptionalInt integer = IntStream.range(0, starts.length).filter(idx -> starts[idx].equals(cPos)).findFirst();
            return integer.isPresent() ? String.valueOf(integer.getAsInt()) : null;
        };
    }

    @Override
    public boolean isValidFeature(Feature<?, ?> feature) {
        return feature instanceof Stronghold;
    }

    @Override
    public void addPositions(Feature<?, ?> feature, Fragment fragment, List<BPos> positions) {
        if (this.starts == null) return;

        for (CPos start : this.starts) {
            BPos bPos = this.getContext().dimension == Dimension.OVERWORLD ?
                    start.toBlockPos().add(8, 0, 8) :  // TODO check for old version 1.15+ ok
                    new BPos(start.toBlockPos().getX() >> 3, start.toBlockPos().getY(), start.toBlockPos().getZ() >> 3);
            positions.add(bPos);
        }
    }

}

package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.Stronghold;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.feature.NEStronghold;
import kaptainwutax.minemap.init.Logger;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.seedutils.rand.JRand;

import java.util.List;
import java.util.OptionalInt;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.IntStream;

public class StrongholdIcon extends StaticIcon {


    public StrongholdIcon(MapContext context) {
        super(context);
    }

    public CPos[] getStarts() {
        return this.getContext().getStarts();
    }


    @Override
    public Function<BPos, String> getExtraInfo() {
        return (input) -> {
            CPos cPos = new CPos(
                input.getX() >> (this.getContext().dimension == Dimension.OVERWORLD ? 4 : 1),
                input.getZ() >> (this.getContext().dimension == Dimension.OVERWORLD ? 4 : 1)
            );
            CPos[] starts = this.getStarts();
            if (starts == null) return null;
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
        if (this.getStarts() == null) return;

        for (CPos start : this.getStarts()) {
            BPos bPos = this.getContext().dimension == Dimension.OVERWORLD ?
                start.toBlockPos().add(8, 0, 8) :  // TODO check for old version 1.15+ ok
                new BPos(start.toBlockPos().getX() >> 3, start.toBlockPos().getY(), start.toBlockPos().getZ() >> 3);
            positions.add(bPos);
        }
    }

}

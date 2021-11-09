package com.seedfinding.minemap.ui.map.icon;

import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.structure.Stronghold;
import com.seedfinding.minemap.ui.map.MapContext;
import com.seedfinding.minemap.ui.map.fragment.Fragment;

import java.util.List;
import java.util.OptionalInt;
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

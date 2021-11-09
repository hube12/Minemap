package com.seedfinding.minemap.ui.map.icon;

import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.decorator.EndGateway;
import com.seedfinding.minemap.ui.map.MapContext;
import com.seedfinding.minemap.ui.map.fragment.Fragment;

import java.util.List;

public class EndGatewayIcon extends StaticIcon {

    public EndGatewayIcon(MapContext context) {
        super(context);
    }

    @Override
    public boolean isValidFeature(Feature<?, ?> feature) {
        return feature instanceof EndGateway;
    }

    @Override
    public void addPositions(Feature<?, ?> feature, Fragment fragment, List<BPos> positions) {
        EndGateway gateway = (EndGateway) feature;
        ChunkRand rand = new ChunkRand();

        for (int x = fragment.getX(); x < fragment.getX() + fragment.getSize(); x += 16) {
            for (int z = fragment.getZ(); z < fragment.getZ() + fragment.getSize(); z += 16) {
                EndGateway.Data data = gateway.getData(this.getContext().worldSeed, x >> 4, z >> 4, rand);

                if (data != null && gateway.canSpawn(x >> 4, z >> 4, this.getContext().getBiomeSource())) {
                    positions.add(new BPos(data.blockX, 0, data.blockZ));
                }
            }
        }
    }

}

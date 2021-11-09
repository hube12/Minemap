package com.seedfinding.minemap.ui.map.icon;

import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.misc.SpawnPoint;
import com.seedfinding.mcterrain.TerrainGenerator;
import com.seedfinding.mcterrain.terrain.OverworldTerrainGenerator;
import com.seedfinding.minemap.ui.map.MapContext;
import com.seedfinding.minemap.ui.map.fragment.Fragment;

import java.util.List;

public class SpawnIcon extends StaticIcon {

    private final BPos pos;

    public SpawnIcon(MapContext context) {
        super(context);
        TerrainGenerator generator = this.getContext().getTerrainGenerator();
        this.pos = generator instanceof OverworldTerrainGenerator ? SpawnPoint.getSpawn((OverworldTerrainGenerator) generator) : null;
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

package com.seedfinding.minemap.feature.chests.loot;

import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.structure.Shipwreck;
import com.seedfinding.minemap.feature.chests.Loot;

public class ShipwreckLoot extends Loot {


    @Override
    public boolean isCorrectInstance(Feature<?, ?> feature) {
        return feature instanceof Shipwreck;
    }
}

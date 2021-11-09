package com.seedfinding.minemap.feature.chests.loot;

import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.structure.BuriedTreasure;
import com.seedfinding.minemap.feature.chests.Loot;

public class BuriedTreasureloot extends Loot {

    @Override
    public boolean isCorrectInstance(Feature<?, ?> feature) {
        return feature instanceof BuriedTreasure;
    }
}

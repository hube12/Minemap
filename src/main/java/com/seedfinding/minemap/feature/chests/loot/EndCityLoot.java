package com.seedfinding.minemap.feature.chests.loot;

import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.structure.EndCity;
import com.seedfinding.minemap.feature.chests.Loot;

public class EndCityLoot extends Loot {

    @Override
    public boolean isCorrectInstance(Feature<?, ?> feature) {
        return feature instanceof EndCity;
    }
}

package kaptainwutax.minemap.feature.chests.loot;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.EndCity;
import kaptainwutax.minemap.feature.chests.Loot;

public class EndCityLoot extends Loot {

    @Override
    public boolean isCorrectInstance(Feature<?, ?> feature) {
        return feature instanceof EndCity;
    }
}

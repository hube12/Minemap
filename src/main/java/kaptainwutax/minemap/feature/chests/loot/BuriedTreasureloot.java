package kaptainwutax.minemap.feature.chests.loot;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.BuriedTreasure;
import kaptainwutax.minemap.feature.chests.Loot;

public class BuriedTreasureloot extends Loot {

    @Override
    public boolean isCorrectInstance(Feature<?, ?> feature) {
        return feature instanceof BuriedTreasure;
    }
}

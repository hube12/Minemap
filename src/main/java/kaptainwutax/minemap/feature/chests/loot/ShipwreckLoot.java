package kaptainwutax.minemap.feature.chests.loot;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.Shipwreck;
import kaptainwutax.minemap.feature.chests.Loot;

public class ShipwreckLoot extends Loot {


    @Override
    public boolean isCorrectInstance(Feature<?, ?> feature) {
        return feature instanceof Shipwreck;
    }
}

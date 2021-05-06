package kaptainwutax.minemap.feature.chests.loot;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.DesertPyramid;
import kaptainwutax.minemap.feature.chests.Loot;

public class DesertPyramidLoot extends Loot {

    @Override
    public boolean isCorrectInstance(Feature<?, ?> feature) {
        return feature instanceof DesertPyramid;
    }
}

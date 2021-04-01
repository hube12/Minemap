package kaptainwutax.minemap.feature.chests;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.BuriedTreasure;
import kaptainwutax.featureutils.structure.DesertPyramid;
import kaptainwutax.minemap.feature.chests.loot.DesertPyramidLoot;

import java.util.HashMap;
import java.util.Map;

public class Chests {
    public static final Map<Class<? extends Feature<?, ?>>, Loot.LootFactory<?>> REGISTRY = new HashMap<>();

    public static void registerChests() {
        register(DesertPyramid.class, DesertPyramidLoot::new);
        register(BuriedTreasure.class, DesertPyramidLoot::new);
    }

    public static <T extends Feature<?, ?>> void register(Class<T> clazz, Loot.LootFactory<?> lootFactory) {
        REGISTRY.put(clazz, lootFactory);
    }

}

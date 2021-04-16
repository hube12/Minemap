package kaptainwutax.minemap.feature.chests;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.BuriedTreasure;
import kaptainwutax.featureutils.structure.DesertPyramid;
import kaptainwutax.featureutils.structure.Shipwreck;
import kaptainwutax.minemap.feature.NERuinedPortal;
import kaptainwutax.minemap.feature.OWRuinedPortal;
import kaptainwutax.minemap.feature.chests.loot.BurriedTreasureLoot;
import kaptainwutax.minemap.feature.chests.loot.DesertPyramidLoot;
import kaptainwutax.minemap.feature.chests.loot.RuinedPortalLoot;
import kaptainwutax.minemap.feature.chests.loot.ShipwreckLoot;

import java.util.HashMap;
import java.util.Map;

public class Chests {
    private static final Map<Class<? extends Feature<?, ?>>, Loot.LootFactory<?>> REGISTRY = new HashMap<>();

    public static void registerChests() {
        register(DesertPyramid.class, DesertPyramidLoot::new);
        register(BuriedTreasure.class, BurriedTreasureLoot::new);
        register(NERuinedPortal.class, RuinedPortalLoot::new);
        register(OWRuinedPortal.class, RuinedPortalLoot::new);
        register(Shipwreck.class, ShipwreckLoot::new);
    }

    public static <T extends Feature<?, ?>> void register(Class<T> clazz, Loot.LootFactory<?> lootFactory) {
        REGISTRY.put(clazz, lootFactory);
    }

    public static <T extends Feature<?, ?>> Loot.LootFactory<?> get(Class<T> clazz) {
        return REGISTRY.get(clazz);
    }
}

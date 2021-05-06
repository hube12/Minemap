package kaptainwutax.minemap.feature.chests;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.BuriedTreasure;
import kaptainwutax.featureutils.structure.DesertPyramid;
import kaptainwutax.featureutils.structure.EndCity;
import kaptainwutax.featureutils.structure.Shipwreck;
import kaptainwutax.minemap.feature.NERuinedPortal;
import kaptainwutax.minemap.feature.OWNERuinedPortal;
import kaptainwutax.minemap.feature.OWRuinedPortal;
import kaptainwutax.minemap.feature.chests.loot.*;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class Chests {
    private static final LinkedHashMap<Class<? extends Feature<?, ?>>, Loot.LootFactory<?>> REGISTRY = new LinkedHashMap<>();
    // map the registry keys to their super present in FeatureUtils
    private static final LinkedHashMap<Class<? extends Feature<?, ?>>, Class<? extends Feature<?, ?>>> SUPER_REGISTRY = new LinkedHashMap<>();

    public static void registerChests() {
        register(DesertPyramid.class, DesertPyramidLoot::new,true);
        register(BuriedTreasure.class, BuriedTreasureloot::new,true);
        register(NERuinedPortal.class, RuinedPortalLoot::new,false);
        register(OWNERuinedPortal.class, RuinedPortalLoot::new,false);
        register(OWRuinedPortal.class, RuinedPortalLoot::new,false);
        register(Shipwreck.class, ShipwreckLoot::new,true);
        register(EndCity.class, EndCityLoot::new,true);
    }

    @SuppressWarnings("unchecked")
    public static <T extends Feature<?, ?>> void register(Class<T> clazz, Loot.LootFactory<?> lootFactory, boolean isSuper) {
        REGISTRY.put(clazz, lootFactory);
        SUPER_REGISTRY.put(clazz, isSuper?clazz: (Class<? extends Feature<?, ?>>) clazz.getSuperclass());
    }

    public static <T extends Feature<?, ?>> Loot.LootFactory<?> get(Class<T> clazz) {
        return REGISTRY.get(clazz);
    }

    public static LinkedHashMap<Class<? extends Feature<?, ?>>, Loot.LootFactory<?>> getRegistry() {
        return REGISTRY;
    }

    public static LinkedHashMap<Class<? extends Feature<?, ?>>, Class<? extends Feature<?, ?>>> getSuperRegistry() {
        return SUPER_REGISTRY;
    }
}

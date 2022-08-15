package com.seedfinding.minemap.init;

import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.decorator.EndGateway;
import com.seedfinding.mcfeature.misc.SlimeChunk;
import com.seedfinding.mcfeature.misc.SpawnPoint;
import com.seedfinding.mcfeature.structure.*;
import com.seedfinding.minemap.feature.*;
import com.seedfinding.minemap.ui.map.fragment.FeatureFactory;

import java.util.HashMap;
import java.util.Map;

public class Features {

    public static final Map<Class<? extends Feature<?, ?>>, FeatureFactory<?>> REGISTRY = new HashMap<>();

    public static void registerFeatures() {
        register(BastionRemnant.class, BastionRemnant::new);
        register(BuriedTreasure.class, BuriedTreasure::new);
        register(DesertPyramid.class, DesertPyramid::new);
        register(EndCity.class, EndCity::new);
        register(Fortress.class, Fortress::new);
        register(Igloo.class, Igloo::new);
        register(JunglePyramid.class, JunglePyramid::new);
        register(Mansion.class, Mansion::new);
        register(Mineshaft.class, Mineshaft::new);
        register(Monument.class, Monument::new);
        register(NetherFossil.class, NetherFossil::new);
        register(OceanRuin.class, OceanRuin::new);
        register(PillagerOutpost.class, PillagerOutpost::new);
        register(Shipwreck.class, Shipwreck::new);
        register(SwampHut.class, SwampHut::new);
        register(Village.class, Village::new);
        register(Stronghold.class, Stronghold::new);

        register(OWRuinedPortal.class, OWRuinedPortal::new);
        register(NERuinedPortal.class, NERuinedPortal::new);
        register(OWNERuinedPortal.class, OWNERuinedPortal::new);

        register(OWBastionRemnant.class, OWBastionRemnant::new);
        register(OWFortress.class, OWFortress::new);

        register(NEStronghold.class, NEStronghold::new);

        register(EndGateway.class, EndGateway::new);
        register(SlimeChunk.class, SlimeChunk::new);

        register(SpawnPoint.class, v -> new SpawnPoint());
    }

    public static <T extends Feature<?, ?>> void register(Class<T> clazz, FeatureFactory<T> factory) {
        REGISTRY.put(clazz, factory);
    }

    public static Map<Class<? extends Feature<?, ?>>, Feature<?, ?>> getForVersion(MCVersion version) {
        Map<Class<? extends Feature<?, ?>>, Feature<?, ?>> result = new HashMap<>();

        for (Map.Entry<Class<? extends Feature<?, ?>>, FeatureFactory<?>> entry : REGISTRY.entrySet()) {
            try {
                Feature<?, ?> feature = entry.getValue().create(version);
                if (feature.getConfig() != null) {
                    result.put(entry.getKey(), feature);
                } else {
                    Logger.LOGGER.severe(feature.getName() + " was ignored due to no Config available.");
                }
            } catch (NullPointerException ignored) {
                Logger.LOGGER.severe(ignored.toString());
            }
        }

        return result;
    }
}

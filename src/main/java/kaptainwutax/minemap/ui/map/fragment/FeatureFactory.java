package kaptainwutax.minemap.ui.map.fragment;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.seedutils.mc.MCVersion;

@FunctionalInterface
public interface FeatureFactory<T extends Feature<?, ?>> {

    T create(MCVersion version);

}

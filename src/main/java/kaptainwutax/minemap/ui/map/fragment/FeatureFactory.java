package kaptainwutax.minemap.ui.map.fragment;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.mcutils.version.MCVersion;

@FunctionalInterface
public interface FeatureFactory<T extends Feature<?, ?>> {

    T create(MCVersion version);

}

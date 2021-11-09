package com.seedfinding.minemap.ui.map.fragment;

import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.Feature;

@FunctionalInterface
public interface FeatureFactory<T extends Feature<?, ?>> {

    T create(MCVersion version);

}

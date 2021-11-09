package com.seedfinding.minemap.ui.map.fragment;

import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mccore.version.MCVersion;

@FunctionalInterface
public interface FeatureFactory<T extends Feature<?, ?>> {

    T create(MCVersion version);

}

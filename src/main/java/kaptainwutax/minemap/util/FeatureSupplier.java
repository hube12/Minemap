package kaptainwutax.minemap.util;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.Structure;
import kaptainwutax.seedutils.mc.MCVersion;

@FunctionalInterface
public interface FeatureSupplier {

	Feature<?, ?> create(MCVersion version);

}

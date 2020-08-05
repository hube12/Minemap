package kaptainwutax.minemap.util;

import kaptainwutax.featureutils.structure.Structure;
import kaptainwutax.seedutils.mc.MCVersion;

@FunctionalInterface
public interface FeatureSupplier {

	Structure<?, ?> create(MCVersion version);

}

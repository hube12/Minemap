package kaptainwutax.minemap.feature;

import kaptainwutax.featureutils.structure.Stronghold;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.MCVersion;

public class NEStronghold extends Stronghold {

	public NEStronghold(MCVersion version) {
		super(version);
	}

	public NEStronghold(Config config, MCVersion version) {
		super(config, version);
	}

	@Override
	public String getName() {
		return name();
	}

	public static String name() {
		return "NE_Stronghold";
	}

	@Override
	public boolean isValidDimension(Dimension dimension) {
		return dimension == Dimension.NETHER;
	}

}

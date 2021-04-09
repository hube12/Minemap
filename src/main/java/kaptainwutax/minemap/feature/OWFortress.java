package kaptainwutax.minemap.feature;

import kaptainwutax.featureutils.structure.Fortress;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.MCVersion;

public class OWFortress extends Fortress {

	public OWFortress(MCVersion version) {
		super(version);
	}

	public OWFortress(Config config, MCVersion version) {
		super(config, version);
	}

	@Override
	public String getName() {
		return name();
	}

	public static String name() {
		return "OW_fortress";
	}

	@Override
	public boolean isValidDimension(Dimension dimension) {
		return dimension == Dimension.OVERWORLD;
	}

}

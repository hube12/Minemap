package kaptainwutax.minemap.feature;

import kaptainwutax.featureutils.structure.Stronghold;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.version.MCVersion;

public class NEStronghold extends Stronghold {

    public NEStronghold(MCVersion version) {
        super(version);
    }

    public NEStronghold(Config config, MCVersion version) {
        super(config, version);
    }

    public static String name() {
        return "NE_Stronghold";
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public boolean isValidDimension(Dimension dimension) {
        return dimension == Dimension.NETHER;
    }

}

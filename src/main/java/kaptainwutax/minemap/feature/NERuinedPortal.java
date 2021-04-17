package kaptainwutax.minemap.feature;

import kaptainwutax.featureutils.structure.RuinedPortal;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.version.MCVersion;

public class NERuinedPortal extends RuinedPortal {

    public NERuinedPortal(MCVersion version) {
        super(Dimension.NETHER, version);
    }

    public NERuinedPortal(Config config, MCVersion version) {
        super(Dimension.NETHER, config, version);
    }

    public static String name() {
        return "NE_ruined_portal";
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

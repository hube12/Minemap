package kaptainwutax.minemap.feature;

import kaptainwutax.featureutils.structure.RuinedPortal;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.MCVersion;

public class OWRuinedPortal extends RuinedPortal {

    public OWRuinedPortal(MCVersion version) {
        super(Dimension.OVERWORLD, version);
    }

    public OWRuinedPortal(Config config, MCVersion version) {
        super(Dimension.OVERWORLD, config, version);
    }

    public static String name() {
        return "OW_ruined_portal";
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public boolean isValidDimension(Dimension dimension) {
        return dimension == Dimension.OVERWORLD;
    }

}

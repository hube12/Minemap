package com.seedfinding.minemap.feature;

import com.seedfinding.mcfeature.structure.RuinedPortal;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.version.MCVersion;

public class OWNERuinedPortal extends RuinedPortal {

    public OWNERuinedPortal(MCVersion version) {
        super(Dimension.NETHER, version);
    }

    public OWNERuinedPortal(Config config, MCVersion version) {
        super(Dimension.NETHER, config, version);
    }

    public static String name() {
        return "OW_NE_ruined_portal";
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

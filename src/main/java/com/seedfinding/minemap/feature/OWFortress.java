package com.seedfinding.minemap.feature;

import com.seedfinding.mcfeature.structure.Fortress;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.version.MCVersion;

public class OWFortress extends Fortress {

    public OWFortress(MCVersion version) {
        super(version);
    }

    public OWFortress(Config config, MCVersion version) {
        super(config, version);
    }

    public static String name() {
        return "OW_fortress";
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

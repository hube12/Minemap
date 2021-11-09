package com.seedfinding.minemap.feature;

import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.structure.Stronghold;

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

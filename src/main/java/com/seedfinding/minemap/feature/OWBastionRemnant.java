package com.seedfinding.minemap.feature;

import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.structure.BastionRemnant;

public class OWBastionRemnant extends BastionRemnant {

    public OWBastionRemnant(MCVersion version) {
        super(version);
    }

    public OWBastionRemnant(Config config, MCVersion version) {
        super(config, version);
    }

    public static String name() {
        return "OW_bastion_remnant";
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

package com.seedfinding.minemap.ui.map.icon;

import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.minemap.ui.map.fragment.Fragment;
import com.seedfinding.minemap.util.data.DrawInfo;

import java.awt.*;
import java.util.List;

public class NullIcon extends IconRenderer {

    public static final NullIcon INSTANCE = new NullIcon();

    public NullIcon() {
        super(null);
    }

    @Override
    public boolean isValidFeature(Feature<?, ?> feature) {
        return true;
    }

    @Override
    public void addPositions(Feature<?, ?> feature, Fragment fragment, List<BPos> positions) {

    }

    @Override
    public void render(Graphics graphics, DrawInfo info, Feature<?, ?> feature, Fragment fragment, BPos pos, boolean hovered) {

    }

    @Override
    public boolean isHovered(Fragment fragment, BPos hoveredPos, BPos featurePos, int width, int height, Feature<?, ?> feature) {
        return false;
    }

}

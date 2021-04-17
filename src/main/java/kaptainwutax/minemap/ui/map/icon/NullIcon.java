package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.minemap.util.data.DrawInfo;
import kaptainwutax.mcutils.util.pos.BPos;

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

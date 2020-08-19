package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.ui.DrawInfo;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.seedutils.mc.pos.BPos;

import java.awt.*;
import java.util.List;

public abstract class IconRenderer {

    private final MapContext context;

    public IconRenderer(MapContext context) {
        this.context = context;
    }

    public MapContext getContext() {
        return this.context;
    }

    public abstract boolean isValidFeature(Feature<?, ?> feature);

    public abstract void addPositions(Feature<?, ?> feature, Fragment fragment, List<BPos> positions);

    public abstract void render(Graphics graphics, DrawInfo info, Feature<?, ?> feature, Fragment fragment, BPos pos);

}

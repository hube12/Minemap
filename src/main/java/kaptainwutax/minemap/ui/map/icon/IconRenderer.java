package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.minemap.util.data.DrawInfo;
import kaptainwutax.mcutils.util.pos.BPos;

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

    public float getZValue() {
        return 0.0F;
    }

    public abstract boolean isValidFeature(Feature<?, ?> feature);

    public abstract void addPositions(Feature<?, ?> feature, Fragment fragment, List<BPos> positions);

    public abstract void render(Graphics graphics, DrawInfo info, Feature<?, ?> feature, Fragment fragment, BPos pos, boolean hovered);

    public abstract boolean isHovered(Fragment fragment, BPos hoveredPos, BPos featurePos, int width, int height, Feature<?, ?> feature);

    public float getZoomScaleFactor() {
        if (MineMap.INSTANCE == null) return 1F;
        double pxFrag = MineMap.INSTANCE.worldTabs.getSelectedMapPanel().getManager().pixelsPerFragment;
        if (pxFrag < 64) {
            return 1 / 2F;
        } else if (pxFrag < 128) {
            return 1 / 1.5F;
        }
        return 1F;
    }

    public float getHoverScaleFactor() {
        return 1.5F;
    }
}

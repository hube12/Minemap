package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.ui.DrawInfo;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.seedutils.mc.pos.BPos;

import java.awt.*;

public abstract class StaticIcon extends IconRenderer {

    private final int iconSize;

    public StaticIcon(MapContext context) {
        this(context, 24);

    }

    public StaticIcon(MapContext context, int iconSize) {
        super(context);
        this.iconSize = iconSize;
    }

    public float getHoverScaleFactor() {
        return 1.5F;
    }

    @Override
    public void render(Graphics graphics, DrawInfo info, Feature<?, ?> feature, Fragment fragment, BPos pos, boolean hovered) {
        float size = hovered ? this.iconSize * this.getHoverScaleFactor() : this.iconSize;
        int sx = (int)((double)(pos.getX() - fragment.getX()) / fragment.getSize() * info.width - size / 2.0F);
        int sy = (int)((double)(pos.getZ() - fragment.getZ()) / fragment.getSize() * info.height - size / 2.0F);
        graphics.drawImage(Icons.REGISTRY.get(feature.getClass()), info.x + sx, info.y + sy,
                (int)size, (int)size, null);
    }

    @Override
    public boolean isHovered(Fragment fragment, BPos hoveredPos, BPos featurePos, int width, int height) {
        double distanceX = (fragment.getSize() / (double)width) * (this.iconSize * this.getHoverScaleFactor() / 2.0D);
        double distanceZ = (fragment.getSize() / (double)height) * (this.iconSize * this.getHoverScaleFactor() / 2.0D);
        int dx = Math.abs(hoveredPos.getX() - featurePos.getX());
        int dz = Math.abs(hoveredPos.getZ() - featurePos.getZ());
        return dx < distanceX && dz < distanceZ;
    }

}

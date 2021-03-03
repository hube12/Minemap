package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.minemap.util.data.DrawInfo;
import kaptainwutax.seedutils.mc.pos.BPos;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class StaticIcon extends IconRenderer {

    private int iconSizeX;
    private int iconSizeZ;
    private static final int DEFAULT_VALUE = 20;

    public StaticIcon(MapContext context) {
        this(context, DEFAULT_VALUE, DEFAULT_VALUE);

    }

    public StaticIcon(MapContext context, int iconSizeX, int iconSizeZ) {
        super(context);
        this.iconSizeX = iconSizeX;
        this.iconSizeZ = iconSizeZ;
    }

    public float getHoverScaleFactor() {
        return 1.5F;
    }

    @Override
    public void render(Graphics graphics, DrawInfo info, Feature<?, ?> feature, Fragment fragment, BPos pos, boolean hovered) {
        BufferedImage icon = Icons.REGISTRY.get(feature.getClass());
        if (icon.getRaster().getWidth() > icon.getRaster().getHeight()) {
            this.iconSizeX = DEFAULT_VALUE;
            this.iconSizeZ=(int)(DEFAULT_VALUE*(float)icon.getRaster().getHeight()/icon.getRaster().getWidth());
        }else{
            this.iconSizeZ = DEFAULT_VALUE;
            this.iconSizeX=(int)(DEFAULT_VALUE*(float)icon.getRaster().getWidth()/icon.getRaster().getHeight());
        }
        float sizeX = hovered ? this.iconSizeX * this.getHoverScaleFactor() : this.iconSizeX;
        float sizeZ = hovered ? this.iconSizeZ * this.getHoverScaleFactor() : this.iconSizeZ;
        int sx = (int) ((double) (pos.getX() - fragment.getX()) / fragment.getSize() * info.width - sizeX / 2.0F);
        int sy = (int) ((double) (pos.getZ() - fragment.getZ()) / fragment.getSize() * info.height - sizeZ / 2.0F);

        graphics.drawImage(icon, info.x + sx, info.y + sy, (int) sizeX, (int) sizeZ, null);
    }

    @Override
    public boolean isHovered(Fragment fragment, BPos hoveredPos, BPos featurePos, int width, int height) {
        double distanceX = (fragment.getSize() / (double) width) * (this.iconSizeX * this.getHoverScaleFactor() / 2.0D);
        double distanceZ = (fragment.getSize() / (double) height) * (this.iconSizeZ * this.getHoverScaleFactor() / 2.0D);
        int dx = Math.abs(hoveredPos.getX() - featurePos.getX());
        int dz = Math.abs(hoveredPos.getZ() - featurePos.getZ());
        return dx < distanceX && dz < distanceZ;
    }

}

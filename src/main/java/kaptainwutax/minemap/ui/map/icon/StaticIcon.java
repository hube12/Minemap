package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.minemap.util.data.DrawInfo;
import kaptainwutax.mcutils.util.pos.BPos;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.function.Function;

public abstract class StaticIcon extends IconRenderer {

    private static final int DEFAULT_VALUE = 24;
    private int iconSizeX;
    private int iconSizeZ;

    public StaticIcon(MapContext context) {
        this(context, DEFAULT_VALUE, DEFAULT_VALUE);
    }

    public StaticIcon(MapContext context, int iconSizeX, int iconSizeZ) {
        super(context);
        this.iconSizeX = iconSizeX;
        this.iconSizeZ = iconSizeZ;
    }

    public Function<Object, String> getExtraInfo() {
        return null;
    }

    public Function<Object, String> getExtraIcon() {
        return null;
    }

    @Override
    public void render(Graphics graphics, DrawInfo info, Feature<?, ?> feature, Fragment fragment, BPos pos, boolean hovered) {
        BufferedImage icon = Icons.get(feature.getClass());
        if (icon == null) return;
        if (icon.getRaster().getWidth() > icon.getRaster().getHeight()) {
            this.iconSizeX = DEFAULT_VALUE;
            this.iconSizeZ = (int) (DEFAULT_VALUE * (float) icon.getRaster().getHeight() / icon.getRaster().getWidth());
        } else {
            this.iconSizeZ = DEFAULT_VALUE;
            this.iconSizeX = (int) (DEFAULT_VALUE * (float) icon.getRaster().getWidth() / icon.getRaster().getHeight());
        }
        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        // disable stroke change
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        // disable weird floating point since pixel accurate
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
        // Interpolation correctly
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        // disable dithering for full color accuracy
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);


        float sizeX = hovered ? this.iconSizeX * this.getHoverScaleFactor() : this.iconSizeX;
        float sizeZ = hovered ? this.iconSizeZ * this.getHoverScaleFactor() : this.iconSizeZ;
        double scaleFactor = getZoomScaleFactor() * Configs.ICONS.getSize(feature.getClass());
        sizeX *= scaleFactor;
        sizeZ *= scaleFactor;

        int sx = (int) ((double) (pos.getX() - fragment.getX()) / fragment.getSize() * info.width - sizeX / 2.0F);
        int sy = (int) ((double) (pos.getZ() - fragment.getZ()) / fragment.getSize() * info.height - sizeZ / 2.0F);

        g2d.drawImage(icon, info.x + sx, info.y + sy, (int) sizeX, (int) sizeZ, null);
        if (getExtraInfo() != null && this.getContext().getSettings().showExtraInfos) {
            String stringInfo = getExtraInfo().apply(pos);
            if (stringInfo != null) {
                Color old = g2d.getColor();
//                g2d.setColor(Color.GRAY);
//                g2d.setStroke(new BasicStroke(2));
//                g2d.fillOval(info.x + sx + 15, info.y + sy+15, 10, 10);
                char[] charArray = stringInfo.toCharArray();
                g2d.setColor(Color.BLACK);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, (float) (14 * scaleFactor)));
                g2d.drawChars(charArray, 0, charArray.length, info.x + sx + (charArray.length == 1 ? 1 : 0) * ((int) sizeX / 2 - 5) - 1, info.y + sy + (int) sizeZ - 5 - 1);
                g2d.setColor(Color.WHITE);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, (float) (13 * scaleFactor)));
                g2d.drawChars(charArray, 0, charArray.length, info.x + sx + (charArray.length == 1 ? 1 : 0) * ((int) sizeX / 2 - 5), info.y + sy + (int) sizeZ - 5);
                g2d.setColor(old);
            }
        }
    }


    @Override
    public boolean isHovered(Fragment fragment, BPos hoveredPos, BPos featurePos, int width, int height, Feature<?, ?> feature) {
        double scaleFactor = this.getHoverScaleFactor() * this.getZoomScaleFactor() * Configs.ICONS.getSize(feature.getClass()) / 2.0D;
        double distanceX = (fragment.getSize() / (double) width) * this.iconSizeX * scaleFactor;
        double distanceZ = (fragment.getSize() / (double) height) * this.iconSizeZ * scaleFactor;
        int dx = Math.abs(hoveredPos.getX() - featurePos.getX());
        int dz = Math.abs(hoveredPos.getZ() - featurePos.getZ());
        return dx < distanceX && dz < distanceZ;
    }

}

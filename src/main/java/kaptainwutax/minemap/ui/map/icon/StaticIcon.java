package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.minemap.util.data.DrawInfo;
import kaptainwutax.minemap.util.ui.graphics.Graphic;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.util.function.Function;

import static kaptainwutax.minemap.util.ui.graphics.Icon.paintImage;

public abstract class StaticIcon extends IconRenderer {

    private static final int DEFAULT_VALUE = 24;
    private final int iconSizeX;
    private final int iconSizeZ;

    public StaticIcon(MapContext context) {
        this(context, DEFAULT_VALUE, DEFAULT_VALUE);
    }

    public StaticIcon(MapContext context, int iconSizeX, int iconSizeZ) {
        super(context);
        this.iconSizeX = iconSizeX;
        this.iconSizeZ = iconSizeZ;
    }

    public Function<BPos, String> getExtraInfo() {
        return null;
    }

    public Function<BPos, Pair<Color,BufferedImage>> getExtraIcon() {
        return null;
    }

    @Override
    public void render(Graphics graphics, DrawInfo info, Feature<?, ?> feature, Fragment fragment, BPos pos, boolean hovered) {
        float scaleFactor = (float) (getZoomScaleFactor() * Configs.ICONS.getSize(feature.getClass()) * (hovered ? this.getHoverScaleFactor() : 1));
        int sx = (int) ((double) (pos.getX() - fragment.getX()) / fragment.getSize() * info.width);
        int sy = (int) ((double) (pos.getZ() - fragment.getZ()) / fragment.getSize() * info.height);
        Graphics2D g2d = Graphic.setGoodRendering(Graphic.withoutDithering(graphics));
        BufferedImage icon = Icons.get(feature.getClass());
        paintImage(icon, g2d, DEFAULT_VALUE, new Pair<>(scaleFactor, scaleFactor), new Pair<>(info.x + sx, info.y + sy), true);

        if (getExtraInfo() != null && this.getContext().getSettings().showExtraInfos) {
            String stringInfo = getExtraInfo().apply(pos);
            if (stringInfo != null) {
                Color old = g2d.getColor();
//                g2d.setColor(Color.GRAY);
//                g2d.setStroke(new BasicStroke(2));
//                g2d.fillOval(info.x + sx + 15, info.y + sy+15, 10, 10);
                char[] charArray = stringInfo.toCharArray();
                g2d.setColor(Color.BLACK);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 14F * scaleFactor));
                int posX = info.x + sx + 5 * (charArray.length == 1 ? 1 : 0);
                int posY = (int) (info.y + sy - 5 + DEFAULT_VALUE * scaleFactor);
                g2d.drawChars(charArray, 0, charArray.length, posX - 1, posY - 1);
                g2d.setColor(Color.WHITE);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 13 * scaleFactor));
                g2d.drawChars(charArray, 0, charArray.length, posX, posY);
                g2d.setColor(old);
            }
        }
        if (getExtraIcon() != null && this.getContext().getSettings().showExtraIcons) {
            Pair<Color,BufferedImage> extraIcon = getExtraIcon().apply(pos);
            if (extraIcon != null) {
                int posX = (int) (info.x + sx + (DEFAULT_VALUE - 16) * scaleFactor / 2);
                int posY = (int) (info.y + sy - (DEFAULT_VALUE + 16) * scaleFactor / 2);
                Shape oldClip = g2d.getClip();
                Color oldColor = g2d.getColor();
                int size = (int) (16 * scaleFactor);
                g2d.setClip(new Ellipse2D.Float(posX, posY, size, size));
                g2d.setColor(extraIcon.getFirst());
                g2d.fillRect(posX,posY,size,size);
                int offset=Math.max(1,(int)(2*scaleFactor));
                paintImage(extraIcon.getSecond(), g2d, 12, new Pair<>(scaleFactor, scaleFactor), new Pair<>(posX+offset, posY+offset), false);
                g2d.setClip(oldClip);
                g2d.setColor(oldColor);
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

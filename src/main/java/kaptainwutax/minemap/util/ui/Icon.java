package kaptainwutax.minemap.util.ui;

import kaptainwutax.mcutils.util.data.Pair;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Icon {

    public static void paintImage(BufferedImage icon, Graphics g) {
        paintImage(icon, g, 20, 1.5F);
    }

    public static void paintImage(BufferedImage icon, Graphics g, int defaultSize, float factor) {
        paintImage(icon, g, defaultSize, factor, 0);
    }

    public static void paintImage(BufferedImage icon, Graphics g, int defaultSize, float factor, int inset) {
        paintImage(icon, g, defaultSize, new Pair<>(factor, factor), new Pair<>(inset, inset),false);
    }

    public static void paintImage(BufferedImage icon, Graphics g, int defaultSize, Pair<Float, Float> factor) {
        paintImage(icon, g, defaultSize, factor, new Pair<>(0, 0),false);
    }

    public static void paintImage(BufferedImage icon, Graphics g, int defaultSize, Pair<Float, Float> factor, Pair<Integer, Integer> insets, boolean shouldOffsetByHalf) {
        if (icon == null) return;
        int iconSizeX, iconSizeZ;
        if (icon.getRaster().getWidth() > icon.getRaster().getHeight()) {
            iconSizeX = defaultSize;
            iconSizeZ = (int) (defaultSize * (float) icon.getRaster().getHeight() / icon.getRaster().getWidth());
        } else {
            iconSizeZ = defaultSize;
            iconSizeX = (int) (defaultSize * (float) icon.getRaster().getWidth() / icon.getRaster().getHeight());
        }
        iconSizeX *= factor.getFirst();
        iconSizeZ *= factor.getSecond();
        int offsetX = shouldOffsetByHalf ? -(int) (iconSizeX / 2.0F) : (int) ((defaultSize * factor.getFirst() - iconSizeX) / 2.0F);
        int offsetZ = shouldOffsetByHalf ? -(int) (iconSizeZ / 2.0F) : (int) ((defaultSize * factor.getSecond() - iconSizeZ) / 2.0F);
        g.drawImage(icon,
            insets.getFirst() + offsetX,
            insets.getSecond() + offsetZ,
            iconSizeX,
            iconSizeZ,
            null
        );
    }
}

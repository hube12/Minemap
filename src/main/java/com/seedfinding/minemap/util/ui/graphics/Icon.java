package com.seedfinding.minemap.util.ui.graphics;

import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mccore.util.data.Triplet;
import com.seedfinding.minemap.init.Configs;
import com.seedfinding.minemap.init.Icons;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.HashMap;

public class Icon {
    public static HashMap<Triplet<BufferedImage, Integer, Integer>, Image> storage = new HashMap<>();

    public static void paintImage(BufferedImage icon, Graphics g) {
        paintImage(icon, g, 20, 1.5F);
    }

    public static void paintImage(BufferedImage icon, Graphics g, int defaultSize, float factor) {
        paintImage(icon, g, defaultSize, factor, 0);
    }

    public static void paintImage(BufferedImage icon, Graphics g, int defaultSize, float factor, int inset) {
        paintImage(icon, g, defaultSize, new Pair<>(factor, factor), new Pair<>(inset, inset), false);
    }

    public static void paintImage(BufferedImage icon, Graphics g, int defaultSize, Pair<Float, Float> factor) {
        paintImage(icon, g, defaultSize, factor, new Pair<>(0, 0), false);
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
        Image newimg = storage.get(new Triplet<>(icon, iconSizeX, iconSizeX));
        if (newimg == null) {
            newimg = icon.getScaledInstance(iconSizeX, iconSizeZ, Image.SCALE_FAST);
            storage.put(new Triplet<>(icon, iconSizeX, iconSizeZ), newimg);
        }
        g.drawImage(newimg,
            insets.getFirst() + offsetX,
            insets.getSecond() + offsetZ,
            null
        );
    }

    public static ImageIcon getIcon(Class<?> feature, int scaledSize, double size, BufferedImage background) {
        BufferedImage icon = Icons.get(feature);
        double iconSize = Configs.ICONS.getSize(feature);
        if (icon == null) return null;

        BufferedImage scaledIcon = scaleImage(icon, scaledSize, scaledSize, size / icon.getWidth() * iconSize, size / icon.getHeight() * iconSize);
        double diffScaled = (scaledSize - Math.min(size * iconSize, size)) / 2;
        BufferedImage translatedIcon = translateImage(scaledIcon, scaledSize, scaledSize, diffScaled, diffScaled);

        if (background != null) {
            BufferedImage finalIcon = new BufferedImage(scaledSize, scaledSize, BufferedImage.TYPE_INT_ARGB);
            finalIcon.createGraphics().drawImage(background, 0, 0, scaledSize, scaledSize, null);
            finalIcon.createGraphics().drawImage(translatedIcon, 0, 0, scaledSize, scaledSize, null);
            translatedIcon = finalIcon;
        }

        return new ImageIcon(translatedIcon);
    }

    public static BufferedImage translateImage(BufferedImage input, int newWidth, int newHeight, double scaleX, double scaleZ) {
        BufferedImage scaledIcon = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.translate(scaleX, scaleZ);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return scaleOp.filter(input, scaledIcon);
    }

    public static BufferedImage scaleImage(BufferedImage input, int newWidth, int newHeight, double scaleX, double scaleZ) {
        BufferedImage scaledIcon = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scaleX, scaleZ);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        return scaleOp.filter(input, scaledIcon);
    }
}

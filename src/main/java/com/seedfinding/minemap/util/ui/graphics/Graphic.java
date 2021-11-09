package com.seedfinding.minemap.util.ui.graphics;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Graphic {

    public static Graphics2D setGoodRendering(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        // Interpolation correctly
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        // disable stroke change
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        // disable weird floating point since pixel accurate
        g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

        return g2d;
    }

    public static Graphics2D withDithering(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // disable dithering for average color accuracy
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        return g2d;
    }

    public static Graphics2D withoutDithering(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        // disable dithering for full color accuracy
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
        return g2d;
    }

    public static void scheduleAction(long timeout, Runnable runnable) {
        Executors.newSingleThreadScheduledExecutor().schedule(() -> {
            SwingUtilities.invokeLater(runnable);
            return null;
        }, timeout, TimeUnit.MILLISECONDS);
    }


    public static void centerParent(Frame parent) {
        GraphicsConfiguration config = parent.getGraphicsConfiguration();
        if (config != null) {
            GraphicsDevice currentScreen = config.getDevice();
            if (currentScreen != null) {
                JFrame dummy = new JFrame(currentScreen.getDefaultConfiguration());
                parent.setLocationRelativeTo(dummy);
                dummy.dispose();
                return;
            }
        }
        parent.setLocationRelativeTo(null);
    }
}



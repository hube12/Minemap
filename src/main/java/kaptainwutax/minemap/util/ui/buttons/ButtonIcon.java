package kaptainwutax.minemap.util.ui.buttons;

import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.util.ui.RoundedBorder;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class ButtonIcon extends JButton {
    public final int size;
    public final int inset;
    public final float factor;
    public final boolean background;
    public final boolean border;
    public Color backgroundColor;

    public ButtonIcon(int size, int inset, float factor, boolean background, Color backgroundColor) {
        this(size, inset, factor, background, backgroundColor, true);
    }

    public ButtonIcon(int size, int inset, float factor, boolean background, Color backgroundColor, boolean border) {
        super();
        this.size = size;
        this.inset = inset;
        this.factor = factor;
        this.border = border;
        this.background = background;
        this.backgroundColor = backgroundColor;
    }

    public ButtonIcon(int size, int inset, float factor) {
        this(size, inset, factor, false, Color.WHITE);
    }

    public ButtonIcon(int size, int inset) {
        this(size, inset, 1.7F);
    }

    public ButtonIcon(int size) {
        this(size, 1);
    }

    public ButtonIcon() {
        this(16);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(30, 30);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        this.setOpaque(false);
        this.setContentAreaFilled(false);
        if (border) this.setBorder(new RoundedBorder(size - 2, 30));
        this.setForeground(Color.DARK_GRAY);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
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

        if (this.background) {
            Color old = g.getColor();
            g.setColor(backgroundColor);
            int bSize = 30;
            int bRadius = size - 2;
            int bDiff = bSize / 2 - bRadius;
            g.fillRoundRect(bDiff, bDiff, bRadius * 2 - 2, bRadius * 2 - 2, bSize, bSize);

            g.setColor(old);
        }

        BufferedImage icon = Icons.get(this.getClass());
        int iconSizeX, iconSizeZ;
        int defaultValue = size;
        if (icon.getRaster().getWidth() > icon.getRaster().getHeight()) {
            iconSizeX = defaultValue;
            iconSizeZ = (int) (defaultValue * (float) icon.getRaster().getHeight() / icon.getRaster().getWidth());
        } else {
            iconSizeZ = defaultValue;
            iconSizeX = (int) (defaultValue * (float) icon.getRaster().getWidth() / icon.getRaster().getHeight());
        }
        g.drawImage(icon, (defaultValue - iconSizeX) / 2 + inset, (defaultValue - iconSizeZ) / 2 + inset, (int) (iconSizeX * factor), (int) (iconSizeZ * factor), null);
    }

    public void changeBColor(Color color) {
        this.backgroundColor = color;
        this.revalidate();
        this.repaint();
    }
}
package kaptainwutax.minemap.util.ui;

import kaptainwutax.minemap.init.Icons;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;

public class CloseIcon extends JButton {
    private int size = 16;
    private int inset = 1;
    private float factor = 1.7F;

    public CloseIcon(int size, int inset,float factor) {
        super();
        this.size = size;
        this.inset = inset;
        this.factor=factor;

    }

    public CloseIcon() {
        super();
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
        this.setBorder(new RoundedBorder(size - 2, 30)); //10 is the radius
        this.setForeground(Color.DARK_GRAY);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

        BufferedImage icon = Icons.REGISTRY.get(this.getClass());
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
}

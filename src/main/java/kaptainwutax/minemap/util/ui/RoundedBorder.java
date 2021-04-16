package kaptainwutax.minemap.util.ui;

import javax.swing.border.Border;
import java.awt.*;

public class RoundedBorder implements Border {
    private final int radius;
    private final int size;
    private final int diff;

    /*
    The radius is the radius of the inside circle and size is the size of the rectangle around it
     */
    public RoundedBorder(int radius, int size) {
        this.radius = radius;
        this.size = size;
        this.diff = size / 2 - radius;
    }

    public Insets getBorderInsets(Component c) {
        return new Insets(diff, diff, diff, diff);
    }

    public boolean isBorderOpaque() {
        return true;
    }

    public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(2));
        g.drawRoundRect(x + diff, y + diff, width - 2 - size + radius * 2, height - 2 - size + radius * 2, size, size);

    }
}


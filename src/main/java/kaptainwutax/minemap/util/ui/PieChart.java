package kaptainwutax.minemap.util.ui;

import kaptainwutax.seedutils.rand.JRand;
import kaptainwutax.terrainutils.utils.MathHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.HashMap;
import java.util.Map;

public class PieChart extends JComponent {
    private final HashMap<Color, Long> proportions;
    private final double unknownSize;
    private final long total;
    private final HashMap<Color, String> labels;

    public PieChart(HashMap<Color, Long> proportions, HashMap<Color, String> labels) {
        this(proportions, labels, proportions.values().stream().reduce(0L, Long::sum));
    }

    public PieChart(HashMap<Color, Long> proportions, HashMap<Color, String> labels, long total) {
        this.proportions = proportions;
        this.unknownSize = (total - proportions.values().stream().reduce(0L, Long::sum)) / (double) total;
        this.total = total;
        this.labels = labels;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = Graphic.setGoodRendering(Graphic.withDithering(g));
        g2d.setStroke(new BasicStroke(5f));
        double curAngle = 0.0D;
        double diameter = Math.min(this.getWidth(), this.getHeight()) * 0.8;
        double radius = diameter / 2;
        int x = (int) (this.getWidth() / 2 - radius);
        int y = (int) (this.getHeight() / 2 - radius);
        int mx = (int) (x + radius);
        int my = (int) (y + radius);
        int offsetText = (int) (radius * 0.6);
        JRand jRand = new JRand(42); // we use rand to allow user to capture a perfect snapshot in case of overlapping text (a bit of a hack)
        for (Map.Entry<Color, Long> proportion : proportions.entrySet()) {
            g2d.setColor(proportion.getKey());
            double extend = proportion.getValue() / (double) total * 360.0D;
            Arc2D arc2D = new Arc2D.Double(x, y, diameter, diameter, curAngle, extend, Arc2D.PIE);
            g2d.fill(arc2D);
            curAngle += extend;
        }
        // set the unknown part
        g2d.setColor(Color.BLACK);
        g2d.fill(new Arc2D.Double(x, y, diameter, diameter, curAngle, unknownSize / (double) total * 360.0D, Arc2D.PIE));
        // make the text
        curAngle = 0;
        FontMetrics metrics = g2d.getFontMetrics(getFont());
        int textHeight = metrics.getHeight();
        OutlinedText outlinedText = new OutlinedText(Color.DARK_GRAY, new BasicStroke(1.5f));
        for (Map.Entry<Color, Long> proportion : proportions.entrySet()) {
            double extend = proportion.getValue() / (double) total * Math.PI * 2;
            double curAngleRad = curAngle + extend / 2;
            int ix = (int) ((offsetText + MathHelper.clamp(jRand.nextDouble() - 0.5D, -0.2D, 0.2D)) * Math.cos(curAngleRad));
            int iy = (int) ((offsetText + MathHelper.clamp(jRand.nextDouble() - 0.5D, -0.2D, 0.2D)) * Math.sin(curAngleRad));
            String label = labels.get(proportion.getKey());
            int textWidth = metrics.stringWidth(label);
            int offsetX = mx + ix - textWidth / 2;
            int offsetY = my - iy - textHeight / 2;
            outlinedText.drawOutline(g2d, label, offsetX, offsetY);
            g.setColor(Color.WHITE);
            g2d.drawString(label, offsetX, offsetY);
            curAngle += extend;
        }
        if (unknownSize > 0) {
            double extend = unknownSize / (double) total * Math.PI * 2;
            double curAngleRad = curAngle + extend / 2;
            int ix = (int) ((offsetText + MathHelper.clamp(jRand.nextDouble() - 0.5D, -0.2D, 0.2D)) * Math.cos(curAngleRad));
            int iy = (int) ((offsetText + MathHelper.clamp(jRand.nextDouble() - 0.5D, -0.2D, 0.2D)) * Math.sin(curAngleRad));
            String label = String.format("Unknown %.2f%%", unknownSize * 100.0D);
            int textWidth = metrics.stringWidth(label);
            int offsetX = mx + ix - textWidth / 2;
            int offsetY = my - iy - textHeight / 2;
            outlinedText.drawOutline(g2d, label, offsetX, offsetY);
            g.setColor(Color.WHITE);
            g2d.drawString(label, offsetX, offsetY);
        }


    }
}

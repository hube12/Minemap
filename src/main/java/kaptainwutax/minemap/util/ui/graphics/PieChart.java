package kaptainwutax.minemap.util.ui.graphics;

import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.seedutils.rand.JRand;
import kaptainwutax.terrainutils.utils.MathHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Arc2D;
import java.util.HashMap;
import java.util.Map;

public class PieChart extends JComponent {
    private final HashMap<Color, Long> proportions;
    private final double unknownSize;
    private final long total;
    private final String totalLabel;
    private final HashMap<Color, Pair<String, String>> labels;
    private final HashMap<Arc2D, String> tooltips = new HashMap<>();

    /**
     * Create a pie Chart from a collection of Color,Long,label,tooltip
     *
     * @param proportions the hashmap with the color associated to the raw quantity
     *                    (could be reduced as the total is calculated but we recommend
     *                    to not do so for precision)
     * @param labels      the hashmap with the color associated to a Pair of label and tooltip
     * @param totalLabel  the label for the total
     */
    public PieChart(HashMap<Color, Long> proportions, HashMap<Color, Pair<String, String>> labels, String totalLabel) {
        this(proportions, labels, totalLabel, proportions.values().stream().reduce(0L, Long::sum));
    }

    public PieChart(HashMap<Color, Long> proportions, HashMap<Color, Pair<String, String>> labels, String totalLabel, long total) {
        this.proportions = proportions;
        this.unknownSize = (total - proportions.values().stream().reduce(0L, Long::sum)) / (double) total;
        this.total = total;
        this.labels = labels;
        this.totalLabel = totalLabel;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        Graphics2D g2d = Graphic.setGoodRendering(Graphic.withDithering(g));
        g2d.setStroke(new BasicStroke(5f));

        // draw total
        g2d.drawString(String.format(totalLabel, total), 5, 15);
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
            tooltips.put(arc2D, labels.get(proportion.getKey()).getSecond());
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
            String label = labels.get(proportion.getKey()).getFirst();
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
        setToolTipText("");

    }

    @Override
    public String getToolTipText(MouseEvent event) {
        for (Arc2D arc2D : tooltips.keySet()) {
            if (arc2D.contains(event.getPoint())) {
                return tooltips.get(arc2D);
            }
        }
        return null;
    }
}

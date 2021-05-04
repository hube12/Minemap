package kaptainwutax.minemap.util.ui.graphics;

import kaptainwutax.minemap.init.Logger;

import java.awt.*;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;

public class OutlinedText {
    private final Color outlineColor;
    private final Stroke outlineStroke;
    public OutlinedText(Color outlineColor,Stroke outlineStroke){
        this.outlineColor=outlineColor;
        this.outlineStroke=outlineStroke;
    }

    public void drawOutline(Graphics2D g2d,String text,int offsetX,int offsetY){
        Stroke oldStroke=g2d.getStroke();
        Color oldColor=g2d.getColor();
        AffineTransform transform=new AffineTransform();
        transform.translate(offsetX, offsetY);
        AffineTransform reverseTransform;
        try {
            reverseTransform=transform.createInverse();
        } catch (NoninvertibleTransformException e) {
            e.printStackTrace();
            Logger.LOGGER.severe(e.getMessage());
            return;
        }

        GlyphVector glyphVector = g2d.getFont().createGlyphVector(g2d.getFontRenderContext(), text);
        Shape textShape = glyphVector.getOutline();
        g2d.transform(transform);
        g2d.setColor(outlineColor);
        g2d.setStroke(outlineStroke);
        g2d.draw(textShape);

        // reset
        g2d.transform(reverseTransform);
        g2d.setColor(oldColor);
        g2d.setStroke(oldStroke);

    }

    //            TextLayout textTl = new TextLayout(label,getFont(),g2d.getFontRenderContext());
//            AffineTransform transform=new AffineTransform();
//            transform.translate(mx + ix - textWidth / 2.0, my - iy - textHeight / 2.0);
//           // transform.scale(0.8,0.8);
//            Shape outline = textTl.getOutline(transform);
//            g2d.setColor(Color.GRAY);
//            g2d.draw(outline);

}

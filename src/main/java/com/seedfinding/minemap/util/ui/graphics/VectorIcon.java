package com.seedfinding.minemap.util.ui.graphics;

import com.github.weisj.jsvg.SVGDocument;
import com.github.weisj.jsvg.attributes.ViewBox;
import com.github.weisj.jsvg.parser.LoaderContext;
import com.github.weisj.jsvg.parser.SVGLoader;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

/** An SVG icon that stays sharp at any size (rendered by JSVG, nothing is rasterised or cached at a fixed size). */
public final class VectorIcon {
    private final SVGDocument document;

    private VectorIcon(SVGDocument document) {
        this.document = document;
    }

    /**
     * @param inputStream the SVG content (opened by {@code Assets}); it is closed here
     * @return the icon, or null if it cannot be parsed
     */
    public static VectorIcon load(InputStream inputStream) {
        try (inputStream) {
            SVGDocument document = new SVGLoader().load(inputStream, null, LoaderContext.createDefault());
            return document == null ? null : new VectorIcon(document);
        } catch (IOException e) {
            return null;
        }
    }

    /** Draws the icon fitted (aspect ratio kept) inside the given box. */
    public void paint(Graphics g, double x, double y, double width, double height) {
        Graphics2D g2d = (Graphics2D) g.create();
        try {
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            this.document.render((Component) null, g2d, new ViewBox((float) x, (float) y, (float) width, (float) height));
        } finally {
            g2d.dispose();
        }
    }
}

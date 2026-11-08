package com.seedfinding.minemap.util.ui.interactive;

import org.jdesktop.swingx.JXCollapsiblePane;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

/**
 * A panel that slides in and out next to a small tab button holding an arrow.
 * The arrow always points the way the panel will move: toward the middle of the screen to open it,
 * back toward the screen edge to close it.
 */
public class DockableContainer extends JPanel {
    public boolean isHiddenForSize = false;

    /**
     * @param direction  collapse direction of the panel
     * @param panel      the content
     * @param opensRight true if the container sits on the left edge of the screen (so the arrow points right to open),
     *                   false if it sits on the right edge (the arrow points left to open)
     */
    public DockableContainer(JXCollapsiblePane.Direction direction, JPanel panel, boolean opensRight) {
        this.setOpaque(false);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JXCollapsiblePane collapsiblePane = new JXCollapsiblePane(direction);
        collapsiblePane.setLayout(new BorderLayout());
        collapsiblePane.setCollapsed(true);
        collapsiblePane.add(panel, BorderLayout.CENTER);

        ArrowButton toggle = new ArrowButton(collapsiblePane.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION), opensRight);

        this.add(opensRight ? collapsiblePane : toggle);
        this.add(opensRight ? toggle : collapsiblePane);
    }

    /**
     * Tab shaped button holding a chevron: flat on the screen edge side and rounded on the side the panel opens to.
     * The chevron flips every time it is clicked.
     */
    private static class ArrowButton extends JButton {
        private static final int WIDTH = 20;
        private static final int HEIGHT = 44;
        private static final int ARC = 16;
        private final boolean opensRight;
        private boolean expanded = false;

        ArrowButton(Action action, boolean opensRight) {
            super(action);
            this.opensRight = opensRight;
            this.setText("");
            this.setHideActionText(true);
            this.setToolTipText(null);
            this.setFocusable(false);
            this.setContentAreaFilled(false);
            this.setBorderPainted(false);
            this.setOpaque(false);
            this.setMargin(new Insets(0, 0, 0, 0));
            Dimension size = new Dimension(WIDTH, HEIGHT);
            this.setPreferredSize(size);
            this.setMinimumSize(size);
            this.setMaximumSize(size);
            this.addActionListener(e -> {
                this.expanded = !this.expanded;
                this.repaint();
            });
        }

        private Shape tabShape(int w, int h) {
            Area shape = new Area(new RoundRectangle2D.Float(0, 0, w, h, ARC, ARC));
            // square the corners on the screen edge side
            shape.add(new Area(this.opensRight ? new Rectangle2D.Float(0, 0, w / 2f, h) : new Rectangle2D.Float(w / 2f, 0, w / 2f, h)));
            return shape;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            int w = this.getWidth();
            int h = this.getHeight();
            Color fg = this.getForeground();
            Color bg = UIManager.getColor("Button.background");
            if (bg == null) bg = this.getBackground();
            if (this.getModel().isPressed()) bg = bg.darker();
            else if (this.getModel().isRollover()) bg = bg.brighter();

            Shape tab = this.tabShape(w, h);
            g2.setColor(bg);
            g2.fill(tab);
            g2.setColor(new Color(fg.getRed(), fg.getGreen(), fg.getBlue(), 90));
            g2.setStroke(new BasicStroke(1f));
            g2.translate(0.5, 0.5);
            g2.draw(this.tabShape(w - 1, h - 1));
            g2.translate(-0.5, -0.5);

            // chevron, pointing right when the panel moves right
            boolean pointsRight = this.expanded != this.opensRight;
            float cx = w / 2f;
            float cy = h / 2f;
            float half = 6f;
            float depth = 3.5f;
            float tip = pointsRight ? cx + depth : cx - depth;
            float back = pointsRight ? cx - depth : cx + depth;
            Path2D chevron = new Path2D.Float();
            chevron.moveTo(back, cy - half);
            chevron.lineTo(tip, cy);
            chevron.lineTo(back, cy + half);
            g2.setColor(fg);
            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(chevron);
            g2.dispose();
        }
    }
}

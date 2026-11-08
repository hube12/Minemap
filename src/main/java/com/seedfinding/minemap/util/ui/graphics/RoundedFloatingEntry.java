package com.seedfinding.minemap.util.ui.graphics;

import com.seedfinding.minemap.init.Icons;
import com.seedfinding.minemap.listener.Events;
import com.seedfinding.minemap.util.ui.buttons.CloseButton;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.seedfinding.minemap.util.ui.graphics.Icon.paintImage;

public class RoundedFloatingEntry extends RoundedPanel {
    /** Icon box of the action buttons (jump, copy, close): size 16 * factor 1.5 = 24 px, centred on the round button.
     * The inset only matters for icons still drawn from PNG: the button circle is centred at 14, so 24 px starts at 2. */
    public static final int ACTION_ICON_INSET = 2;
    public static final float ACTION_ICON_FACTOR = 1.5F;
    private final JComponent iconView;
    private final JTextArea positionText;
    private final JButton closeButton;
    private final GridBagConstraints constraints;

    public RoundedFloatingEntry(String text, Class<?> classIcon, Function<RoundedFloatingEntry, Consumer<MouseEvent>> onClose) {
        this.setLayout(new GridBagLayout());
        this.constraints = new GridBagConstraints();
        this.constraints.insets = new Insets(3, 3, 3, 3);

        this.iconView = new JComponent() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(30, 30);
            }

            @Override
            public Dimension getMinimumSize() {
                return this.getPreferredSize(); // a narrow row shrinks the text, never the icon
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                VectorIcon vector = Icons.getVector(classIcon);
                if (vector != null) vector.paint(g, 0, 0, 30, 30); // paintImage's default: 20 * 1.5
                else paintImage(Icons.get(classIcon), g);
            }
        };
        this.positionText = new JTextArea(String.join("\n", text));
        this.positionText.setFont(new Font(this.positionText.getFont().getName(), Font.PLAIN, 18));
        this.positionText.setBackground(new Color(0, 0, 0, 0));
        this.positionText.setFocusable(false);
        this.positionText.setOpaque(true);
        this.positionText.setForeground(Color.WHITE);
        this.positionText.setEditable(false);
        this.positionText.setHighlighter(null);
        // disable caret selection
        this.positionText.setCaret(new Caret() {
            public void install(JTextComponent c) {}

            public void deinstall(JTextComponent c) {}

            public void paint(Graphics g) {}

            public void addChangeListener(ChangeListener l) {}

            public void removeChangeListener(ChangeListener l) {}

            public boolean isVisible() {
                return false;
            }

            public void setVisible(boolean v) {}

            public boolean isSelectionVisible() {
                return false;
            }

            public void setSelectionVisible(boolean v) {}

            public void setMagicCaretPosition(Point p) {}

            public Point getMagicCaretPosition() {
                return new Point(0, 0);
            }

            public void setBlinkRate(int rate) {}

            public int getBlinkRate() {
                return 10000;
            }

            public int getDot() {
                return 0;
            }

            public int getMark() {
                return 0;
            }

            public void setDot(int dot) {}

            public void moveDot(int dot) {}
        });
        this.closeButton = new CloseButton(16, ACTION_ICON_INSET, ACTION_ICON_FACTOR);
        this.closeButton.addMouseListener(Events.Mouse.onPressed(onClose.apply(this)));

        this.add(this.iconView, constraints);
        this.add(this.positionText, constraints);
        this.add(this.closeButton, constraints);


        this.setBackground(new Color(0, 0, 0, 200));
        this.setOpaque(false);
        this.setFocusable(false);
    }

    /**
     * Makes the entry fill its row: the text sits centred in the space between the icon and the buttons, and the
     * buttons added after it stay pinned to the right edge, so they line up from one row to the next.
     * The text may shrink (and be clipped) when the row gets narrow, the buttons never do.
     */
    public void stretchText() {
        JPanel textHolder = new JPanel(new GridBagLayout()) {
            @Override
            public Dimension getMinimumSize() {
                return new Dimension(0, super.getMinimumSize().height);
            }
        };
        textHolder.setOpaque(false);
        // paint the box at the real size of the row (the legacy frozen size is cut when the window gets smaller)
        this.fitComponentSize = true;
        this.remove(this.positionText);
        textHolder.add(this.positionText); // default constraints: centred, natural size
        GridBagConstraints holderConstraints = (GridBagConstraints) this.constraints.clone();
        holderConstraints.weightx = 1.0D;
        holderConstraints.fill = GridBagConstraints.BOTH;
        this.add(textHolder, holderConstraints, 1);
        this.revalidate();
    }

    public JComponent getIconView() {
        return iconView;
    }

    public JButton getCloseButton() {
        return closeButton;
    }

    public JTextArea getPositionText() {
        return positionText;
    }

    public void addAtIndex(Component comp, int index) {
        super.add(comp, constraints, index);
    }

    public GridBagConstraints getConstraints() {
        return constraints;
    }
}

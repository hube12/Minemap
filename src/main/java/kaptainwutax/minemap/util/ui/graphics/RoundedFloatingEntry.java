package kaptainwutax.minemap.util.ui.graphics;

import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.map.tool.Tool;
import kaptainwutax.minemap.util.ui.buttons.CloseButton;
import kaptainwutax.minemap.util.ui.interactive.ColorChooserButton;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import static kaptainwutax.minemap.util.ui.graphics.Icon.paintImage;

public class RoundedFloatingEntry extends RoundedPanel {
    private final JComponent iconView;
    private final JTextArea positionText;
    private final JButton closeButton;
    private final GridBagConstraints constraints;

    public RoundedFloatingEntry(String text, Class<?> classIcon, Consumer<MouseEvent> onClose) {
        this.setLayout(new GridBagLayout());
        this.constraints = new GridBagConstraints();
        this.constraints.insets = new Insets(3, 3, 3, 3);

        this.iconView = new JComponent() {
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(30, 30);
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintImage(Icons.get(classIcon), g);
            }
        };
        this.positionText = new JTextArea(String.join("\n",text));
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

            public boolean isVisible() {return false;}

            public void setVisible(boolean v) {}

            public boolean isSelectionVisible() {return false;}

            public void setSelectionVisible(boolean v) {}

            public void setMagicCaretPosition(Point p) {}

            public Point getMagicCaretPosition() {return new Point(0, 0);}

            public void setBlinkRate(int rate) {}

            public int getBlinkRate() {return 10000;}

            public int getDot() {return 0;}

            public int getMark() {return 0;}

            public void setDot(int dot) {}

            public void moveDot(int dot) {}
        });
        this.closeButton = new CloseButton(16, 0, 1.8f);
        this.closeButton.addMouseListener(Events.Mouse.onPressed(onClose));

        this.add(this.iconView, constraints);
        this.add(this.positionText, constraints);
        this.add(this.closeButton, constraints);


        this.setBackground(new Color(0, 0, 0, 200));
        this.setOpaque(false);
        this.setFocusable(false);
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

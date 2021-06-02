package kaptainwutax.minemap.util.ui.interactive;

import org.jdesktop.swingx.JXCollapsiblePane;

import javax.swing.*;
import java.awt.*;

public class DockableContainer extends JPanel {
    public boolean isHiddenForSize = false;

    public DockableContainer(JXCollapsiblePane.Direction direction, JPanel panel) {
        this.setOpaque(false);
        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        JXCollapsiblePane collapsiblePane = new JXCollapsiblePane(direction);
        collapsiblePane.setLayout(new BorderLayout());
        collapsiblePane.setCollapsed(true);
        collapsiblePane.add(panel, BorderLayout.CENTER);

        JButton toggle = new JButton(collapsiblePane.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION));
        toggle.setText("<");
        toggle.addActionListener(e -> {
            toggle.setText(!collapsiblePane.isCollapsed() ? "<" : ">");
        });

        this.add(toggle);
        this.add(collapsiblePane);
    }
}

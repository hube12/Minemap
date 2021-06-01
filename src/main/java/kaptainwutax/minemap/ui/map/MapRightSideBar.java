package kaptainwutax.minemap.ui.map;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.ui.map.sidebar.TooltipTools;
import org.jdesktop.swingx.JXCollapsiblePane;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class MapRightSideBar extends JPanel {

    public final TooltipTools tooltip;
    public final JPanel searchBox;
    private final MapPanel map;

    public MapRightSideBar(MapPanel map) {
        this.map = map;
        this.tooltip = new TooltipTools(this.map);
        this.tooltip.setVisible(true);

        JXCollapsiblePane cp = new JXCollapsiblePane(JXCollapsiblePane.Direction.LEFT);
        cp.setLayout(new BorderLayout());
        cp.setCollapsed(true);
        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        JTextField searchEntry = new JTextField(10);
        controls.add(searchEntry);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(MineMap.INSTANCE, "This has yet to be finished, we did receive your request for free cookies... ahem : " + searchEntry.getText());
        });
        controls.add(searchButton);
        controls.setBorder(new TitledBorder("Help"));
        cp.add(controls, BorderLayout.CENTER);
        JButton toggle = new JButton(cp.getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION));
        toggle.setText("<");
        toggle.addActionListener(e -> {
            toggle.setText(!cp.isCollapsed() ? "<" : ">");
        });
        searchBox = new JPanel();
        searchBox.setOpaque(false);
        searchBox.setLayout(new BoxLayout(searchBox, BoxLayout.X_AXIS));
        searchBox.add(toggle);
        searchBox.add(cp);

        this.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.PAGE_START;
        gridBagConstraints.fill = GridBagConstraints.NONE;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        this.add(this.tooltip, gridBagConstraints);
        gridBagConstraints.anchor = GridBagConstraints.LAST_LINE_END;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        this.add(searchBox, gridBagConstraints);

        this.setBackground(new Color(0, 0, 0, 0));
        this.setOpaque(false);
    }

}

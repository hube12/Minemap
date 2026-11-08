package com.seedfinding.minemap.ui.map;

import com.seedfinding.minemap.listener.Events;
import com.seedfinding.minemap.util.data.HelpIndex;
import com.seedfinding.minemap.util.ui.interactive.DockableContainer;
import com.seedfinding.minemap.util.ui.interactive.HelpRenderer;
import com.seedfinding.minemap.ui.map.sidebar.SettingsSidebar;
import com.seedfinding.minemap.ui.map.sidebar.TooltipSidebar;

import org.jdesktop.swingx.JXCollapsiblePane;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class MapLeftSideBar extends JPanel {

    public final TooltipSidebar tooltip;
    public final SettingsSidebar settings;
    public final DockableContainer searchBox;
    private final MapPanel map;

    public MapLeftSideBar(MapPanel map) {
        this.map = map;
        this.tooltip = new TooltipSidebar(this.map);
        this.settings = new SettingsSidebar(this.map);

        this.tooltip.setVisible(true);
        this.settings.setVisible(false);
        this.settings.closeButton.addMouseListener(Events.Mouse.onPressed(e -> this.settings.setVisible(false)));

        this.searchBox = new DockableContainer(JXCollapsiblePane.Direction.LEFT, createHelpPanel(), true);

        // the settings and tooltip keep their own grid, the help dock sits below it in the bottom left corner
        JPanel main = new JPanel(new GridBagLayout());
        main.setOpaque(false);
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.fill = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        main.add(this.settings, gridBagConstraints);
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        main.add(this.tooltip, gridBagConstraints);
        JPanel mainHolder = new JPanel(new BorderLayout());
        mainHolder.setOpaque(false);
        mainHolder.add(main, BorderLayout.WEST);
        JPanel dockHolder = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        dockHolder.setOpaque(false);
        dockHolder.add(this.searchBox);

        this.setLayout(new BorderLayout());
        this.add(mainHolder, BorderLayout.CENTER);
        this.add(dockHolder, BorderLayout.SOUTH);
        this.setBackground(new Color(0, 0, 0, 0));
        this.setOpaque(false);
    }

    public static JPanel createHelpPanel() {
        JPanel helpPanel = new JPanel();
        helpPanel.setLayout(new BoxLayout(helpPanel, BoxLayout.Y_AXIS));

        JPanel searchRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        JTextField searchEntry = new JTextField(10);
        searchEntry.setToolTipText("Ask a question or type keywords (best matches first). /regex/ greps. Empty lists everything");
        JButton searchButton = new JButton("Search");
        searchRow.add(searchEntry);
        searchRow.add(searchButton);

        JTextPane results = new JTextPane();
        results.setEditable(false);
        HelpRenderer.showNotice(results, "Help", "Ask a question (e.g. how do I take a screenshot?) or type keywords and press Enter.");
        JScrollPane scroll = new JScrollPane(results);
        scroll.setPreferredSize(new Dimension(380, 150));
        scroll.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        Runnable search = () -> {
            List<String> found = HelpIndex.search(searchEntry.getText());
            if (found.isEmpty()) {
                HelpRenderer.showNotice(results, "No match", "nothing found for \"" + searchEntry.getText() + "\", try other words like zoom, biome or screenshot.");
            } else {
                HelpRenderer.showMessages(results, found);
            }
        };
        searchButton.addActionListener(e -> search.run());
        searchEntry.addActionListener(e -> search.run());

        helpPanel.add(searchRow);
        helpPanel.add(scroll);
        helpPanel.setBorder(new TitledBorder("Help"));
        return helpPanel;
    }
}

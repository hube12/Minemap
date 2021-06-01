package kaptainwutax.minemap.ui.map;

import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.ui.map.interactive.chest.ChestPanel;
import kaptainwutax.minemap.ui.map.sidebar.TooltipTools;
import kaptainwutax.minemap.util.ui.interactive.DockableContainer;
import org.jdesktop.swingx.JXCollapsiblePane;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class MapRightSideBar extends JPanel {

    public final TooltipTools tooltip;
    public final DockableContainer searchBox;
    public final ChestPanel chestContent;
    public final DockableContainer chestBox;
    private final MapPanel map;

    public MapRightSideBar(MapPanel map) {
        this.map = map;
        this.tooltip = new TooltipTools(this.map);
        this.tooltip.setVisible(true);
        Pair<ChestPanel,JPanel> chest=createChestPanel();
        this.chestContent=chest.getFirst();
        this.chestBox = new DockableContainer(JXCollapsiblePane.Direction.LEFT, chest.getSecond());
        this.searchBox = new DockableContainer(JXCollapsiblePane.Direction.LEFT, createHelpPanel());

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
        this.add(chestBox, gridBagConstraints);
        this.add(searchBox, gridBagConstraints);

        this.setBackground(new Color(0, 0, 0, 0));
        this.setOpaque(false);
    }

    public static JPanel createHelpPanel(){
        JPanel helpPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        JTextField searchEntry = new JTextField(10);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e ->
            JOptionPane.showMessageDialog(MineMap.INSTANCE,
            "This has yet to be finished, we did receive your request for free cookies... ahem : " + searchEntry.getText()));
        helpPanel.add(searchEntry);
        helpPanel.add(searchButton);
        helpPanel.setBorder(new TitledBorder("Help"));
        return helpPanel;
    }

    public static Pair<ChestPanel,JPanel> createChestPanel(){
        JPanel chestPanel=new JPanel();
        ChestPanel chest=new ChestPanel(new Pair<>(1.0,0.8));
        chestPanel.add(chest);
        return new Pair<>(chest,chestPanel);
    }
}

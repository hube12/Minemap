package kaptainwutax.minemap.ui.map;

import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.ui.map.interactive.chest.ChestInstance;
import kaptainwutax.minemap.ui.map.interactive.chest.ChestPanel;
import kaptainwutax.minemap.ui.map.sidebar.TooltipTools;
import kaptainwutax.minemap.util.ui.interactive.DockableContainer;
import org.jdesktop.swingx.JXCollapsiblePane;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

public class MapRightSideBar extends JPanel {

    public final TooltipTools tooltip;
    public final DockableContainer searchBox;
    public final ChestTopBar chestTopBar;
    public final DockableContainer chestBox;
    private final MapPanel map;

    public MapRightSideBar(MapPanel map) {
        this.map = map;
        this.tooltip = new TooltipTools(this.map);
        this.tooltip.setVisible(true);
        Pair<ChestTopBar,JPanel> chest=createChestPanel(this.map.chestInstance);
        this.chestTopBar=chest.getFirst();
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

    public static Pair<ChestTopBar,JPanel> createChestPanel(ChestInstance instance){
        JPanel chestPanel=new JPanel();
        ChestPanel chest=new ChestPanel(new Pair<>(1.0,0.8));
        ChestTopBar topBar=new ChestTopBar(chest,instance);
        chestPanel.add(topBar);
        chestPanel.add(chest);
        return new Pair<>(topBar,chestPanel);
    }

    public static class ChestTopBar extends JPanel{
        private final ChestPanel panel;
        private final ChestInstance instance;
        public ChestTopBar(ChestPanel panel, ChestInstance instance){
            instance.registerUpdateable(this::update);
            this.panel=panel;
            this.instance=instance;

        }
        public void updateContent(){

        }

        public void update(boolean hasChanged){
            List<List<ItemStack>> listItems=instance.getListItems();
            this.panel.update(listItems == null || listItems.size() < 1 ? null : listItems.get(this.instance.getCurrentChestIndex()));
        }
    }
}

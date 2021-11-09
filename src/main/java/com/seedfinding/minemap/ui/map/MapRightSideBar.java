package com.seedfinding.minemap.ui.map;

import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mcfeature.loot.item.ItemStack;
import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.ui.map.interactive.chest.ChestInstance;
import com.seedfinding.minemap.ui.map.interactive.chest.ChestPanel;
import com.seedfinding.minemap.ui.map.sidebar.TooltipTools;
import com.seedfinding.minemap.util.ui.graphics.RoundedBorder;
import com.seedfinding.minemap.util.ui.interactive.DockableContainer;
import org.jdesktop.swingx.JXCollapsiblePane;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.List;

import static com.seedfinding.minemap.util.data.Str.prettifyDashed;

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
        Pair<ChestTopBar, JPanel> chest = createChestPanel(this.map.chestInstance);
        this.chestTopBar = chest.getFirst();
        this.chestBox = new DockableContainer(JXCollapsiblePane.Direction.LEFT, chest.getSecond());
        this.searchBox = new DockableContainer(JXCollapsiblePane.Direction.LEFT, createHelpPanel());

        this.setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.anchor = GridBagConstraints.FIRST_LINE_END;
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

    public static JPanel createHelpPanel() {
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

    public static Pair<ChestTopBar, JPanel> createChestPanel(ChestInstance instance) {
        JPanel chestPanel = new JPanel();
        chestPanel.setLayout(new BoxLayout(chestPanel, BoxLayout.Y_AXIS));
        ChestPanel chest = new ChestPanel(new Pair<>(1.0, 0.8));
        ChestTopBar topBar = new ChestTopBar(chest, instance);
        JPanel rounded = new JPanel();
        rounded.add(topBar);
        rounded.setBorder(new RoundedBorder(10, 30));
        chestPanel.add(rounded);
        chestPanel.add(chest);
        return new Pair<>(topBar, chestPanel);
    }

    public static class ChestTopBar extends JPanel {
        private final ChestPanel panel;
        private final ChestInstance instance;
        private final JLabel currentChest;
        private final JLabel currentFeature;
        private final JButton previousChest;
        private final JButton nextChest;
        private final JButton indexedButton;
        private final static String[] indexedString = {"Spread", "Reassemble"};

        public ChestTopBar(ChestPanel panel, ChestInstance instance) {
            instance.registerUpdateable(this::update);
            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
            this.panel = panel;
            this.instance = instance;
            this.currentChest = new JLabel("No chest");
            this.currentFeature = new JLabel("No feature selected");
            this.previousChest = new JButton("<");
            this.nextChest = new JButton(">");
            this.previousChest.addActionListener(e -> this.cycle(true));
            this.nextChest.addActionListener(e -> this.cycle(false));
            this.indexedButton = new JButton(indexedString[this.instance.isIndexed() ? 1 : 0]);
            this.indexedButton.addActionListener(e -> {
                this.instance.toggleIndexed();
                instance.generate(); // this call update for us
                this.indexedButton.setText(indexedString[this.instance.isIndexed() ? 1 : 0]);
            });
            this.add(Box.createHorizontalGlue());
            this.add(this.indexedButton);
            this.add(Box.createRigidArea(new Dimension(20, 0)));
            this.add(this.previousChest);
            this.add(Box.createRigidArea(new Dimension(5, 0)));
            this.add(this.currentChest);
            this.add(Box.createRigidArea(new Dimension(5, 0)));
            this.add(this.nextChest);
            this.add(Box.createRigidArea(new Dimension(20, 0)));
            this.add(this.currentFeature);
            this.add(Box.createHorizontalGlue());

        }

        public void setIndexContent(int index) {
            // this will call update
            this.instance.setCurrentChestIndex(index);
        }

        private void cycle(boolean left) {
            int current = instance.getCurrentChestIndex();
            int size = instance.size();
            if (size == 0) {
                return;
            }
            this.setIndexContent((current + (left ? -1 : 1) + size) % size);
        }

        public void updateFirst() {
            this.setIndexContent(0);
        }

        public void update(boolean hasChanged) {
            this.currentChest.setText(instance.getCurrentChestIndex() + 1 + "/" + (instance.getListItems() == null ? "?" : instance.size()));
            this.currentFeature.setText(this.instance.getFeature() == null || this.instance.getPos() == null ? "No feature selected" :
                String.format("%s at x:%d z:%d", prettifyDashed(this.instance.getFeature().getName()), this.instance.getPos().getX(), this.instance.getPos().getZ()));
            this.indexedButton.setText(indexedString[this.instance.isIndexed() ? 1 : 0]);
            List<List<ItemStack>> listItems = instance.getListItems();
            this.panel.update(listItems == null || listItems.size() < 1 ? null : listItems.get(this.instance.getCurrentChestIndex()));
        }
    }
}

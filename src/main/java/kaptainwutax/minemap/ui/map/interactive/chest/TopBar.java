package kaptainwutax.minemap.ui.map.interactive.chest;

import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.minemap.listener.Events;

import javax.swing.*;
import java.awt.*;
import java.util.List;

import static kaptainwutax.minemap.util.ui.graphics.Graphic.centerParent;

public class TopBar extends JPanel {
    private final ChestInstance instance;
    private final ChestFrame window;
    private final JButton indexedButton;
    private final JButton showAllButton;
    private final JButton centerButton;
    private final JToggleButton pinButton;
    private final JMenu chestSelectionMenu;
    private final JMenuBar menuBar;
    private final JLabel currentChest;
    private boolean showAll = false;
    private final static String[] indexedString = {"Spread", "Reassemble"};
    private final static String[] showString = {"Show All", "Show One"};

    public TopBar(ChestInstance instance, ChestFrame chest) {
        instance.registerUpdateable(this::update);
        this.window =chest;
        this.instance = instance;
        // spread button
        this.indexedButton = new JButton(indexedString[ this.instance.isIndexed() ? 1 : 0]);
        this.indexedButton.addActionListener(e -> {
            this.instance.toggleIndexed();
            instance.generate();
            updateContent();
            this.indexedButton.setText(indexedString[ this.instance.isIndexed() ? 1 : 0]);
        });
        // menu to select which chest
        this.chestSelectionMenu = new JMenu("Select chest");
        this.menuBar = new JMenuBar();
        this.menuBar.add(this.chestSelectionMenu);
        // label that show x/n
        this.currentChest = new JLabel("");
        // show all chests
        this.showAllButton = new JButton(showString[showAll ? 1 : 0]);
        this.showAllButton.addActionListener(e -> {
            setShowAll(!showAll);
            // FIXME why was that needed for?
            instance.generate(); // this call update
            this.showAllButton.setText(showString[showAll ? 1 : 0]);
        });
        // center chest on the current screen
        this.centerButton = new JButton("Center Chest");
        this.centerButton.addActionListener(e -> centerParent(this.window));
        // pin the chest window on top
        this.pinButton = new JToggleButton("Always on top");
        this.pinButton.addActionListener(e -> {
            this.window.setAlwaysOnTop(!this.window.isAlwaysOnTop());
            this.window.revalidate();
            this.window.repaint();
        });
        this.add(this.indexedButton);
        this.add(this.menuBar);
        this.add(this.currentChest);
        this.add(this.showAllButton);
        this.add(this.centerButton);
        this.add(this.pinButton);
    }

    private void setShowAll(boolean showAll) {
        this.showAll = showAll;
        this.chestSelectionMenu.setVisible(!this.showAll);
        this.currentChest.setVisible(!this.showAll);
    }

    public void updateContent(){
        this.setNumberChest(instance.getListItems() == null ? 0 : instance.getListItems().size());
    }

    /**
     * Update the chest content
     *
     * @param hasChanged tri state, if true then
     */
    private void update(Boolean hasChanged) {
        List<List<ItemStack>> listItems=instance.getListItems();
        List<ChestPanel> chestContents = this.window.getChestContents();
        if (hasChanged) {
            Dimension dimension = this.window.getPreferredSize();
            LayoutManager layoutManager = this.window.getContent().getLayout();
            int factor = showAll && listItems != null && listItems.size() > 1 ? 2 : 1;
            if (layoutManager instanceof GridLayout) {
                GridLayout gridLayout = (GridLayout) layoutManager;
                gridLayout.setColumns(factor);
            }
            this.window.setSize(new Dimension(dimension.width * factor, dimension.height * (showAll && listItems != null ? (listItems.size() / 2 + listItems.size() % 2) : 1)));

            for (int i = 1; i < chestContents.size(); i++) {
                if (!showAll) {
                    this.window.getContent().remove(chestContents.get(i)); // this will not fail if the component was not there
                } else {
                    if (listItems != null && i < listItems.size()) {
                        this.window.getContent().add(chestContents.get(i));
                    } else {
                        this.window.getContent().remove(chestContents.get(i));
                    }
                }
            }
        }
        if (showAll) {
            for (int i = 0; i < (listItems == null ? 1 : listItems.size()); i++) {
                this.window.getChestContents().get(i).update(listItems == null || listItems.size() < 1 ? null : listItems.get(i));
            }
        } else {
            this.window.getChestContents().get(0).update(listItems == null || listItems.size() < 1 ? null : listItems.get(this.instance.getCurrentChestIndex()));
        }

        this.window.getContent().revalidate();
        this.window.getContent().repaint();
        this.window.getScrollPane().revalidate();
        this.window.getScrollPane().revalidate();
        this.showAllButton.setVisible(listItems != null && listItems.size() != 1);
        this.menuBar.setVisible(listItems != null && listItems.size() != 1);
        this.currentChest.setVisible(listItems != null && listItems.size() != 1 && !this.showAll);
    }

    public void setIndexContent(int index) {
        // this will call update
        this.instance.setCurrentChestIndex(index);
        this.currentChest.setText(index + 1 + "/" + (instance.getListItems() == null ? "?" : instance.getListItems().size()));
    }

    public void setNumberChest(int numberChest) {
        this.chestSelectionMenu.removeAll();
        for (int i = 0; i < numberChest; i++) {
            int currentIndex = i;
            JMenuItem menuItem = new JMenuItem("Chest " + (currentIndex + 1));
            menuItem.addMouseListener(Events.Mouse.onReleased(e -> this.setIndexContent(currentIndex)));
            this.chestSelectionMenu.add(menuItem);
        }
    }
}

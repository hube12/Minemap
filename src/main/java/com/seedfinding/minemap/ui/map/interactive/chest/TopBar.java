package com.seedfinding.minemap.ui.map.interactive.chest;

import com.seedfinding.mcfeature.loot.item.ItemStack;
import com.seedfinding.minemap.listener.Events;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TopBar extends JPanel {
    private final ChestInstance instance;
    private final ChestFrame window;
    private final JButton indexedButton;
    private final JButton showAllButton;
    private final JMenu chestSelectionMenu;
    private final JMenuBar menuBar;
    private final JLabel currentChest;
    private final JButton prevPageButton = new JButton("<");
    private final JButton nextPageButton = new JButton(">");
    private final JLabel pageLabel = new JLabel("");
    private int page = 0;
    private boolean showAll = false;
    private final static String[] indexedString = {"Spread", "Reassemble"};
    private final static String[] showString = {"Show All", "Show One"};

    public TopBar(ChestInstance instance, ChestFrame chest) {
        instance.registerUpdateable(this::update);
        this.window = chest;
        this.instance = instance;
        // spread button
        this.indexedButton = new JButton(indexedString[this.instance.isIndexed() ? 1 : 0]);
        this.indexedButton.addActionListener(e -> {
            this.instance.toggleIndexed();
            instance.generate(); // this call update for us
            this.indexedButton.setText(indexedString[this.instance.isIndexed() ? 1 : 0]);
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
            this.update(true); //important to redraw the ui with the new chests
            this.showAllButton.setText(showString[showAll ? 1 : 0]);
        });
        // center chest on the current screen
        JButton centerButton = new JButton("Center Chest");
        centerButton.addActionListener(e -> this.window.centerOnMap());
        // pin the chest window on top
        JToggleButton pinButton = new JToggleButton("Always on top");
        pinButton.addActionListener(e -> {
            this.window.setAlwaysOnTop(!this.window.isAlwaysOnTop());
            this.window.revalidate();
            this.window.repaint();
        });
        this.add(this.indexedButton);
        this.add(this.menuBar);
        this.add(this.currentChest);
        this.add(this.showAllButton);
        this.prevPageButton.addActionListener(e -> changePage(-1));
        this.nextPageButton.addActionListener(e -> changePage(1));
        this.add(this.prevPageButton);
        this.add(this.pageLabel);
        this.add(this.nextPageButton);
        this.add(centerButton);
        this.add(pinButton);
    }

    private int pageCount() {
        int n = instance.size();
        return Math.max(1, (n + ChestFrame.PAGE_SIZE - 1) / ChestFrame.PAGE_SIZE);
    }

    private void changePage(int delta) {
        int newPage = Math.clamp(page + delta, 0, pageCount() - 1);
        if (newPage != page) {
            page = newPage;
            this.update(true);
        }
    }

    private void setShowAll(boolean showAll) {
        this.showAll = showAll;
        this.chestSelectionMenu.setVisible(!this.showAll);
        this.currentChest.setVisible(!this.showAll);
    }

    public void updateLabel() {
        this.createChestMenu(instance.getListItems() == null ? 0 : instance.size());
    }

    /**
     * Update the chest content
     *
     * @param hasChanged tri state, if true then
     */
    private void update(Boolean hasChanged) {
        this.indexedButton.setText(indexedString[this.instance.isIndexed() ? 1 : 0]);
        List<List<ItemStack>> listItems = instance.getListItems();
        int total = listItems == null ? 0 : listItems.size();
        if (hasChanged && (!showAll || page >= pageCount())) page = 0;
        int start = showAll ? page * ChestFrame.PAGE_SIZE : 0;
        int shown = showAll ? Math.clamp(total - start, 1, ChestFrame.PAGE_SIZE) : 1;
        this.window.setEmptyMessage(total == 0);
        // the grid shape depends on the whole set, not on the current page, so a last page with
        // a single chest keeps the same 2x2 layout (unused cells are hidden placeholders)
        int slots = showAll ? Math.clamp(total, 1, ChestFrame.PAGE_SIZE) : 1;
        this.window.ensureCapacity(slots);
        List<ChestPanel> chestContents = this.window.getChestContents();
        if (hasChanged) {
            this.updateLabel();
            LayoutManager layoutManager = this.window.getContent().getLayout();
            if (layoutManager instanceof GridLayout gridLayout) {
                gridLayout.setColumns(slots > 1 ? 2 : 1);
            }
            this.window.resizeFor(slots > 1 ? 2 : 1, (slots + 1) / 2);
            for (int i = 1; i < chestContents.size(); i++) {
                if (i < slots) {
                    this.window.getContent().add(chestContents.get(i)); // keeps the order
                    chestContents.get(i).setVisible(i < shown);
                } else {
                    this.window.getContent().remove(chestContents.get(i));
                }
            }
        }
        if (showAll) {
            for (int i = 0; i < shown; i++) {
                chestContents.get(i).update(total == 0 ? null : listItems.get(start + i));
            }
        } else {
            chestContents.getFirst().update(total == 0 ? null : listItems.get(this.instance.getCurrentChestIndex()));
        }
        boolean paged = showAll && total > ChestFrame.PAGE_SIZE;
        this.prevPageButton.setVisible(paged);
        this.nextPageButton.setVisible(paged);
        this.pageLabel.setVisible(paged);
        this.prevPageButton.setEnabled(page > 0);
        this.nextPageButton.setEnabled(page < pageCount() - 1);
        this.pageLabel.setText(String.format("Chests %d-%d of %d", start + 1, start + shown, total));

        this.window.getContent().revalidate();
        this.window.getContent().repaint();
        this.window.getScrollPane().revalidate();
        this.showAllButton.setVisible(total > 1);
        this.menuBar.setVisible(total > 1);
        this.currentChest.setVisible(total > 1 && !this.showAll);
    }

    public void setIndexContent(int index) {
        // this will call update
        this.instance.setCurrentChestIndex(index);
        this.currentChest.setText(index + 1 + "/" + (instance.getListItems() == null ? "?" : instance.size()));
    }

    public void createChestMenu(int numberChest) {
        this.chestSelectionMenu.removeAll();
        for (int i = 0; i < numberChest; i++) {
            int currentIndex = i;
            JMenuItem menuItem = new JMenuItem("Chest " + (currentIndex + 1));
            menuItem.addMouseListener(Events.Mouse.onReleased(e -> this.setIndexContent(currentIndex)));
            this.chestSelectionMenu.add(menuItem);
        }
    }
}

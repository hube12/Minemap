package kaptainwutax.minemap.ui.map.interactive.chest;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.minemap.feature.chests.Chests;
import kaptainwutax.minemap.feature.chests.Loot;
import kaptainwutax.minemap.listener.Events;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TopBar extends JPanel {
    private final ChestInstance chestInstance;
    private final ChestFrame chest;
    private final JButton indexedButton;
    private final JButton showAllButton;
    private final JButton centerButton;
    private final JToggleButton pinButton;
    private final JMenu chestMenu;
    private final JMenuBar menuBar;
    private final JLabel currentChest;
    private int currentChestIndex;
    private int numberChest;
    private boolean indexed = false;
    private boolean showAll = false;
    private final static String[] indexedString = {"Spread", "Reassemble"};
    private final static String[] showString = {"Show All", "Show One"};
    private List<List<ItemStack>> listItems;

    public TopBar(ChestInstance chestInstance, ChestFrame chest) {
        this.chest=chest;
        this.chestInstance = chestInstance;
        // spread button
        this.indexedButton = new JButton(indexedString[indexed ? 1 : 0]);
        this.indexedButton.addActionListener(e -> {
            setIndexed(!indexed);
            generate(false);
            this.indexedButton.setText(indexedString[indexed ? 1 : 0]);
        });
        // menu to select which chest
        this.chestMenu = new JMenu("Select chest");
        this.menuBar = new JMenuBar();
        this.menuBar.add(this.chestMenu);
        // label that show x/n
        this.currentChest = new JLabel("");
        // show all chests
        this.showAllButton = new JButton(showString[showAll ? 1 : 0]);
        this.showAllButton.addActionListener(e -> {
            setShowAll(!showAll);
            update(true);
            this.showAllButton.setText(showString[showAll ? 1 : 0]);
        });
        // center chest on the current screen
        this.centerButton = new JButton("Center Chest");
        this.centerButton.addActionListener(e -> {
            GraphicsConfiguration config = this.chest.getGraphicsConfiguration();
            if (config != null) {
                GraphicsDevice currentScreen = config.getDevice();
                if (currentScreen != null) {
                    JFrame dummy = new JFrame(currentScreen.getDefaultConfiguration());
                    this.chest.setLocationRelativeTo(dummy);
                    dummy.dispose();
                    return;
                }
            }
            this.chest.setLocationRelativeTo(null);
        });
        // pin the chest window on top
        this.pinButton = new JToggleButton("Always on top");
        this.pinButton.addActionListener(e -> {
            this.chest.setAlwaysOnTop(!this.chest.isAlwaysOnTop());
            this.chest.revalidate();
            this.chest.repaint();
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
        this.chestMenu.setVisible(!this.showAll);
        this.currentChest.setVisible(!this.showAll);
    }

    private void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }

    public int getNumberChest() {
        return numberChest;
    }

    private void update() {
        this.update(false);
    }

    /**
     * Update the chest content
     *
     * @param hasChanged tri state, if true then
     */
    private void update(Boolean hasChanged) {
        List<ChestPanel> chestContents = this.chest.getChestContents();
        if (hasChanged) {
            Dimension dimension = this.chest.getPreferredSize();
            LayoutManager layoutManager = this.chest.getContent().getLayout();
            int factor = showAll && listItems != null && listItems.size() > 1 ? 2 : 1;
            if (layoutManager instanceof GridLayout) {
                GridLayout gridLayout = (GridLayout) layoutManager;
                gridLayout.setColumns(factor);
            }
            this.chest.setSize(new Dimension(dimension.width * factor, dimension.height * (showAll && listItems != null ? (listItems.size() / 2 + listItems.size() % 2) : 1)));

            for (int i = 1; i < chestContents.size(); i++) {
                if (!showAll) {
                    this.chest.getContent().remove(chestContents.get(i)); // this will not fail if the component was not there
                } else {
                    if (listItems != null && i < listItems.size()) {
                        this.chest.getContent().add(chestContents.get(i));
                    } else {
                        this.chest.getContent().remove(chestContents.get(i));
                    }
                }
            }
        }
        if (showAll) {
            for (int i = 0; i < (this.listItems == null ? 1 : listItems.size()); i++) {
                this.chest.getChestContents().get(i).update(listItems == null || listItems.size() < 1 ? null : listItems.get(i));
                this.chestInstance.getMap().rightBar.chestContent.update(listItems == null || listItems.size() < 1 ? null : listItems.get(i));
            }
        } else {
            this.chest.getChestContents().get(0).update(listItems == null || listItems.size() < 1 ? null : listItems.get(currentChestIndex));
            this.chestInstance.getMap().rightBar.chestContent.update(listItems == null || listItems.size() < 1 ? null : listItems.get(currentChestIndex));
        }

        this.chest.getContent().revalidate();
        this.chest.getContent().repaint();
        this.chest.getScrollPane().revalidate();
        this.chest.getScrollPane().revalidate();
        this.showAllButton.setVisible(this.listItems != null && listItems.size() != 1);
        this.menuBar.setVisible(this.listItems != null && listItems.size() != 1);
        this.currentChest.setVisible(this.listItems != null && listItems.size() != 1 && !this.showAll);
    }

    public void generate(boolean initial) {
        Pair<Feature<?, ?>, CPos> informations = this.chestInstance.getInformations();
        Loot.LootFactory<?> lootFactory = Chests.get(informations.getFirst().getClass());
        if (lootFactory != null) {
            listItems = lootFactory.create().getLootAt(
                informations.getSecond(),
                informations.getFirst(),
                indexed,
                this.chestInstance.getContext()
            );
        } else {
            listItems = null;
        }
        this.setNumberChest(listItems == null ? 0 : listItems.size());
        if (initial) {
            this.setIndexContent(0);
        }
        this.update(true);
    }

    public void setIndexContent(int index) {
        this.currentChestIndex = index;
        this.currentChest.setText(this.currentChestIndex + 1 + "/" + (this.listItems == null ? "?" : this.listItems.size()));
    }

    public void setNumberChest(int numberChest) {
        this.numberChest = numberChest;
        this.chestMenu.removeAll();
        for (int i = 0; i < numberChest; i++) {
            int currentIndex = i;
            JMenuItem menuItem = new JMenuItem("Chest " + (currentIndex + 1));
            menuItem.addMouseListener(Events.Mouse.onReleased(e -> {
                this.setIndexContent(currentIndex);
                this.update();
            }));
            this.chestMenu.add(menuItem);
        }
    }
}

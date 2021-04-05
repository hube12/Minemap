package kaptainwutax.minemap.ui.map.interactive;

import kaptainwutax.featureutils.loot.item.Item;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.minemap.feature.chests.Chests;
import kaptainwutax.minemap.feature.chests.Loot;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.util.data.Str;
import kaptainwutax.seedutils.mc.pos.CPos;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Chest extends JFrame {
    private CPos pos;
    private RegionStructure<?, ?> feature;
    private final Content content;
    private final TopBar topBar;

    public Chest(MapPanel map) {
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);
        content = new Content(map);
        topBar = new TopBar(content);
        this.add(content);
        this.setSize(this.getSize());
        this.setLocationRelativeTo(null); // center
        this.setVisible(false);
        this.setIconImage(Icons.get(this.getClass()));
    }

    @Override
    public Dimension getSize() {
        return this.getPreferredSize();
    }

    @Override
    public String getName() {
        return "Chest Content";
    }

    @Override
    public int getDefaultCloseOperation() {
        return JFrame.HIDE_ON_CLOSE;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(700, 300);
    }

    public void setFeature(RegionStructure<?, ?> feature) {
        this.feature = feature;
        this.setTitle(this.getName()+" of "+ Arrays.stream(this.feature.getName().split("_")).map(Str::capitalize).collect(Collectors.joining(" ")));
    }

    public void setPos(CPos pos) {
        this.pos = pos;
    }

    public void updateContent() {
        int numberChest = this.content.update(feature, pos);
        this.topBar.setNumberChest(numberChest);
    }

    @Override
    public void repaint() {
        super.repaint();
    }

    public static class TopBar extends JPanel {
        private int numberChest;
        private final Content content;

        public TopBar(Content content) {
            this.content = content;
        }

        private void setIndexContent(int index) {
            this.content.setIndex(index);
        }

        public void setNumberChest(int numberChest) {
            this.numberChest = numberChest;
        }
    }

    public static class Content extends JPanel {
        private static final int ROW_NUMBER = 3;
        private static final int COL_NUMBER = 9;
        private final java.util.List<java.util.List<JButton>> list;
        private final MapPanel mapPanel;
        private int index = 0;

        public Content(MapPanel mapPanel) {
            this.setLayout(new GridLayout(ROW_NUMBER, COL_NUMBER));
            this.list = new ArrayList<>();
            for (int row = 0; row < ROW_NUMBER; row++) {
                List<JButton> temp = new ArrayList<>();
                for (int col = 0; col < COL_NUMBER; col++) {
                    JButton button = new JButton("");
                    temp.add(button);
                    this.add(button);
                }
                list.add(temp);
            }
            this.mapPanel = mapPanel;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public int update(RegionStructure<?, ?> feature, CPos pos) {
            this.clean();
            Loot.LootFactory<?> lootFactory = Chests.get(feature.getClass());
            if (lootFactory != null) {
                Loot loot = lootFactory.create();
                List<List<ItemStack>> listItems = loot.getLootAt(this.mapPanel.context.worldSeed, pos, this.mapPanel.context.version);
                Iterator<ItemStack> currentIterator = listItems.get(index).iterator();
                for (int row = 0; row < ROW_NUMBER; row++) {
                    List<JButton> rowButton = this.list.get(row);
                    for (int col = 0; col < COL_NUMBER; col++) {
                        if (!currentIterator.hasNext()) break;
                        Item item=currentIterator.next().getItem();
                        BufferedImage icon=Icons.getItem(item);
                        JButton current=rowButton.get(col);
                        current.setMargin(new Insets(0, 0, 0, 0));
                        if (icon==null) {
                            current.setText(String.join(" ",item.getName().split("_")));
                        }
                        else {
                            ImageIcon imageIcon=new ImageIcon(icon);
                            imageIcon.setImage(imageIcon.getImage().getScaledInstance(current.getWidth(),current.getWidth(), Image.SCALE_SMOOTH));
                            current.setIcon(imageIcon);
                        }

                    }
                }
                return listItems.size();
            } else {
                for (int row = 0; row < ROW_NUMBER; row++) {
                    List<JButton> rowButton = this.list.get(row);
                    for (int col = 0; col < COL_NUMBER; col++) {
                        rowButton.get(col).setText("U");
                    }
                }
            }
            return 0;
        }

        public void clean(){
            for (int row = 0; row < ROW_NUMBER; row++) {
                List<JButton> rowButton = this.list.get(row);
                for (int col = 0; col < COL_NUMBER; col++) {
                    rowButton.get(col).setText("");
                    rowButton.get(col).setIcon(null);
                }
            }
        }

        @Override
        public void paint(Graphics g) {
            // this is a trick to have a background
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRect(0, 0, this.getWidth(), this.getHeight());
            g2d.dispose();
            // only paint the stuff atop after
            super.paint(g);
        }
    }

}

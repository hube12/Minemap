package kaptainwutax.minemap.ui.map.interactive;


import kaptainwutax.featureutils.loot.effect.Effect;
import kaptainwutax.featureutils.loot.item.Item;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.featureutils.loot.item.Items;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.minemap.feature.chests.Chests;
import kaptainwutax.minemap.feature.chests.Loot;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.init.Logger;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.util.data.Str;
import kaptainwutax.minemap.util.ui.Graphic;
import org.jdesktop.swingx.image.ColorTintFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static kaptainwutax.minemap.util.data.Str.prettifyDashed;

public class Chest extends JFrame {
    private final JPanel content;
    private final List<ChestContent> chestContents = new ArrayList<>();
    private final TopBar topBar;
    private final MapPanel map;
    private final static int MAX_NUMBER_CHESTS = 4; //TODO find the max we will support
    private CPos pos;
    private RegionStructure<?, ?> feature;

    public Chest(MapPanel map) {
        this.map = map;
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);
        GridLayout gridLayout=new GridLayout(-1,2,15,15);
        content = new JPanel();
        content.setLayout(gridLayout);
        for (int i = 0; i < MAX_NUMBER_CHESTS; i++) {
            chestContents.add(new ChestContent());
        }
        content.add(chestContents.get(0));
        topBar = new TopBar(this);
        this.add(topBar, BorderLayout.NORTH);
        JScrollPane scrollPane=new JScrollPane(content);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setSize(this.getPreferredSize());
        this.setLocationRelativeTo(null); // center
        this.setVisible(false);
        this.setIconImage(Icons.get(this.getClass()));
    }

    public MapPanel getMap() {
        return map;
    }

    public MapContext getContext() {
        return map.getContext();
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
        return new Dimension(700, 330);
    }

    public void setFeature(RegionStructure<?, ?> feature) {
        this.feature = feature;
    }

    public void setPos(CPos pos) {
        this.pos = pos;
    }

    public void generateContent() {
        this.generateContent(false);
    }

    public List<ChestContent> getChestContents() {
        return chestContents;
    }

    public JPanel getContent() {
        return content;
    }

    public Pair<RegionStructure<?, ?>, CPos> getInformations() {
        return new Pair<>(this.feature, this.pos);
    }

    public void generateContent(boolean indexed) {
        this.setTitle(String.format("%s of %s at x:%d z:%d", this.getName(), prettifyDashed(this.feature.getName()), this.pos.getX() * 16 + 9, this.pos.getZ() * 16 + 9));
        this.topBar.setIndexed(indexed);
        this.topBar.setIndexContent(0);
        this.topBar.generate(true);
    }

    @Override
    public void repaint() {
        super.repaint();
    }

    public static class TopBar extends JPanel {
        private final Chest chest;
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
        private final static String[] indexedString={"Spread","Reassemble"};
        private final static String[] showString={"Show All","Show One"};
        private List<List<ItemStack>> listItems;

        public TopBar(Chest chest) {
            this.chest = chest;
            this.indexedButton = new JButton(indexedString[indexed?1:0]);
            this.indexedButton.addActionListener(e -> {
                setIndexed(!indexed);
                generate(false);
                this.indexedButton.setText(indexedString[indexed?1:0]);
            });
            this.showAllButton = new JButton(showString[showAll?1:0]);
            this.showAllButton.addActionListener(e -> {
                setShowAll(!showAll);
                update(true);
                this.showAllButton.setText(showString[showAll?1:0]);
            });
            this.centerButton=new JButton("Center Chest");
            this.centerButton.addActionListener(e -> {
                this.chest.setLocationRelativeTo(null);
                this.chest.revalidate();
                this.chest.repaint();
            });
            this.pinButton=new JToggleButton("Always on top");
            this.pinButton.addActionListener(e -> {
                this.chest.setAlwaysOnTop(!this.chest.isAlwaysOnTop());
                this.chest.revalidate();
                this.chest.repaint();
            });
            this.chestMenu = new JMenu("Select chest");
            this.menuBar = new JMenuBar();
            this.menuBar.add(this.chestMenu);
            this.currentChest = new JLabel("");
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
         * @param hasChanged tri state, if true then
         */
        private void update(Boolean hasChanged) {
            List<ChestContent> chestContents = this.chest.getChestContents();
            if (hasChanged) {
                Dimension dimension=this.chest.getPreferredSize();
                LayoutManager layoutManager=this.chest.getContent().getLayout();
                int factor=showAll && listItems.size()>1?2:1;
                if (layoutManager instanceof GridLayout){
                    GridLayout gridLayout=(GridLayout) layoutManager;
                    gridLayout.setColumns(factor);
                }
                this.chest.setSize(new Dimension(dimension.width*factor,dimension.height*(showAll?(listItems.size()/2+listItems.size()%2):1)));

                for (int i = 1; i < chestContents.size(); i++) {
                    if (!showAll) {
                        this.chest.getContent().remove(chestContents.get(i)); // this will not fail if the component was not there
                    } else {
                        if (i<listItems.size()){
                            this.chest.getContent().add(chestContents.get(i));
                        }else{
                            this.chest.getContent().remove(chestContents.get(i));
                        }
                    }
                }
            }
            if (showAll) {
                for (int i = 0; i < listItems.size(); i++) {
                    this.chest.getChestContents().get(i).update(listItems == null || listItems.size() < 1 ? null : listItems.get(i));
                }
            }else{
                this.chest.getChestContents().get(0).update(listItems == null || listItems.size() < 1 ? null : listItems.get(currentChestIndex));
            }
            this.chest.getContent().revalidate();
            this.chest.getContent().repaint();
        }

        private void generate(boolean initial) {
            Pair<RegionStructure<?, ?>, CPos> informations = this.chest.getInformations();
            Loot.LootFactory<?> lootFactory = Chests.get(informations.getFirst().getClass());
            if (lootFactory != null) {
                listItems = lootFactory.create().getLootAt(
                    informations.getSecond(),
                    informations.getFirst(),
                    indexed,
                    this.chest.getContext()
                );
            } else {
                listItems = null;
            }
            this.setNumberChest(listItems == null ? 0 : listItems.size());
            if (initial){
                this.setIndexContent(0);
            }
            this.update(true);
        }

        private void setIndexContent(int index) {
            this.currentChestIndex = index;
            this.currentChest.setText("Viewing chest " + this.currentChestIndex);
        }

        public void setNumberChest(int numberChest) {
            this.numberChest = numberChest;
            this.chestMenu.removeAll();
            for (int i = 0; i < numberChest; i++) {
                int currentIndex = i;
                JMenuItem menuItem = new JMenuItem("Chest " + currentIndex);
                menuItem.addMouseListener(Events.Mouse.onReleased(e -> {
                    this.setIndexContent(currentIndex);
                    this.update();
                }));
                this.chestMenu.add(menuItem);
            }
        }
    }

    public static class ChestContent extends JPanel {
        private static final int ROW_NUMBER = 3;
        private static final int COL_NUMBER = 9;
        private final List<List<JButton>> list;

        public ChestContent() {
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
        }

        public void update(List<ItemStack> itemsList) {
            this.clean();
            if (itemsList == null) {
                createEmptyChest();
            } else {
                createFilledChest(itemsList.iterator());
            }
            this.repaint();
        }

        public void createEmptyChest() {
            for (int row = 0; row < ROW_NUMBER; row++) {
                List<JButton> rowButton = this.list.get(row);
                for (int col = 0; col < COL_NUMBER; col++) {
                    rowButton.get(col).setText("U");
                }
            }
        }

        public void createFilledChest(Iterator<ItemStack> currentIterator) {
            this.clean();
            for (int row = 0; row < ROW_NUMBER; row++) {
                List<JButton> rowButton = this.list.get(row);
                for (int col = 0; col < COL_NUMBER; col++) {
                    if (!currentIterator.hasNext()) break;
                    ItemStack itemStack = currentIterator.next();
                    if (itemStack.isEmpty()) continue;
                    Item item = itemStack.getItem();
                    boolean shouldShine = item.getName().startsWith("enchanted_") || !item.getEnchantments().isEmpty() || !item.getEffects().isEmpty();
                    boolean isPlate = item.getName().endsWith("_plate");
                    BufferedImage icon = Icons.getObject(item);
                    JButton current = rowButton.get(col);
                    current.setMargin(new Insets(0, 0, 0, 0));
                    FontMetrics fontMetrics = current.getFontMetrics(current.getFont());
                    String enchantmentToolTip = getToolTipString(
                        item.getEnchantments().iterator(),
                        enchantment -> new Pair<>(
                            String.format("<font color=%s>%s</font>",
                                enchantment.getFirst().contains("curse") ? "red" : "#4c66be",
                                Str.prettifyDashed(enchantment.getFirst())
                            ),
                            Str.toRomanNumeral(enchantment.getSecond()).replaceFirst("^I$", "")
                        ),
                        fontMetrics
                    );
                    String effectTooltip = getToolTipString(
                        item.getEffects().iterator(),
                        effect -> new Pair<>(
                            String.format("<font color=%s>%s</font>",
                                effect.getFirst().getCategory() == Effect.EffectType.BENEFICIAL ? "green" :
                                    effect.getFirst().getCategory() == Effect.EffectType.NEUTRAL ? "white" : "red",
//                                effect.getFirst().getColor(),
                                Str.prettifyDashed(effect.getFirst().getDescription())
                            ),

                            (!effect.getFirst().isInstantenous() ? (effect.getSecond()) / 20 + "s" : effect.getSecond().toString())
                        ),
                        fontMetrics
                    );
                    // only set one of them (by default it's null if none)
                    current.setToolTipText(enchantmentToolTip != null ? enchantmentToolTip : effectTooltip);
                    if (icon == null) {
                        current.setText("<html>" + Str.prettifyDashed(item.getName()) + "<br>" + itemStack.getCount() + "</html>");
                    } else {
                        int w = icon.getWidth();
                        int h = icon.getHeight();
                        double scaleFactor = 64.0 / Math.max(w, h);
                        BufferedImage scaledIcon = new BufferedImage((int) (w * scaleFactor), (int) (h * scaleFactor), BufferedImage.TYPE_INT_ARGB);
                        // scale icon
                        AffineTransform at = new AffineTransform();
                        at.scale(scaleFactor, scaleFactor);
                        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                        scaledIcon = scaleOp.filter(icon, scaledIcon);
                        // set hints
                        Graphics2D g2d = Graphic.setGoodRendering(Graphic.withoutDithering(scaledIcon.getGraphics()));
                        // add leather
                        doLeatherOverlay(item, w, h, scaleFactor, scaledIcon, g2d, scaleOp);
                        if (isPlate) {
                            g2d.setColor(Color.DARK_GRAY);
                            g2d.setStroke(new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
                            g2d.drawRect(0, 0, scaledIcon.getWidth(), scaledIcon.getHeight());
                        }
                        if (item.getName().equals(Items.FILLED_MAP.getName())) {
                            ColorTintFilter colorTintFilter = new ColorTintFilter(Color.BLUE, 0.4f);
                            colorTintFilter.filter(scaledIcon, scaledIcon);
                        } else if (shouldShine) {
                            ColorTintFilter colorTintFilter = new ColorTintFilter(Color.PINK, 0.4f);
                            colorTintFilter.filter(scaledIcon, scaledIcon);
                        }
                        // add the item count
                        drawCount(g2d, itemStack);
                        current.setIcon(new ImageIcon(scaledIcon));
                    }
                }
            }
        }

        public void clean() {
            for (int row = 0; row < ROW_NUMBER; row++) {
                List<JButton> rowButton = this.list.get(row);
                for (int col = 0; col < COL_NUMBER; col++) {
                    rowButton.get(col).setText("");
                    rowButton.get(col).setToolTipText(null);
                    rowButton.get(col).setIcon(null);

                }
            }
        }

        public static void doLeatherOverlay(Item item, int w, int h, double scaleFactor, BufferedImage scaledIcon, Graphics2D g2d, AffineTransformOp scaleOp) {
            if (item.getName().startsWith("leather_")) {
                BufferedImage overlay = null;
                String[] overlayName = item.getName().split("_");
                switch (overlayName[overlayName.length - 1]) {
                    case "boots":
                        overlay = Icons.getObject(Icons.LEATHER_BOOTS_OVERLAY);
                        break;
                    case "leggings":
                        overlay = Icons.getObject(Icons.LEATHER_LEGGINGS_OVERLAY);
                        break;
                    case "chestplate":
                        overlay = Icons.getObject(Icons.LEATHER_CHESTPLATE_OVERLAY);
                        break;
                    case "helmet":
                        overlay = Icons.getObject(Icons.LEATHER_HELMET_OVERLAY);
                        break;
                }
                if (overlay != null) {
                    BufferedImage scaledOverlay = new BufferedImage((int) (w * scaleFactor), (int) (h * scaleFactor), BufferedImage.TYPE_INT_ARGB);
                    scaledOverlay = scaleOp.filter(overlay, scaledOverlay);
                    g2d.drawImage(scaledOverlay, 0, 0, scaledIcon.getWidth(), scaledIcon.getHeight(), null);
                } else {
                    Logger.LOGGER.warning("Missing overlay for " + item.getName());
                }
            }
        }

        public static void drawCount(Graphics2D g2d, ItemStack itemStack) {
            if (itemStack.getCount() > 1) {
                g2d.setColor(Color.GRAY);
                g2d.setStroke(new BasicStroke(2));
                g2d.fillOval(40, 40, 20, 20);
                char[] charArray = Integer.toString(itemStack.getCount()).toCharArray();
                g2d.setColor(Color.WHITE);
                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD));
                g2d.drawChars(charArray, 0, charArray.length, charArray.length == 1 ? 47 : 43, 55);
            }
        }

        public static <T> String getToolTipString(Iterator<Pair<T, Integer>> properties, Function<Pair<T, Integer>, Pair<String, String>> display, FontMetrics fontMetrics) {
            if (properties.hasNext()) {
                StringBuilder sb = new StringBuilder("<html>");
                while (properties.hasNext()) {
                    Pair<T, Integer> property = properties.next();
                    Pair<String, String> sentence = display.apply(property);
                    if (sentence.getFirst() == null) continue;
                    sb.append("<p style=\"text-align:center\" width=\"")
//                        .append(fontMetrics.stringWidth(sentence.getFirst())+5)
                        .append(100)
                        .append("pt\">").append(sentence.getFirst());
                    if (sentence.getSecond() != null) {
                        sb.append(" ").append(sentence.getSecond());
                    }
                    sb.append("</p>");
                    if (properties.hasNext()) sb.append("<br>");
                }
                return sb.toString();
            }
            return null;
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

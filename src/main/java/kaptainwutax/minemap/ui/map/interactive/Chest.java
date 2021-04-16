package kaptainwutax.minemap.ui.map.interactive;


import kaptainwutax.featureutils.loot.effect.Effect;
import kaptainwutax.featureutils.loot.item.Item;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.minemap.feature.chests.Chests;
import kaptainwutax.minemap.feature.chests.Loot;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.util.data.Str;
import kaptainwutax.seedutils.mc.pos.CPos;
import kaptainwutax.seedutils.util.Pair;
import org.jdesktop.swingx.image.ColorTintFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static kaptainwutax.minemap.util.data.Str.prettifyDashed;

public class Chest extends JFrame {
    private CPos pos;
    private RegionStructure<?, ?> feature;
    private final Content content;
    private final TopBar topBar;

    public Chest(MapPanel map) {
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);
        content = new Content(map);
        topBar = new TopBar(content,this);
        this.add(topBar,BorderLayout.NORTH);
        this.add(content,BorderLayout.CENTER);
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
    }

    public void setPos(CPos pos) {
        this.pos = pos;
    }

    public void updateContent() {
        this.updateContent(false);
    }
    public void updateContent(boolean indexed) {
        this.setTitle(String.format("%s of %s at x:%d z:%d", this.getName(), prettifyDashed(this.feature.getName()), this.pos.getX() * 16 + 9, this.pos.getZ() * 16 + 9));
        int numberChest = this.content.update(feature, pos,indexed);
        this.topBar.setNumberChest(numberChest);
    }

    @Override
    public void repaint() {
        super.repaint();
    }

    public static class TopBar extends JPanel {
        private int numberChest;
        private final Content content;
        private final Chest chest;
        private final JButton indexedButton;
        private boolean indexed=false;

        public TopBar(Content content,Chest chest) {
            this.content = content;
            this.chest=chest;
            this.indexedButton =new JButton("Spread");
            this.indexedButton.addActionListener(e->{
                setIndexed(!indexed);
            });
            this.add(this.indexedButton);
        }

        private void setIndexed(boolean indexed){
            this.indexed=indexed;
            this.chest.updateContent(indexed);
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

        public int update(RegionStructure<?, ?> feature, CPos pos,boolean indexed) {
            this.clean();
            Loot.LootFactory<?> lootFactory = Chests.get(feature.getClass());
            if (lootFactory != null) {
                Loot loot = lootFactory.create();
                List<List<ItemStack>> listItems = loot.getLootAt(this.mapPanel.context.worldSeed, pos, feature, indexed,this.mapPanel.context.version);
                if (listItems != null) {
                    Iterator<ItemStack> currentIterator = listItems.get(index).iterator();
                    for (int row = 0; row < ROW_NUMBER; row++) {
                        List<JButton> rowButton = this.list.get(row);
                        for (int col = 0; col < COL_NUMBER; col++) {
                            if (!currentIterator.hasNext()) break;
                            ItemStack itemStack = currentIterator.next();
                            if (itemStack.isEmpty()) continue;
                            Item item = itemStack.getItem();
                            boolean shouldShine = item.getName().startsWith("enchanted_") || !item.getEnchantment().isEmpty() || !item.getEffects().isEmpty();
                            boolean isPlate = item.getName().endsWith("_plate");
                            BufferedImage icon = Icons.getObject(item);
                            JButton current = rowButton.get(col);
                            current.setMargin(new Insets(0, 0, 0, 0));
                            if (!item.getEnchantment().isEmpty()) {
                                StringBuilder sb = new StringBuilder("<html>");
                                ArrayList<String> enchantments = item.getEnchantment();
                                ArrayList<Integer> levels = item.getLevel();
                                for (int idx = 0; idx < item.getEnchantment().size(); idx++) {
                                    sb.append(Str.capitalize(enchantments.get(idx))).append(" ").append(Str.toRomanNumeral(levels.get(idx))).append("<br>");
                                }
                                sb.append("</html>");
                                current.setToolTipText(sb.toString());
                            }
                            if (!item.getEffects().isEmpty()){
                                StringBuilder sb = new StringBuilder("<html>");
                                ArrayList<Pair<Effect, Integer>> effects = item.getEffects();
                                for (Pair<Effect, Integer> effect:effects){
                                    sb.append(effect.getFirst().getDescription())
                                            .append(" ")
                                            .append((!effect.getFirst().isInstantenous() ? effect.getSecond() / 20 : effect.getSecond().toString()));
                                }
                                sb.append("</html>");
                                current.setToolTipText(sb.toString());
                            }
                            if (icon == null) {
                                current.setText("<html>" + Str.prettifyDashed(item.getName()) + "<br>" + itemStack.getCount() + "</html>");
                            } else {
                                int w = icon.getWidth();
                                int h = icon.getHeight();
                                double scaleFactor = 64.0 / Math.max(w, h);

                                BufferedImage scaledIcon = new BufferedImage((int) (w * scaleFactor), (int) (h * scaleFactor), BufferedImage.TYPE_INT_ARGB);
                                AffineTransform at = new AffineTransform();
                                at.scale(scaleFactor, scaleFactor);
                                AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                                scaledIcon = scaleOp.filter(icon, scaledIcon);
                                Graphics2D g2d = (Graphics2D) scaledIcon.getGraphics();
                                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                                g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                                g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                                g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
                                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                                g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                                g2d.setColor(Color.GRAY);
                                g2d.setStroke(new BasicStroke(2));
                                g2d.fillOval(40, 40, 20, 20);
                                char[] charArray = Integer.toString(itemStack.getCount()).toCharArray();
                                g2d.setColor(Color.WHITE);
                                g2d.setFont(g2d.getFont().deriveFont(Font.BOLD));
                                g2d.drawChars(charArray, 0, charArray.length, charArray.length == 1 ? 47 : 43, 55);
                                if (shouldShine) {
                                    ColorTintFilter colorTintFilter = new ColorTintFilter(Color.PINK, 0.4f);
                                    colorTintFilter.filter(scaledIcon, scaledIcon);
                                }
                                if (isPlate) {
                                    g2d.setColor(Color.DARK_GRAY);
                                    g2d.setStroke(new BasicStroke(7, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
                                    g2d.drawRect(0, 0, scaledIcon.getWidth(), scaledIcon.getHeight());
                                }
                                current.setIcon(new ImageIcon(scaledIcon));

                                //current.addMouseListener(Events.Mouse.onEntered(e->current.getto));


                            }

                        }
                    }
                    return listItems.size();
                }
            }
            for (int row = 0; row < ROW_NUMBER; row++) {
                List<JButton> rowButton = this.list.get(row);
                for (int col = 0; col < COL_NUMBER; col++) {
                    rowButton.get(col).setText("U");
                }
            }
            return 0;
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

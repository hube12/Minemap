package com.seedfinding.minemap.ui.map.interactive.chest;

import com.seedfinding.mcfeature.loot.effect.Effect;
import com.seedfinding.mcfeature.loot.item.Item;
import com.seedfinding.mcfeature.loot.item.ItemStack;
import com.seedfinding.mcfeature.loot.item.Items;
import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.util.ui.graphics.Graphic;
import com.seedfinding.minemap.init.Icons;
import com.seedfinding.minemap.init.Logger;
import com.seedfinding.minemap.util.data.Str;
import com.seedfinding.minemap.util.ui.buttons.UnknownButton;
import org.jdesktop.swingx.image.ColorTintFilter;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static com.seedfinding.minemap.ui.map.interactive.chest.ChestFrame.CHEST_HEIGHT;
import static com.seedfinding.minemap.ui.map.interactive.chest.ChestFrame.CHEST_WIDTH;

public class ChestPanel extends JPanel {
    private static final int ROW_NUMBER = 3;
    private static final int COL_NUMBER = 9;
    private final List<List<JButton>> list;

    public ChestPanel(Pair<Double, Double> scales) {
        this.setLayout(new GridLayout(ROW_NUMBER, COL_NUMBER));
        this.list = new ArrayList<>();
        for (int row = 0; row < ROW_NUMBER; row++) {
            List<JButton> temp = new ArrayList<>();
            for (int col = 0; col < COL_NUMBER; col++) {
                JButton button = new JButton("");
                button.setPreferredSize(new Dimension((int) (CHEST_WIDTH / COL_NUMBER * scales.getFirst()), (int) (CHEST_HEIGHT / ROW_NUMBER * scales.getSecond())));
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
        BufferedImage icon = Icons.get(UnknownButton.class);
        assert icon != null;
        int w = icon.getWidth();
        int h = icon.getHeight();
        double iconSize = 64.0;
        double scaleFactor = iconSize / Math.max(w, h);
        // scale icon
        BufferedImage scaledIcon = new BufferedImage((int) (w * scaleFactor), (int) (h * scaleFactor), BufferedImage.TYPE_INT_ARGB);
        AffineTransform at = new AffineTransform();
        at.scale(scaleFactor, scaleFactor);
        AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
        scaledIcon = scaleOp.filter(icon, scaledIcon);
        this.list.get(1).get(4).setIcon(new ImageIcon(scaledIcon));
    }

    public void createFilledChest(Iterator<ItemStack> currentIterator) {
        for (int row = 0; row < ROW_NUMBER; row++) {
            List<JButton> rowButton = this.list.get(row);
            for (int col = 0; col < COL_NUMBER; col++) {
                if (!currentIterator.hasNext()) break;
                ItemStack itemStack = currentIterator.next();
                if (itemStack.isEmpty()) continue;
                Item item = itemStack.getItem();
                BufferedImage icon = Icons.getObject(item);
                String information = Icons.getObjectInformation(item);

                JButton current = rowButton.get(col);
                current.setMargin(new Insets(0, 0, 0, 0));
                FontMetrics fontMetrics = current.getFontMetrics(current.getFont());

                String[] toIgnoreFirstEnchantment = new String[] {"aqua_affinity", "binding_curse", "flame", "infinity", "silk_touch", "mending", "vanishing_curse", "channeling", "multishot"};
                String enchantmentToolTip = getToolTipString(
                    item.getEnchantments().iterator(),
                    enchantment -> new Pair<>(
                        String.format("<font color=%s>%s</font>",
                            enchantment.getFirst().contains("curse") ? "red" : MineMap.isDarkTheme() ? "#7bf7e6" : "#0850d6",
                            Str.prettifyDashed(enchantment.getFirst())
                        ),
                        String.format("<font color=%s>%s</font>",
                            MineMap.isDarkTheme() ? "#fcf955" : "#f21509",
                            Arrays.stream(toIgnoreFirstEnchantment).anyMatch(e -> e.equals(enchantment.getFirst())) ?
                                Str.toRomanNumeral(enchantment.getSecond()).replaceFirst("^I$", "") :
                                Str.toRomanNumeral(enchantment.getSecond())
                        )
                    ),
                    fontMetrics
                );

                String effectTooltip = getToolTipString(
                    item.getEffects().iterator(),
                    effect -> new Pair<>(
                        String.format("<font color=%s>%s</font>",
                            effect.getFirst().getCategory() == Effect.EffectType.BENEFICIAL ? "green" :
                                effect.getFirst().getCategory() == Effect.EffectType.NEUTRAL ? (MineMap.isDarkTheme() ? "white" : "black") : "red",
                            Str.prettifyDashed(effect.getFirst().getDescription())
                        ),

                        (!effect.getFirst().isInstantenous() ? (effect.getSecond()) / 20 + "s" : effect.getSecond().toString())
                    ),
                    fontMetrics
                );

                // set the tool tip text as ItemName\nEnchantments\nEffects
                StringBuilder toolTipSb = new StringBuilder("<html>");
                toolTipSb.append("<p style=\"text-align:center;color:").append(MineMap.isDarkTheme() ? "white" : "black").append("\">")
                    .append(Str.prettifyDashed(item.getName()))
                    .append("</p>");
                if (enchantmentToolTip != null) toolTipSb.append(enchantmentToolTip);
                if (effectTooltip != null) toolTipSb.append(effectTooltip);
                current.setToolTipText(toolTipSb.append("</html>").toString());

                if (icon == null || information == null) {
                    current.setText("<html>" + Str.prettifyDashed(item.getName()) + "<br>" + itemStack.getCount() + "</html>");
                } else {
                    boolean shouldShine = item.getName().startsWith("enchanted_") || !item.getEnchantments().isEmpty() || !item.getEffects().isEmpty();
                    boolean isPlate = item.getName().endsWith("_plate"); // same thing as block as most use the top texture
                    boolean isBlock = information.contains("block");

                    int w = icon.getWidth();
                    int h = icon.getHeight();
                    int offset = isBlock ? 8 : 0;
                    double iconSize = 64.0;
                    double scaleFactor = iconSize / Math.max(w, h);
                    BufferedImage background = new BufferedImage((int) (w * scaleFactor), (int) (h * scaleFactor), BufferedImage.TYPE_INT_ARGB);
                    // scale icon
                    BufferedImage scaledIcon = new BufferedImage((int) (w * scaleFactor), (int) (h * scaleFactor), BufferedImage.TYPE_INT_ARGB);
                    AffineTransform at = new AffineTransform();
                    at.scale(scaleFactor, scaleFactor);
                    AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
                    scaledIcon = scaleOp.filter(icon, scaledIcon);
                    // set hints
                    Graphics2D g2d = Graphic.setGoodRendering(Graphic.withoutDithering(background.getGraphics()));
                    if (isPlate) {
                        g2d.rotate(Math.PI / 8, background.getWidth() / 2.0, background.getHeight() / 2.0);
                    }
                    // write image to the background
                    g2d.drawImage(scaledIcon, offset, offset, (int) iconSize - offset * 2, (int) iconSize - offset * 2, null);
                    // add a border around the block
                    if (isBlock) {
                        g2d.setColor(isPlate ? Color.LIGHT_GRAY : Color.BLACK);
                        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
                        g2d.drawRect(offset, offset, background.getWidth() - offset * 2, background.getHeight() - offset * 2);
                    }
                    if (isPlate) {
                        g2d.rotate(-Math.PI / 8, background.getWidth() / 2.0, background.getHeight() / 2.0);
                    }
                    // add leather
                    doLeatherOverlay(item, w, h, scaleFactor, background, g2d, scaleOp);
                    if (item.getName().equals(Items.FILLED_MAP.getName())) {
                        ColorTintFilter colorTintFilter = new ColorTintFilter(Color.BLUE, 0.4f);
                        colorTintFilter.filter(background, background);
                    } else if (shouldShine) {
                        ColorTintFilter colorTintFilter = new ColorTintFilter(Color.PINK, 0.4f);
                        colorTintFilter.filter(background, background);
                    }
                    // add the item count
                    drawCount(g2d, itemStack);
                    current.setIcon(new ImageIcon(background));
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
            StringBuilder sb = new StringBuilder();
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


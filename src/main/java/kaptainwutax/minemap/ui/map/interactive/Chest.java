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
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.util.data.Str;
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
import static kaptainwutax.minemap.util.ui.graphics.setGoodRendering;

public class Chest extends JFrame {
	private final Content content;
	private final TopBar topBar;
	private CPos pos;
	private RegionStructure<?, ?> feature;

	public Chest(MapPanel map) {
		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);
		content = new Content(map);
		topBar = new TopBar(this);
		this.add(topBar, BorderLayout.NORTH);
		this.add(content, BorderLayout.CENTER);
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
		return new Dimension(700, 350);
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

	public Content getContent() {
		return content;
	}

	public void update() {
		this.topBar.update();
	}

	public Pair<RegionStructure<?, ?>, CPos> getInformations() {
		return new Pair<>(this.feature, this.pos);
	}

	public void updateContent(boolean indexed) {
		this.setTitle(String.format("%s of %s at x:%d z:%d", this.getName(), prettifyDashed(this.feature.getName()), this.pos.getX() * 16 + 9, this.pos.getZ() * 16 + 9));
		this.topBar.setIndexed(indexed);
		this.topBar.setIndexContent(0);
		this.update();
	}

	@Override
	public void repaint() {
		super.repaint();
	}

	public static class TopBar extends JPanel {
		private final Chest chest;
		private final JButton indexedButton;
		private final JMenu chestMenu;
		private final JMenuBar menuBar;
		private final JLabel currentChest;
		private int numberChest;
		private boolean indexed = false;

		public TopBar(Chest chest) {
			this.chest = chest;
			this.indexedButton = new JButton("Spread");
			this.indexedButton.addActionListener(e -> {
				setIndexed(!indexed);
				update();
			});
			this.chestMenu = new JMenu("Select chest");
			this.menuBar = new JMenuBar();
			this.menuBar.add(this.chestMenu);
			this.currentChest = new JLabel("Viewing chest " + this.chest.getContent().index);
			this.add(this.indexedButton);
			this.add(this.menuBar);
			this.add(this.currentChest);
		}

		private void setIndexed(boolean indexed) {
			this.indexed = indexed;
		}

		private void update() {
			Pair<RegionStructure<?, ?>, CPos> informations = this.chest.getInformations();
			int numberChests = this.chest.getContent().update(informations.getFirst(), informations.getSecond(), indexed);
			this.setNumberChest(numberChests);
		}

		private void setIndexContent(int index) {
			this.chest.getContent().setIndex(index);
			this.currentChest.setText("Viewing chest " + this.chest.getContent().index);
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

		public int update(RegionStructure<?, ?> feature, CPos pos, boolean indexed) {
			this.clean();
			Loot.LootFactory<?> lootFactory = Chests.get(feature.getClass());
			if (lootFactory != null) {
				Loot loot = lootFactory.create();
				List<List<ItemStack>> listItems = loot.getLootAt(this.mapPanel.context.worldSeed, pos, feature, indexed, this.mapPanel.context.getBiomeSource(), this.mapPanel.context.version);
				if (listItems != null) {
					Iterator<ItemStack> currentIterator = listItems.get(index).iterator();
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
							if (!item.getEnchantments().isEmpty()) {
								StringBuilder sb = new StringBuilder("<html>");
								for (Pair<String, Integer> enchantment : item.getEnchantments()) {
									sb.append(Str.capitalize(enchantment.getFirst())).append(" ").append(Str.toRomanNumeral(enchantment.getSecond())).append("<br>");
								}
								sb.append("</html>");
								current.setToolTipText(sb.toString());
							}
							if (!item.getEffects().isEmpty()) {
								StringBuilder sb = new StringBuilder("<html>");
								ArrayList<Pair<Effect, Integer>> effects = item.getEffects();
								for (Pair<Effect, Integer> effect : effects) {
									sb.append(effect.getFirst().getDescription())
											.append(" ")
											.append((!effect.getFirst().isInstantenous() ? (effect.getSecond()) / 20 +"s": effect.getSecond().toString()));
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
								// scale icon
								AffineTransform at = new AffineTransform();
								at.scale(scaleFactor, scaleFactor);
								AffineTransformOp scaleOp = new AffineTransformOp(at, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
								scaledIcon = scaleOp.filter(icon, scaledIcon);
								// set hints
								Graphics2D g2d = setGoodRendering(scaledIcon.getGraphics());
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
					return listItems.size();
				}
			}
			for (int row = 0; row < ROW_NUMBER; row++) {
				List<JButton> rowButton = this.list.get(row);
				for (int col = 0; col < COL_NUMBER; col++) {
					rowButton.get(col).setText("U");
				}
			}
			this.repaint();
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
			g2d.setColor(Color.GRAY);
			g2d.setStroke(new BasicStroke(2));
			g2d.fillOval(40, 40, 20, 20);
			char[] charArray = Integer.toString(itemStack.getCount()).toCharArray();
			g2d.setColor(Color.WHITE);
			g2d.setFont(g2d.getFont().deriveFont(Font.BOLD));
			g2d.drawChars(charArray, 0, charArray.length, charArray.length == 1 ? 47 : 43, 55);
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

package kaptainwutax.minemap.ui.map.sidebar;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.ui.map.tool.Tool;
import kaptainwutax.minemap.util.math.DisplayMaths;
import kaptainwutax.minemap.util.ui.ColorChooserButton;
import kaptainwutax.minemap.util.ui.ListPanel;
import kaptainwutax.minemap.util.ui.RoundedPanel;
import kaptainwutax.minemap.util.ui.buttons.CloseButton;
import kaptainwutax.minemap.util.ui.buttons.InfoButton;
import kaptainwutax.mcutils.util.pos.BPos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class TooltipTools extends JPanel {

    private final MapPanel map;
    public boolean isHiddenForSize = false;

    public TooltipTools(MapPanel map) {
        this.map = map;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(new Color(0, 0, 0, 0));
    }

    public void updateToolsMetrics(ArrayList<Tool> tools) {
        this.removeAll();
        for (Tool tool : tools) {
            this.add(new Entry(tool, e -> map.manager.removeTool(tool)));
        }
        map.repaint();
    }

    public static class Entry extends RoundedPanel {
        private final JComponent iconView;
        private final JTextArea positionText;
        private final ColorChooserButton colorChooser;
        private final JButton closeButton;
        private final JButton infoButton;

        public Entry(Tool tool, Consumer<MouseEvent> onClose) {
            this.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 3, 3, 3);

            this.iconView = new JComponent() {
                @Override
                public Dimension getPreferredSize() {
                    return new Dimension(30, 30);
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    BufferedImage icon = Icons.get(tool.getClass());
                    int iconSizeX, iconSizeZ;
                    int defaultValue = 20;
                    float factor = 1.5F;
                    if (icon.getRaster().getWidth() > icon.getRaster().getHeight()) {
                        iconSizeX = defaultValue;
                        iconSizeZ = (int) (defaultValue * (float) icon.getRaster().getHeight() / icon.getRaster().getWidth());
                    } else {
                        iconSizeZ = defaultValue;
                        iconSizeX = (int) (defaultValue * (float) icon.getRaster().getWidth() / icon.getRaster().getHeight());
                    }
                    g.drawImage(icon, (defaultValue - iconSizeX) / 2, (defaultValue - iconSizeZ) / 2, (int) (iconSizeX * factor), (int) (iconSizeZ * factor), null);
                }
            };

            this.colorChooser = new ColorChooserButton(tool.getColor());
            this.colorChooser.addColorChangedListener(tool::setColor);

            this.positionText = new JTextArea(
                    String.join("\n", tool.getMetricString())
            );
            this.positionText.setFont(new Font(this.positionText.getFont().getName(), Font.PLAIN, 18));
            this.positionText.setBackground(new Color(0, 0, 0, 0));
            this.positionText.setFocusable(false);
            this.positionText.setOpaque(true);
            this.positionText.setForeground(Color.WHITE);

            this.infoButton = new InfoButton(16, -1, 1.9f);
            this.infoButton.addActionListener(e -> {
                MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
                if (map == null) return;
                java.awt.geom.Area area = new java.awt.geom.Area(tool.getPartialShape());
                List<BPos> coords = DisplayMaths.getPointsInArea(area);
                HashMap<Biome, Integer> biomesCount = new HashMap<>();
                for (BPos coord : coords) {
                    int biomeId = TooltipSidebar.getBiome(map, coord.getX(), coord.getZ());
                    Biome biome = Biome.REGISTRY.get(biomeId);
                    biomesCount.merge(biome, 1, Integer::sum);
                }

                JFrame frame = new JFrame("List of Biomes");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setPreferredSize(new java.awt.Dimension(500, 80 + Math.min(350, 70 * biomesCount.size())));

                // create the inner list
                final ListPanel listPanel = new ListPanel();
                biomesCount.forEach((biome, count) -> {
                    JLabel label = new JLabel(String.format("There are %d blocks of %s", count, biome.getName()));
                    JPanel panel = new JPanel();
                    panel.add(label);
                    listPanel.addPanel(panel);
                });
                listPanel.removeLastBorder();

                frame.add(listPanel);

                // display it
                frame.pack();
                frame.setLocationRelativeTo(null); // center
                frame.setVisible(true);
            });

            this.closeButton = new CloseButton(16, 0, 1.8f);
            this.closeButton.addMouseListener(Events.Mouse.onPressed(onClose));

            this.add(this.iconView, gbc);
            this.add(this.colorChooser, gbc);
            this.add(this.positionText, gbc);
            this.add(this.infoButton, gbc);
            this.add(this.closeButton, gbc);

            this.setBackground(new Color(0, 0, 0, 200));
        }

    }

}

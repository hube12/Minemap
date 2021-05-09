package kaptainwutax.minemap.ui.map.sidebar;

import kaptainwutax.biomeutils.biome.Biome;
import kaptainwutax.biomeutils.biome.Biomes;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.ui.map.tool.Tool;
import kaptainwutax.minemap.util.data.Str;
import kaptainwutax.minemap.util.math.DisplayMaths;
import kaptainwutax.minemap.util.ui.buttons.CloseButton;
import kaptainwutax.minemap.util.ui.buttons.InfoButton;
import kaptainwutax.minemap.util.ui.graphics.PieChart;
import kaptainwutax.minemap.util.ui.graphics.RoundedPanel;
import kaptainwutax.minemap.util.ui.interactive.ColorChooserButton;

import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.text.Caret;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

import static kaptainwutax.minemap.util.ui.graphics.Icon.paintImage;

public class TooltipTools extends JPanel {

    private final MapPanel map;
    public boolean isHiddenForSize = false;

    public TooltipTools(MapPanel map) {
        this.map = map;
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(new Color(0, 0, 0, 0));
        this.setOpaque(false);
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
                    paintImage(Icons.get(tool.getClass()), g);
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
            this.positionText.setEditable(false);
            this.positionText.setHighlighter(null);
            // disable caret selection
            this.positionText.setCaret(new Caret()
            {
                public void install(JTextComponent c){}
                public void deinstall(JTextComponent c){}
                public void paint(Graphics g){}
                public void addChangeListener(ChangeListener l){}
                public void removeChangeListener(ChangeListener l){}
                public boolean isVisible(){return false;}
                public void setVisible(boolean v){}
                public boolean isSelectionVisible(){return false;}
                public void setSelectionVisible(boolean v){}
                public void setMagicCaretPosition(Point p){}
                public Point getMagicCaretPosition(){return new Point(0,0);}
                public void setBlinkRate(int rate){}
                public int getBlinkRate(){return 10000;}
                public int getDot(){return 0;}
                public int getMark(){return 0;}
                public void setDot(int dot){}
                public void moveDot(int dot){}
            });

            this.infoButton = new InfoButton(16, -1, 1.9f);
            this.infoButton.addActionListener(e -> {
                MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
                if (map == null) return;
                Shape shape = tool.getExactShape();
                List<BPos> coords;
                if (shape == null) {
                    List<Shape> shapes = tool.getExactShapes();
                    if (shapes == null || shapes.size() == 0) return;
                    coords = new ArrayList<>();
                    for (Shape s : shapes) {
                        coords.addAll(DisplayMaths.getPointsInArea(s, tool.getPointsTraced() / (shapes.size() + 1) + 1));
                    }
                } else {
                    coords = DisplayMaths.getPointsInArea(shape, tool.getPointsTraced());
                }

                HashMap<Biome, Long> biomesCount = new HashMap<>();
                for (BPos coord : coords) {
                    int biomeId = TooltipSidebar.getBiome(map, coord.getX(), coord.getZ());
                    Biome biome = Biomes.REGISTRY.get(biomeId);
                    biomesCount.merge(biome, 1L, Long::sum);
                }
                long total = biomesCount.values().stream().reduce(0L, Long::sum);
                HashMap<Color, Long> colorCount = new HashMap<>();
                HashMap<Color, Pair<String, String>> colorToName = new HashMap<>();
                for (Biome biome : biomesCount.keySet()) {
                    long count = biomesCount.get(biome);
                    Color color = Configs.BIOME_COLORS.get(Configs.USER_PROFILE.getUserSettings().style, biome);
                    colorCount.put(color == null ? Color.BLACK : color, count);
                    colorToName.put(color, new Pair<>(
                        String.format("%s : %.2f%%", Str.prettifyDashed(biome.getName()), count / (double) total * 100.0),
                        String.format("%d blocks of %s were found", count, Str.prettifyDashed(biome.getName()))
                    ));
                }

                JFrame frame = new JFrame("List of Biomes");
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setPreferredSize(new Dimension(500, 400));

                PieChart pieChart = new PieChart(colorCount, colorToName, "On a total of %d blocks", total);
                pieChart.setSize(new Dimension(400, 400));

                frame.add(pieChart);

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
            this.setOpaque(false);
            this.setFocusable(false);

        }

    }

}

package kaptainwutax.minemap.ui.map.interactive;

import kaptainwutax.featureutils.structure.RuinedPortal;
import kaptainwutax.featureutils.structure.generator.structure.RuinedPortalGenerator;
import kaptainwutax.mcutils.block.Block;
import kaptainwutax.mcutils.block.Blocks;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.util.math.Vec3i;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.minemap.feature.OWRuinedPortal;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.init.Logger;
import kaptainwutax.minemap.ui.map.MapPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static kaptainwutax.minemap.util.data.Str.prettifyDashed;

public class Portal extends JFrame {
    private final JPanel content;
    private final MapPanel map;
    private CPos pos;
    private RuinedPortal feature;
    static final int PORTAL_HEIGHT = 600;
    static final int PORTAL_WIDTH = 600;
    private final List<PortalContent> portalContents = new ArrayList<>();

    public Portal(MapPanel map) {
        this.map = map;
        content = new JPanel();
        GridLayout gridLayout = new GridLayout(1, 1, 15, 15);
        content.setLayout(gridLayout);
        portalContents.add(new PortalContent(this));
        content.add(portalContents.get(0));
        this.setSize(this.getPreferredSize());
        this.setLocationRelativeTo(null); // center
        this.setVisible(false);
        this.setIconImage(Icons.get(OWRuinedPortal.class));
    }

    public void setFeature(RuinedPortal feature) {
        this.feature = feature;
    }

    public Pair<RuinedPortal, CPos> getInformations() {
        return new Pair<>(this.feature, this.pos);
    }

    @Override
    public String getName() {
        return "Portal";
    }

    @Override
    public int getDefaultCloseOperation() {
        return JFrame.HIDE_ON_CLOSE;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(PORTAL_WIDTH, PORTAL_HEIGHT);
    }

    public boolean generateContent() {
        this.setTitle(String.format("%s of %s at x:%d z:%d", this.getName(), prettifyDashed(this.feature.getName()), this.pos.getX() * 16 + 9, this.pos.getZ() * 16 + 9));
        return this.getPortalContents().get(0).generate();
    }

    public void setPos(CPos pos) {
        this.pos = pos;
    }

    public JPanel getContent() {
        return content;
    }

    public List<PortalContent> getPortalContents() {
        return portalContents;
    }

    public static class PortalContent extends JPanel {
        private final Portal portal;
        private final List<List<JButton>> list;
        private final int maxRows;
        private final int maxColumns;

        public PortalContent(Portal portal) {
            this.portal = portal;
            maxRows = RuinedPortalGenerator.STRUCTURE_SIZE.values()
                .stream().mapToInt(Vec3i::getY)
                .max().orElse(0);
            maxColumns = RuinedPortalGenerator.STRUCTURE_SIZE.values()
                .stream().mapToInt(e -> Math.max(e.getX(), e.getZ()))
                .max().orElse(0);
            this.setLayout(new GridLayout(maxRows, maxColumns));
            this.list = new ArrayList<>();
            for (int row = 0; row < maxRows; row++) {
                List<JButton> temp = new ArrayList<>();
                for (int col = 0; col < maxColumns; col++) {
                    JButton button = new JButton("");
                    button.setPreferredSize(new Dimension((int) (PORTAL_WIDTH / maxColumns * 0.70), (int) (PORTAL_HEIGHT / maxRows * 0.70)));
                    temp.add(button);
                    this.add(button);
                }
                list.add(temp);
            }
        }

        private boolean generate() {
            this.clean();
            Pair<RuinedPortal, CPos> informations = this.portal.getInformations();
            RuinedPortalGenerator ruinedPortalGenerator = new RuinedPortalGenerator(informations.getFirst().getVersion());
            if (!ruinedPortalGenerator.generate(this.portal.map.context.getChunkGenerator(), informations.getSecond())) return false;
            List<Pair<Block, BPos>> blocks = ruinedPortalGenerator.getPortal();
            if (blocks == null) return false;
            int minX = Integer.MAX_VALUE;
            int minY = Integer.MAX_VALUE;
            int minZ = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int maxY = Integer.MIN_VALUE;
            int maxZ = Integer.MIN_VALUE;
            HashMap<BPos, Block> blockHashMap = new HashMap<>();
            for (Pair<Block, BPos> block : blocks) {
                if (blockHashMap.containsKey(block.getSecond())) {
                    Logger.LOGGER.severe("Impossible case " + informations);
                    System.err.println("Impossible case " + informations);
                }
                blockHashMap.put(block.getSecond(), block.getFirst());
                minX = Math.min(block.getSecond().getX(), minX);
                minY = Math.min(block.getSecond().getY(), minY);
                minZ = Math.min(block.getSecond().getZ(), minZ);
                maxX = Math.max(block.getSecond().getX(), maxX);
                maxY = Math.max(block.getSecond().getY(), maxY);
                maxZ = Math.max(block.getSecond().getZ(), maxZ);
            }
            int dx = maxX - minX;
            int dy = maxY - minY;
            int dz = maxZ - minZ;
            if (dy == 0) {
                Logger.LOGGER.severe("Impossible case " + maxY + " " + dx + " " + dz + " " + informations);
                System.err.println("Impossible case " + maxY + " " + dx + " " + dz + " " + informations);
                return false;
            }
            if (dx != 0 && dz != 0) {
                // we are on the case on block on some other axis
                // dunno how to handle it yet
                return false;
            }
            if (dx > maxColumns || dz > maxColumns || dy > maxRows) {
                Logger.LOGGER.severe("Impossible case " + dy + " " + dx + " " + dz + " " + informations + " " + maxRows + " " + maxColumns);
                System.err.println("Impossible case " + dy + " " + dx + " " + dz + " " + informations + " " + maxRows + " " + maxColumns);
                return false;
            }
            if (dx == 0) {
                BPos base = new BPos(minX, minY, minZ);
                for (int y = 0; y < dy; y++) {
                    List<JButton> buttons = list.get(y);
                    for (int x = 0; x < dy; x++) {
                        BPos current = base.add(0, y, x);
                        Block block = blockHashMap.get(current);
                        if (block != null) {
                            buttons.get(x).setText(block == Blocks.OBSIDIAN ? "O" : "C");
                        }
                    }
                }
            } else {
                BPos base = new BPos(minX, minY, minZ);
                for (int y = 0; y < dy; y++) {
                    List<JButton> buttons = list.get(y);
                    for (int x = 0; x < dy; x++) {
                        BPos current = base.add(x, y, 0);
                        Block block = blockHashMap.get(current);
                        if (block != null) {
                            buttons.get(x).setText(block == Blocks.OBSIDIAN ? "O" : "C");
                        }
                    }
                }
            }
            this.repaint();
            this.revalidate();
            return true;
        }

        public void clean() {
            for (int row = 0; row < maxRows; row++) {
                List<JButton> rowButton = this.list.get(row);
                for (int col = 0; col < maxColumns; col++) {
                    rowButton.get(col).setText("");
                    rowButton.get(col).setToolTipText(null);
                    rowButton.get(col).setIcon(null);
                }
            }
        }
    }


}

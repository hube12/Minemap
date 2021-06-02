package kaptainwutax.minemap.ui.map;

import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.RPos;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.minemap.ui.component.TabHeader;
import kaptainwutax.minemap.ui.component.WorldTabs;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.minemap.ui.map.fragment.FragmentScheduler;
import kaptainwutax.minemap.ui.map.icon.StrongholdIcon;
import kaptainwutax.minemap.ui.map.interactive.chest.ChestInstance;
import kaptainwutax.minemap.util.data.DrawInfo;
import org.jdesktop.swingx.JXCollapsiblePane;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static kaptainwutax.minemap.MineMap.DEBUG;
import static kaptainwutax.minemap.MineMap.isDarkTheme;

public class MapPanel extends JPanel {
    public final MapContext context;
    public final ChestInstance chestInstance;
    public final MapManager manager;
    public final MapLeftSideBar leftBar;
    public final MapRightSideBar rightBar;
    public final int threadCount;
    public FragmentScheduler scheduler;
    public TabHeader header;

    public MapPanel(MCVersion version, Dimension dimension, long worldSeed, int threadCount) {
        this.threadCount = threadCount;
        this.setLayout(new BorderLayout());

        this.context = new MapContext(version, dimension, worldSeed);
        this.chestInstance = new ChestInstance(this);
        this.manager = new MapManager(this);

        this.leftBar = new MapLeftSideBar(this);
        this.rightBar = new MapRightSideBar(this);

        if (isDarkTheme()) {
            this.setBackground(WorldTabs.BACKGROUND_COLOR.darker());
        } else {
            this.setBackground(WorldTabs.BACKGROUND_COLOR_LIGHT.darker());
        }

        this.add(this.leftBar, BorderLayout.WEST);
        this.add(this.rightBar, BorderLayout.EAST);

        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if (e.getComponent().getSize().width <= 800) {
                    if (leftBar.settings.isVisible()) {
                        leftBar.settings.setVisible(false);
                        leftBar.settings.isHiddenForSize = true;
                    }
                    if (rightBar.tooltip.isVisible()) {
                        rightBar.tooltip.setVisible(false);
                        rightBar.tooltip.isHiddenForSize = true;
                    }
                    if (rightBar.chestBox.isVisible()) {
                        rightBar.chestBox.setVisible(false);
                        rightBar.chestBox.isHiddenForSize = true;
                    }
                    if (rightBar.searchBox.isVisible()) {
                        rightBar.searchBox.setVisible(false);
                        rightBar.searchBox.isHiddenForSize = true;
                    }
                } else {
                    if (leftBar.settings.isHiddenForSize) {
                        leftBar.settings.setVisible(true);
                        leftBar.settings.isHiddenForSize = false;
                    }
                    if (rightBar.tooltip.isHiddenForSize) {
                        rightBar.tooltip.setVisible(true);
                        rightBar.tooltip.isHiddenForSize = false;
                    }
                    if (rightBar.chestBox.isHiddenForSize) {
                        rightBar.chestBox.setVisible(true);
                        rightBar.chestBox.isHiddenForSize = false;
                    }
                    if (rightBar.searchBox.isHiddenForSize) {
                        rightBar.searchBox.setVisible(true);
                        rightBar.searchBox.isHiddenForSize = false;
                    }
                }
            }
        });

        this.context.calculateStarts(this);
        this.restart();
    }

    public void updateInteractive() {
        this.manager.updateInteractive();
    }

    public void setHeader(TabHeader tabHeader) {
        this.header = tabHeader;
    }

    public TabHeader getHeader() {
        return header;
    }

    public MapContext getContext() {
        return this.context;
    }

    public MapManager getManager() {
        return this.manager;
    }

    public void restart() {
        if (this.scheduler != null) this.scheduler.terminate();
        this.scheduler = new FragmentScheduler(this, this.threadCount);
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics graphics) {
        long start = System.nanoTime();
        super.paintComponent(graphics);
        if (DEBUG) System.out.println("Draw super " + " " + (System.nanoTime() - start));
        start = System.nanoTime();

        this.scheduler.purge();
        if (DEBUG) System.out.println("Draw scheduler " + " " + (System.nanoTime() - start));
        start = System.nanoTime();
        this.drawMap(graphics);
        if (DEBUG) System.out.println("Draw map " + " " + (System.nanoTime() - start));
        start = System.nanoTime();
        this.drawCrossHair(graphics);
        if (DEBUG) System.out.println("Draw crosshair " + " " + (System.nanoTime() - start));
    }

    public void drawMap(Graphics graphics) {
        long start = System.nanoTime();
        Map<Fragment, DrawInfo> drawQueue = this.getDrawQueue();
        if (DEBUG) System.out.println("Draw queue " + " " + (System.nanoTime() - start));
        start = System.nanoTime();
        drawQueue.forEach((fragment, info) -> fragment.drawBiomes(graphics, info));
        if (DEBUG) System.out.println("Draw Biomes " + " " + (System.nanoTime() - start));
        start = System.nanoTime();
        drawQueue.forEach((fragment, info) -> fragment.drawFeatures(graphics, info));
        if (DEBUG) System.out.println("Draw feature " + " " + (System.nanoTime() - start));
        start = System.nanoTime();
        drawQueue.forEach((fragment, info) -> fragment.drawTools(graphics, info, this.manager.toolsList));
        if (DEBUG) System.out.println("Draw tools " + " " + (System.nanoTime() - start));
    }

    public void drawCrossHair(Graphics graphics) {
        graphics.setXORMode(Color.BLACK);
        int cx = this.getWidth() / 2, cz = this.getHeight() / 2;
        graphics.fillRect(cx - 4, cz - 1, 8, 2);
        graphics.fillRect(cx - 1, cz - 4, 2, 8);
        graphics.setPaintMode();
    }

    public Map<Fragment, DrawInfo> getDrawQueue() {
        Map<Fragment, DrawInfo> drawQueue = new HashMap<>();
        int w = this.getWidth(), h = this.getHeight();

        BPos min = this.manager.getPos(0, 0);
        BPos max = this.manager.getPos(w, h);

        double scaleFactor = this.manager.pixelsPerFragment / this.manager.blocksPerFragment;
        int factor = 1;
//        if (scaleFactor<0.04){
//            factor=8;
//        }
        RPos regionMin = min.toRegionPos(this.manager.blocksPerFragment);
        RPos regionMax = max.toRegionPos(this.manager.blocksPerFragment);
        int blockOffsetX = regionMin.toBlockPos().getX() - min.getX();
        int blockOffsetZ = regionMin.toBlockPos().getZ() - min.getZ();
        double pixelOffsetX = blockOffsetX * scaleFactor;
        double pixelOffsetZ = blockOffsetZ * scaleFactor;
        for (int regionX = regionMin.getX() / factor; regionX <= regionMax.getX() / factor; regionX++) {
            for (int regionZ = regionMin.getZ() / factor; regionZ <= regionMax.getZ() / factor; regionZ++) {
                Fragment fragment = this.scheduler.getFragmentAt(regionX * factor, regionZ * factor, factor);
                double x = (regionX * factor - regionMin.getX()) * this.manager.pixelsPerFragment + pixelOffsetX;
                double z = (regionZ * factor - regionMin.getZ()) * this.manager.pixelsPerFragment + pixelOffsetZ;
                int size = (int) (this.manager.pixelsPerFragment) * factor;
                drawQueue.put(fragment, new DrawInfo((int) x, (int) z, size, size));
            }
        }

        return drawQueue;
    }

    public BufferedImage getScreenshot() {
        BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
        this.drawMap(image.getGraphics());
        return image;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MapPanel)) return false;
        MapPanel mapPanel = (MapPanel) o;
        return threadCount == mapPanel.threadCount && context.equals(mapPanel.context);
    }

    @Override
    public int hashCode() {
        return Objects.hash(context, threadCount);
    }
}

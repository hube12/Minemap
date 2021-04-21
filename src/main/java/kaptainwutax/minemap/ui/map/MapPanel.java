package kaptainwutax.minemap.ui.map;

import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.RPos;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.minemap.ui.component.WorldTabs;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.minemap.ui.map.fragment.FragmentScheduler;
import kaptainwutax.minemap.util.data.DrawInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class MapPanel extends JPanel {

    public final MapContext context;
    public final MapManager manager;
    public final int threadCount;
    public FragmentScheduler scheduler;
    public final MapLeftSideBar leftBar;
    public final MapRightSideBar rightBar;
    private final MapCanvas canvas;

    public MapPanel(MCVersion version, Dimension dimension, long worldSeed, int threadCount) {
        this.threadCount = threadCount;
        this.setLayout(new OverlayLayout(this));
        this.context = new MapContext(version, dimension, worldSeed);
        this.manager = new MapManager(this);
        this.canvas = new MapCanvas(this);

        this.leftBar = new MapLeftSideBar(this);
        this.rightBar = new MapRightSideBar(this);
        this.add(this.canvas);
        this.add(this.leftBar);
        this.add(this.rightBar);
        this.setMinimumSize(new java.awt.Dimension(200,200));
        this.setBackground(WorldTabs.BACKGROUND_COLOR.darker().darker());
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if (e.getComponent().getSize().width <= 600) {
                    if (getLeftBar().settings.isVisible()) {
                        getLeftBar().settings.setVisible(false);
                        getLeftBar().settings.isHiddenForSize = true;
                    }
                    if (getRightBar().tooltip.isVisible()) {
                        getRightBar().tooltip.setVisible(false);
                        getRightBar().tooltip.isHiddenForSize = true;
                    }
                } else {
                    if (getLeftBar().settings.isHiddenForSize) {
                        getLeftBar().settings.setVisible(true);
                        getLeftBar().settings.isHiddenForSize = false;
                    }
                    if (getRightBar().tooltip.isHiddenForSize) {
                        getRightBar().tooltip.setVisible(true);
                        getRightBar().tooltip.isHiddenForSize = false;
                    }
                }
            }
        });

        this.restart();
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
        super.paintComponent(graphics);
        this.scheduler.purge();
        canvas.repaint();
        this.drawMap(graphics);
        this.drawCrossHair(graphics);
    }

    public MapLeftSideBar getLeftBar() {
        return leftBar;
    }

    public MapRightSideBar getRightBar() {
        return rightBar;
    }

    public void drawMap(Graphics graphics) {
        Map<Fragment, DrawInfo> drawQueue = this.getDrawQueue();
        drawQueue.forEach((fragment, info) -> fragment.drawGrid(graphics, info));
        drawQueue.forEach((fragment, info) -> fragment.drawFeatures(graphics, info));
        drawQueue.forEach((fragment, info) -> fragment.drawTools(graphics, info, this.manager.toolsList));
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
        RPos regionMin = min.toRegionPos(this.manager.blocksPerFragment);
        RPos regionMax = max.toRegionPos(this.manager.blocksPerFragment);
        double scaleFactor = this.manager.pixelsPerFragment / this.manager.blocksPerFragment;

        for (int regionX = regionMin.getX(); regionX <= regionMax.getX(); regionX++) {
            for (int regionZ = regionMin.getZ(); regionZ <= regionMax.getZ(); regionZ++) {
                Fragment fragment = this.scheduler.getFragmentAt(regionX, regionZ);
                int blockOffsetX = regionMin.toBlockPos().getX() - min.getX();
                int blockOffsetZ = regionMin.toBlockPos().getZ() - min.getZ();
                double pixelOffsetX = blockOffsetX * scaleFactor;
                double pixelOffsetZ = blockOffsetZ * scaleFactor;
                double x = (regionX - regionMin.getX()) * this.manager.pixelsPerFragment + pixelOffsetX;
                double z = (regionZ - regionMin.getZ()) * this.manager.pixelsPerFragment + pixelOffsetZ;
                int size = (int) (this.manager.pixelsPerFragment);
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

}

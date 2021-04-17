package kaptainwutax.minemap.ui.map;

import kaptainwutax.minemap.ui.component.WorldTabs;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.minemap.ui.map.fragment.FragmentScheduler;
import kaptainwutax.minemap.util.data.DrawInfo;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.RPos;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

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
    public final MapLeftSideBar leftBar;
    public final MapRightSideBar rightBar;
    public final int threadCount;
    public FragmentScheduler scheduler;
    private AWTGLCanvas canvas;

    public MapPanel(MCVersion version, Dimension dimension, long worldSeed, int threadCount) {
        this.threadCount = threadCount;
        this.setLayout(new BorderLayout());

        this.context = new MapContext(version, dimension, worldSeed);
        this.manager = new MapManager(this);
        this.leftBar = new MapLeftSideBar(this);
        this.rightBar = new MapRightSideBar(this);
        GLData data = new GLData();
        data.samples = 4;
        data.swapInterval = 0;
        AWTGLCanvas canvas;

        this.setBackground(WorldTabs.BACKGROUND_COLOR.darker().darker());
        this.add(this.leftBar, BorderLayout.WEST);
        this.add(this.rightBar, BorderLayout.EAST);
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                if (e.getComponent().getSize().width <= 600) {
                    if (leftBar.settings.isVisible()) {
                        leftBar.settings.setVisible(false);
                        leftBar.settings.isHiddenForSize = true;
                    }
                    if (rightBar.tooltip.isVisible()) {
                        rightBar.tooltip.setVisible(false);
                        rightBar.tooltip.isHiddenForSize = true;
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
        this.drawMap(graphics);
        this.drawCrossHair(graphics);
    }

    public void drawMap(Graphics graphics) {
        Map<Fragment, DrawInfo> drawQueue = this.getDrawQueue();
        drawQueue.forEach((fragment, info) -> fragment.drawBiomes(graphics, info));
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

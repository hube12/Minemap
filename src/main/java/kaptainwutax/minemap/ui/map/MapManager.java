package kaptainwutax.minemap.ui.map;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.util.math.Vec3i;

import java.awt.*;

public class MapManager {

    private final MapPanel panel;

    public final int blocksPerFragment = 512;
    public double pixelsPerFragment = (int)(300.0D * (this.blocksPerFragment / 512.0D));
    public double centerX = 0.0D;
    public double centerY = 0.0D;

    public Point mousePointer;

    public MapManager(MapPanel panel) {
        this.panel = panel;

        this.panel.addMouseMotionListener(Events.Mouse.onDragged(e -> {
            int dx = e.getX() - mousePointer.x;
            int dy = e.getY() - mousePointer.y;
            mousePointer = e.getPoint();
            centerX += dx;
            centerY += dy;
            this.panel.repaint();
        }));

        this.panel.addMouseMotionListener(Events.Mouse.onMoved(e -> {
            BPos pos = getPos(e.getX(), e.getY());
            int x = pos.getX();
            int z = pos.getZ();

            Biome biome = this.panel.info.getBiome(x, z);
            this.panel.displayBar.setBiomeDisplay(x, z, biome == null ? "UNKNOWN" : biome.getName().toUpperCase());
            this.panel.repaint();
        }));

        this.panel.addMouseListener(Events.Mouse.onPressed(e -> {
            mousePointer = e.getPoint();
            this.panel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
            this.panel.repaint();
        }));

        this.panel.addMouseListener(Events.Mouse.onReleased(mouseEvent -> {
            this.panel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }));

        this.panel.addMouseWheelListener(e -> {
            double newPixelsPerFragment = this.pixelsPerFragment;

            if(e.getUnitsToScroll() > 0) {
                newPixelsPerFragment /= e.getUnitsToScroll() / 2.0D;
            } else {
                newPixelsPerFragment *= -e.getUnitsToScroll() / 2.0D;
            }

            if(newPixelsPerFragment < 40.0D * (this.blocksPerFragment / 512.0D)) {
                newPixelsPerFragment = 40.0D * (this.blocksPerFragment / 512.0D);
            } else if(newPixelsPerFragment > 2000.0D * (this.blocksPerFragment / 512.0D)) {
                newPixelsPerFragment = 2000.0D * (this.blocksPerFragment / 512.0D);
            }

            double scaleFactor = newPixelsPerFragment / this.pixelsPerFragment;
            this.centerX *= scaleFactor;
            this.centerY *= scaleFactor;
            this.pixelsPerFragment = newPixelsPerFragment;
            this.panel.repaint();
        });
    }

    public Vec3i getScreenSize() {
        return new Vec3i(this.panel.getWidth(), 0, this.panel.getHeight());
    }

    public BPos getCenterPos() {
        Vec3i screenSize = this.getScreenSize();
        return getPos(screenSize.getX() / 2.0D, screenSize.getZ() / 2.0D);
    }

    public void setCenterPos(int blockX, int blockZ) {
        double scaleFactor = this.pixelsPerFragment / this.blocksPerFragment;
        this.centerX = -blockX * scaleFactor;
        this.centerY = -blockZ * scaleFactor;
        this.panel.repaint();
    }

    public BPos getPos(double mouseX, double mouseY) {
        Vec3i screenSize = this.getScreenSize();
        double x = (mouseX - screenSize.getX() / 2.0D - centerX) / screenSize.getX();
        double y = (mouseY - screenSize.getZ() / 2.0D - centerY) / screenSize.getZ();
        double blocksPerWidth = (screenSize.getX() / pixelsPerFragment) * (double) blocksPerFragment;
        double blocksPerHeight = (screenSize.getZ() / pixelsPerFragment) * (double) blocksPerFragment;
        x *= blocksPerWidth;
        y *= blocksPerHeight;
        int xi = (int)Math.round(x);
        int yi = (int)Math.round(y);
        return new BPos(xi, 0, yi);
    }

}

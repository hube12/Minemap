package com.seedfinding.minemap.ui.map;

import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.RPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.init.Configs;
import com.seedfinding.minemap.ui.component.TabHeader;
import com.seedfinding.minemap.ui.component.WorldTabs;
import com.seedfinding.minemap.ui.map.fragment.Fragment;
import com.seedfinding.minemap.ui.map.fragment.FragmentScheduler;
import com.seedfinding.minemap.ui.map.interactive.chest.ChestInstance;
import com.seedfinding.minemap.util.data.DrawInfo;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MapPanel extends JPanel {
    public final MapContext context;
    public final ChestInstance chestInstance;
    public final MapManager manager;
    public final MapLeftSideBar leftBar;
    public final MapRightSideBar rightBar;
    public final int threadCount;
    public FragmentScheduler scheduler;
    public TabHeader header;

    private static final long HIGHLIGHT_MS = 2_000L;
    private int highlightX, highlightZ;
    private String highlightLabel;
    private long highlightStart;
    private javax.swing.Timer highlightTimer;

    // Flood-filled region of the found biome (recomputed, debounced, when the view changes).
    private static final long REGION_MS = 2_000L;      // total lifetime of the glow
    private static final long REGION_FADE_MS = 1_000L;  // fade-out at the end of that lifetime
    private static final int REGION_DEBOUNCE_MS = 40;   // keep small: a stale overlay is stretched while zooming
    private long regionStart;
    private volatile BiomeRegionOverlay regionOverlay;
    private int regionSeedX, regionSeedZ, regionBiomeId;
    private boolean regionActive;
    private SwingWorker<BiomeRegionOverlay, Void> regionWorker;
    private double[] regionPendingSignature;
    private javax.swing.Timer regionDebounce, regionAnim;

    public MapPanel(MCVersion version, Dimension dimension, long worldSeed, int threadCount) {
        this.threadCount = threadCount;
        this.setLayout(new BorderLayout());

        this.context = new MapContext(version, dimension, worldSeed);
        this.chestInstance = new ChestInstance(this);
        this.manager = new MapManager(this);

        this.leftBar = new MapLeftSideBar(this);
        this.rightBar = new MapRightSideBar(this);

        if (MineMap.isDarkTheme()) {
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

    public boolean isLocked() {
        return this.header != null && this.header.isSaved();
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
        if (MineMap.DEBUG) System.out.println("Draw super " + " " + (System.nanoTime() - start));
        start = System.nanoTime();
        this.scheduler.purge();
        if (MineMap.DEBUG) System.out.println("Draw scheduler " + " " + (System.nanoTime() - start));
        start = System.nanoTime();
        this.drawMap(graphics);
        if (MineMap.DEBUG) System.out.println("Draw map " + " " + (System.nanoTime() - start));
        start = System.nanoTime();
        this.drawCrossHair(graphics);
        if (MineMap.DEBUG) System.out.println("Draw crosshair " + " " + (System.nanoTime() - start));
        this.drawRegion(graphics);
        this.drawHighlight(graphics);
    }

    public void drawMap(Graphics graphics) {
        long start = System.nanoTime();
        Map<Fragment, DrawInfo> drawQueue = this.getDrawQueue();
        if (MineMap.DEBUG) System.out.println("Draw queue " + " " + (System.nanoTime() - start));

        start = System.nanoTime();
        if (Configs.USER_PROFILE.getUserSettings().doHeightmapGrayScale) {
            drawQueue.forEach((fragment, info) -> fragment.drawNonLoading(f -> f.drawHeight(graphics, info)));
        } else {
            drawQueue.forEach((fragment, info) -> fragment.drawNonLoading(f -> f.drawBiomes(graphics, info)));
        }
        if (MineMap.DEBUG) System.out.println("Draw Biomes/Heights " + " " + (System.nanoTime() - start));

        drawQueue.forEach((fragment, info) -> fragment.drawNonLoading(f -> f.drawGrid(graphics, info)));

        start = System.nanoTime();
        drawQueue.forEach((fragment, info) -> fragment.drawNonLoading(f -> f.drawFeatures(graphics, info)));
        if (MineMap.DEBUG) System.out.println("Draw feature " + " " + (System.nanoTime() - start));

        start = System.nanoTime();
        // This can be drawn onto a loading fragment (no issue)
        drawQueue.forEach((fragment, info) -> fragment.drawTools(graphics, info, this.manager.toolsList));
        if (MineMap.DEBUG) System.out.println("Draw tools " + " " + (System.nanoTime() - start));
    }

    /** Marks a block position with a pulsing ring and a label; fades out after a few seconds. */
    public void highlight(int blockX, int blockZ, String label) {
        this.highlightX = blockX;
        this.highlightZ = blockZ;
        this.highlightLabel = label;
        this.highlightStart = System.currentTimeMillis();
        if (this.highlightTimer != null) this.highlightTimer.stop();
        this.highlightTimer = new javax.swing.Timer(33, e -> {
            if (System.currentTimeMillis() - this.highlightStart > HIGHLIGHT_MS) {
                ((javax.swing.Timer) e.getSource()).stop();
                this.highlightLabel = null;
            }
            this.repaint();
        });
        this.highlightTimer.start();
        this.repaint();
    }

    public void clearHighlight() {
        if (this.highlightTimer != null) this.highlightTimer.stop();
        this.highlightLabel = null;
        this.clearRegion();
        this.repaint();
    }

    /**
     * Flood-fills the given biome from a block position over everything currently on screen
     * and shows it as a translucent area with an outline (lasts a few seconds, then fades out; follows pan/zoom).
     */
    public void highlightRegion(int seedBlockX, int seedBlockZ, int biomeId) {
        this.regionSeedX = seedBlockX;
        this.regionSeedZ = seedBlockZ;
        this.regionBiomeId = biomeId;
        this.regionActive = true;
        this.regionOverlay = null;
        this.regionStart = System.currentTimeMillis();
        if (this.regionAnim != null) this.regionAnim.stop();
        // Drives the pulse and the fade, and removes the glow once its lifetime is over.
        this.regionAnim = new javax.swing.Timer(33, e -> {
            if (System.currentTimeMillis() - this.regionStart >= REGION_MS) this.clearRegion();
            else this.repaint();
        });
        this.regionAnim.start();
        this.startRegionCompute();
    }

    public void clearRegion() {
        this.regionActive = false;
        this.regionOverlay = null;
        this.regionPendingSignature = null;
        if (this.regionWorker != null) this.regionWorker.cancel(true);
        if (this.regionDebounce != null) this.regionDebounce.stop();
        if (this.regionAnim != null) this.regionAnim.stop();
        this.repaint();
    }

    private double[] viewSignature() {
        return new double[] {this.manager.centerX, this.manager.centerY, this.manager.pixelsPerFragment,
            this.manager.blocksPerFragment, this.getWidth(), this.getHeight(), this.context.getLayerId()};
    }

    private void startRegionCompute() {
        if (!this.regionActive || this.getWidth() <= 0 || this.getHeight() <= 0) return;
        if (this.regionWorker != null) this.regionWorker.cancel(true);

        final double[] signature = this.viewSignature();
        this.regionPendingSignature = signature;
        BPos min = this.manager.getPos(0, 0);
        BPos max = this.manager.getPos(this.getWidth(), this.getHeight());
        final int minX = min.getX(), minZ = min.getZ(), maxX = max.getX(), maxZ = max.getZ();
        final double pxPerBlock = this.manager.pixelsPerFragment / this.manager.blocksPerFragment;
        final int seedX = this.regionSeedX, seedZ = this.regionSeedZ, biomeId = this.regionBiomeId;
        final MapContext ctx = this.context;

        this.regionWorker = new SwingWorker<BiomeRegionOverlay, Void>() {
            @Override
            protected BiomeRegionOverlay doInBackground() {
                return BiomeRegionOverlay.compute(ctx.getBiomeLayer(), biomeId, seedX, seedZ,
                    minX, minZ, maxX, maxZ, pxPerBlock, signature);
            }

            @Override
            protected void done() {
                if (isCancelled() || !regionActive) return;
                try {
                    // A null result means the start point is off screen (e.g. after zooming in elsewhere):
                    // keep the previous overlay, it is anchored in block coordinates and still lines up.
                    BiomeRegionOverlay computed = get();
                    if (computed != null) regionOverlay = computed;
                } catch (Exception e) {
                    // cancelled or failed: keep what is shown, the glow expires on its own
                }
                // Keep regionPendingSignature: it records the view this result was computed for, so
                // drawRegion() only recomputes when the view really changes (not after every repaint).
                repaint();
            }
        };
        this.regionWorker.execute();
    }

    private void drawRegion(Graphics graphics) {
        if (!this.regionActive) return;
        // View changed since the fill was computed: refresh after the user stops moving (debounced).
        double[] now = this.viewSignature();
        BiomeRegionOverlay overlay = this.regionOverlay;
        // Compare with the last view a computation was started for, so an empty result
        // (seed off screen) does not trigger endless recomputes.
        if (!java.util.Arrays.equals(this.regionPendingSignature, now)) {
            if (this.regionDebounce == null) {
                this.regionDebounce = new javax.swing.Timer(REGION_DEBOUNCE_MS, e -> this.startRegionCompute());
                this.regionDebounce.setRepeats(false);
            }
            this.regionDebounce.restart();
        }
        if (overlay == null) return;

        double scale = this.manager.pixelsPerFragment / this.manager.blocksPerFragment;
        double x = overlay.originBlockX * scale + this.getWidth() / 2.0D + this.manager.centerX;
        double z = overlay.originBlockZ * scale + this.getHeight() / 2.0D + this.manager.centerY;
        int w = (int) Math.ceil(overlay.widthCells * overlay.cellSize * scale);
        int h = (int) Math.ceil(overlay.heightCells * overlay.cellSize * scale);
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            long elapsed = System.currentTimeMillis() - this.regionStart;
            float fade = elapsed < REGION_MS - REGION_FADE_MS ? 1f : Math.max(0f, (REGION_MS - elapsed) / (float) REGION_FADE_MS);
            float pulse = (0.85f + 0.15f * (float) Math.sin(elapsed / 200.0D)) * fade;
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, pulse));
            g.drawImage(overlay.image, (int) Math.round(x), (int) Math.round(z), w, h, null);
        } finally {
            g.dispose();
        }
    }

    private void drawHighlight(Graphics graphics) {
        if (this.highlightLabel == null) return;
        long elapsed = System.currentTimeMillis() - this.highlightStart;
        if (elapsed > HIGHLIGHT_MS) return;

        double scale = this.manager.pixelsPerFragment / this.manager.blocksPerFragment;
        int px = (int) Math.round(this.highlightX * scale + this.getWidth() / 2.0D + this.manager.centerX);
        int pz = (int) Math.round(this.highlightZ * scale + this.getHeight() / 2.0D + this.manager.centerY);

        float fade = elapsed < HIGHLIGHT_MS - 2000L ? 1f : Math.max(0f, (HIGHLIGHT_MS - elapsed) / 2000f);
        Graphics2D g = (Graphics2D) graphics.create();
        try {
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            // Label above the marker.
            g.setFont(g.getFont().deriveFont(Font.BOLD, 14f));
            FontMetrics fm = g.getFontMetrics();
            String text = this.highlightLabel;
            int tx = px - fm.stringWidth(text) / 2, ty = pz - 8;
            g.setColor(new Color(0f, 0f, 0f, 0.7f * fade));
            g.fillRoundRect(tx - 5, ty - fm.getAscent() - 1, fm.stringWidth(text) + 10, fm.getHeight() + 2, 8, 8);
            g.setColor(new Color(1f, 1f, 1f, fade));
            g.drawString(text, tx, ty);
        } finally {
            g.dispose();
        }
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
        Graphics graphics = image.getGraphics();
        // Because this has no idea what the clip is, see #14
        graphics.setClip(0, 0, this.getWidth(), this.getHeight());
        this.drawMap(graphics);
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

package com.seedfinding.minemap.ui.map.fragment;

import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.RPos;
import com.seedfinding.minemap.init.Configs;
import com.seedfinding.minemap.init.Logger;
import com.seedfinding.minemap.ui.map.MapPanel;
import com.seedfinding.minemap.ui.map.tool.Tool;
import com.seedfinding.minemap.util.data.DrawInfo;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class FragmentScheduler {

    private static final AtomicInteger POOL_ID = new AtomicInteger();
    /**
     * Margin (in regions) around the viewport that is kept loaded.
     */
    private static final int MARGIN = 40;

    protected final Map<RPos, Fragment> fragments = new ConcurrentHashMap<>();
    public static Fragment LOADING_FRAGMENT = new Fragment(0, 0, 0, null) {
        @Override
        public void drawBiomes(Graphics graphics, DrawInfo info) {
        }

        @Override
        public void drawFeatures(Graphics graphics, DrawInfo info) {
        }

        @Override
        public void drawTools(Graphics graphics, DrawInfo info, ArrayList<Tool> tools) {
        }
    };

    /**
     * Regions waiting for a worker. Guarded by itself; workers block on it instead of polling.
     */
    private final Set<RPos> pending = new LinkedHashSet<>();
    private final AtomicBoolean repaintQueued = new AtomicBoolean(false);
    private final Thread[] workers;
    private volatile boolean running = true;
    protected MapPanel listener;

    public FragmentScheduler(MapPanel listener, int threadCount) {
        this.listener = listener;
        int count = Math.max(1, threadCount);
        int id = POOL_ID.incrementAndGet();
        this.workers = new Thread[count];
        for (int i = 0; i < count; i++) {
            Thread worker = new Thread(this::workLoop, "FragmentWorker-" + id + "-" + i);
            worker.setDaemon(true);
            this.workers[i] = worker;
            worker.start();
        }
    }

    private void workLoop() {
        while (this.running) {
            RPos next;
            try {
                next = this.takeNearest();
            } catch (InterruptedException e) {
                return;
            }
            if (next == null) return;

            try {
                Fragment fragment = new Fragment(next, this.listener.getContext());
                // replace() is a no-op if the region was purged meanwhile, so nothing leaks back in.
                if (this.fragments.replace(next, fragment) != null) {
                    this.requestRepaint();
                }
            } catch (Throwable t) {
                // Keep the worker alive; the region stays "loading" to avoid a retry storm.
                Logger.LOGGER.severe("Fragment generation failed at " + next + ": " + t);
                t.printStackTrace();
            }
        }
    }

    /**
     * Blocks until a region is pending, then returns the one closest to the view center
     * (evaluated now, so panning is always honoured). Out-of-bounds regions are dropped.
     * Returns null when the scheduler is terminated.
     */
    private RPos takeNearest() throws InterruptedException {
        synchronized (this.pending) {
            while (this.running) {
                if (this.pending.isEmpty()) {
                    this.pending.wait();
                    continue;
                }
                Bounds bounds = this.bounds();
                RPos center = this.centerRegion();
                RPos best = null;
                double bestDistance = Double.MAX_VALUE;
                for (Iterator<RPos> it = this.pending.iterator(); it.hasNext(); ) {
                    RPos region = it.next();
                    if (!bounds.contains(region)) {
                        it.remove();
                        this.fragments.remove(region);
                        continue;
                    }
                    double distance = this.distance(region, center);
                    if (distance < bestDistance) {
                        bestDistance = distance;
                        best = region;
                    }
                }
                if (best != null) {
                    this.pending.remove(best);
                    return best;
                }
            }
            return null;
        }
    }

    /**
     * Coalesces repaint requests so a burst of finished fragments triggers a single repaint.
     */
    private void requestRepaint() {
        if (this.repaintQueued.compareAndSet(false, true)) {
            SwingUtilities.invokeLater(() -> {
                this.repaintQueued.set(false);
                this.listener.repaint();
            });
        }
    }

    public void forEachFragment(Consumer<Fragment> consumer) {
        this.fragments.values().forEach(consumer);
    }

    public void terminate() {
        this.running = false;
        synchronized (this.pending) {
            this.pending.clear();
            this.pending.notifyAll();
        }
        for (Thread worker : this.workers) worker.interrupt();
    }

    public void purge() {
        Bounds bounds = this.bounds();
        synchronized (this.pending) {
            this.pending.removeIf(region -> !bounds.contains(region));
        }
        this.fragments.keySet().removeIf(region -> !bounds.contains(region));
    }

    private RPos centerRegion() {
        return this.listener.getManager().getCenterPos().toRegionPos(this.listener.getManager().blocksPerFragment);
    }

    private double distance(RPos region, RPos center) {
        return region.distanceTo(center, Configs.USER_PROFILE.getUserSettings().getFragmentMetric());
    }

    public double distanceToCenter(RPos regionPos) {
        return this.distance(regionPos, this.centerRegion());
    }

    /**
     * Snapshot of the loaded area, computed once per batch instead of once per region.
     * @param minX
     * @param maxX
     * @param minZ
     * @param maxZ
     */
    private record Bounds(int minX, int maxX, int minZ, int maxZ) {
        boolean contains(RPos region) {
            return region.getX() >= this.minX && region.getX() <= this.maxX
                && region.getZ() >= this.minZ && region.getZ() <= this.maxZ;
        }
    }

    private Bounds bounds() {
        BPos min = this.listener.getManager().getPos(0, 0);
        BPos max = this.listener.getManager().getPos(this.listener.getWidth(), this.listener.getHeight());
        int size = this.listener.getManager().blocksPerFragment;
        RPos regionMin = min.toRegionPos(size);
        RPos regionMax = max.toRegionPos(size);
        return new Bounds(regionMin.getX() - MARGIN, regionMax.getX() + MARGIN,
            regionMin.getZ() - MARGIN, regionMax.getZ() + MARGIN);
    }

    public boolean isInBounds(RPos region) {
        return this.bounds().contains(region);
    }

    public Fragment getFragmentAt(int regionX, int regionZ) {
        return this.getFragmentAt(regionX, regionZ, 1);
    }

    public Fragment getFragmentAt(int regionX, int regionZ, int factor) {
        RPos regionPos = new RPos(regionX, regionZ, this.listener.getManager().blocksPerFragment * factor);

        // Atomic claim: exactly one caller schedules a given region.
        Fragment existing = this.fragments.putIfAbsent(regionPos, LOADING_FRAGMENT);
        if (existing == null) {
            synchronized (this.pending) {
                this.pending.add(regionPos);
                this.pending.notify();
            }
            return LOADING_FRAGMENT;
        }
        return existing;
    }
}

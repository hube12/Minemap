package com.seedfinding.minemap.ui.map;

import com.seedfinding.mcbiome.layer.BiomeLayer;

import java.awt.image.BufferedImage;

/**
 * Flood fill of one biome over the part of the map currently on screen.
 * The fill runs on a coarse grid (about 4 screen pixels per cell) and is returned as a small
 * ARGB image anchored in block coordinates, so it can be drawn at any pan/zoom cheaply.
 */
public final class BiomeRegionOverlay {
    private static final int FILL_ARGB = 0x5539FF14;    // translucent neon green
    private static final int BORDER_ARGB = 0xFFEFFFEA;  // near-white core line
    private static final int GLOW_RGB = 0x39FF14;       // neon green halo
    private static final int GLOW_RADIUS = 3;           // cells
    private static final int MAX_CELLS = 1_000_000;

    public final BufferedImage image;
    public final int originBlockX, originBlockZ;
    public final int cellSize, widthCells, heightCells;
    public final double[] viewSignature;
    public final int cellCount;

    private BiomeRegionOverlay(BufferedImage image, int originBlockX, int originBlockZ, int cellSize,
                               int w, int h, double[] viewSignature, int cellCount) {
        this.image = image;
        this.originBlockX = originBlockX;
        this.originBlockZ = originBlockZ;
        this.cellSize = cellSize;
        this.widthCells = w;
        this.heightCells = h;
        this.viewSignature = viewSignature;
        this.cellCount = cellCount;
    }

    /**
     * @param minX,minZ,maxX,maxZ visible block rectangle
     * @param pxPerBlock          current zoom, used to size the cells
     * @return the overlay, or null if the seed is off screen or does not hit the biome
     */
    public static BiomeRegionOverlay compute(BiomeLayer layer, int biomeId, int seedX, int seedZ,
                                             int minX, int minZ, int maxX, int maxZ,
                                             double pxPerBlock, double[] viewSignature) {
        int layerScale = Math.max(1, layer.getScale());
        int cs = Math.max(layerScale, (int) Math.ceil(4.0D / Math.max(pxPerBlock, 1e-6)));

        int ci0, cj0, w, h;
        while (true) {
            ci0 = Math.floorDiv(minX, cs);
            cj0 = Math.floorDiv(minZ, cs);
            w = Math.floorDiv(maxX, cs) - ci0 + 1;
            h = Math.floorDiv(maxZ, cs) - cj0 + 1;
            if ((long) w * h <= MAX_CELLS) break;
            cs *= 2;
        }
        if (w <= 0 || h <= 0) return null;

        byte[] state = new byte[w * h]; // 0 unknown, 1 match (queued/visited), 2 no match
        int[] queue = new int[w * h];
        int head = 0, tail = 0;

        int sx = Math.floorDiv(seedX, cs) - ci0, sz = Math.floorDiv(seedZ, cs) - cj0;
        int start = -1;
        // The seed was sampled on a different grid, so accept a close neighbour cell.
        search:
        for (int r = 0; r <= 3; r++) {
            for (int dx = -r; dx <= r; dx++) {
                for (int dz = -r; dz <= r; dz++) {
                    int x = sx + dx, z = sz + dz;
                    if (x < 0 || z < 0 || x >= w || z >= h) continue;
                    if (matches(layer, layerScale, biomeId, (ci0 + x) * cs + cs / 2, (cj0 + z) * cs + cs / 2)) {
                        start = z * w + x;
                        break search;
                    }
                }
            }
        }
        if (start < 0) return null;

        state[start] = 1;
        queue[tail++] = start;
        int count = 0;
        while (head < tail) {
            int cur = queue[head++];
            count++;
            int x = cur % w, z = cur / w;
            if (x > 0) tail = visit(layer, layerScale, biomeId, state, queue, tail, cur - 1, ci0 + x - 1, cj0 + z, cs);
            if (x < w - 1) tail = visit(layer, layerScale, biomeId, state, queue, tail, cur + 1, ci0 + x + 1, cj0 + z, cs);
            if (z > 0) tail = visit(layer, layerScale, biomeId, state, queue, tail, cur - w, ci0 + x, cj0 + z - 1, cs);
            if (z < h - 1) tail = visit(layer, layerScale, biomeId, state, queue, tail, cur + w, ci0 + x, cj0 + z + 1, cs);
        }

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        int[] pixels = new int[w * h];
        for (int z = 0; z < h; z++) {
            for (int x = 0; x < w; x++) {
                if (state[z * w + x] != 1) continue;
                // Border = member with a non-member neighbour inside the grid (screen edge is not a border).
                boolean border = (x > 0 && state[z * w + x - 1] != 1)
                    || (x < w - 1 && state[z * w + x + 1] != 1)
                    || (z > 0 && state[(z - 1) * w + x] != 1)
                    || (z < h - 1 && state[(z + 1) * w + x] != 1);
                pixels[z * w + x] = border ? BORDER_ARGB : FILL_ARGB;
            }
        }
        // Glow: blur the border mask (two box-blur passes) and paint it around/inside the outline.
        float[] glow = new float[w * h];
        for (int i = 0; i < glow.length; i++) glow[i] = pixels[i] == BORDER_ARGB ? 1f : 0f;
        for (int pass = 0; pass < 2; pass++) {
            glow = boxBlur(glow, w, h, GLOW_RADIUS, true);
            glow = boxBlur(glow, w, h, GLOW_RADIUS, false);
        }
        for (int i = 0; i < glow.length; i++) {
            if (pixels[i] == BORDER_ARGB) continue;
            int a = (int) Math.min(255f, glow[i] * 9f * 255f);
            if (a < 6) continue;
            if (pixels[i] == FILL_ARGB) {
                // Inner glow brightens the fill near the edge.
                a = Math.min(255, a + 0x55);
            }
            pixels[i] = (a << 24) | GLOW_RGB;
        }
        image.setRGB(0, 0, w, h, pixels, 0, w);
        return new BiomeRegionOverlay(image, ci0 * cs, cj0 * cs, cs, w, h, viewSignature, count);
    }

    private static float[] boxBlur(float[] src, int w, int h, int r, boolean horizontal) {
        float[] dst = new float[src.length];
        float norm = 1f / (2 * r + 1);
        int outer = horizontal ? h : w, inner = horizontal ? w : h;
        for (int o = 0; o < outer; o++) {
            float sum = 0f;
            for (int i = -r; i <= r; i++) {
                if (i >= 0 && i < inner) sum += src[horizontal ? o * w + i : i * w + o];
            }
            for (int i = 0; i < inner; i++) {
                dst[horizontal ? o * w + i : i * w + o] = sum * norm;
                int add = i + r + 1, rem = i - r;
                if (add < inner) sum += src[horizontal ? o * w + add : add * w + o];
                if (rem >= 0) sum -= src[horizontal ? o * w + rem : rem * w + o];
            }
        }
        return dst;
    }

    private static int visit(BiomeLayer layer, int layerScale, int biomeId, byte[] state, int[] queue, int tail,
                             int index, int ci, int cj, int cs) {
        if (state[index] != 0) return tail;
        if (matches(layer, layerScale, biomeId, ci * cs + cs / 2, cj * cs + cs / 2)) {
            state[index] = 1;
            queue[tail++] = index;
        } else {
            state[index] = 2;
        }
        return tail;
    }

    private static boolean matches(BiomeLayer layer, int layerScale, int biomeId, int blockX, int blockZ) {
        return layer.getBiome(Math.floorDiv(blockX, layerScale), 0, Math.floorDiv(blockZ, layerScale)) == biomeId;
    }
}

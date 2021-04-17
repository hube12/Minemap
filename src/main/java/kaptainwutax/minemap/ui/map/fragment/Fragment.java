package kaptainwutax.minemap.ui.map.fragment;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.layer.BiomeLayer;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.ui.map.IconManager;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.icon.IconRenderer;
import kaptainwutax.minemap.ui.map.tool.Tool;
import kaptainwutax.minemap.util.data.DrawInfo;
import kaptainwutax.minemap.util.math.DisplayMaths;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.RPos;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;

public class Fragment {

    private final int blockX;
    private final int blockZ;
    private final int regionSize;
    private final MapContext context;

    private int layerIdCache;
    private int[][] biomeCache;
    private Set<Biome> activeBiomesCache;
    private BufferedImage imageCache;

    private Map<Feature<?, ?>, List<BPos>> features;
    private BPos hoveredPos;
    private BPos clickedPos;

    public Fragment(int blockX, int blockZ, int regionSize, MapContext context) {
        this.blockX = blockX;
        this.blockZ = blockZ;
        this.regionSize = regionSize;
        this.context = context;

        if (this.context != null) {
            this.refreshBiomeCache();
            this.refreshImageCache();
            this.generateFeatures();
        }
    }

    public Fragment(BPos pos, int regionSize, MapContext context) {
        this(pos.getX(), pos.getZ(), regionSize, context);
    }

    public Fragment(RPos pos, MapContext context) {
        this(pos.toBlockPos(), pos.getRegionSize(), context);
    }

    public int getX() {
        return this.blockX;
    }

    public int getZ() {
        return this.blockZ;
    }

    public int getSize() {
        return this.regionSize;
    }

    public MapContext getContext() {
        return this.context;
    }

    public void drawBiomes(Graphics graphics, DrawInfo info) {
        this.refreshBiomeCache();
        this.refreshImageCache();

        if (this.imageCache != null && this.context.getSettings().showBiomes) {
            graphics.drawImage(this.imageCache, info.x, info.y, info.width, info.height, null);
        }

        if (this.context.getSettings().showGrid) {
            Color old = graphics.getColor();
            graphics.setColor(Color.BLACK);
            graphics.drawRect(info.x, info.y, info.width, info.height);
            graphics.setColor(old);
        }
    }

    public void drawFeatures(Graphics graphics, DrawInfo info) {
        if (!this.context.getSettings().showFeatures) return;

        Map<Feature<?, ?>, List<BPos>> hovered = this.getHoveredFeatures(info.width, info.height);
        for (Map.Entry<Feature<?, ?>, List<BPos>> entry : this.features.entrySet()) {
            if (!this.context.getSettings().isActive(entry.getKey()) || entry.getValue() == null) continue;

            for (BPos pos : entry.getValue()) {
                this.context.getIconManager().render(graphics, info, entry.getKey(), this, pos, hovered.getOrDefault(entry.getKey(), Collections.emptyList()).contains(pos));
            }
        }
    }

    public void drawTools(Graphics graphics, DrawInfo info, ArrayList<Tool> tools) {
        for (Tool tool : tools) {
            if (tool.isPartial()) {
                Area polygon;
                polygon = new Area(tool.getPartialShape());
                Area rectangle = new Area(this.getRectangle());

                polygon.intersect(rectangle);
                if (!polygon.isEmpty()) {
                    Color old = graphics.getColor();
                    Graphics2D g2d = (Graphics2D) graphics;
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
                    g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
                    g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                    g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
                    g2d.setColor(tool.getColor());

                    // get the correct polygon in the fragment
                    AffineTransform translateToZero = AffineTransform.getTranslateInstance(-blockX, -blockZ);
                    AffineTransform scaleToDisplayFragment = AffineTransform.getScaleInstance(((double) info.width) / ((double) regionSize), ((double) info.height) / ((double) regionSize));
                    scaleToDisplayFragment.concatenate(translateToZero);
                    AffineTransform translateBackToDisplay = AffineTransform.getTranslateInstance(info.x, info.y);
                    translateBackToDisplay.concatenate(scaleToDisplayFragment);
                    polygon = polygon.createTransformedArea(translateBackToDisplay);

                    // decide to fill or hide artefacts due to fragments
                    if (tool.shouldFill()) {
                        g2d.fill(polygon);
                    }
                    if (tool.shouldHideArtefact()) {
                        Color color = new Color(tool.getColor().getRed(), tool.getColor().getGreen(), tool.getColor().getBlue(), 140);
                        g2d.setColor(color);
                        g2d.fill(polygon);
                    } else {
                        int strokeSize = (int) (((double) regionSize) / info.height);
                        g2d.setStroke(new BasicStroke(DisplayMaths.clamp(strokeSize, 1, 7), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
                        g2d.draw(polygon);
                    }
                    g2d.setColor(old);
                }
            }
        }

    }

    public void onHovered(int blockX, int blockZ) {
        this.hoveredPos = new BPos(blockX, 0, blockZ);
    }

    public void onClicked(int blockX, int blockZ) {
        this.clickedPos = new BPos(blockX, 0, blockZ);
    }

    public Map<Feature<?, ?>, List<BPos>> getClickedFeatures(int width, int height) {
        return getFeatures(width, height, this.clickedPos);
    }

    public Map<Feature<?, ?>, List<BPos>> getHoveredFeatures(int width, int height) {
        return getFeatures(width, height, this.hoveredPos);
    }

    public Map<Feature<?, ?>, List<BPos>> getFeatures(int width, int height, BPos checkPos) {
        if (checkPos == null || this.context == null || !this.context.getSettings().showFeatures) {
            return Collections.emptyMap();
        }

        Map<Feature<?, ?>, List<BPos>> map = new HashMap<>();

        for (Map.Entry<Feature<?, ?>, List<BPos>> entry : this.features.entrySet()) {
            if (!this.context.getSettings().isActive(entry.getKey()) || entry.getValue() == null) continue;
            IconRenderer renderer = this.context.getIconManager().getFor(entry.getKey());
            ArrayList<BPos> newList = new ArrayList<>(entry.getValue());
            newList.removeIf(pos -> !renderer.isHovered(this, checkPos, pos, width, height, entry.getKey()));
            map.put(entry.getKey(), newList);
        }

        return map;
    }

    private void refreshBiomeCache() {
        if (this.biomeCache != null && this.layerIdCache == this.context.getLayerId()) return;

        this.layerIdCache = this.context.getLayerId();
        BiomeLayer layer = this.context.getBiomeLayer();
        int effectiveRegion = Math.max(this.regionSize / layer.getScale(), 1);
        RPos region = new BPos(this.blockX, 0, this.blockZ).toRegionPos(layer.getScale());

        if (this.biomeCache == null || this.biomeCache.length != effectiveRegion) {
            this.biomeCache = new int[effectiveRegion][effectiveRegion];
        }

        for (int x = 0; x < effectiveRegion; x++) {
            for (int z = 0; z < effectiveRegion; z++) {
                this.biomeCache[x][z] = layer.get(region.getX() + x, 0, region.getZ() + z);
            }
        }

        this.refreshImageCache();
    }

    private void refreshImageCache() {
        if (this.imageCache != null && this.context.getSettings().getActiveBiomes().equals(this.activeBiomesCache)) return;

        int scaledSize = this.biomeCache.length;
        this.activeBiomesCache = this.context.getSettings().getActiveBiomes();
        this.imageCache = new BufferedImage(scaledSize, scaledSize, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < scaledSize; x++) {
            for (int z = 0; z < scaledSize; z++) {
                Biome biome = Biome.REGISTRY.get(this.biomeCache[x][z]);
                if (biome == null) continue;
                Color color = Configs.BIOME_COLORS.get(Configs.USER_PROFILE.getUserSettings().style, biome);

                if (!this.activeBiomesCache.contains(biome)) {
                    color = makeInactive(color);
                }

                this.imageCache.setRGB(x, z, color.getRGB());
            }
        }
    }

    private Color makeInactive(Color c) {
        int r = c.getRed(), g = c.getGreen(), b = c.getBlue(), sum = r + g + b;
        return new Color((r + sum) / 30, (g + sum) / 30, (b + sum) / 30, c.getAlpha());
    }

    private void generateFeatures() {
        this.features = new LinkedHashMap<>();
        IconManager iconManager = this.context.getIconManager();
        for (Feature<?, ?> feature : this.context.getSettings().getAllFeatures(iconManager.getZValueSorter())) {
            List<BPos> positions = iconManager.getPositions(feature, this);
            positions.removeIf(pos -> !this.isPosInFragment(pos));
            this.features.put(feature, positions);
        }
    }

    public boolean isPosInFragment(BPos pos) {
        return this.isPosInFragment(pos.getX(), pos.getZ());
    }

    public boolean isPosInFragment(int blockX, int blockZ) {
        if (blockX < this.getX() || blockX >= this.getX() + this.getSize()) return false;
        return blockZ >= this.getZ() && blockZ < this.getZ() + this.getSize();
    }

    public Rectangle getRectangle() {
        return new Rectangle(blockX, blockZ, regionSize, regionSize);
    }

    @Override
    public String toString() {
        return "Fragment{" +
                "blockX=" + blockX +
                ", blockZ=" + blockZ +
                ", regionSize=" + regionSize +
                ", context=" + context +
                ", layerIdCache=" + layerIdCache +
                ", biomeCache=" + Arrays.toString(biomeCache) +
                ", activeBiomesCache=" + activeBiomesCache +
                ", imageCache=" + imageCache +
                ", features=" + features +
                ", hoveredPos=" + hoveredPos +
                '}';
    }
}

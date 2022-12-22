package com.seedfinding.minemap.ui.map.fragment;

import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mcbiome.biome.Biomes;
import com.seedfinding.mcbiome.layer.BiomeLayer;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.RPos;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcterrain.TerrainGenerator;
import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.init.Configs;
import com.seedfinding.minemap.ui.map.IconManager;
import com.seedfinding.minemap.ui.map.MapContext;
import com.seedfinding.minemap.ui.map.MapPanel;
import com.seedfinding.minemap.ui.map.icon.IconRenderer;
import com.seedfinding.minemap.ui.map.tool.Tool;
import com.seedfinding.minemap.util.data.DrawInfo;
import com.seedfinding.minemap.util.math.DisplayMaths;
import com.seedfinding.minemap.util.ui.graphics.Graphic;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.*;
import java.util.function.Consumer;

public class Fragment {

    private final int blockX;
    private final int blockZ;
    private final int regionSize;
    private final MapContext context;

    private int layerIdCache;
    private int[][] biomeCache;
    private int[][] heightCache;
    private Set<Biome> activeBiomesCache;
    private BufferedImage imageCache;
    private int lastCheatingBiome = 1;
    private int lastCheatingHeight = 1;
    private boolean hasBiomeModified = false;
    private boolean hasHeightModified = false;

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
        if (this.context.getSettings().isDirty()) {
            this.refreshBiomeImageCache();
        }

        if (this.imageCache != null) {
            graphics.drawImage(this.imageCache, info.x, info.y, info.width, info.height, null);
        }
    }

    public void drawHeight(Graphics graphics, DrawInfo info) {
        this.refreshHeightCache();
        if (this.context.getSettings().isDirty()) {
            this.refreshHeightImageCache();
        }

        if (this.imageCache != null && this.context.getSettings().showBiomes) {
            graphics.drawImage(this.imageCache, info.x, info.y, info.width, info.height, null);
        }
    }

    public void drawGrid(Graphics graphics, DrawInfo info) {
        if (this.context.getSettings().showGrid) {
            Color old = graphics.getColor();
            graphics.setColor(Color.BLACK);
            graphics.drawRect(info.x, info.y, info.width, info.height);
            graphics.setColor(old);
        }
    }

    /**
     * Specific Wrapper that check the context is not null for critical parts
     *
     * @param action the function to be called onto the fragment
     */
    public void drawNonLoading(Consumer<Fragment> action) {
        if (this.context != null) {
            action.accept(this);
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
                if (tool.isMultiplePolygon()) {
                    if (tool.getPartialShapes() == null) continue;
                    List<Shape> shapes = tool.getPartialShapes();
                    for (Shape shape : shapes) {
                        drawSinglePolygon(graphics, info, tool, new Area(shape));
                    }
                } else {
                    if (tool.getPartialShape() == null) continue;
                    drawSinglePolygon(graphics, info, tool, new Area(tool.getPartialShape()));
                }
            }
        }
    }


    private void drawSinglePolygon(Graphics graphics, DrawInfo info, Tool tool, Area polygon) {
        Area rectangle = new Area(this.getRectangle());

        polygon.intersect(rectangle);
        if (!polygon.isEmpty()) {
            Color old = graphics.getColor();
            Graphics2D g2d = Graphic.setGoodRendering(Graphic.withoutDithering(graphics));
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

    private void refreshHeightCache() {
        MapPanel panel = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
        int cheating;
        if (panel != null && panel.manager != null) {
            cheating = Math.max(Configs.USER_PROFILE.getUserSettings().cheatingHeight, (int) (panel.manager.blocksPerFragment / 2 / panel.manager.pixelsPerFragment));
            cheating = Math.max(cheating,1);
            if (this.heightCache != null && lastCheatingHeight <= cheating) return;
        } else {
            cheating = Configs.USER_PROFILE.getUserSettings().cheatingHeight;
        }
        lastCheatingHeight = cheating;
        BiomeLayer layer = this.context.getBiomeLayer();
        int effectiveRegion = Math.max(Math.max(this.regionSize / layer.getScale(), 1) / cheating, 1);
        TerrainGenerator terrainGenerator = this.context.getTerrainGenerator();

        if (this.heightCache == null || this.heightCache.length != effectiveRegion) {
            this.heightCache = new int[effectiveRegion][effectiveRegion];
        }
        for (int x = 0; x < effectiveRegion; x++) {
            for (int z = 0; z < effectiveRegion; z++) {
                this.heightCache[x][z] = terrainGenerator == null ? -1 : terrainGenerator.getHeightOnGround(this.blockX + x * layer.getScale() * cheating, this.blockZ + z * layer.getScale() * cheating);
            }
        }
        hasHeightModified = true;
        this.refreshHeightImageCache();
    }

    private void refreshHeightImageCache() {
        if (this.imageCache != null && !hasHeightModified) return;
        hasHeightModified = false;
        int scaledSize = this.heightCache.length;
        this.imageCache = new BufferedImage(scaledSize, scaledSize, BufferedImage.TYPE_INT_RGB);
        TerrainGenerator generator = this.context.getTerrainGenerator();
        int minGen = generator == null ? 0 : generator.getMinWorldHeight();
        int maxGen = generator == null ? 0 : generator.getMaxWorldHeight();
        Color minColor = Color.WHITE;
        Color maxColor = Color.BLACK;
        Color defaultColor = Color.orange;
        for (int x = 0; x < scaledSize; x++) {
            for (int z = 0; z < scaledSize; z++) {
                Color color = get2DGradientColor(this.heightCache[x][z], minGen, maxGen, minColor, maxColor, defaultColor);
                this.imageCache.setRGB(x, z, color.getRGB());
            }
        }
    }

    private void refreshBiomeCache() {
        int cheating=1;
        if (MineMap.INSTANCE ==null){
            if(this.biomeCache != null && this.layerIdCache == this.context.getLayerId())return;
        }else{
            MapPanel panel = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            if (panel != null && panel.manager != null) {
                cheating = Math.max(1, (int) (panel.manager.blocksPerFragment / 16 / panel.manager.pixelsPerFragment));
                if (this.biomeCache != null && this.layerIdCache == this.context.getLayerId() && lastCheatingBiome <= cheating) return;
            }
            lastCheatingBiome = cheating;
        }

        this.layerIdCache = this.context.getLayerId();
        BiomeLayer layer = this.context.getBiomeLayer();
        int effectiveRegion = Math.max(Math.max(this.regionSize / layer.getScale(), 1) / cheating, 1);
        RPos region = new BPos(this.blockX, 0, this.blockZ).toRegionPos(layer.getScale());

        if (this.biomeCache == null || this.biomeCache.length != effectiveRegion) {
            this.biomeCache = new int[effectiveRegion][effectiveRegion];
        }

//        if (layer instanceof IntBiomeLayer){
//            int[] biomes=((IntBiomeLayer) layer).sample(region.getX() ,0, region.getZ(),effectiveRegion,1,effectiveRegion);
//            for (int x = 0; x < effectiveRegion; x++) {
//                this.biomeCache[x]=Arrays.copyOfRange(biomes,x*effectiveRegion,(x+1)*effectiveRegion);
//            }
//        }else{
//            for (int x = 0; x < effectiveRegion; x++) {
//                for (int z = 0; z < effectiveRegion; z++) {
//                    this.biomeCache[x][z]=layer.getBiome(region.getX() + x, 0, region.getZ() + z);
//                }
//            }
//        }
        for (int x = 0; x < effectiveRegion; x++) {
            for (int z = 0; z < effectiveRegion; z++) {
                this.biomeCache[x][z] = layer.getBiome(region.getX() + x * cheating, 0, region.getZ() + z * cheating);
            }
        }
        hasBiomeModified = true;
        this.refreshBiomeImageCache();
    }

    private void refreshBiomeImageCache() {
        if (this.imageCache != null && this.context.getSettings().getActiveBiomes().equals(this.activeBiomesCache) && !hasBiomeModified) return;
        hasBiomeModified = false;
        int scaledSize = this.biomeCache.length;
        this.activeBiomesCache = this.context.getSettings().getActiveBiomes();
        this.imageCache = new BufferedImage(scaledSize, scaledSize, BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < scaledSize; x++) {
            for (int z = 0; z < scaledSize; z++) {
                Biome biome = Biomes.REGISTRY.get(this.biomeCache[x][z]);
                if (biome == null) continue;
                Color color = Configs.BIOME_COLORS.get(Configs.USER_PROFILE.getUserSettings().style, biome);

                if (!this.activeBiomesCache.contains(biome)) {
                    color = makeInactive(color);
                }

                this.imageCache.setRGB(x, z, color.getRGB());
            }
        }
    }

    /**
     * Calculates a 2D gradient color based on parameters
     *
     * @param value   the value obtained
     * @param min     the min value corresponding to the @from Color
     * @param max     the max value corresponding to the @to Color
     * @param from    The base color corresponding to @min
     * @param to      The base color corresponding to @to
     * @param outside The default color if outside of the range [min;max]
     * @return a Color within the from-to range based on the value of current within the range [min;max],
     * if outside then the default outside is used
     */
    public static Color get2DGradientColor(int value, int min, int max, Color from, Color to, Color outside) {
        // we can not work with such bad decisions
        if (min > max) {
            throw new IllegalArgumentException("Min should be less than max");
        }
        if (value < min || value > max) {
            return outside;
        }
        // if max==min==value then ratio=0/1=0
        double ratio = (double) (value - min) / (double) Math.min(max - min, 1) / 100.0D;
        int red = (int) DisplayMaths.smartClamp(to.getRed() * ratio + from.getRed() * (1 - ratio), from.getRed(), to.getRed());
        int green = (int) DisplayMaths.smartClamp(to.getGreen() * ratio + from.getGreen() * (1 - ratio), from.getGreen(), to.getGreen());
        int blue = (int) DisplayMaths.smartClamp(to.getBlue() * ratio + from.getBlue() * (1 - ratio), from.getBlue(), to.getBlue());
        int alpha = (int) DisplayMaths.smartClamp(to.getAlpha() * ratio + from.getAlpha() * (1 - ratio), from.getAlpha(), to.getAlpha());
        return new Color(red, green, blue, alpha);
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

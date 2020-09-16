package kaptainwutax.minemap.ui.map.fragment;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.layer.BiomeLayer;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.ui.DrawInfo;
import kaptainwutax.minemap.ui.map.IconManager;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.icon.IconRenderer;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.RPos;

import java.awt.*;
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

    public Fragment(int blockX, int blockZ, int regionSize, MapContext context) {
        this.blockX = blockX;
        this.blockZ = blockZ;
        this.regionSize = regionSize;
        this.context = context;

        if(this.context != null) {
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

        if(this.imageCache != null && this.context.getSettings().showBiomes) {
            graphics.drawImage(this.imageCache, info.x, info.y, info.width, info.height, null);
        }

        if(this.context.getSettings().showGrid) {
            Color old = graphics.getColor();
            graphics.setColor(Color.BLACK);
            graphics.drawRect(info.x, info.y, info.width - 1, info.height - 1);
            graphics.setColor(old);
        }
    }

    public void drawFeatures(Graphics graphics, DrawInfo info) {
        if(!this.context.getSettings().showFeatures)return;

        Map<Feature<?, ?>, List<BPos>> hovered = this.getHoveredFeatures(info.width, info.height);

        for(Map.Entry<Feature<?, ?>, List<BPos>> entry: this.features.entrySet()) {
            if(!this.context.getSettings().isActive(entry.getKey()) || entry.getValue() == null)continue;

            for(BPos pos: entry.getValue()) {
                if(hovered.getOrDefault(entry.getKey(), Collections.emptyList()).contains(pos))continue;
                this.context.getIconManager().render(graphics, info, entry.getKey(), this, pos, false);
            }
        }

        this.getHoveredFeatures(info.width, info.height).forEach((feature, positions) -> {
            if(!this.context.getSettings().isActive(feature) || positions == null)return;

            for(BPos pos: positions) {
                this.context.getIconManager().render(graphics, info, feature, this, pos, true);
            }
        });
    }

    public void onHovered(int blockX, int blockZ) {
        this.hoveredPos = new BPos(blockX, 0, blockZ);
    }

    public Map<Feature<?, ?>, List<BPos>> getHoveredFeatures(int width, int height) {
        if(this.hoveredPos == null || this.context == null || !this.context.getSettings().showFeatures) {
            return Collections.emptyMap();
        }

        Map<Feature<?, ?>, List<BPos>> map = new HashMap<>();

        for(Map.Entry<Feature<?, ?>, List<BPos>> entry: this.features.entrySet()) {
            if(!this.context.getSettings().isActive(entry.getKey()) || entry.getValue() == null)continue;
            IconRenderer renderer = this.context.getIconManager().getFor(entry.getKey());
            ArrayList<BPos> newList = new ArrayList<>(entry.getValue());
            newList.removeIf(pos -> !renderer.isHovered(this, this.hoveredPos, pos, width, height));
            map.put(entry.getKey(), newList);
        }

        return map;
    }

    private void refreshBiomeCache() {
        if(this.biomeCache != null && this.layerIdCache == this.context.getLayerId())return;

        this.layerIdCache = this.context.getLayerId();
        BiomeLayer layer = this.context.getBiomeLayer();
        int effectiveRegion = Math.max(this.regionSize / layer.getScale(), 1);
        RPos region = new BPos(this.blockX, 0, this.blockZ).toRegionPos(layer.getScale());

        if(this.biomeCache == null || this.biomeCache.length != effectiveRegion) {
            this.biomeCache = new int[effectiveRegion][effectiveRegion];
        }

        for(int x = 0; x < effectiveRegion; x++) {
            for(int z = 0; z < effectiveRegion; z++) {
                this.biomeCache[x][z] = layer.get(region.getX() + x, 0, region.getZ() + z);
            }
        }

        this.refreshImageCache();
    }

    private void refreshImageCache() {
        if(this.imageCache != null && this.context.getSettings().getActiveBiomes().equals(this.activeBiomesCache))return;

        int scaledSize = this.biomeCache.length;
        this.activeBiomesCache = this.context.getSettings().getActiveBiomes();
        this.imageCache = new BufferedImage(scaledSize, scaledSize, BufferedImage.TYPE_INT_RGB);

        for(int x = 0; x < scaledSize; x++) {
            for(int z = 0; z < scaledSize; z++) {
                Biome biome = Biome.REGISTRY.get(this.biomeCache[x][z]);
                if(biome == null)continue;
                Color color = Configs.BIOME_COLORS.get(Configs.USER_PROFILE.getUserSettings().style, biome);

                if(!this.activeBiomesCache.contains(biome)) {
                    color = makeInactive(color);
                }

                this.imageCache.setRGB(x, z, color.getRGB());
            }
        }
    }

    private static final int INACTIVE_DIVISOR = 30;

    private Color makeInactive(Color c) {
    	int sum = c.getRed() + c.getGreen() + c.getBlue();
    	
    	return new Color((int)((c.getRed() + sum) / INACTIVE_DIVISOR),
    					 (int)((c.getGreen() + sum) / INACTIVE_DIVISOR),
		    			 (int)((c.getBlue() + sum) / INACTIVE_DIVISOR),
		    			 c.getAlpha());
    }

    private void generateFeatures() {
        this.features = new LinkedHashMap<>();
        IconManager iconManager = this.context.getIconManager();

        for(Feature<?, ?> feature: this.context.getSettings().getAllFeatures(iconManager.getZValueSorter())) {
            List<BPos> positions = iconManager.getPositions(feature, this);
            positions.removeIf(pos -> !this.isPosInFragment(pos));
            this.features.put(feature, positions);
        }
    }

    public boolean isPosInFragment(BPos pos) {
        return this.isPosInFragment(pos.getX(), pos.getZ());
    }

    public boolean isPosInFragment(int blockX, int blockZ) {
        if(blockX < this.getX() || blockX >= this.getX() + this.getSize())return false;
        if(blockZ < this.getZ() || blockZ >= this.getZ() + this.getSize())return false;
        return true;
    }

}

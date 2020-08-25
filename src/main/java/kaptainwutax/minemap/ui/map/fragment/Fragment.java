package kaptainwutax.minemap.ui.map.fragment;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.layer.BiomeLayer;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.ui.DrawInfo;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.RPos;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            graphics.setColor(Color.BLACK);
            graphics.drawRect(info.x, info.y, info.width - 1, info.height - 1);
        }
    }

    public void drawFeatures(Graphics graphics, DrawInfo info) {
        if(!this.context.getSettings().showFeatures)return;

        for(Map.Entry<Feature<?, ?>, List<BPos>> entry: this.features.entrySet()) {
            if(!this.context.getSettings().isActive(entry.getKey()) || entry.getValue() == null)continue;

            for(BPos pos: entry.getValue()) {
                this.context.getIconManager().render(graphics, info, entry.getKey(), this, pos);
            }
        }
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
                Biome biome = Biome.REGISTRY.get(layer.get(region.getX() + x, 0, region.getZ() + z));
                this.biomeCache[x][z] = biome == null ? -1 : biome.getId();
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
                Color color = Configs.BIOME_COLORS.get(Configs.USER_PROFILE.getStyle(), biome);

                if(!this.activeBiomesCache.contains(biome)) {
                    color = color.darker().darker().darker().darker().darker();
                }

                this.imageCache.setRGB(x, z, color.getRGB());
            }
        }
    }

    private void generateFeatures() {
        this.features = new HashMap<>();

        for(Feature<?, ?> feature: this.context.getSettings().getAllFeatures()) {
            List<BPos> positions = this.context.getIconManager().getPositions(feature, this);

            positions.removeIf(pos -> {
                if(pos.getX() < this.getX() || pos.getX() >= this.getX() + this.getSize())return true;
                if(pos.getZ() < this.getZ() || pos.getZ() >= this.getZ() + this.getSize())return true;
                return false;
            });

            this.features.put(feature, positions);
        }
    }

}

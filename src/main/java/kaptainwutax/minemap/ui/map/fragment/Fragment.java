package kaptainwutax.minemap.ui.map.fragment;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.layer.BiomeLayer;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.ui.DrawInfo;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.icon.StaticIcon;
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
    private BPos mousePos;

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

        Map<Feature<?, ?>, List<BPos>> hovered = this.getHoveredFeatures(info.width, info.height);

        for(Map.Entry<Feature<?, ?>, List<BPos>> entry: this.features.entrySet()) {
            if(!this.context.getSettings().isActive(entry.getKey()) || entry.getValue() == null)continue;

            for(BPos pos: entry.getValue()) {
                if(hovered.getOrDefault(entry.getKey(), Collections.emptyList()).contains(pos))continue;
                this.context.getIconManager().render(graphics, info, entry.getKey(), this, pos);
            }
        }

        this.getHoveredFeatures(info.width, info.height).forEach((feature, positions) -> {
            if(!this.context.getSettings().isActive(feature) || positions == null)return;
            StaticIcon.ICON_SIZE = 38;

            for(BPos pos: positions) {
                this.context.getIconManager().render(graphics, info, feature, this, pos);
            }
        });

        StaticIcon.ICON_SIZE = 24;
    }

    public void onHovered(int blockX, int blockZ) {
        this.mousePos = this.isPosInFragment(blockX, blockZ) ? new BPos(blockX, 0, blockZ) : null;
    }

    public Map<Feature<?, ?>, List<BPos>> getHoveredFeatures(int width, int height) {
        if(this.mousePos == null || !this.context.getSettings().showFeatures)return Collections.emptyMap();
        double distanceX = (this.regionSize / (double)width) * (StaticIcon.ICON_SIZE / 2.0D);
        double distanceZ = (this.regionSize / (double)height) * (StaticIcon.ICON_SIZE / 2.0D);

        Map<Feature<?, ?>, List<BPos>> map = new HashMap<>();

        for(Map.Entry<Feature<?, ?>, List<BPos>> entry: this.features.entrySet()) {
            if(!this.context.getSettings().isActive(entry.getKey()) || entry.getValue() == null)continue;

            ArrayList<BPos> newList = new ArrayList<>(entry.getValue());

            newList.removeIf(pos -> {
                int dx = Math.abs(this.mousePos.getX() - pos.getX());
                int dz = Math.abs(this.mousePos.getZ() - pos.getZ());
                return dx >= distanceX || dz >= distanceZ;
            });

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

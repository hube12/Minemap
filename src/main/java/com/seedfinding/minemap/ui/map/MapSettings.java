package com.seedfinding.minemap.ui.map;

import com.google.gson.annotations.Expose;
import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mcbiome.biome.Biomes;
import com.seedfinding.mcbiome.source.OverworldBiomeSource;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.misc.SlimeChunk;
import com.seedfinding.mcfeature.structure.Mineshaft;
import com.seedfinding.mcfeature.structure.NetherFossil;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.minemap.feature.*;
import com.seedfinding.minemap.init.Features;
import com.seedfinding.minemap.feature.*;

import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

public class MapSettings {
    public static final Set<Class<? extends Feature<?, ?>>> DEFAULT_HIDE = new HashSet<Class<? extends Feature<?, ?>>>() {{
        add(SlimeChunk.class);
        add(Mineshaft.class);
        add(OWBastionRemnant.class);
        add(OWFortress.class);
        add(NetherFossil.class);
        add(NEStronghold.class);
        add(OWNERuinedPortal.class);
        add(NERuinedPortal.class);
    }};
    public static final boolean DEFAULT_SHOW_BIOMES = true;
    public static final boolean DEFAULT_SHOW_FEATURES = true;
    public static final boolean DEFAULT_SHOW_GRID = false;
    public static final boolean DEFAULT_SHOW_EXTRA_INFOS = false;
    public static final boolean DEFAULT_SHOW_EXTRA_ICONS = true;
    public static final int DEFAULT_BIOME_SIZE = OverworldBiomeSource.DEFAULT_BIOME_SIZE;
    public static final int DEFAULT_RIVER_SIZE = OverworldBiomeSource.DEFAULT_RIVER_SIZE;

    private MCVersion version;
    private Dimension dimension;
    @Expose
    public Boolean showBiomes = true;
    @Expose
    public Boolean showFeatures = true;
    @Expose
    public Boolean showGrid = false;
    @Expose
    public Boolean showExtraInfos = false;
    @Expose
    public Boolean showExtraIcons = true;
    @Expose
    public Integer biomeSize = 4;
    @Expose
    public Integer riverSize = 4;
    @Expose
    private Map<String, Boolean> features;
    @Expose
    private Map<String, Boolean> biomes;
    private Map<Class<? extends Feature<?, ?>>, Feature<?, ?>> featureTypes;
    private Map<Class<? extends Feature<?, ?>>, Boolean> featureStates;
    private Map<Biome, Boolean> biomeStates;
    private boolean isDirty;

    public MapSettings(Dimension dimension) {
        this(MCVersion.values()[0], dimension);
    }

    public MapSettings(MCVersion version, Dimension dimension) {
        this.version = version;
        this.dimension = dimension;

        this.features = Features.getForVersion(this.version).values().stream()
            .filter(f -> f.isValidDimension(this.dimension))
            .map(Feature::getName)
            .collect(Collectors.toMap(e -> e, e -> true));

        this.biomes = Biomes.REGISTRY.values().stream()
            .filter(b -> b.getDimension() == this.dimension)
            .map(Biome::getName)
            .collect(Collectors.toMap(e -> e, e -> true));
    }

    public MCVersion getVersion() {
        return this.version;
    }

    public Dimension getDimension() {
        return this.dimension;
    }

    public MapSettings refresh() {
        this.featureTypes = Features.getForVersion(this.version).entrySet().stream()
            .filter(e -> this.dimension == null || e.getValue().isValidDimension(this.dimension))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        this.featureStates = this.featureTypes.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                e -> this.features.getOrDefault(e.getValue().getName(), true)
            ));

        this.biomeStates = Biomes.REGISTRY.values().stream()
            .filter(b -> b.getDimension() == this.dimension)
            .filter(b -> b.getVersion().isOlderOrEqualTo(this.version) || b.getDimension() == Dimension.END)
            .collect(Collectors.toMap(
                e -> e,
                e -> this.biomes.getOrDefault(e.getName(), true)
            ));

        return this;
    }

    @SuppressWarnings("unchecked")
    public MapSettings setState(Feature<?, ?> feature, boolean state) {
        return this.setState((Class<? extends Feature<?, ?>>) feature.getClass(), state);
    }

    public MapSettings setState(Class<? extends Feature<?, ?>> feature, boolean state) {
        this.featureStates.replace(feature, state);
        Feature<?, ?> f = this.featureTypes.get(feature);
        if (f != null) this.features.put(f.getName(), state);
        return this;
    }

    public boolean isDirty() {
        return isDirty;
    }

    public void setDirty(boolean dirty) {
        isDirty = dirty;
    }

    public MapSettings setState(Biome biome, boolean state) {
        this.biomeStates.replace(biome, state);
        this.biomes.put(biome.getName(), state);
        return this;
    }

    @SuppressWarnings("unchecked")
    public final MapSettings hide(Feature<?, ?>... features) {
        for (Feature<?, ?> feature : features) {
            this.setState(feature, false);
        }

        return this;
    }

    @SafeVarargs
    public final MapSettings hide(Class<? extends Feature<?, ?>>... features) {
        for (Class<? extends Feature<?, ?>> feature : features) {
            this.setState(feature, false);
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    public final MapSettings show(Feature<?, ?>... features) {
        for (Feature<?, ?> feature : features) {
            this.setState(feature, true);
        }

        return this;
    }

    @SafeVarargs
    public final MapSettings show(Class<? extends Feature<?, ?>>... features) {
        for (Class<? extends Feature<?, ?>> feature : features) {
            this.setState(feature, true);
        }

        return this;
    }

    public final MapSettings hide(Biome... biomes) {
        for (Biome biome : biomes) {
            this.setState(biome, false);
        }

        return this;
    }

    public final MapSettings show(Biome... biomes) {
        for (Biome biome : biomes) {
            this.setState(biome, true);
        }

        return this;
    }

    @SuppressWarnings("unchecked")
    public <T extends Feature<?, ?>> T getFeatureOfType(Class<? extends T> feature) {
        Feature<?, ?> v = this.featureTypes.get(feature);
        return v == null ? null : (T) v;
    }

    public List<Feature<?, ?>> getAllFeatures() {
        return this.getAllFeatures((o1, o2) -> Collator.getInstance().compare(o1.getName(), o2.getName()));
    }

    public List<Feature<?, ?>> getAllFeatures(Comparator<Feature<?, ?>> comparator) {
        List<Feature<?, ?>> f = new ArrayList<>(this.featureTypes.values());
        f.sort(comparator);
        return f;
    }

    public List<Biome> getAllBiomes() {
        return this.getAllBiomes(Comparator.comparingInt(Biome::getId));
    }

    public List<Biome> getAllBiomes(Comparator<Biome> comparator) {
        List<Biome> b = new ArrayList<>(this.biomeStates.keySet());
        b.sort(comparator);
        return b;
    }

    public Set<Feature<?, ?>> getActiveFeatures() {
        return this.featureStates.entrySet().stream()
            .filter(Map.Entry::getValue)
            .map(Map.Entry::getKey)
            .map(this.featureTypes::get)
            .collect(Collectors.toSet());
    }

    public Set<Biome> getActiveBiomes() {
        return this.biomeStates.entrySet().stream()
            .filter(Map.Entry::getValue)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

    public boolean isActive(Feature<?, ?> feature) {
        return this.featureStates.getOrDefault(feature.getClass(), false)
            && this.featureTypes.containsKey(feature.getClass());
    }

    public boolean isActive(Class<? extends Feature<?, ?>> feature) {
        return this.featureStates.getOrDefault(feature, false)
            && this.featureTypes.containsKey(feature);
    }

    public Integer getBiomeSize() {
        return biomeSize;
    }

    public Integer getRiverSize() {
        return riverSize;
    }

    public void setBiomeSize(Integer biomeSize) {
        this.biomeSize = biomeSize;
    }

    public void setRiverSize(Integer riverSize) {
        this.riverSize = riverSize;
    }

    @SuppressWarnings("unchecked")
    public void resetConfig() {
        this.hide(DEFAULT_HIDE.toArray(new Class[0]));
    }

    public void maintainConfig(Dimension dimension, MCVersion version) {
        this.dimension = dimension;
        this.version = version;
        List<Feature<?, ?>> features = Features.getForVersion(this.version).values().stream()
            .filter(f -> f.isValidDimension(this.dimension))
            .collect(Collectors.toList());
        for (Feature<?, ?> feature : features) {
            if (!this.features.containsKey(feature.getName())) {
                this.features.put(feature.getName(), !DEFAULT_HIDE.contains(feature.getClass()));
            }
        }
        List<Biome> biomes = Biomes.REGISTRY.values().stream()
            .filter(b -> b.getDimension() == this.dimension)
            .collect(Collectors.toList());
        for (Biome biome : biomes) {
            this.biomes.putIfAbsent(biome.getName(), true);
        }
        this.showBiomes = this.showBiomes != null ? this.showBiomes : DEFAULT_SHOW_BIOMES;
        this.showFeatures = this.showFeatures != null ? this.showFeatures : DEFAULT_SHOW_FEATURES;
        this.showGrid = this.showGrid != null ? this.showGrid : DEFAULT_SHOW_GRID;
        this.showExtraInfos = this.showExtraInfos != null ? this.showExtraInfos : DEFAULT_SHOW_EXTRA_INFOS;
        this.showExtraIcons = this.showExtraIcons != null ? this.showExtraIcons : DEFAULT_SHOW_EXTRA_ICONS;
        this.biomeSize = this.biomeSize != null ? this.biomeSize : DEFAULT_BIOME_SIZE;
        this.riverSize = this.riverSize != null ? this.riverSize : DEFAULT_RIVER_SIZE;
    }

    public boolean isActive(Biome biome) {
        return this.biomeStates.getOrDefault(biome, false);
    }

    public MapSettings set(MapSettings other) {
        this.showBiomes = other.showBiomes;
        this.showFeatures = other.showFeatures;
        this.showGrid = other.showGrid;
        this.showExtraInfos = other.showExtraInfos;
        this.showExtraIcons = other.showExtraIcons;
        this.biomeSize = other.biomeSize;
        this.riverSize = other.riverSize;
        this.getAllFeatures().forEach(this::hide);
        this.getAllBiomes().forEach(this::hide);
        other.getActiveBiomes().forEach(this::show);
        other.getActiveFeatures().forEach(this::show);
        return this;
    }

    public MapSettings copy() {
        return this.copyFor(this.version, this.dimension);
    }

    public MapSettings copyFor(MCVersion version, Dimension dimension) {
        MapSettings copy = new MapSettings(version, dimension);
        copy.showBiomes = this.showBiomes;
        copy.showFeatures = this.showFeatures;
        copy.showGrid = this.showGrid;
        copy.showExtraInfos = this.showExtraInfos;
        copy.showExtraIcons = this.showExtraIcons;
        copy.biomeSize = this.biomeSize;
        copy.riverSize = this.riverSize;
        copy.biomes = new HashMap<>(this.biomes);
        copy.features = new HashMap<>(this.features);
        return copy.refresh();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MapSettings)) return false;
        MapSettings that = (MapSettings) o;
        return isDirty == that.isDirty && version == that.version && dimension == that.dimension && Objects.equals(showBiomes, that.showBiomes) && Objects.equals(showFeatures, that.showFeatures) && Objects.equals(showGrid, that.showGrid) && Objects.equals(showExtraInfos, that.showExtraInfos) && Objects.equals(showExtraIcons, that.showExtraIcons) && Objects.equals(biomeSize, that.biomeSize) && Objects.equals(riverSize, that.riverSize);
    }

    @Override
    public int hashCode() {
        return Objects.hash(version, dimension, showBiomes, showFeatures, showGrid, showExtraInfos, showExtraIcons, biomeSize, riverSize, isDirty);
    }
}

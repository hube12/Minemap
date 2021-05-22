package kaptainwutax.minemap.ui.map;

import com.google.gson.annotations.Expose;
import kaptainwutax.biomeutils.biome.Biome;
import kaptainwutax.biomeutils.biome.Biomes;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.minemap.init.Features;

import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

public class MapSettings {

    private final MCVersion version;
    private final Dimension dimension;
    @Expose
    public boolean showBiomes = true;
    @Expose
    public boolean showFeatures = true;
    @Expose
    public boolean showGrid = false;
    @Expose
    public boolean showExtraInfos = false;
    @Expose
    public boolean showExtraIcons = true;
    @Expose
    public int biomeSize=4;
    @Expose
    public int riverSize=4;
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
            .map(Feature::getName).collect(Collectors.toMap(e -> e, e -> true));

        this.biomes = Biomes.REGISTRY.values().stream()
            .filter(b -> b.getDimension() == dimension).map(Biome::getName)
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
            .filter(b -> b.getVersion().isOlderOrEqualTo(this.version) || b.getDimension()==Dimension.END)
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
        this.setDirty(true);
        return this;
    }

    public final MapSettings hide(Feature<?, ?>... features) {
        for (Feature<?, ?> feature : features) {
            this.setState(feature, false);
        }

        return this;
    }

    public final MapSettings show(Feature<?, ?>... features) {
        for (Feature<?, ?> feature : features) {
            this.setState(feature, true);
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

    public boolean isActive(Biome biome) {
        return this.biomeStates.getOrDefault(biome, false);
    }

    public MapSettings set(MapSettings other) {
        this.showBiomes = other.showBiomes;
        this.showFeatures = other.showFeatures;
        this.showGrid = other.showGrid;
        this.showExtraInfos = other.showExtraInfos;
        this.showExtraIcons = other.showExtraIcons;
        this.biomeSize=other.biomeSize;
        this.riverSize=other.riverSize;
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
        copy.biomeSize=this.biomeSize;
        copy.riverSize=this.riverSize;
        copy.biomes = new HashMap<>(this.biomes);
        copy.features = new HashMap<>(this.features);
        return copy.refresh();
    }

}

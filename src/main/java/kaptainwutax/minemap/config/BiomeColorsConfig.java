package kaptainwutax.minemap.config;

import com.google.gson.annotations.Expose;
import kaptainwutax.biomeutils.Biome;

import java.awt.*;
import java.util.List;
import java.util.*;

public class BiomeColorsConfig extends Config {

    public static final String DEFAULT_STYLE_NAME = "Default";
    public final static HashMap<Biome, String> BIOME_COLORS = new LinkedHashMap<>();
    public final static HashMap<Biome, String> BIOME_COLORS_VIBRANT = new LinkedHashMap<>();

    static {
        BIOME_COLORS.put(Biome.OCEAN, "#000070");
        BIOME_COLORS.put(Biome.PLAINS, "#8DB360");
        BIOME_COLORS.put(Biome.SUNFLOWER_PLAINS, "#B5DB88");
        BIOME_COLORS.put(Biome.DESERT, "#FA9418");
        BIOME_COLORS.put(Biome.DESERT_LAKES, "#FFBC40");
        BIOME_COLORS.put(Biome.MOUNTAINS, "#606060");
        BIOME_COLORS.put(Biome.GRAVELLY_MOUNTAINS, "#888888");
        BIOME_COLORS.put(Biome.FOREST, "#056621");
        BIOME_COLORS.put(Biome.FLOWER_FOREST, "#2D8E49");
        BIOME_COLORS.put(Biome.TAIGA, "#0B6659");
        BIOME_COLORS.put(Biome.TAIGA_MOUNTAINS, "#338E81");
        BIOME_COLORS.put(Biome.SWAMP, "#07F9B2");
        BIOME_COLORS.put(Biome.SWAMP_HILLS, "#2FFFDA");
        BIOME_COLORS.put(Biome.RIVER, "#0000FF");
        BIOME_COLORS.put(Biome.NETHER_WASTES, "#572526");
        BIOME_COLORS.put(Biome.THE_END, "#8080FF");
        BIOME_COLORS.put(Biome.FROZEN_OCEAN, "#7070D6");
        BIOME_COLORS.put(Biome.FROZEN_RIVER, "#A0A0FF");
        BIOME_COLORS.put(Biome.SNOWY_TUNDRA, "#FFFFFF");
        BIOME_COLORS.put(Biome.ICE_SPIKES, "#B4DCDC");
        BIOME_COLORS.put(Biome.SNOWY_MOUNTAINS, "#A0A0A0");
        BIOME_COLORS.put(Biome.MUSHROOM_FIELDS, "#FF00FF");
        BIOME_COLORS.put(Biome.MUSHROOM_FIELD_SHORE, "#A000FF");
        BIOME_COLORS.put(Biome.BEACH, "#FADE55");
        BIOME_COLORS.put(Biome.DESERT_HILLS, "#D25F12");
        BIOME_COLORS.put(Biome.WOODED_HILLS, "#22551C");
        BIOME_COLORS.put(Biome.TAIGA_HILLS, "#163933");
        BIOME_COLORS.put(Biome.MOUNTAIN_EDGE, "#72789A");
        BIOME_COLORS.put(Biome.JUNGLE, "#537B09");
        BIOME_COLORS.put(Biome.MODIFIED_JUNGLE, "#7BA331");
        BIOME_COLORS.put(Biome.JUNGLE_HILLS, "#2C4205");
        BIOME_COLORS.put(Biome.JUNGLE_EDGE, "#628B17");
        BIOME_COLORS.put(Biome.MODIFIED_JUNGLE_EDGE, "#8AB33F");
        BIOME_COLORS.put(Biome.DEEP_OCEAN, "#000030");
        BIOME_COLORS.put(Biome.STONE_SHORE, "#A2A284");
        BIOME_COLORS.put(Biome.SNOWY_BEACH, "#FAF0C0");
        BIOME_COLORS.put(Biome.BIRCH_FOREST, "#307444");
        BIOME_COLORS.put(Biome.TALL_BIRCH_FOREST, "#589C6C");
        BIOME_COLORS.put(Biome.BIRCH_FOREST_HILLS, "#1F5F32");
        BIOME_COLORS.put(Biome.TALL_BIRCH_HILLS, "#47875A");
        BIOME_COLORS.put(Biome.DARK_FOREST, "#40511A");
        BIOME_COLORS.put(Biome.DARK_FOREST_HILLS, "#687942");
        BIOME_COLORS.put(Biome.SNOWY_TAIGA, "#31554A");
        BIOME_COLORS.put(Biome.SNOWY_TAIGA_MOUNTAINS, "#597D72");
        BIOME_COLORS.put(Biome.SNOWY_TAIGA_HILLS, "#243F36");
        BIOME_COLORS.put(Biome.GIANT_TREE_TAIGA, "#596651");
        BIOME_COLORS.put(Biome.GIANT_SPRUCE_TAIGA, "#818E79");
        BIOME_COLORS.put(Biome.GIANT_TREE_TAIGA_HILLS, "#454F3E");
        BIOME_COLORS.put(Biome.GIANT_SPRUCE_TAIGA_HILLS, "#6D7766");
        BIOME_COLORS.put(Biome.WOODED_MOUNTAINS, "#507050");
        BIOME_COLORS.put(Biome.MODIFIED_GRAVELLY_MOUNTAINS, "#789878");
        BIOME_COLORS.put(Biome.SAVANNA, "#BDB25F");
        BIOME_COLORS.put(Biome.SHATTERED_SAVANNA, "#E5DA87");
        BIOME_COLORS.put(Biome.SAVANNA_PLATEAU, "#A79D64");
        BIOME_COLORS.put(Biome.SHATTERED_SAVANNA_PLATEAU, "#CFC58C");
        BIOME_COLORS.put(Biome.BADLANDS, "#D94515");
        BIOME_COLORS.put(Biome.ERODED_BADLANDS, "#FF6D3D");
        BIOME_COLORS.put(Biome.WOODED_BADLANDS_PLATEAU, "#B09765");
        BIOME_COLORS.put(Biome.MODIFIED_WOODED_BADLANDS_PLATEAU, "#D8BF8D");
        BIOME_COLORS.put(Biome.BADLANDS_PLATEAU, "#CA8C65");
        BIOME_COLORS.put(Biome.MODIFIED_BADLANDS_PLATEAU, "#F2B48D");
        BIOME_COLORS.put(Biome.SMALL_END_ISLANDS, "#4B4BAB");
        BIOME_COLORS.put(Biome.BAMBOO_JUNGLE, "#768E14");
        BIOME_COLORS.put(Biome.END_MIDLANDS, "#C9C95D");
        BIOME_COLORS.put(Biome.BAMBOO_JUNGLE_HILLS, "#3B470A");
        BIOME_COLORS.put(Biome.END_HIGHLANDS, "#B5B536");
        BIOME_COLORS.put(Biome.SOUL_SAND_VALLEY, "#4D3A2E");
        BIOME_COLORS.put(Biome.END_BARRENS, "#8080FF");
        BIOME_COLORS.put(Biome.CRIMSON_FOREST, "#981A11");
        BIOME_COLORS.put(Biome.WARM_OCEAN, "#0000AC");
        BIOME_COLORS.put(Biome.WARPED_FOREST, "#565C4C");
        BIOME_COLORS.put(Biome.LUKEWARM_OCEAN, "#000090");
        BIOME_COLORS.put(Biome.BASALT_DELTAS, "#6B5F63");
        BIOME_COLORS.put(Biome.COLD_OCEAN, "#202070");
        BIOME_COLORS.put(Biome.DEEP_WARM_OCEAN, "#000050");
        BIOME_COLORS.put(Biome.DEEP_LUKEWARM_OCEAN, "#000040");
        BIOME_COLORS.put(Biome.DEEP_COLD_OCEAN, "#202038");
        BIOME_COLORS.put(Biome.DEEP_FROZEN_OCEAN, "#404090");
        BIOME_COLORS.put(Biome.THE_VOID, "#000000");

        BIOME_COLORS_VIBRANT.put(Biome.CRIMSON_FOREST, "#DD0808");
        BIOME_COLORS_VIBRANT.put(Biome.BASALT_DELTAS, "#403636");
        BIOME_COLORS_VIBRANT.put(Biome.NETHER_WASTES, "#FF7700");
        BIOME_COLORS_VIBRANT.put(Biome.WARPED_FOREST, "#49907B");
        BIOME_COLORS_VIBRANT.put(Biome.SOUL_SAND_VALLEY, "#5E3830");
    }

    @Expose
    protected Map<String, String> DEFAULT_BIOME_COLORS = new LinkedHashMap<>();
    @Expose
    protected Map<String, Map<String, String>> OVERRIDES = new LinkedHashMap<>();
    protected Map<Integer, Color> defaultBiomeColorCache;
    protected Map<String, Map<Integer, Color>> biomeColorCache;

    @Override
    public String getName() {
        return "biome_colors";
    }

    public Color get(String style, Biome biome) {
        return this.get(style, biome.getId());
    }

    public synchronized Color get(String style, int biome) {
        if (this.biomeColorCache == null) {
            this.generateCache();
        }

        return this.biomeColorCache.getOrDefault(style, this.defaultBiomeColorCache)
                .getOrDefault(biome, this.defaultBiomeColorCache.get(biome));
    }

    private void generateCache() {
        this.defaultBiomeColorCache = new HashMap<>();
        this.biomeColorCache = new HashMap<>();

        DEFAULT_BIOME_COLORS.forEach((biomeEntry, colorEntry) -> {
            for (Biome b : Biome.REGISTRY.values()) {
                if (!b.getName().equalsIgnoreCase(biomeEntry.trim())) continue;
                this.defaultBiomeColorCache.put(b.getId(), Color.decode(colorEntry));
                break;
            }
        });

        OVERRIDES.forEach((styleEntry, mapEntry) -> {
            Map<Integer, Color> map = new HashMap<>();
            this.biomeColorCache.put(styleEntry, map);

            mapEntry.forEach((biomeEntry, colorEntry) -> {
                for (Biome b : Biome.REGISTRY.values()) {
                    if (!b.getName().equalsIgnoreCase(biomeEntry.trim())) continue;
                    map.put(b.getId(), Color.decode(colorEntry));
                    break;
                }
            });
        });
    }

    public List<String> getStyles() {
        List<String> styles = new ArrayList<>();
        styles.add(BiomeColorsConfig.DEFAULT_STYLE_NAME);
        styles.addAll(new ArrayList<>(this.OVERRIDES.keySet()));
        return styles;
    }

    @Override
    protected void resetConfig() {
        for (Map.Entry<Biome, String> entry : BIOME_COLORS.entrySet()) {
            this.addDefaultEntry(entry.getKey(), entry.getValue());
        }
        String[] shippedMaps = {"Vibrant"};
        for (String shippedMap : shippedMaps) {
            for (Map.Entry<Biome, String> entry : BIOME_COLORS_VIBRANT.entrySet()) {
                this.addOverrideEntry(shippedMap, entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void maintainConfig() {
        for (Map.Entry<Biome, String> entry : BIOME_COLORS.entrySet()) {
            if (!this.DEFAULT_BIOME_COLORS.containsKey(entry.getKey().getName().toUpperCase())) {
                this.addDefaultEntry(entry.getKey(), entry.getValue());
            }
        }
        String[] shippedMaps = {"Vibrant"};
        for (String shippedMap : shippedMaps) {
            Map<String, String> typeMap = this.OVERRIDES.computeIfAbsent(shippedMap, s -> new LinkedHashMap<>());
            for (Map.Entry<Biome, String> entry : BIOME_COLORS_VIBRANT.entrySet()) {
                if (!typeMap.containsKey(entry.getKey().getName().toUpperCase())) {
                    this.addOverrideEntry(shippedMap, entry.getKey(), entry.getValue());
                }
            }
        }
    }

    protected void addDefaultEntry(Biome biome, String color) {
        this.DEFAULT_BIOME_COLORS.put(biome.getName().toUpperCase(), color.toUpperCase());
    }

    protected void addOverrideEntry(String type, Biome biome, String color) {
        Map<String, String> typeMap = this.OVERRIDES.computeIfAbsent(type, s -> new LinkedHashMap<>());
        typeMap.put(biome.getName().toUpperCase(), color.toUpperCase());
    }
}

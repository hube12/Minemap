package kaptainwutax.minemap.config;

import com.google.gson.annotations.Expose;
import kaptainwutax.biomeutils.biome.Biome;
import kaptainwutax.biomeutils.biome.Biomes;

import java.awt.*;
import java.util.List;
import java.util.*;

public class BiomeColorsConfig extends Config {

    public static final String DEFAULT_STYLE_NAME = "Default";
    public final static HashMap<Biome, String> BIOME_COLORS = new LinkedHashMap<>();
    public final static HashMap<Biome, String> BIOME_COLORS_VIBRANT = new LinkedHashMap<>();
    @Expose
    protected Map<String, String> DEFAULT_BIOME_COLORS = new LinkedHashMap<>();
    @Expose
    protected Map<String, Map<String, String>> OVERRIDES = new LinkedHashMap<>();
    protected Map<Integer, Color> defaultBiomeColorCache;
    protected Map<String, Map<Integer, Color>> biomeColorCache;

    static {
        BIOME_COLORS.put(Biomes.OCEAN, "#000070");
        BIOME_COLORS.put(Biomes.PLAINS, "#8DB360");
        BIOME_COLORS.put(Biomes.SUNFLOWER_PLAINS, "#B5DB88");
        BIOME_COLORS.put(Biomes.DESERT, "#FA9418");
        BIOME_COLORS.put(Biomes.DESERT_LAKES, "#FFBC40");
        BIOME_COLORS.put(Biomes.MOUNTAINS, "#606060");
        BIOME_COLORS.put(Biomes.GRAVELLY_MOUNTAINS, "#888888");
        BIOME_COLORS.put(Biomes.FOREST, "#056621");
        BIOME_COLORS.put(Biomes.FLOWER_FOREST, "#2D8E49");
        BIOME_COLORS.put(Biomes.TAIGA, "#0B6659");
        BIOME_COLORS.put(Biomes.TAIGA_MOUNTAINS, "#338E81");
        BIOME_COLORS.put(Biomes.SWAMP, "#07F9B2");
        BIOME_COLORS.put(Biomes.SWAMP_HILLS, "#2FFFDA");
        BIOME_COLORS.put(Biomes.RIVER, "#0000FF");
        BIOME_COLORS.put(Biomes.NETHER_WASTES, "#572526");
        BIOME_COLORS.put(Biomes.THE_END, "#8080FF");
        BIOME_COLORS.put(Biomes.FROZEN_OCEAN, "#7070D6");
        BIOME_COLORS.put(Biomes.FROZEN_RIVER, "#A0A0FF");
        BIOME_COLORS.put(Biomes.SNOWY_TUNDRA, "#FFFFFF");
        BIOME_COLORS.put(Biomes.ICE_SPIKES, "#B4DCDC");
        BIOME_COLORS.put(Biomes.SNOWY_MOUNTAINS, "#A0A0A0");
        BIOME_COLORS.put(Biomes.MUSHROOM_FIELDS, "#FF00FF");
        BIOME_COLORS.put(Biomes.MUSHROOM_FIELD_SHORE, "#A000FF");
        BIOME_COLORS.put(Biomes.BEACH, "#FADE55");
        BIOME_COLORS.put(Biomes.DESERT_HILLS, "#D25F12");
        BIOME_COLORS.put(Biomes.WOODED_HILLS, "#22551C");
        BIOME_COLORS.put(Biomes.TAIGA_HILLS, "#163933");
        BIOME_COLORS.put(Biomes.MOUNTAIN_EDGE, "#72789A");
        BIOME_COLORS.put(Biomes.JUNGLE, "#537B09");
        BIOME_COLORS.put(Biomes.MODIFIED_JUNGLE, "#7BA331");
        BIOME_COLORS.put(Biomes.JUNGLE_HILLS, "#2C4205");
        BIOME_COLORS.put(Biomes.JUNGLE_EDGE, "#628B17");
        BIOME_COLORS.put(Biomes.MODIFIED_JUNGLE_EDGE, "#8AB33F");
        BIOME_COLORS.put(Biomes.DEEP_OCEAN, "#000030");
        BIOME_COLORS.put(Biomes.STONE_SHORE, "#A2A284");
        BIOME_COLORS.put(Biomes.SNOWY_BEACH, "#FAF0C0");
        BIOME_COLORS.put(Biomes.BIRCH_FOREST, "#307444");
        BIOME_COLORS.put(Biomes.TALL_BIRCH_FOREST, "#589C6C");
        BIOME_COLORS.put(Biomes.BIRCH_FOREST_HILLS, "#1F5F32");
        BIOME_COLORS.put(Biomes.TALL_BIRCH_HILLS, "#47875A");
        BIOME_COLORS.put(Biomes.DARK_FOREST, "#40511A");
        BIOME_COLORS.put(Biomes.DARK_FOREST_HILLS, "#687942");
        BIOME_COLORS.put(Biomes.SNOWY_TAIGA, "#31554A");
        BIOME_COLORS.put(Biomes.SNOWY_TAIGA_MOUNTAINS, "#597D72");
        BIOME_COLORS.put(Biomes.SNOWY_TAIGA_HILLS, "#243F36");
        BIOME_COLORS.put(Biomes.GIANT_TREE_TAIGA, "#596651");
        BIOME_COLORS.put(Biomes.GIANT_SPRUCE_TAIGA, "#818E79");
        BIOME_COLORS.put(Biomes.GIANT_TREE_TAIGA_HILLS, "#454F3E");
        BIOME_COLORS.put(Biomes.GIANT_SPRUCE_TAIGA_HILLS, "#6D7766");
        BIOME_COLORS.put(Biomes.WOODED_MOUNTAINS, "#507050");
        BIOME_COLORS.put(Biomes.MODIFIED_GRAVELLY_MOUNTAINS, "#789878");
        BIOME_COLORS.put(Biomes.SAVANNA, "#BDB25F");
        BIOME_COLORS.put(Biomes.SHATTERED_SAVANNA, "#E5DA87");
        BIOME_COLORS.put(Biomes.SAVANNA_PLATEAU, "#A79D64");
        BIOME_COLORS.put(Biomes.SHATTERED_SAVANNA_PLATEAU, "#CFC58C");
        BIOME_COLORS.put(Biomes.BADLANDS, "#D94515");
        BIOME_COLORS.put(Biomes.ERODED_BADLANDS, "#FF6D3D");
        BIOME_COLORS.put(Biomes.WOODED_BADLANDS_PLATEAU, "#B09765");
        BIOME_COLORS.put(Biomes.MODIFIED_WOODED_BADLANDS_PLATEAU, "#D8BF8D");
        BIOME_COLORS.put(Biomes.BADLANDS_PLATEAU, "#CA8C65");
        BIOME_COLORS.put(Biomes.MODIFIED_BADLANDS_PLATEAU, "#F2B48D");
        BIOME_COLORS.put(Biomes.SMALL_END_ISLANDS, "#4B4BAB");
        BIOME_COLORS.put(Biomes.BAMBOO_JUNGLE, "#768E14");
        BIOME_COLORS.put(Biomes.END_MIDLANDS, "#C9C95D");
        BIOME_COLORS.put(Biomes.BAMBOO_JUNGLE_HILLS, "#3B470A");
        BIOME_COLORS.put(Biomes.END_HIGHLANDS, "#B5B536");
        BIOME_COLORS.put(Biomes.SOUL_SAND_VALLEY, "#4D3A2E");
        BIOME_COLORS.put(Biomes.END_BARRENS, "#8080FF");
        BIOME_COLORS.put(Biomes.CRIMSON_FOREST, "#981A11");
        BIOME_COLORS.put(Biomes.WARM_OCEAN, "#0000AC");
        BIOME_COLORS.put(Biomes.WARPED_FOREST, "#565C4C");
        BIOME_COLORS.put(Biomes.LUKEWARM_OCEAN, "#000090");
        BIOME_COLORS.put(Biomes.BASALT_DELTAS, "#6B5F63");
        BIOME_COLORS.put(Biomes.COLD_OCEAN, "#202070");
        BIOME_COLORS.put(Biomes.DEEP_WARM_OCEAN, "#000050");
        BIOME_COLORS.put(Biomes.DEEP_LUKEWARM_OCEAN, "#000040");
        BIOME_COLORS.put(Biomes.DEEP_COLD_OCEAN, "#202038");
        BIOME_COLORS.put(Biomes.DEEP_FROZEN_OCEAN, "#404090");
        BIOME_COLORS.put(Biomes.THE_VOID, "#000000");

        BIOME_COLORS_VIBRANT.put(Biomes.CRIMSON_FOREST, "#DD0808");
        BIOME_COLORS_VIBRANT.put(Biomes.BASALT_DELTAS, "#403636");
        BIOME_COLORS_VIBRANT.put(Biomes.NETHER_WASTES, "#FF7700");
        BIOME_COLORS_VIBRANT.put(Biomes.WARPED_FOREST, "#49907B");
        BIOME_COLORS_VIBRANT.put(Biomes.SOUL_SAND_VALLEY, "#5E3830");
    }

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
            for (Biome b : Biomes.REGISTRY.values()) {
                if (!b.getName().equalsIgnoreCase(biomeEntry.trim())) continue;
                this.defaultBiomeColorCache.put(b.getId(), Color.decode(colorEntry));
                break;
            }
        });

        OVERRIDES.forEach((styleEntry, mapEntry) -> {
            Map<Integer, Color> map = new HashMap<>();
            this.biomeColorCache.put(styleEntry, map);

            mapEntry.forEach((biomeEntry, colorEntry) -> {
                for (Biome b : Biomes.REGISTRY.values()) {
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

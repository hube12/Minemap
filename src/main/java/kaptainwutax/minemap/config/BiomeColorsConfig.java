package kaptainwutax.minemap.config;

import com.google.gson.annotations.Expose;
import javafx.scene.paint.Color;
import kaptainwutax.biomeutils.Biome;

import java.util.*;

public class BiomeColorsConfig extends Config {

	@Expose protected Map<String, String> DEFAULT_BIOME_COLORS = new LinkedHashMap<>();
	@Expose protected Map<String, Map<String, String>> OVERRIDES = new LinkedHashMap<>();

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
		if(this.biomeColorCache == null) {
			this.defaultBiomeColorCache = new HashMap<>();
			this.biomeColorCache = new HashMap<>();

			DEFAULT_BIOME_COLORS.forEach((biomeEntry, colorEntry) -> {
				for(Biome b: Biome.REGISTRY.values()) {
					if(!b.getName().equalsIgnoreCase(biomeEntry.trim()))continue;
					java.awt.Color awtColor = java.awt.Color.decode(colorEntry);
					this.defaultBiomeColorCache.put(b.getId(), new Color(awtColor.getRed() / 255.0D, awtColor.getGreen() / 255.0D, awtColor.getBlue() / 255.0D, 1.0D));
					break;
				}
			});

			OVERRIDES.forEach((styleEntry, mapEntry) -> {
				Map<Integer, Color> map = new HashMap<>();
				this.biomeColorCache.put(styleEntry, map);

				mapEntry.forEach((biomeEntry, colorEntry) -> {
					for(Biome b: Biome.REGISTRY.values()) {
						if(!b.getName().equalsIgnoreCase(biomeEntry.trim()))continue;
						java.awt.Color awtColor = java.awt.Color.decode(colorEntry);
						map.put(b.getId(), new Color(awtColor.getRed() / 255.0D, awtColor.getGreen() / 255.0D, awtColor.getBlue() / 255.0D, 1.0D));
						break;
					}
				});
			});
		}

		return this.biomeColorCache.getOrDefault(style, this.defaultBiomeColorCache)
									.getOrDefault(biome, this.defaultBiomeColorCache.get(biome));
	}

	public List<String> getStyles() {
		List<String> styles = new ArrayList<>();
		styles.add("Default");
		styles.addAll(new ArrayList<>(this.OVERRIDES.keySet()));
		return styles;
	}

	@Override
	protected void resetConfig() {
		this.addDefaultEntry(Biome.OCEAN, "#000070");
		this.addDefaultEntry(Biome.PLAINS, "#8DB360");
		this.addDefaultEntry(Biome.SUNFLOWER_PLAINS, "#B5DB88");
		this.addDefaultEntry(Biome.DESERT, "#FA9418");
		this.addDefaultEntry(Biome.DESERT_LAKES, "#FFBC40");
		this.addDefaultEntry(Biome.MOUNTAINS, "#606060");
		this.addDefaultEntry(Biome.GRAVELLY_MOUNTAINS, "#888888");
		this.addDefaultEntry(Biome.FOREST, "#056621");
		this.addDefaultEntry(Biome.FLOWER_FOREST, "#2D8E49");
		this.addDefaultEntry(Biome.TAIGA, "#0B6659");
		this.addDefaultEntry(Biome.TAIGA_MOUNTAINS, "#338E81");
		this.addDefaultEntry(Biome.SWAMP, "#07F9B2");
		this.addDefaultEntry(Biome.SWAMP_HILLS, "#2FFFDA");
		this.addDefaultEntry(Biome.RIVER, "#0000FF");
		this.addDefaultEntry(Biome.NETHER_WASTES, "#572526");
		this.addDefaultEntry(Biome.THE_END, "#8080FF");
		this.addDefaultEntry(Biome.FROZEN_OCEAN, "#7070D6");
		this.addDefaultEntry(Biome.FROZEN_RIVER, "#A0A0FF");
		this.addDefaultEntry(Biome.SNOWY_TUNDRA, "#FFFFFF");
		this.addDefaultEntry(Biome.ICE_SPIKES, "#B4DCDC");
		this.addDefaultEntry(Biome.SNOWY_MOUNTAINS, "#A0A0A0");
		this.addDefaultEntry(Biome.MUSHROOM_FIELDS, "#FF00FF");
		this.addDefaultEntry(Biome.MUSHROOM_FIELD_SHORE, "#A000FF");
		this.addDefaultEntry(Biome.BEACH, "#FADE55");
		this.addDefaultEntry(Biome.DESERT_HILLS, "#D25F12");
		this.addDefaultEntry(Biome.WOODED_HILLS, "#22551C");
		this.addDefaultEntry(Biome.TAIGA_HILLS, "#163933");
		this.addDefaultEntry(Biome.MOUNTAIN_EDGE, "#72789A");
		this.addDefaultEntry(Biome.JUNGLE, "#537B09");
		this.addDefaultEntry(Biome.MODIFIED_JUNGLE, "#7BA331");
		this.addDefaultEntry(Biome.JUNGLE_HILLS, "#2C4205");
		this.addDefaultEntry(Biome.JUNGLE_EDGE, "#628B17");
		this.addDefaultEntry(Biome.MODIFIED_JUNGLE_EDGE, "#8AB33F");
		this.addDefaultEntry(Biome.DEEP_OCEAN, "#000030");
		this.addDefaultEntry(Biome.STONE_SHORE, "#A2A284");
		this.addDefaultEntry(Biome.SNOWY_BEACH, "#FAF0C0");
		this.addDefaultEntry(Biome.BIRCH_FOREST, "#307444");
		this.addDefaultEntry(Biome.TALL_BIRCH_FOREST, "#589C6C");
		this.addDefaultEntry(Biome.BIRCH_FOREST_HILLS, "#1F5F32");
		this.addDefaultEntry(Biome.TALL_BIRCH_HILLS, "#47875A");
		this.addDefaultEntry(Biome.DARK_FOREST, "#40511A");
		this.addDefaultEntry(Biome.DARK_FOREST_HILLS, "#687942");
		this.addDefaultEntry(Biome.SNOWY_TAIGA, "#31554A");
		this.addDefaultEntry(Biome.SNOWY_TAIGA_MOUNTAINS, "#597D72");
		this.addDefaultEntry(Biome.SNOWY_TAIGA_HILLS, "#243F36");
		this.addDefaultEntry(Biome.GIANT_TREE_TAIGA, "#596651");
		this.addDefaultEntry(Biome.GIANT_SPRUCE_TAIGA, "#818E79");
		this.addDefaultEntry(Biome.GIANT_TREE_TAIGA_HILLS, "#454F3E");
		this.addDefaultEntry(Biome.GIANT_SPRUCE_TAIGA_HILLS, "#6D7766");
		this.addDefaultEntry(Biome.WOODED_MOUNTAINS, "#507050");
		this.addDefaultEntry(Biome.MODIFIED_GRAVELLY_MOUNTAINS, "#789878");
		this.addDefaultEntry(Biome.SAVANNA, "#BDB25F");
		this.addDefaultEntry(Biome.SHATTERED_SAVANNA, "#E5DA87");
		this.addDefaultEntry(Biome.SAVANNA_PLATEAU, "#A79D64");
		this.addDefaultEntry(Biome.SHATTERED_SAVANNA_PLATEAU, "#CFC58C");
		this.addDefaultEntry(Biome.BADLANDS, "#D94515");
		this.addDefaultEntry(Biome.ERODED_BADLANDS, "#FF6D3D");
		this.addDefaultEntry(Biome.WOODED_BADLANDS_PLATEAU, "#B09765");
		this.addDefaultEntry(Biome.MODIFIED_WOODED_BADLANDS_PLATEAU, "#D8BF8D");
		this.addDefaultEntry(Biome.BADLANDS_PLATEAU, "#CA8C65");
		this.addDefaultEntry(Biome.MODIFIED_BADLANDS_PLATEAU, "#F2B48D");
		this.addDefaultEntry(Biome.SMALL_END_ISLANDS, "#4B4BAB");
		this.addDefaultEntry(Biome.BAMBOO_JUNGLE, "#768E14");
		this.addDefaultEntry(Biome.END_MIDLANDS, "#C9C95D");
		this.addDefaultEntry(Biome.BAMBOO_JUNGLE_HILLS, "#3B470A");
		this.addDefaultEntry(Biome.END_HIGHLANDS, "#B5B536");
		this.addDefaultEntry(Biome.SOUL_SAND_VALLEY, "#4D3A2E");
		this.addDefaultEntry(Biome.END_BARRENS, "#8080FF");
		this.addDefaultEntry(Biome.CRIMSON_FOREST, "#981A11");
		this.addDefaultEntry(Biome.WARM_OCEAN, "#0000AC");
		this.addDefaultEntry(Biome.WARPED_FOREST, "#565C4C");
		this.addDefaultEntry(Biome.LUKEWARM_OCEAN, "#000090");
		this.addDefaultEntry(Biome.BASALT_DELTAS, "#6B5F63");
		this.addDefaultEntry(Biome.COLD_OCEAN, "#202070");
		this.addDefaultEntry(Biome.DEEP_WARM_OCEAN, "#000050");
		this.addDefaultEntry(Biome.DEEP_LUKEWARM_OCEAN, "#000040");
		this.addDefaultEntry(Biome.DEEP_COLD_OCEAN, "#202038");
		this.addDefaultEntry(Biome.DEEP_FROZEN_OCEAN, "#404090");
		this.addDefaultEntry(Biome.THE_VOID, "#000000");

		this.addOverrideEntry("Vibrant", Biome.CRIMSON_FOREST, "#DD0808");
		this.addOverrideEntry("Vibrant", Biome.BASALT_DELTAS, "#403636");
		this.addOverrideEntry("Vibrant", Biome.NETHER_WASTES, "#FF7700");
		this.addOverrideEntry("Vibrant", Biome.WARPED_FOREST, "#49907B");
		this.addOverrideEntry("Vibrant", Biome.SOUL_SAND_VALLEY, "#5E3830");
	}
	
	protected void addDefaultEntry(Biome biome, String color) {
		this.DEFAULT_BIOME_COLORS.put(biome.getName().toUpperCase(), color.toUpperCase());
	}

	protected void addOverrideEntry(String type, Biome biome, String color) {
		Map<String, String> typeMap = this.OVERRIDES.computeIfAbsent(type, s -> new HashMap<>());
		typeMap.put(biome.getName().toUpperCase(), color.toUpperCase());
	}

}

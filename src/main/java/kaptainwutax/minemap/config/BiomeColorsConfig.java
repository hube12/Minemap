package kaptainwutax.minemap.config;

import com.google.gson.annotations.Expose;
import javafx.scene.paint.Color;
import kaptainwutax.biomeutils.Biome;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BiomeColorsConfig extends Config {

	@Expose protected Map<String, String> BIOME_COLORS = new LinkedHashMap<>();
	protected Map<Integer, Color> biomeColorCache;

	@Override
	public String getName() {
		return "biome_colors";
	}

	public Color get(Biome biome) {
		return this.get(biome.getId());
	}

	public Color get(int biome) {
		if(this.biomeColorCache == null) {
			this.biomeColorCache = new HashMap<>();

			BIOME_COLORS.forEach((s, s2) -> {
				for(Biome b: Biome.REGISTRY.values()) {
					if(!b.getName().equalsIgnoreCase(s.trim()))continue;
					java.awt.Color awtColor = java.awt.Color.decode(s2);
					this.biomeColorCache.put(b.getId(), new Color(awtColor.getRed() / 255.0D, awtColor.getGreen() / 255.0D, awtColor.getBlue() / 255.0D, 1.0D));
					break;
				}
			});
		}

		return this.biomeColorCache.get(biome);
	}

	@Override
	protected void resetConfig() {
		this.addEntry(Biome.OCEAN, "#000070");
		this.addEntry(Biome.PLAINS, "#8DB360");
		this.addEntry(Biome.SUNFLOWER_PLAINS, "#B5DB88");
		this.addEntry(Biome.DESERT, "#FA9418");
		this.addEntry(Biome.DESERT_LAKES, "#FFBC40");
		this.addEntry(Biome.MOUNTAINS, "#606060");
		this.addEntry(Biome.GRAVELLY_MOUNTAINS, "#888888");
		this.addEntry(Biome.FOREST, "#056621");
		this.addEntry(Biome.FLOWER_FOREST, "#2D8E49");
		this.addEntry(Biome.TAIGA, "#0B6659");
		this.addEntry(Biome.TAIGA_MOUNTAINS, "#338E81");
		this.addEntry(Biome.SWAMP, "#07F9B2");
		this.addEntry(Biome.SWAMP_HILLS, "#2FFFDA");
		this.addEntry(Biome.RIVER, "#0000FF");
		this.addEntry(Biome.NETHER_WASTES, "#FF7700");
		this.addEntry(Biome.THE_END, "#8080FF");
		this.addEntry(Biome.FROZEN_OCEAN, "#7070D6");
		this.addEntry(Biome.FROZEN_RIVER, "#A0A0FF");
		this.addEntry(Biome.SNOWY_TUNDRA, "#FFFFFF");
		this.addEntry(Biome.ICE_SPIKES, "#B4DCDC");
		this.addEntry(Biome.SNOWY_MOUNTAINS, "#A0A0A0");
		this.addEntry(Biome.MUSHROOM_FIELDS, "#FF00FF");
		this.addEntry(Biome.MUSHROOM_FIELD_SHORE, "#A000FF");
		this.addEntry(Biome.BEACH, "#FADE55");
		this.addEntry(Biome.DESERT_HILLS, "#D25F12");
		this.addEntry(Biome.WOODED_HILLS, "#22551C");
		this.addEntry(Biome.TAIGA_HILLS, "#163933");
		this.addEntry(Biome.MOUNTAIN_EDGE, "#72789A");
		this.addEntry(Biome.JUNGLE, "#537B09");
		this.addEntry(Biome.MODIFIED_JUNGLE, "#7BA331");
		this.addEntry(Biome.JUNGLE_HILLS, "#2C4205");
		this.addEntry(Biome.JUNGLE_EDGE, "#628B17");
		this.addEntry(Biome.MODIFIED_JUNGLE_EDGE, "#8AB33F");
		this.addEntry(Biome.DEEP_OCEAN, "#000030");
		this.addEntry(Biome.STONE_SHORE, "#A2A284");
		this.addEntry(Biome.SNOWY_BEACH, "#FAF0C0");
		this.addEntry(Biome.BIRCH_FOREST, "#307444");
		this.addEntry(Biome.TALL_BIRCH_FOREST, "#589C6C");
		this.addEntry(Biome.BIRCH_FOREST_HILLS, "#1F5F32");
		this.addEntry(Biome.TALL_BIRCH_HILLS, "#47875A");
		this.addEntry(Biome.DARK_FOREST, "#40511A");
		this.addEntry(Biome.DARK_FOREST_HILLS, "#687942");
		this.addEntry(Biome.SNOWY_TAIGA, "#31554A");
		this.addEntry(Biome.SNOWY_TAIGA_MOUNTAINS, "#597D72");
		this.addEntry(Biome.SNOWY_TAIGA_HILLS, "#243F36");
		this.addEntry(Biome.GIANT_TREE_TAIGA, "#596651");
		this.addEntry(Biome.GIANT_SPRUCE_TAIGA, "#818E79");
		this.addEntry(Biome.GIANT_TREE_TAIGA_HILLS, "#454F3E");
		this.addEntry(Biome.GIANT_SPRUCE_TAIGA_HILLS, "#6D7766");
		this.addEntry(Biome.WOODED_MOUNTAINS, "#507050");
		this.addEntry(Biome.MODIFIED_GRAVELLY_MOUNTAINS, "#789878");
		this.addEntry(Biome.SAVANNA, "#BDB25F");
		this.addEntry(Biome.SHATTERED_SAVANNA, "#E5DA87");
		this.addEntry(Biome.SAVANNA_PLATEAU, "#A79D64");
		this.addEntry(Biome.SHATTERED_SAVANNA_PLATEAU, "#CFC58C");
		this.addEntry(Biome.BADLANDS, "#D94515");
		this.addEntry(Biome.ERODED_BADLANDS, "#FF6D3D");
		this.addEntry(Biome.WOODED_BADLANDS_PLATEAU, "#B09765");
		this.addEntry(Biome.MODIFIED_WOODED_BADLANDS_PLATEAU, "#D8BF8D");
		this.addEntry(Biome.BADLANDS_PLATEAU, "#CA8C65");
		this.addEntry(Biome.MODIFIED_BADLANDS_PLATEAU, "#F2B48D");
		this.addEntry(Biome.SMALL_END_ISLANDS, "#4B4BAB");
		this.addEntry(Biome.BAMBOO_JUNGLE, "#768E14");
		this.addEntry(Biome.END_MIDLANDS, "#C9C95D");
		this.addEntry(Biome.BAMBOO_JUNGLE_HILLS, "#3B470A");
		this.addEntry(Biome.END_HIGHLANDS, "#B5B536");
		this.addEntry(Biome.SOUL_SAND_VALLEY, "#5E3830");
		this.addEntry(Biome.END_BARRENS, "#8080FF");
		this.addEntry(Biome.CRIMSON_FOREST, "#DD0808");
		this.addEntry(Biome.WARM_OCEAN, "#0000AC");
		this.addEntry(Biome.WARPED_FOREST, "#49907B");
		this.addEntry(Biome.LUKEWARM_OCEAN, "#000090");
		this.addEntry(Biome.BASALT_DELTAS, "#403636");
		this.addEntry(Biome.COLD_OCEAN, "#202070");
		this.addEntry(Biome.DEEP_WARM_OCEAN, "#000050");
		this.addEntry(Biome.DEEP_LUKEWARM_OCEAN, "#000040");
		this.addEntry(Biome.DEEP_COLD_OCEAN, "#202038");
		this.addEntry(Biome.DEEP_FROZEN_OCEAN, "#404090");
		this.addEntry(Biome.THE_VOID, "#000000");
	}

	protected void addEntry(Biome biome, String color) {
		this.BIOME_COLORS.put(biome.getName().toUpperCase(), color.toUpperCase());
	}

}

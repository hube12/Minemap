package kaptainwutax.minemap.util;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.layer.BiomeLayer;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.structure.Stronghold;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.seedutils.lcg.rand.JRand;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.CPos;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public class WorldInfo {

	public static final List<Biome> SPAWN_BIOMES = Arrays.asList(Biome.FOREST, Biome.PLAINS, Biome.TAIGA,
			Biome.TAIGA_HILLS, Biome.WOODED_HILLS, Biome.JUNGLE, Biome.JUNGLE_HILLS);

	public static final int VORONOI_ID = 0;
	public static final int QUARTER_RES_ID = 1;

	public final MCVersion version;
	public final long worldSeed;
	private final int layerId;

	private final ThreadLocal<BiomeSource> source;

	//TODO: remove mega hack
	public final List<CPos> strongholds = new ArrayList<>();
	public final List<BPos> spawns = new ArrayList<>();

	public WorldInfo(MCVersion version, long worldSeed, int layerId, BiomeSource.BiomeSourceSupplier gen) {
		this.version = version;
		this.worldSeed = worldSeed;
		this.layerId = layerId;

		this.source = ThreadLocal.withInitial(() -> gen.create(this.version, this.worldSeed));

		if(this.getBiomeSource() instanceof OverworldBiomeSource) {
			Stronghold stronghold = new Stronghold(version);
			this.strongholds.addAll(Arrays.asList(stronghold.getAllStarts(this.getBiomeSource(), new JRand(0L))));
			this.spawns.add(getSpawnPoint(this.getBiomeSource()));
		}
	}

	public static BPos getSpawnPoint(BiomeSource source) {
		JRand rand = new JRand(source.getWorldSeed());
		BPos spawnPos = source.locateBiome(0, 0, 0, 256, SPAWN_BIOMES, rand);
		return spawnPos == null ? new BPos(0, 0, 0) : spawnPos;
	}

	public Image getRegionImage(int posX, int posZ, int regionSize) {
		BiomeLayer layer = this.getLayer();
		int scale = layer.getScale();
		int px = posX / scale;
		int pz = posZ / scale;
		if(posX < 0)px -= 1;
		if(posZ < 0)pz -= 1;
		regionSize /= scale;
		regionSize = Math.max(regionSize, 1);

		BufferedImage image = new BufferedImage(regionSize, regionSize, BufferedImage.TYPE_INT_RGB);

		for(int x = 0; x < regionSize; x++) {
			for(int z = 0; z < regionSize; z++) {
				Color color = Configs.BIOME_COLORS.get(Configs.USER_PROFILE.getStyle(), layer.get(x + px, 0, z + pz));
				if(color != null)image.setRGB(x, z, color.getRGB());
			}
		}

		return image;
	}

	public Biome getBiome(int posX, int posZ) {
		BiomeLayer layer = this.getLayer();
		int scale = layer.getScale();
		int px = posX / scale;
		int pz = posZ / scale;
		if(posX < 0)px -= 1;
		if(posZ < 0)pz -= 1;

		return Biome.REGISTRY.get(layer.get(px, 0, pz));
	}

	public BiomeSource getBiomeSource() {
		return this.source.get();
	}

	public BiomeLayer getLayer() {
		BiomeSource biomeSource = this.getBiomeSource();
		return biomeSource.getLayers().get(biomeSource.getLayerCount() - this.layerId - 1);
	}

}

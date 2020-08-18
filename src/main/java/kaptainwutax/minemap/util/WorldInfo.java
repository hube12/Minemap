package kaptainwutax.minemap.util;

import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.layer.BiomeLayer;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.structure.Stronghold;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.seedutils.lcg.rand.JRand;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.CPos;
import kaptainwutax.seedutils.mc.pos.RPos;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WorldInfo {

	public final MCVersion version;
	public final Dimension dimension;
	public final long worldSeed;

	public int layerId;

	private final ThreadLocal<BiomeSource> source;

	//TODO: remove mega hack
	public final List<CPos> strongholds = new ArrayList<>();
	public final List<BPos> spawns = new ArrayList<>();

	public WorldInfo(MCVersion version, Dimension dimension, long worldSeed) {
		this.version = version;
		this.dimension = dimension;
		this.worldSeed = worldSeed;
		this.source = ThreadLocal.withInitial(() -> BiomeSource.of(dimension, version, worldSeed));

		this.layerId = this.source.get().getLayerCount() - 2;

		if(dimension == Dimension.OVERWORLD) {
			Stronghold stronghold = new Stronghold(version);
			this.strongholds.addAll(Arrays.asList(stronghold.getAllStarts(this.getBiomeSource(), new JRand(0L))));
			this.spawns.add(((OverworldBiomeSource)this.getBiomeSource()).getSpawnPoint());
		}
	}

	public Image getRegionImage(int posX, int posZ, int regionSize) {
		BiomeLayer layer = this.getLayer();
		int scale = layer.getScale();
		int effectiveRegion = Math.max(regionSize / scale, 1);

		RPos region = new BPos(posX, 0, posZ).toRegionPos(scale);

		BufferedImage image = new BufferedImage(effectiveRegion, effectiveRegion, BufferedImage.TYPE_INT_RGB);

		for(int x = 0; x < effectiveRegion; x++) {
			for(int z = 0; z < effectiveRegion; z++) {
				Biome biome = Biome.REGISTRY.get(layer.get(region.getX() + x, 0, region.getZ() + z));
				if(biome == null)continue;
				Color color = Configs.BIOME_COLORS.get(Configs.USER_PROFILE.getStyle(), biome);
				if(color != null)image.setRGB(x, z, color.getRGB());
			}
		}

		return image;
	}

	public Biome getBiome(int blockX, int blockZ) {
		return this.getBiome(this.getLayer(), blockX, blockZ);
	}

	public Biome getBiome(BiomeLayer layer, int blockX, int blockZ) {
		RPos pos = new BPos(blockX, 0, blockZ).toRegionPos(layer.getScale());
		return Biome.REGISTRY.get(layer.get(pos.getX(), 0, pos.getZ()));
	}

	public BiomeSource getBiomeSource() {
		return this.source.get();
	}

	public BiomeLayer getLayer() {
		BiomeSource biomeSource = this.getBiomeSource();
		return biomeSource.getLayers().get(this.layerId);
	}

}

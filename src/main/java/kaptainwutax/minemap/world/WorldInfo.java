package kaptainwutax.minemap.world;

import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.layer.BiomeLayer;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.biomeutils.source.EndBiomeSource;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.seedutils.mc.MCVersion;

import java.util.function.BiFunction;

public class WorldInfo {

	public static final int VORONOI_ID = 0;
	public static final int QUARTER_RES_ID = 1;

	public final MCVersion version;
	public final long worldSeed;
	private final int layerId;

	private final ThreadLocal<BiomeSource> source;

	public WorldInfo(MCVersion version, long worldSeed, int layerId, BiFunction<MCVersion, Long, BiomeSource> gen) {
		this.version = version;
		this.worldSeed = worldSeed;
		this.layerId = layerId;

		this.source = ThreadLocal.withInitial(() -> gen.apply(this.version, this.worldSeed));
	}

	public Image getRegionImage(int posX, int posZ, int regionSize) {
		boolean isEnd = this.getBiomeSource() instanceof EndBiomeSource;

		BiomeLayer layer = this.getLayer();
		int scale = layer.getScale();
		int px = posX / scale;
		int pz = posZ / scale;
		if(posX < 0)px -= 1;
		if(posZ < 0)pz -= 1;
		regionSize /= scale;
		regionSize = Math.max(regionSize, 1);

		WritableImage image = new WritableImage(regionSize, regionSize);

		for(int x = 0; x < regionSize; x++) {
			for(int z = 0; z < regionSize; z++) {
				int rx = (x + px) >> (isEnd ? 2 : 0);
				int rz = (z + pz) >> (isEnd ? 2 : 0);
				int id = layer.get(rx, 0, rz);

				Color color = Configs.BIOME_COLORS.get(Configs.USER_PROFILE.getStyle(), id);

				if(color != null) {
					image.getPixelWriter().setColor(x, z, color);
				} else {
					System.err.println("null color at " + rx + ", " + rz + " with id " + Biome.REGISTRY.get(id).getName());
				}
			}
		}

		return image;
	}

	public Biome getBiome(int posX, int posZ) {
		boolean isEnd = this.getBiomeSource() instanceof EndBiomeSource;

		BiomeLayer layer = this.getLayer();
		int scale = layer.getScale();
		int px = posX / scale;
		int pz = posZ / scale;
		if(posX < 0)px -= 1;
		if(posZ < 0)pz -= 1;

		return Biome.REGISTRY.get(layer.get(px >> (isEnd ? 2 : 0), 0, pz >> (isEnd ? 2 : 0)));
	}

	public BiomeSource getBiomeSource() {
		return this.source.get();
	}

	public BiomeLayer getLayer() {
		BiomeSource biomeSource = this.getBiomeSource();
		return biomeSource.getLayers().get(biomeSource.getLayerCount() - this.layerId - 1);
	}

}

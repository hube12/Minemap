package kaptainwutax.minemap.world;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.minemap.util.FeatureSupplier;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.CPos;

import java.util.HashMap;
import java.util.Map;

public class Fragment {

	public static final int STRUCTURE_ICON_SIZE = 32;
	public static final boolean DRAW_GRID = false;
	public static final Map<FeatureSupplier, Image> STRUCTURE_ICONS = new HashMap<>();

	private final int posX;
	private final int posZ;
	private final int size;
	private final WorldInfo info;

	private Image biomeMap;
	private Map<BPos, Image> structureMap;

	static {
		STRUCTURE_ICONS.put(BastionRemnant::new, getIcon(Structure.getName(BastionRemnant.class)));
		STRUCTURE_ICONS.put(BuriedTreasure::new, getIcon(Structure.getName(BuriedTreasure.class)));
		STRUCTURE_ICONS.put(DesertPyramid::new, getIcon(Structure.getName(DesertPyramid.class)));
		STRUCTURE_ICONS.put(EndCity::new, getIcon(Structure.getName(EndCity.class)));
		STRUCTURE_ICONS.put(Fortress::new, getIcon(Fortress.getName(Fortress.class)));
		STRUCTURE_ICONS.put(Igloo::new, getIcon(Igloo.getName(Igloo.class)));
		STRUCTURE_ICONS.put(JunglePyramid::new, getIcon(JunglePyramid.getName(JunglePyramid.class)));
		STRUCTURE_ICONS.put(Mansion::new, getIcon(Structure.getName(Mansion.class)));
		STRUCTURE_ICONS.put(Mineshaft::new, getIcon(Structure.getName(Mineshaft.class)));
		STRUCTURE_ICONS.put(Monument::new, getIcon(Structure.getName(Monument.class)));
		STRUCTURE_ICONS.put(NetherFossil::new, getIcon(Structure.getName(NetherFossil.class)));
		STRUCTURE_ICONS.put(OceanRuin::new, getIcon(Structure.getName(OceanRuin.class)));
		STRUCTURE_ICONS.put(PillagerOutpost::new, getIcon(Structure.getName(PillagerOutpost.class)));
		STRUCTURE_ICONS.put(RuinedPortal::new, getIcon(Structure.getName(RuinedPortal.class)));
		STRUCTURE_ICONS.put(Shipwreck::new, getIcon(Structure.getName(Shipwreck.class)));
		STRUCTURE_ICONS.put(Stronghold::new, getIcon(Structure.getName(Stronghold.class)));
		STRUCTURE_ICONS.put(SwampHut::new, getIcon(Structure.getName(SwampHut.class)));
		STRUCTURE_ICONS.put(Village::new, getIcon(Structure.getName(Village.class)));
	}

	public Fragment(int posX, int posZ, int size, WorldInfo info) {
		this.posX = posX;
		this.posZ = posZ;
		this.size = size;
		this.info = info;

		if(this.info != null) {
			this.generateBiomes();
			this.generateStructures();
		}
	}

	public void drawBiomes(GraphicsContext g, double x, double y, double width, double height) {
		g.drawImage(this.biomeMap, x, y, width, height);
		g.setImageSmoothing(false);

		if(DRAW_GRID) {
			g.setFill(Color.BLACK);
			g.fillRect(x, y, width - 1, height - 1);
		}
	}

	public void drawStructures(GraphicsContext g, double x, double y, double width, double height) {
		for(Map.Entry<BPos, Image> e: this.structureMap.entrySet()) {
			BPos pos = e.getKey();
			Image image = e.getValue();
			int sx = (int)((double)(pos.getX() - this.posX) / this.size * width) - STRUCTURE_ICON_SIZE / 2;
			int sy = (int)((double)(pos.getZ() - this.posZ) / this.size * height) - STRUCTURE_ICON_SIZE / 2;
			g.drawImage(image, x + sx, y + sy, STRUCTURE_ICON_SIZE, STRUCTURE_ICON_SIZE);
		}
	}

	private void generateBiomes() {
		this.biomeMap = this.info.getRegionImage(this.posX, this.posZ, this.size);
	}

	private void generateStructures() {
		this.structureMap = new HashMap<>();
		ChunkRand rand = new ChunkRand();
		BiomeSource biomeSource = this.info.getBiomeSource();

		for(Map.Entry<FeatureSupplier, Image> e: STRUCTURE_ICONS.entrySet()) {
			Structure<?, ?> structure;

			try {
				structure = e.getKey().create(this.info.version);
			} catch(Exception _e) {
				continue;
			}

			if(structure instanceof RegionStructure<?, ?>) {
				RegionStructure<?, ?> regionStructure = (RegionStructure<?, ?>)structure;

				int increment = 16 * regionStructure.getSpacing();

				for(int x = this.posX - increment; x < this.posX + this.size + increment; x += increment) {
					for(int z = this.posZ - increment; z < this.posZ + this.size + increment; z += increment) {
						RegionStructure.Data<?> data = regionStructure.at(x >> 4, z >> 4);
						CPos struct = regionStructure.getInRegion(this.info.worldSeed, data.regionX, data.regionZ, rand);
						if(struct == null)continue;
						if(!regionStructure.canSpawn(struct.getX(), struct.getZ(), biomeSource))continue;
						BPos pos = struct.toBlockPos().add(9, 0, 9);
						if(pos.getX() < this.posX || pos.getX() >= this.posX + this.size)continue;
						if(pos.getZ() < this.posZ || pos.getZ() >= this.posZ + this.size)continue;
						this.structureMap.put(pos, e.getValue());
					}
				}
			} else if(structure instanceof Mineshaft) {
				for(int x = this.posX - 16; x < this.posX + this.size + 16; x += 16) {
					for(int z = this.posZ - 16; z < this.posZ + this.size + 16; z += 16) {
						Feature.Data<Mineshaft> data = ((Mineshaft)structure).at(x >> 4, z >> 4);
						if(!data.testStart(this.info.worldSeed, rand))continue;
						if(!data.testBiome(biomeSource))continue;
						BPos pos = new BPos((data.chunkX) << 4 + 9, 0, (data.chunkZ << 4) + 9);
						if(pos.getX() < this.posX || pos.getX() >= this.posX + this.size)continue;
						if(pos.getZ() < this.posZ || pos.getZ() >= this.posZ + this.size)continue;
						this.structureMap.put(pos, e.getValue());
					}
				}
			} else if(structure instanceof Stronghold) {
				//TODO: strongholds
			}
		}
	}

	public static Image getIcon(String name) {
		try {
			Image image = new Image(Fragment.class.getResource("/icon/" + name + ".png").openStream());
			System.out.println("Found structure icon " + name + ".");
			return image;
		} catch(Exception e) {
			System.err.println("Didn't find structure icon " + name + ".");
		}

		return null;
	}

}

package kaptainwutax.minemap.util;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.decorator.EndGateway;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.CPos;

import javax.imageio.ImageIO;
import java.awt.*;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class Fragment {

	public static final int STRUCTURE_ICON_SIZE = 22;
	public static boolean DRAW_GRID = false;
	public static boolean DRAW_STRUCTURES = true;

	public static final Map<FeatureSupplier, Image> FEATURE_ICONS = new HashMap<>();

	private final int posX;
	private final int posZ;
	private final int size;
	private final WorldInfo info;

	private Image biomeMap;
	private Map<BPos, Image> structureMap;

	static {
		FEATURE_ICONS.put(BastionRemnant::new, getIcon(Structure.getName(BastionRemnant.class)));
		FEATURE_ICONS.put(BuriedTreasure::new, getIcon(Structure.getName(BuriedTreasure.class)));
		FEATURE_ICONS.put(DesertPyramid::new, getIcon(Structure.getName(DesertPyramid.class)));
		FEATURE_ICONS.put(EndCity::new, getIcon(Structure.getName(EndCity.class)));
		FEATURE_ICONS.put(Fortress::new, getIcon(Fortress.getName(Fortress.class)));
		FEATURE_ICONS.put(Igloo::new, getIcon(Igloo.getName(Igloo.class)));
		FEATURE_ICONS.put(JunglePyramid::new, getIcon(JunglePyramid.getName(JunglePyramid.class)));
		FEATURE_ICONS.put(Mansion::new, getIcon(Structure.getName(Mansion.class)));
		FEATURE_ICONS.put(Mineshaft::new, getIcon(Structure.getName(Mineshaft.class)));
		FEATURE_ICONS.put(Monument::new, getIcon(Structure.getName(Monument.class)));
		FEATURE_ICONS.put(NetherFossil::new, getIcon(Structure.getName(NetherFossil.class)));
		FEATURE_ICONS.put(OceanRuin::new, getIcon(Structure.getName(OceanRuin.class)));
		FEATURE_ICONS.put(PillagerOutpost::new, getIcon(Structure.getName(PillagerOutpost.class)));
		FEATURE_ICONS.put(RuinedPortal::new, getIcon(Structure.getName(RuinedPortal.class)));
		FEATURE_ICONS.put(Shipwreck::new, getIcon(Structure.getName(Shipwreck.class)));
		FEATURE_ICONS.put(SwampHut::new, getIcon(Structure.getName(SwampHut.class)));
		FEATURE_ICONS.put(Village::new, getIcon(Structure.getName(Village.class)));
		FEATURE_ICONS.put(Stronghold::new, getIcon(Structure.getName(Stronghold.class)));

		FEATURE_ICONS.put(EndGateway::new, getIcon("end_gateway"));
	}

	public static final Image SPAWN = getIcon("spawn");

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

	public void drawBiomes(Graphics g, int x, int y, int width, int height) {
		g.drawImage(this.biomeMap, x, y, width, height, null);

		if(DRAW_GRID) {
			g.setColor(Color.BLACK);
			g.drawRect(x, y, width - 1, height - 1);
		}
	}

	public void drawStructures(Graphics g, int x, int y, int width, int height) {
		if(!DRAW_STRUCTURES)return;

		for(Map.Entry<BPos, Image> e: this.structureMap.entrySet()) {
			BPos pos = e.getKey();
			Image image = e.getValue();
			int sx = (int)((double)(pos.getX() - this.posX) / this.size * width) - STRUCTURE_ICON_SIZE / 2;
			int sy = (int)((double)(pos.getZ() - this.posZ) / this.size * height) - STRUCTURE_ICON_SIZE / 2;
			g.drawImage(image, x + sx, y + sy, STRUCTURE_ICON_SIZE, STRUCTURE_ICON_SIZE, null);
		}
	}

	private void generateBiomes() {
		this.biomeMap = this.info.getRegionImage(this.posX, this.posZ, this.size);
	}

	private void generateStructures() {
		this.structureMap = new HashMap<>();
		ChunkRand rand = new ChunkRand();
		BiomeSource biomeSource = this.info.getBiomeSource();

		for(Map.Entry<FeatureSupplier, Image> e: FEATURE_ICONS.entrySet()) {
			Feature<?, ?> feature;

			try {
				feature = e.getKey().create(this.info.version);
			} catch(Exception _e) {
				continue;
			}

			if(feature instanceof RegionStructure<?, ?>) {
				RegionStructure<?, ?> regionStructure = (RegionStructure<?, ?>)feature;

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
			} else if(feature instanceof Mineshaft) {
				for(int x = this.posX - 16; x < this.posX + this.size + 16; x += 16) {
					for(int z = this.posZ - 16; z < this.posZ + this.size + 16; z += 16) {
						Feature.Data<Mineshaft> data = ((Mineshaft)feature).at(x >> 4, z >> 4);
						if(!data.testStart(this.info.worldSeed, rand))continue;
						if(!data.testBiome(biomeSource))continue;
						BPos pos = new BPos((data.chunkX) << 4 + 9, 0, (data.chunkZ << 4) + 9);
						if(pos.getX() < this.posX || pos.getX() >= this.posX + this.size)continue;
						if(pos.getZ() < this.posZ || pos.getZ() >= this.posZ + this.size)continue;
						this.structureMap.put(pos, e.getValue());
					}
				}
			} else if(feature instanceof Stronghold) {
				for(CPos stronghold: this.info.strongholds) {
					BPos p = stronghold.toBlockPos().add(9, 0, 9);
					if(p.getX() < this.posX || p.getX() >= this.posX + this.size)continue;
					if(p.getZ()  < this.posZ || p.getZ() >= this.posZ + this.size)continue;
					this.structureMap.put(p, e.getValue());
				}
			} else if(feature instanceof EndGateway) {
				EndGateway gateway = (EndGateway)feature;

				for(int x = this.posX; x < this.posX + this.size; x += 16) {
					for(int z = this.posZ; z < this.posZ + this.size; z += 16) {
						EndGateway.Data data = gateway.getData(this.info.worldSeed, x >> 4, z >> 4, rand);
						if(data == null)continue;
						BPos p = new BPos(data.blockX, 0, data.blockZ);
						if(!gateway.canSpawn(x >> 4, z >> 4, biomeSource))continue;
						if(p.getX() < this.posX || p.getX() >= this.posX + this.size)continue;
						if(p.getZ()  < this.posZ || p.getZ() >= this.posZ + this.size)continue;
						this.structureMap.put(p, e.getValue());
					}
				}
			}
		}

		for(BPos p: this.info.spawns) {
			if(p.getX() < this.posX || p.getX() >= this.posX + this.size)continue;
			if(p.getZ()  < this.posZ || p.getZ() >= this.posZ + this.size)continue;
			this.structureMap.put(p, SPAWN);
		}
	}

	public static Image getIcon(String name) {
		try {
			URL url = Fragment.class.getResource("/icon/" + name + ".png");
			System.out.println("Found structure icon " + name + ".");
			return ImageIO.read(url);
		} catch(Exception e) {
			System.err.println("Didn't find structure icon " + name + ".");
		}

		return null;
	}

}

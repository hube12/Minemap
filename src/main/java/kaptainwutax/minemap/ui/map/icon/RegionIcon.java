package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.terrainutils.TerrainGenerator;

import java.util.List;
import java.util.function.Function;

public class RegionIcon extends StaticIcon {

    public RegionIcon(MapContext context) {
        super(context);
    }

    @Override
    public boolean isValidFeature(Feature<?, ?> feature) {
        return feature instanceof RegionStructure;
    }

    @Override
    public void addPositions(Feature<?, ?> feature, Fragment fragment, List<BPos> positions) {
        RegionStructure<?, ?> structure = (RegionStructure<?, ?>) feature;
        int increment = 16 * structure.getSpacing();
        ChunkRand rand = new ChunkRand();

        long worldSeedWithSalt = this.getContext().worldSeed;
        if (Configs.SALTS.getSalt(this.getContext().version, feature.getName()) != null) {
            worldSeedWithSalt -= Configs.SALTS.getDefaultSalt(this.getContext().version, feature.getName());
            worldSeedWithSalt += Configs.SALTS.getSalt(this.getContext().version, feature.getName());
        }
        int fragXMin = integerTranslation().apply(fragment.getX());
        int fragXMax = integerTranslation().apply(fragment.getX() + fragment.getSize());
        int fragZMin = integerTranslation().apply(fragment.getZ());
        int fragZMax = integerTranslation().apply(fragment.getZ() + fragment.getSize());
        for (int x = fragXMin - increment; x < fragXMax + increment; x += increment) {
            for (int z = fragZMin - increment; z < fragZMax + increment; z += increment) {
                RegionStructure.Data<?> data = structure.at(x >> 4, z >> 4);
                CPos pos = structure.getInRegion(worldSeedWithSalt, data.regionX, data.regionZ, rand);
                if (pos != null) {
                    BPos currentPos = blockPosTranslation().apply(pos.toBlockPos().add(9, 0, 9));
                    if (structure.canSpawn(pos.getX(), pos.getZ(), this.getContext().getBiomeSource(getDimension()))) {
                        TerrainGenerator generator = this.getContext().getTerrainGenerator(structure).getFirst();
                        if (generator == null) {
                            positions.add(currentPos);
                        } else if (structure.canGenerate(pos.getX(), pos.getZ(), generator)) {
                            positions.add(currentPos);
                        }
                    } else if (Configs.USER_PROFILE.getUserSettings().structureMode) {
                        positions.add(currentPos);
                    }
                }

            }
        }
    }

    public Dimension getDimension() {
        return this.getContext().getDimension();
    }

    public Function<BPos, BPos> blockPosTranslation() {
        return e -> e;
    }

    public Function<Integer, Integer> integerTranslation() {
        return e -> e;
    }

}

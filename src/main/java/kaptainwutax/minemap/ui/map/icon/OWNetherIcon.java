package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.minemap.feature.OWBastionRemnant;
import kaptainwutax.minemap.feature.OWFortress;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;

import java.util.List;

public class OWNetherIcon extends StaticIcon {

    public OWNetherIcon(MapContext context) {
        super(context);
    }

    @Override
    public boolean isValidFeature(Feature<?, ?> feature) {
        return feature instanceof OWBastionRemnant || feature instanceof OWFortress;
    }

    @Override
    public void addPositions(Feature<?, ?> feature, Fragment fragment, List<BPos> positions) {
        RegionStructure<?, ?> structure = (RegionStructure<?, ?>) feature;
        int increment = 16 * structure.getSpacing();
        ChunkRand rand = new ChunkRand();

        for (int x = (fragment.getX() >> 3) - increment; x < (fragment.getX() + fragment.getSize() >> 3) + increment; x += increment) {
            for (int z = (fragment.getZ() >> 3) - increment; z < (fragment.getZ() + fragment.getSize() >> 3) + increment; z += increment) {
                RegionStructure.Data<?> data = structure.at(x >> 4, z >> 4);
                CPos pos = structure.getInRegion(this.getContext().worldSeed, data.regionX, data.regionZ, rand);

                if (pos != null && structure.canSpawn(pos.getX(), pos.getZ(), this.getContext().getBiomeSource(Dimension.NETHER))) {
                    BPos netherPos = pos.toBlockPos().add(9, 0, 9);
                    positions.add(new BPos(netherPos.getX() << 3, 0, netherPos.getZ() << 3));
                }
            }
        }
    }

}

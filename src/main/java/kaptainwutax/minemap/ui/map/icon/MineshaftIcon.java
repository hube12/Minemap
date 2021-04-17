package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.Mineshaft;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.util.pos.BPos;

import java.util.List;

public class MineshaftIcon extends StaticIcon {

    public MineshaftIcon(MapContext context) {
        super(context);
    }

    @Override
    public boolean isValidFeature(Feature<?, ?> feature) {
        return feature instanceof Mineshaft;
    }

    @Override
    public void addPositions(Feature<?, ?> feature, Fragment fragment, List<BPos> positions) {
        ChunkRand rand = new ChunkRand();

        for (int x = fragment.getX() - 16; x < fragment.getX() + fragment.getSize() + 16; x += 16) {
            for (int z = fragment.getZ() - 16; z < fragment.getZ() + fragment.getSize() + 16; z += 16) {
                Feature.Data<Mineshaft> data = ((Mineshaft) feature).at(x >> 4, z >> 4);
                if (!data.testStart(this.getContext().worldSeed, rand)) continue;
                if (!data.testBiome(this.getContext().getBiomeSource())) continue;
                positions.add(new BPos((data.chunkX << 4) + 9, 0, (data.chunkZ << 4) + 9));
            }
        }
    }

}

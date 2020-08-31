package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.misc.SlimeChunk;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.pos.BPos;

import java.util.List;

public class SlimeIcon extends DynamicIcon {

    public SlimeIcon(MapContext context) {
        super(context, 16);
    }

    @Override
    public boolean isValidFeature(Feature<?, ?> feature) {
        return feature instanceof SlimeChunk;
    }

    @Override
    public void addPositions(Feature<?, ?> feature, Fragment fragment, List<BPos> positions) {
        for(int x = fragment.getX() - 16; x < fragment.getX() + fragment.getSize() + 16; x += 16) {
            for(int z = fragment.getZ() - 16; z < fragment.getZ() + fragment.getSize() + 16; z += 16) {
                SlimeChunk.Data data = ((SlimeChunk)feature).at(x >> 4, z >> 4, true);
                if(!data.testStart(fragment.getContext().worldSeed, new ChunkRand()))continue;
                positions.add(new BPos(x, 0, z).toChunkCorner());
            }
        }
    }

}
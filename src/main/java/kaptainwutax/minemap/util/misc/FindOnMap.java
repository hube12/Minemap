package kaptainwutax.minemap.util.misc;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.RPos;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.ui.map.fragment.Fragment;

import java.util.ArrayList;
import java.util.List;

public class FindOnMap {
    public static ArrayList<Pair<Feature<?, ?>, List<BPos>>> findFeaturesSelected(){
        MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
        ArrayList<Pair<Feature<?, ?>, List<BPos>>> features = new ArrayList<>();
        int size = (int) map.manager.pixelsPerFragment;
        {
            BPos bPos = map.manager.getPos(map.manager.mousePointer.x, map.manager.mousePointer.y);
            RPos rPos = bPos.toRegionPos(map.manager.blocksPerFragment);
            Fragment fragment = map.scheduler.getFragmentAt(rPos.getX(), rPos.getZ());
            fragment.getHoveredFeatures(size, size).forEach((feature, positions) -> {
                if (!positions.isEmpty() && feature != null) {
                    features.add(new Pair<>(feature, positions));
                }
            });
            if (features.isEmpty()) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        RPos offsetRpos = new RPos(rPos.getX() + i, rPos.getZ() + j, map.manager.blocksPerFragment);
                        map.scheduler.getFragmentAt(offsetRpos.getX(), offsetRpos.getZ()).getHoveredFeatures(size, size).forEach((feature, positions) -> {
                            if (!positions.isEmpty() && feature != null) {
                                features.add(new Pair<>(feature, positions));
                            }
                        });
                    }
                }
            }
            if (features.isEmpty()) {
                map.scheduler.forEachFragment(f -> f.getHoveredFeatures(size, size).forEach((feature, positions) -> {
                    if (!positions.isEmpty() && feature != null) {
                        features.add(new Pair<>(feature, positions));
                    }
                }));
            }
        }
        return features;
    }
}

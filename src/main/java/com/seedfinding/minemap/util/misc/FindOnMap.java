package com.seedfinding.minemap.util.misc;

import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.RPos;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.ui.map.MapPanel;
import com.seedfinding.minemap.ui.map.fragment.Fragment;

import java.util.ArrayList;
import java.util.List;

public class FindOnMap {
    public static ArrayList<Pair<Feature<?, ?>, List<BPos>>> findFeaturesSelected() {
        MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
        ArrayList<Pair<Feature<?, ?>, List<BPos>>> features = new ArrayList<>();
        if (map == null || map.manager == null || map.manager.mousePointer == null || map.scheduler == null) return null;
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

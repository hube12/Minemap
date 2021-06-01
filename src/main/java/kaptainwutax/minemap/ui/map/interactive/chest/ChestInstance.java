package kaptainwutax.minemap.ui.map.interactive.chest;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.MapPanel;

public class ChestInstance {
    private final MapPanel map;
    private CPos pos;
    private Feature<?, ?> feature;

    public ChestInstance(MapPanel map){
        this.map=map;
    }

    public MapPanel getMap() {
        return map;
    }

    public MapContext getContext() {
        return map.getContext();
    }

    public void setFeature(Feature<?, ?> feature) {
        this.feature = feature;
    }

    public void setPos(CPos pos) {
        this.pos = pos;
    }

    public Pair<Feature<?, ?>, CPos> getInformations() {
        return new Pair<>(this.feature, this.pos);
    }

    public Feature<?, ?> getFeature() {
        return feature;
    }

    public CPos getPos() {
        return pos;
    }
}

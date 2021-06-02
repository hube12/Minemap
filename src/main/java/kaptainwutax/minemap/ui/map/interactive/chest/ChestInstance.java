package kaptainwutax.minemap.ui.map.interactive.chest;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.minemap.feature.chests.Chests;
import kaptainwutax.minemap.feature.chests.Loot;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.MapPanel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChestInstance {
    private final MapPanel map;
    private CPos pos;
    private Feature<?, ?> feature;
    private List<List<ItemStack>> listItems;
    private boolean indexed = false;
    private int currentChestIndex;
    private boolean hasChanged = false;
    private List<Updatable> toUpdate = new ArrayList<>();

    public ChestInstance(MapPanel map) {
        this.map = map;
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

    public void setCurrentChestIndex(int currentChestIndex) {
        if (currentChestIndex!=this.currentChestIndex){
            this.currentChestIndex = currentChestIndex;
            for (Updatable updatable : toUpdate) {
                updatable.update(false);
            }
        }
    }

    public int size() {
        return this.getListItems() == null ? 0 : this.getListItems().size();
    }

    public int getCurrentChestIndex() {
        return currentChestIndex;
    }

    public boolean isIndexed() {
        return indexed;
    }

    public void setIndexed(boolean indexed) {
        this.indexed = indexed;
    }

    public void toggleIndexed() {
        this.indexed = !indexed;
    }

    public List<List<ItemStack>> getListItems() {
        return listItems;
    }

    public void generate() {
        Pair<Feature<?, ?>, CPos> informations = this.getInformations();
        if (informations == null || informations.getFirst() == null || informations.getSecond() == null) return;
        Loot.LootFactory<?> lootFactory = Chests.get(informations.getFirst().getClass());
        if (lootFactory != null) {
            this.listItems = lootFactory.create().getLootAt(
                informations.getSecond(),
                informations.getFirst(),
                this.indexed,
                this.getContext()
            );
        } else {
            this.listItems = null;
        }
        for (Updatable updatable : toUpdate) {
            updatable.update(true);
        }
    }

    public void registerUpdateable(Updatable... updatables) {
        toUpdate.addAll(Arrays.asList(updatables));
    }

    @FunctionalInterface
    public interface Updatable {
        void update(boolean hasChanged);
    }
}

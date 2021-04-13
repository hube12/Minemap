package kaptainwutax.minemap.feature.chests;

import kaptainwutax.featureutils.loot.item.Item;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.CPos;

import java.util.List;
import java.util.function.Predicate;

public abstract class Loot {

    public static final Predicate<ItemStack> ENCHANTED_GAPPLES_PRED=e->e.getItem().equals(Item.ENCHANTED_GOLDEN_APPLE);
    public List<List<ItemStack>> getLootAt(long worldSeed, int chunkX, int chunkZ, RegionStructure<?,?> structure, MCVersion version) {
        return getLootAt(worldSeed, new CPos(chunkX, chunkZ),structure, version);
    }

    public List<List<ItemStack>> getLootAt(long worldSeed, int chunkX, int chunkZ,RegionStructure<?,?> structure, ChunkRand rand, MCVersion version) {
        return getLootAt(worldSeed, new CPos(chunkX, chunkZ),structure, rand, version);
    }
    public List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos,RegionStructure<?,?> structure, MCVersion version){
        return getLootAt(worldSeed, cPos, structure,new ChunkRand(), version);
    }

    public abstract List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos, RegionStructure<?, ?> structure, ChunkRand rand, MCVersion version);

    public int getSumWithPredicate(List<List<ItemStack>> lists, Predicate<ItemStack> predicate) {
        return lists.stream().mapToInt(list->list.stream().anyMatch(predicate)?1:0).sum();
    }


    @FunctionalInterface
    public interface LootFactory<T extends Loot> {
        T create();
    }
}


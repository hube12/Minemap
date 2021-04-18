package kaptainwutax.minemap.feature.chests;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.featureutils.loot.item.Items;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.mcutils.util.pos.CPos;

import java.util.List;
import java.util.function.Predicate;

public abstract class Loot {

    public static final Predicate<ItemStack> ENCHANTED_GAPPLES_PRED = e -> e.getItem().equals(Items.ENCHANTED_GOLDEN_APPLE);

    public List<List<ItemStack>> getLootAt(long worldSeed, int chunkX, int chunkZ, RegionStructure<?, ?> structure, boolean indexed, BiomeSource biomeSource, MCVersion version) {
        return getLootAt(worldSeed, new CPos(chunkX, chunkZ), structure, indexed,biomeSource, version);
    }

    public List<List<ItemStack>> getLootAt(long worldSeed, int chunkX, int chunkZ, RegionStructure<?, ?> structure, boolean indexed, ChunkRand rand, BiomeSource biomeSource, MCVersion version) {
        return getLootAt(worldSeed, new CPos(chunkX, chunkZ), structure, indexed, rand,biomeSource, version);
    }

    public List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos, RegionStructure<?, ?> structure, boolean indexed, BiomeSource biomeSource, MCVersion version) {
        return getLootAt(worldSeed, cPos, structure, indexed, new ChunkRand(),biomeSource, version);
    }

    public abstract List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos, RegionStructure<?, ?> structure, boolean indexed, ChunkRand rand, BiomeSource biomeSource, MCVersion version);

    public int getSumWithPredicate(List<List<ItemStack>> lists, Predicate<ItemStack> predicate) {
        return lists.stream().mapToInt(list -> list.stream().anyMatch(predicate) ? 1 : 0).sum();
    }


    @FunctionalInterface
    public interface LootFactory<T extends Loot> {
        T create();
    }
}


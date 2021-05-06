package kaptainwutax.minemap.feature.chests;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.featureutils.loot.item.Items;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.featureutils.structure.generator.Generator;
import kaptainwutax.featureutils.structure.generator.Generators;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.terrainutils.ChunkGenerator;

import java.util.List;
import java.util.function.Predicate;

public abstract class Loot {

    public static final Predicate<ItemStack> ENCHANTED_GAPPLES_PRED = e -> e.getItem().equals(Items.ENCHANTED_GOLDEN_APPLE);

    public List<List<ItemStack>> getLootAt(long worldSeed, int chunkX, int chunkZ, RegionStructure<?, ?> structure, boolean indexed, BiomeSource biomeSource, ChunkGenerator generator, MCVersion version) {
        return getLootAt(worldSeed, new CPos(chunkX, chunkZ), structure, indexed, biomeSource, generator, version);
    }

    public List<List<ItemStack>> getLootAt(long worldSeed, int chunkX, int chunkZ, RegionStructure<?, ?> structure, boolean indexed, ChunkRand rand, BiomeSource biomeSource, ChunkGenerator generator, MCVersion version) {
        return getLootAt(worldSeed, new CPos(chunkX, chunkZ), structure, indexed, rand, biomeSource, generator, version);
    }

    public List<List<ItemStack>> getLootAt(CPos cPos, RegionStructure<?, ?> structure, boolean indexed, MapContext context) {
        if (context == null || structure == null || cPos == null) return null;
        return getLootAt(context.getWorldSeed(), cPos, structure, indexed, new ChunkRand(), context.getBiomeSource(), context.getChunkGenerator(), context.getVersion());
    }

    public List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos, RegionStructure<?, ?> structure, boolean indexed, BiomeSource biomeSource, ChunkGenerator generator, MCVersion version) {
        return getLootAt(worldSeed, cPos, structure, indexed, new ChunkRand(), biomeSource, generator, version);
    }

    public abstract List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos, RegionStructure<?, ?> structure, boolean indexed, ChunkRand rand, BiomeSource biomeSource, ChunkGenerator generator, MCVersion version);

    public static int getSumWithPredicate(List<List<ItemStack>> lists, Predicate<ItemStack> predicate) {
        return lists.stream().mapToInt(list -> list.stream().anyMatch(predicate) ? 1 : 0).sum();
    }

    public Generator.GeneratorFactory<?> getGeneratorFactory(Feature<?,?> feature){
        return Generators.get(feature.getClass());
    }

    @FunctionalInterface
    public interface LootFactory<T extends Loot> {
        T create();
    }
}


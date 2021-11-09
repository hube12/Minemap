package com.seedfinding.minemap.feature.chests;

import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.loot.ChestContent;
import com.seedfinding.mcfeature.loot.ILoot;
import com.seedfinding.mcfeature.loot.item.ItemStack;
import com.seedfinding.mcfeature.loot.item.Items;
import com.seedfinding.mcfeature.structure.generator.Generator;
import com.seedfinding.mcfeature.structure.generator.Generators;
import com.seedfinding.mcterrain.TerrainGenerator;
import com.seedfinding.minemap.init.Logger;
import com.seedfinding.minemap.ui.map.MapContext;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Loot {

    public static final Predicate<ItemStack> ENCHANTED_GAPPLES_PRED = e -> e.getItem().equals(Items.ENCHANTED_GOLDEN_APPLE);

    public List<List<ItemStack>> getLootAt(long worldSeed, int chunkX, int chunkZ, Feature<?, ?> feature, boolean indexed, TerrainGenerator generator, MCVersion version) {
        return getLootAt(worldSeed, new CPos(chunkX, chunkZ), feature, indexed, generator, version);
    }

    public List<List<ItemStack>> getLootAt(long worldSeed, int chunkX, int chunkZ, Feature<?, ?> feature, boolean indexed, ChunkRand rand, TerrainGenerator generator, MCVersion version) {
        return getLootAt(worldSeed, new CPos(chunkX, chunkZ), feature, indexed, rand, generator, version);
    }

    public List<List<ItemStack>> getLootAt(CPos cPos, Feature<?, ?> feature, boolean indexed, MapContext context) {
        if (context == null || feature == null || cPos == null) return null;
        Pair<TerrainGenerator, Function<CPos, CPos>> generator = context.getTerrainGenerator(feature);
        if (generator == null) return null;
        return getLootAt(context.getWorldSeed(), generator.getSecond().apply(cPos),
            feature, indexed, new ChunkRand(), generator.getFirst(), context.getVersion());
    }

    public List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos, Feature<?, ?> feature, boolean indexed, TerrainGenerator generator, MCVersion version) {
        return getLootAt(worldSeed, cPos, feature, indexed, new ChunkRand(), generator, version);
    }

    public List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos, Feature<?, ?> feature, boolean indexed, ChunkRand rand, TerrainGenerator generator, MCVersion version) {
        Generator.GeneratorFactory<?> factory = this.getGeneratorFactory(feature);
        if (factory == null) return null;
        if (!(feature instanceof ILoot)) return null;
        if (!isCorrectInstance(feature)) return null;
        if (generator == null) return null;
        Generator structureGen = factory.create(version);
        if (!structureGen.generate(generator, cPos, rand)) return null;
        List<ChestContent> loots = ((ILoot) feature).getLoot(worldSeed, structureGen, rand, indexed);
        if (loots == null) return null;
        return loots.stream().map(ChestContent::getItems).collect(Collectors.toList());
    }

    public abstract boolean isCorrectInstance(Feature<?, ?> feature);

    public static int getSumWithPredicate(List<List<ItemStack>> lists, Predicate<ItemStack> predicate) {
        if (lists == null || predicate == null) return 0;
        return lists.stream().mapToInt(list -> list.stream().filter(predicate).mapToInt(ItemStack::getCount).sum()).sum();
    }

    public Generator.GeneratorFactory<?> getGeneratorFactory(Feature<?, ?> feature) {
        Class<? extends Feature<?, ?>> superFeature = Chests.getSuperRegistry().get(feature.getClass());
        if (superFeature == null) {
            Logger.LOGGER.severe("Missing super feature " + feature);
            return null;
        }
        return Generators.get(superFeature);
    }

    @FunctionalInterface
    public interface LootFactory<T extends Loot> {
        T create();
    }
}


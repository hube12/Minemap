package kaptainwutax.minemap.feature.chests;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.loot.ILoot;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.featureutils.loot.item.Items;
import kaptainwutax.featureutils.structure.generator.Generator;
import kaptainwutax.featureutils.structure.generator.Generators;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.minemap.init.Logger;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.terrainutils.ChunkGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class Loot {

    public static final Predicate<ItemStack> ENCHANTED_GAPPLES_PRED = e -> e.getItem().equals(Items.ENCHANTED_GOLDEN_APPLE);

    public List<List<ItemStack>> getLootAt(long worldSeed, int chunkX, int chunkZ, Feature<?, ?> feature, boolean indexed, ChunkGenerator generator, MCVersion version) {
        return getLootAt(worldSeed, new CPos(chunkX, chunkZ), feature, indexed, generator, version);
    }

    public List<List<ItemStack>> getLootAt(long worldSeed, int chunkX, int chunkZ, Feature<?, ?> feature, boolean indexed, ChunkRand rand, ChunkGenerator generator, MCVersion version) {
        return getLootAt(worldSeed, new CPos(chunkX, chunkZ), feature, indexed, rand, generator, version);
    }

    public List<List<ItemStack>> getLootAt(CPos cPos, Feature<?, ?> feature, boolean indexed, MapContext context) {
        if (context == null || feature == null || cPos == null) return null;
        ChunkGenerator generator = null;
        if (feature.isValidDimension(context.getChunkGenerator().getBiomeSource().getDimension())) {
            generator = context.getChunkGenerator();
        } else {
            for (Dimension dimension : Dimension.values()) {
                if (feature.isValidDimension(dimension)) {
                    generator = context.getChunkGenerator(dimension);
                    break;
                }
            }
        }
        if (generator == null) return null;
        return getLootAt(context.getWorldSeed(), cPos, feature, indexed, new ChunkRand(), generator, context.getVersion());
    }

    public List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos, Feature<?, ?> feature, boolean indexed, ChunkGenerator generator, MCVersion version) {
        return getLootAt(worldSeed, cPos, feature, indexed, new ChunkRand(), generator, version);
    }

    public List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos, Feature<?, ?> feature, boolean indexed, ChunkRand rand, ChunkGenerator generator, MCVersion version) {
        Generator.GeneratorFactory<?> factory = this.getGeneratorFactory(feature);
        if (factory == null) return null;
        if (!(feature instanceof ILoot)) return null;
        if (!isCorrectInstance(feature)) return null;
        if (generator == null) return null;
        Generator structureGen = factory.create(version);
        if (!structureGen.generate(generator, cPos, rand)) return null;
        HashMap<Generator.ILootType, List<List<ItemStack>>> loots = ((ILoot) feature).getLoot(worldSeed, structureGen, rand, indexed);
        if (loots == null) return null;
        return loots.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }

    public abstract boolean isCorrectInstance(Feature<?, ?> feature);

    public static int getSumWithPredicate(List<List<ItemStack>> lists, Predicate<ItemStack> predicate) {
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


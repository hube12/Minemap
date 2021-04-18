package kaptainwutax.minemap.feature.chests.loot;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.loot.LootContext;
import kaptainwutax.featureutils.loot.MCLootTables;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.minemap.feature.chests.Loot;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.mcutils.util.pos.CPos;

import java.util.Arrays;
import java.util.List;

public class DesertPyramidLoot extends Loot {

    public List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos, RegionStructure<?, ?> structure, boolean indexed, ChunkRand rand, BiomeSource biomeSource, MCVersion version) {
        rand.setDecoratorSeed(worldSeed, cPos.getX() * 16, cPos.getZ() * 16, 40003, version);
        long lootTableSeed = rand.nextLong();
        LootContext context = new LootContext(lootTableSeed);
        List<ItemStack> loot1 = indexed ? MCLootTables.DESERT_PYRAMID_CHEST.generateIndexed(context) : MCLootTables.DESERT_PYRAMID_CHEST.generate(context);

        lootTableSeed = rand.nextLong();
        context = new LootContext(lootTableSeed);
        List<ItemStack> loot2 = indexed ? MCLootTables.DESERT_PYRAMID_CHEST.generateIndexed(context) : MCLootTables.DESERT_PYRAMID_CHEST.generate(context);

        lootTableSeed = rand.nextLong();
        context = new LootContext(lootTableSeed);
        List<ItemStack> loot3 = indexed ? MCLootTables.DESERT_PYRAMID_CHEST.generateIndexed(context) : MCLootTables.DESERT_PYRAMID_CHEST.generate(context);

        lootTableSeed = rand.nextLong();
        context = new LootContext(lootTableSeed);
        List<ItemStack> loot4 = indexed ? MCLootTables.DESERT_PYRAMID_CHEST.generateIndexed(context) : MCLootTables.DESERT_PYRAMID_CHEST.generate(context);

        return Arrays.asList(loot1, loot2, loot3, loot4);
    }


}

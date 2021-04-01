package kaptainwutax.minemap.feature.chests.loot;

import kaptainwutax.featureutils.loot.LootContext;
import kaptainwutax.featureutils.loot.MCLootTables;
import kaptainwutax.featureutils.loot.item.Item;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.minemap.feature.chests.Loot;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.CPos;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class BurriedTreasureLoot extends Loot {

    public List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos, ChunkRand rand, MCVersion version) {
        rand.setDecoratorSeed(worldSeed, cPos.getX() * 16, cPos.getZ() * 16, 40003, version);
        long lootTableSeed = rand.nextLong();
        LootContext context = new LootContext(lootTableSeed);
        List<ItemStack> loot1 = MCLootTables.DESERT_PYRAMID_CHEST.generate(context);

        lootTableSeed = rand.nextLong();
        context = new LootContext(lootTableSeed);
        List<ItemStack> loot2 = MCLootTables.DESERT_PYRAMID_CHEST.generate(context);

        lootTableSeed = rand.nextLong();
        context = new LootContext(lootTableSeed);
        List<ItemStack> loot3 = MCLootTables.DESERT_PYRAMID_CHEST.generate(context);

        lootTableSeed = rand.nextLong();
        context = new LootContext(lootTableSeed);
        List<ItemStack> loot4 = MCLootTables.DESERT_PYRAMID_CHEST.generate(context);

        return Arrays.asList(loot1, loot2, loot3, loot4);
    }



}

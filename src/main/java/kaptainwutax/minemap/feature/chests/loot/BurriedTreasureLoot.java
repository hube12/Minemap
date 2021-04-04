package kaptainwutax.minemap.feature.chests.loot;

import kaptainwutax.featureutils.loot.LootContext;
import kaptainwutax.featureutils.loot.MCLootTables;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.minemap.feature.chests.Loot;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.CPos;

import java.util.Collections;
import java.util.List;

public class BurriedTreasureLoot extends Loot {

    public List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos, ChunkRand rand, MCVersion version) {
        rand.setDecoratorSeed(worldSeed, cPos.getX() * 16, cPos.getZ() * 16, 30001, version);
        long lootTableSeed = rand.nextLong();
        LootContext context = new LootContext(lootTableSeed);
        List<ItemStack> loot1 = MCLootTables.BURIED_TREASURE_CHEST.generate(context);


        return Collections.singletonList(loot1);
    }



}

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

import java.util.Collections;
import java.util.List;

public class BurriedTreasureLoot extends Loot {

    public List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos, RegionStructure<?, ?> structure, boolean indexed, ChunkRand rand, BiomeSource biomeSource, MCVersion version) {
        rand.setDecoratorSeed(worldSeed, cPos.getX() * 16, cPos.getZ() * 16, 30001, version);
        long lootTableSeed = rand.nextLong();
        LootContext context = new LootContext(lootTableSeed);
        List<ItemStack> loot1 = indexed ? MCLootTables.BURIED_TREASURE_CHEST.generateIndexed(context) : MCLootTables.BURIED_TREASURE_CHEST.generate(context);

        return Collections.singletonList(loot1);
    }


}

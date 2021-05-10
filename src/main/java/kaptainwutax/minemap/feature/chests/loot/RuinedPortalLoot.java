package kaptainwutax.minemap.feature.chests.loot;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.loot.LootContext;
import kaptainwutax.featureutils.loot.MCLootTables;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.featureutils.structure.RuinedPortal;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.minemap.feature.chests.Loot;
import kaptainwutax.terrainutils.ChunkGenerator;

import java.util.Collections;
import java.util.List;

public class RuinedPortalLoot extends Loot {
    // TODO make it available in feature Utils
    @Override
    public List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos, Feature<?, ?> feature, boolean indexed, ChunkRand rand, ChunkGenerator generator, MCVersion version) {
        rand.setDecoratorSeed(worldSeed, cPos.getX() * 16, cPos.getZ() * 16, 40005, version);
        long lootTableSeed = rand.nextLong();
        LootContext context = new LootContext(lootTableSeed);
        List<ItemStack> loot1 = indexed ? MCLootTables.RUINED_PORTAL_CHEST.generateIndexed(context) : MCLootTables.RUINED_PORTAL_CHEST.generate(context);
        return Collections.singletonList(loot1);
    }

    @Override
    public boolean isCorrectInstance(Feature<?, ?> feature) {
        return feature instanceof RuinedPortal;
    }
}

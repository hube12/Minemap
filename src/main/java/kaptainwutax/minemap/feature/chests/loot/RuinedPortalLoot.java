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
import kaptainwutax.terrainutils.TerrainGenerator;

import java.util.Collections;
import java.util.List;

public class RuinedPortalLoot extends Loot {

    @Override
    public boolean isCorrectInstance(Feature<?, ?> feature) {
        return feature instanceof RuinedPortal;
    }
}

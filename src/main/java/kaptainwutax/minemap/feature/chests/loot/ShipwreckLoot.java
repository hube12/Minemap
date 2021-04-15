package kaptainwutax.minemap.feature.chests.loot;

import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.featureutils.structure.Shipwreck;
import kaptainwutax.minemap.feature.chests.Loot;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.CPos;

import java.util.ArrayList;
import java.util.List;

public class ShipwreckLoot extends Loot {

    public List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos, RegionStructure<?, ?> structure, ChunkRand rand, MCVersion version) {
        if (structure instanceof Shipwreck) {
            Shipwreck shipwreck = (Shipwreck) structure;
            return new ArrayList<>(shipwreck.getLoot(cPos, worldSeed, rand).values());
        }
        return null;
    }


}

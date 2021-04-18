package kaptainwutax.minemap.feature.chests.loot;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.featureutils.structure.Shipwreck;
import kaptainwutax.minemap.feature.chests.Loot;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.mcutils.util.pos.CPos;

import java.util.ArrayList;
import java.util.List;

public class ShipwreckLoot extends Loot {

    public List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos, RegionStructure<?, ?> structure, boolean indexed, ChunkRand rand, BiomeSource biomeSource, MCVersion version) {
        if (structure instanceof Shipwreck) {
            Shipwreck shipwreck = (Shipwreck) structure;
            shipwreck.reset();
            structure.canSpawn(cPos.getX(),cPos.getZ(),biomeSource);
            return new ArrayList<>(shipwreck.getLoot(cPos, worldSeed, rand, indexed).values());
        }
        return null;
    }


}

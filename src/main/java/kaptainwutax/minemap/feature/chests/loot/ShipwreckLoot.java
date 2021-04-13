package kaptainwutax.minemap.feature.chests.loot;

import kaptainwutax.featureutils.loot.LootContext;
import kaptainwutax.featureutils.loot.MCLootTables;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.featureutils.structure.Shipwreck;
import kaptainwutax.minemap.feature.chests.Loot;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.CPos;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ShipwreckLoot extends Loot {

    public List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos, RegionStructure<?, ?> structure, ChunkRand rand, MCVersion version) {
       if (structure instanceof Shipwreck){
           Shipwreck shipwreck=(Shipwreck)structure;
           if (shipwreck.isBeached()!=null){
               Shipwreck.Rotation rotation=shipwreck.getRotation(worldSeed,cPos,version);
               int salt=40006; //version dependant
               // this is wrong for 2/3n rotations
               rand.setDecoratorSeed(worldSeed, cPos.getX() * 16, cPos.getZ() * 16, salt, version);
               if (shipwreck.isBeached()){
                   rand.nextInt(3);
               }
               rand.advance(2);
               long lootSeedSupply = rand.nextLong();
               LootContext context3 = new LootContext(lootSeedSupply);
               List<ItemStack> supplyLoot = MCLootTables.SHIPWRECK_SUPPLY_CHEST.generate(context3);

               // this is wrong also for 2/n rotation
               rand.setDecoratorSeed(worldSeed, cPos.getX() * 16, (cPos.getZ()+1) * 16, salt, version);
               if (shipwreck.isBeached()){
                   rand.nextInt(3);
               }
               rand.advance(4); //wrong for 1.15-

               // those 2 might be switched around depending of the rotation
               long lootSeedMap = rand.nextLong();
               long lootSeedTreasure = rand.nextLong();

               List<ItemStack> mapLoot = MCLootTables.SHIPWRECK_MAP_CHEST.generate(new LootContext(lootSeedMap));
               List<ItemStack> treasureLoot = MCLootTables.SHIPWRECK_TREASURE_CHEST.generate(new LootContext(lootSeedTreasure));

               return Arrays.asList(treasureLoot,supplyLoot,mapLoot);
           }

       }
       return null;
    }



}

package kaptainwutax.minemap.feature.chests.loot;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.featureutils.structure.EndCity;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.featureutils.structure.generator.EndCityGenerator;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.minemap.feature.chests.Loot;
import kaptainwutax.terrainutils.ChunkGenerator;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class EndCityLoot extends Loot {
    public List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos, RegionStructure<?, ?> structure, boolean indexed, ChunkRand rand, BiomeSource biomeSource, ChunkGenerator generator, MCVersion version) {
        if (structure instanceof EndCity) {
            EndCityGenerator endCityGenerator = new EndCityGenerator(version);
            endCityGenerator.generate(generator, cPos, rand);
            EndCity endCity = (EndCity) structure;
            return endCity.getLoot(worldSeed, endCityGenerator, rand, indexed).values().stream().flatMap(Collection::stream).collect(Collectors.toList());
        }
        return null;
    }
}

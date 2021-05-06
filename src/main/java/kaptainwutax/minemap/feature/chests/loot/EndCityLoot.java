package kaptainwutax.minemap.feature.chests.loot;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.featureutils.structure.BuriedTreasure;
import kaptainwutax.featureutils.structure.EndCity;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.featureutils.structure.generator.EndCityGenerator;
import kaptainwutax.featureutils.structure.generator.Generator;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.minemap.feature.chests.Loot;
import kaptainwutax.terrainutils.ChunkGenerator;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class EndCityLoot extends Loot {
    public List<List<ItemStack>> getLootAt(long worldSeed, CPos cPos, RegionStructure<?, ?> structure, boolean indexed, ChunkRand rand, BiomeSource biomeSource, ChunkGenerator generator, MCVersion version) {
        Generator.GeneratorFactory<?> factory = this.getGeneratorFactory(structure);
        if (factory == null) return null;
        if (!(structure instanceof EndCity)) return null;
        Generator structureGen = factory.create(version);
        structureGen.generate(generator, cPos, rand);
        HashMap<Generator.ILootType, List<List<ItemStack>>> loots = ((EndCity) structure).getLoot(worldSeed, structureGen, rand, indexed);
        if (loots == null) return null;
        return loots.values().stream().flatMap(Collection::stream).collect(Collectors.toList());
    }
}

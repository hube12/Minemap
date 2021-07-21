package kaptainwutax.minemap.feature;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.featureutils.structure.Stronghold;
import kaptainwutax.featureutils.structure.Structure;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.util.data.SpiralIterator;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.util.pos.RPos;
import kaptainwutax.terrainutils.TerrainGenerator;

import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class StructureHelper {

    public static Stream<BPos> getClosest(Structure<?, ?> structure, BPos currentPos, long worldseed, ChunkRand chunkRand, BiomeSource source, TerrainGenerator terrainGenerator, int dimCoeff) {
        if (structure instanceof RegionStructure<?, ?>) {
            RegionStructure<?, ?> regionStructure = (RegionStructure<?, ?>) structure;
            int chunkInRegion = regionStructure.getSpacing();
            int regionSize = chunkInRegion * 16;
            RPos centerRPos = currentPos.toRegionPos(regionSize);

            final int border = 30_000_000;
            SpiralIterator<RPos> spiral = new SpiralIterator<>(centerRPos, new BPos(-border, 0, -border).toRegionPos(regionSize), new BPos(border, 0, border).toRegionPos(regionSize), 1, (x, y, z) -> new RPos(x, z, regionSize));

            return StreamSupport.stream(spiral.spliterator(), false)
                .map(rPos -> StructureHelper.getInRegion(regionStructure, worldseed, chunkRand, rPos))
                .filter(Objects::nonNull) // remove for methods like bastion that use a float and is not in each region
                .filter(cPos -> StructureHelper.canSpawn(regionStructure, cPos, source))
                .filter(cPos -> terrainGenerator == null || StructureHelper.canGenerate(regionStructure, cPos, terrainGenerator))
                .map(cPos -> {
                    BPos dimPos = cPos.toBlockPos().add(9, 0, 9);
                    return new BPos(dimPos.getX() << dimCoeff, 0, dimPos.getZ() << dimCoeff);
                });
        } else {
            if (structure instanceof Stronghold) {
                return StreamSupport.stream(Spliterators.spliterator(((Stronghold) structure).getAllStarts(source, chunkRand), Spliterator.ORDERED), false)
                    .map(cPos -> {
                        BPos dimPos = ((CPos) cPos).toBlockPos().add(9, 0, 9);
                        return new BPos(dimPos.getX() << dimCoeff, 0, dimPos.getZ() << dimCoeff);
                    });
            }
        }
        return null;
    }

    public static CPos getInRegion(RegionStructure<?, ?> structure, long worldseed, ChunkRand chunkRand, RPos rPos) {
        return structure.getInRegion(worldseed, rPos.getX(), rPos.getZ(), chunkRand);
    }

    public static boolean canSpawn(RegionStructure<?, ?> structure, CPos cPos, BiomeSource source) {
        return structure.canSpawn(cPos.getX(), cPos.getZ(), source);
    }

    public static boolean canGenerate(RegionStructure<?, ?> structure, CPos cPos, TerrainGenerator source) {
        return structure.canGenerate(cPos.getX(), cPos.getZ(), source);
    }
}

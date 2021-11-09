package com.seedfinding.minemap.ui.map.interactive;

import com.seedfinding.mcfeature.structure.RuinedPortal;
import com.seedfinding.mcfeature.structure.generator.structure.RuinedPortalGenerator;
import com.seedfinding.mccore.block.Block;
import com.seedfinding.mccore.block.Blocks;
import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mccore.util.pos.CPos;
import com.seedfinding.minemap.init.Logger;
import com.seedfinding.minemap.ui.map.MapPanel;
import com.seedfinding.minemap.util.snksynthesis.voxelgame.EventManager;
import com.seedfinding.minemap.util.snksynthesis.voxelgame.Visualizer;
import com.seedfinding.minemap.util.snksynthesis.voxelgame.block.BlockType;
import com.seedfinding.mcterrain.TerrainGenerator;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class Portal {
    private final MapPanel map;
    private CPos pos;
    private RuinedPortal feature;
    private static final Visualizer visualizer = new Visualizer();

    public Portal(MapPanel map) {
        this.map = map;
    }

    public Visualizer getVisualizer() {
        return visualizer;
    }

    public void run() {
        // we wait for 5ms in case of spam click so the other thread could be launched
        SwingUtilities.invokeLater(() -> {
                if (!visualizer.isRunning()) {
                    Thread t = new Thread(() -> {
                        visualizer.run(true); //blocking
                    });
                    t.start();
                }
            }
        );
    }

    public void setFeature(RuinedPortal feature) {
        this.feature = feature;
    }

    public Pair<RuinedPortal, CPos> getInformations() {
        return new Pair<>(this.feature, this.pos);
    }

    public boolean generateContent() {
        Pair<RuinedPortal, CPos> informations = this.getInformations();
        RuinedPortalGenerator ruinedPortalGenerator = new RuinedPortalGenerator(informations.getFirst().getVersion());
        Pair<TerrainGenerator, Function<CPos, CPos>> generator = this.map.context.getTerrainGenerator(feature);
        if (generator == null) {
            visualizer.setText("Portal did not generate");
            getVisualizer().getBlockManager().scheduleDestroy();
            return false;
        }
        if (!ruinedPortalGenerator.generate(generator.getFirst(), generator.getSecond().apply(informations.getSecond()))) {
            visualizer.setText("Portal did not generate");
            getVisualizer().getBlockManager().scheduleDestroy();
            return false;
        }
        List<Pair<Block, BPos>> blocks = ruinedPortalGenerator.getObsidian();
        if (blocks == null || blocks.isEmpty()) {
            visualizer.setText("Portal did not generate");
            getVisualizer().getBlockManager().scheduleDestroy();
            return false;
        }
        Pair<HashMap<BPos, Block>, BPos> blockHashMap = getBlockHashmap(blocks, informations);
        getVisualizer().getBlockManager().scheduleDestroy();
        for (BPos pos : blockHashMap.getFirst().keySet()) {
            BPos p = pos.subtract(blockHashMap.getSecond()).add(8, 0, 8);
            Block block = blockHashMap.getFirst().get(pos);
            BlockType type = block == Blocks.OBSIDIAN ? BlockType.OBSIDIAN : block == Blocks.CRYING_OBSIDIAN ? BlockType.CRYING_OBSIDIAN : BlockType.GRASS;
            getVisualizer().getBlockManager().scheduleBlock(p, type);
        }
        BPos pos = ruinedPortalGenerator.getPos();
        getVisualizer().setText(String.format("Generated at %d, %d, %d and was %s with world height at %d",
            pos.getX(), pos.getY(), pos.getZ(), ruinedPortalGenerator.isBuried() ? "buried" : "at the surface", ruinedPortalGenerator.getHeight()));
        Visualizer.getEventManager().clearEventHandler();
        Visualizer.getEventManager().addEventHandler(createHandler(ruinedPortalGenerator, EventManager.EventAction.KEY_1, getVisualizer(), blockHashMap.getSecond(), RuinedPortalGenerator::getObsidian));
        Visualizer.getEventManager().addEventHandler(createHandler(ruinedPortalGenerator, EventManager.EventAction.KEY_2, getVisualizer(), blockHashMap.getSecond(), RuinedPortalGenerator::getPortal));
        Visualizer.getEventManager().addEventHandler(createHandler(ruinedPortalGenerator, EventManager.EventAction.KEY_3, getVisualizer(), blockHashMap.getSecond(), RuinedPortalGenerator::getMinimalPortal));
        return true;
    }

    public static Pair<HashMap<BPos, Block>, BPos> getBlockHashmap(List<Pair<Block, BPos>> blocks, Pair<RuinedPortal, CPos> informations) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;
        HashMap<BPos, Block> blockHashMap = new HashMap<>();
        for (Pair<Block, BPos> block : blocks) {
            if (blockHashMap.containsKey(block.getSecond())) {
                Logger.LOGGER.severe("Impossible case " + informations + " " + block);
                System.err.println("Impossible case " + informations + " " + block);
            }
            blockHashMap.put(block.getSecond(), block.getFirst());
            minX = Math.min(block.getSecond().getX(), minX);
            minY = Math.min(block.getSecond().getY(), minY);
            minZ = Math.min(block.getSecond().getZ(), minZ);
            maxX = Math.max(block.getSecond().getX(), maxX);
            maxY = Math.max(block.getSecond().getY(), maxY);
            maxZ = Math.max(block.getSecond().getZ(), maxZ);
        }
        return new Pair<>(blockHashMap, new BPos(minX, minY, minZ));
    }

    public static Consumer<EventManager.EventAction> createHandler(RuinedPortalGenerator ruinedPortalGenerator,
                                                                   EventManager.EventAction eventAction,
                                                                   Visualizer visualizer,
                                                                   BPos offsetPos,
                                                                   Function<RuinedPortalGenerator, List<Pair<Block, BPos>>> posFn) {
        return e -> {
            if (e == eventAction) {
                if (visualizer != null && ruinedPortalGenerator != null && visualizer.isRunning()) {
                    List<Pair<Block, BPos>> blocks = posFn.apply(ruinedPortalGenerator);
                    if (blocks == null || blocks.isEmpty()) {
                        visualizer.setText("Portal did not generate");
                        visualizer.getBlockManager().scheduleDestroy();
                        return;
                    }
                    Pair<HashMap<BPos, Block>, BPos> blockHashMap = getBlockHashmap(blocks, null);
                    visualizer.getBlockManager().scheduleDestroy();
                    for (BPos pos : blockHashMap.getFirst().keySet()) {
                        BPos p = pos.subtract(offsetPos).add(8, 0, 8);
                        Block block = blockHashMap.getFirst().get(pos);
                        BlockType type = block == Blocks.OBSIDIAN ? BlockType.OBSIDIAN : block == Blocks.CRYING_OBSIDIAN ? BlockType.CRYING_OBSIDIAN : BlockType.GRASS;
                        visualizer.getBlockManager().scheduleBlock(p, type);
                    }
                }
            }
        };
    }

    public void setPos(CPos pos) {
        this.pos = pos;
    }


}

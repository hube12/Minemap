package kaptainwutax.minemap.util.snksynthesis.voxelgame.block;

import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.minemap.util.snksynthesis.voxelgame.gfx.Shader;
import org.lwjgl.system.MemoryStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class BlockManager {

    private final List<Block> blocks = new ArrayList<>();
    private final AtomicBoolean scheduledDestroy=new AtomicBoolean(false);
    private final List<Pair<BPos, BlockType>> scheduledBlocks = new ArrayList<>();

    public final int WIDTH = 50;
    public final int LENGTH = 50;

    public void draw(Shader shader, MemoryStack stack) {
        doScheduledBlock();
        for (Block block : blocks) {
            block.draw(shader, stack);
        }
    }

    public void checkDestroy(){
        if (this.scheduledDestroy.get()){
            destroy();
            this.scheduledDestroy.set(false);
        }
    }

    public void destroy() {
        for (Block block : blocks) {
            block.destroy();
        }
        blocks.clear();
    }

    public void scheduleDestroy(){
        this.scheduledDestroy.set(true);
    }

    public void doScheduledBlock() {
        for (Pair<BPos, BlockType> scheduledBlock : scheduledBlocks) {
            addBlock(scheduledBlock.getFirst(), scheduledBlock.getSecond());
        }
        scheduledBlocks.clear();
    }

    public void scheduleBlock(BPos pos, BlockType type) {
        scheduledBlocks.add(new Pair<>(pos, type));
    }

    public void addBlock(BPos pos, BlockType type) {
        this.addBlock(pos.getX(), pos.getY(), pos.getZ(), type);
    }

    public void addBlock(float x, float y, float z, BlockType type) {
        Block block = new Block(type);
        block.getModel().translate(x, y, z);
        blocks.add(block);
    }

}

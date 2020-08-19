package kaptainwutax.minemap;

import java.util.Collections;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

public class MT {

    protected ThreadPoolExecutor executor;
    protected final int threads;

    public MT() {
        this(Runtime.getRuntime().availableProcessors());
    }

    public MT(int threads) {
        this.threads = threads;
        this.executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(this.threads);
    }

    public int getThreads() {
        return this.threads;
    }

    public void yeet(Runnable task) {
        this.executor.execute(task);
    }

    public void yeetBlock(BlockRunnable task, long blockId, long blockSize) {
        this.yeet(task.toRunnable(blockId, blockSize));
    }

    public void yeetBlocks(BlockRunnable task, long minBlockId, long maxBlockId, long blockSize) {
        for(long blockId = minBlockId; blockId < maxBlockId; blockId++) {
            this.yeetBlock(task, blockId, blockSize);
        }
    }

    public void yeetAllBlocks(BlockRunnable task, long searchSpace) {
        this.yeetAllBlocks(task, searchSpace, this.getThreads());
    }

    public void yeetAllBlocks(BlockRunnable task, long searchSpace, long blockCount) {
        int blockId = 0;
        long blockSize = searchSpace / blockCount, space;

        for(space = searchSpace; space >= blockSize; blockId++, space -= blockSize) {
            this.yeetBlock(task, blockId, blockSize);
        }

        if(space > 0) {
            int finalBlockId = blockId;
            this.yeet(() -> task.run(finalBlockId * blockSize, searchSpace));
        }
    }

    public void chill() {
        try {
            this.executor.shutdown();
            this.executor.awaitTermination(100, TimeUnit.DAYS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public <T> void yeeterate(Iterable<T> iterable, Consumer<T> task) {
        this.flatYeeterate(iterable, Collections::singleton, task);
    }

    public <T> void flatYeeterate(Iterable<T> iterable, Function<T, Iterable<T>> mapper, Consumer<T> task) {
        iterable.forEach(t -> yeet(() -> mapper.apply(t).forEach(task)));
    }

    @FunctionalInterface
    public interface BlockRunnable {
        void run(long start, long end);

        default Runnable toRunnable(long blockId, long blockSize) {
            return () -> this.run(blockId * blockSize, (blockId + 1) * blockSize);
        }
    }

}

package com.seedfinding.minemap.util.snksynthesis.voxelgame;

import java.util.LinkedList;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class EventManager {
    private final LinkedList<Consumer<EventAction>> handlers = new LinkedList<>();
    private final AtomicBoolean shouldClear = new AtomicBoolean(false);
    private final LinkedTransferQueue<Consumer<EventAction>> temp = new LinkedTransferQueue<>();

    public EventManager() {

    }

    public void addEventHandler(Consumer<EventAction> handler) {
        temp.offer(handler);
        process(EventAction.NONE);
    }

    protected void process(EventAction eventAction) {
        if (shouldClear.get()) {
            handlers.clear();
            shouldClear.set(false);
        }
        // we process only one handler at the time (no need to rush)
        Consumer<EventAction> newHandler;
        while ((newHandler = temp.poll()) != null) {
            handlers.add(newHandler);
        }
        if (eventAction == EventAction.NONE) return;
        for (Consumer<EventAction> handler : handlers) {
            handler.accept(eventAction);
        }
    }

    public void clearEventHandler() {
        shouldClear.set(true);
        process(EventAction.NONE);
    }

    protected void destroy() {
        handlers.clear();
    }

    public enum EventAction {
        KEY_0,
        KEY_1,
        KEY_2,
        KEY_3,
        KEY_4,
        KEY_5,
        KEY_6,
        KEY_7,
        KEY_8,
        KEY_9,

        NONE
    }
}

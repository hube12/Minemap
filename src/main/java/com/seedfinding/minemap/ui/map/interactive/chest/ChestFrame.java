package com.seedfinding.minemap.ui.map.interactive.chest;


import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.minemap.init.Icons;
import com.seedfinding.minemap.ui.map.MapPanel;
import com.seedfinding.minemap.util.data.Str;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ChestFrame extends JFrame {
    private final JPanel content;
    private final ChestInstance chestInstance;
    private final JScrollPane scrollPane;
    private final List<ChestPanel> chestContents = new ArrayList<>();
    private final JLabel emptyLabel = new JLabel("No chests available here", SwingConstants.CENTER);
    public static final int PAGE_SIZE = 4; // max chests shown at once
    private int cols = 1;
    private int rows = 1;
    static final int HEADER_HEIGHT = 30;
    static final int CHEST_HEIGHT = 300;
    static final int CHEST_WIDTH = 700;

    public ChestFrame(ChestInstance chestInstance) {
        this.chestInstance = chestInstance;
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);
        GridLayout gridLayout = new GridLayout(-1, 2, 15, 15);
        content = new JPanel();
        content.setLayout(gridLayout);
        ensureCapacity(1);
        content.add(chestContents.getFirst());
        TopBar topBar = new TopBar(chestInstance, this);
        this.add(topBar, BorderLayout.NORTH);
        scrollPane = new JScrollPane(content);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.getVerticalScrollBar().setUnitIncrement(24);
        this.add(scrollPane, BorderLayout.CENTER);
        // to center I need the size first
        this.setSize(this.getPreferredSize());
        this.setLocationRelativeTo(null); // center
        this.setVisible(false);
        this.setIconImage(Icons.get(this.getClass()));
    }


    @Override
    public String getName() {
        return "Chest Content";
    }

    @Override
    public int getDefaultCloseOperation() {
        return JFrame.HIDE_ON_CLOSE;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(CHEST_WIDTH, HEADER_HEIGHT + CHEST_HEIGHT);
    }


    /**
     * Lazily create chest panels so there is no hard limit on the number of chests;
     * the scroll pane takes care of showing them all.
     */
    public void ensureCapacity(int count) {
        while (chestContents.size() < count) {
            chestContents.add(new ChestPanel(new Pair<>(0.7, 0.7)));
        }
    }

    private Rectangle mapBounds() {
        MapPanel map = chestInstance.getMap();
        if (map == null || !map.isShowing()) return null;
        return new Rectangle(map.getLocationOnScreen(), map.getSize());
    }

    /**
     * Set the window size for a grid of chests (same sizes as before), never bigger than the map panel.
     */
    public void resizeFor(int columns, int rowCount) {
        if (columns == this.cols && rowCount == this.rows) return; // same layout: keep the user's size (e.g. paging)
        applySize(columns, rowCount);
    }

    private void applySize(int columns, int rowCount) {
        this.cols = columns;
        this.rows = rowCount;
        Dimension base = this.getPreferredSize();
        Rectangle b = mapBounds();
        int w = base.width * cols;
        int h = base.height * rows;
        if (b != null) {
            w = Math.min(w, b.width);
            h = Math.min(h, b.height);
        }
        // grow/shrink around the current center so a centered window stays centered
        Rectangle old = this.getBounds();
        this.setSize(w, h);
        if (this.isVisible()) {
            this.setLocation(old.x + (old.width - w) / 2, old.y + (old.height - h) / 2);
        }
    }

    /**
     * Apply the default size and center the window on the map panel.
     */
    public void centerOnMap() {
        applySize(cols, rows);
        Rectangle b = mapBounds();
        if (b == null) {
            this.setLocationRelativeTo(null);
            return;
        }
        this.setLocation(b.x + (b.width - getWidth()) / 2, b.y + (b.height - getHeight()) / 2);
    }

    @Override
    public void setVisible(boolean visible) {
        if (visible && !isVisible()) centerOnMap(); // default placement only, the user can then drag it anywhere
        super.setVisible(visible);
    }

    /**
     * Show a default message instead of the chests when there is nothing to display.
     */
    public void setEmptyMessage(boolean empty) {
        Component target = empty ? emptyLabel : content;
        if (scrollPane.getViewport().getView() != target) {
            scrollPane.setViewportView(target);
        }
    }

    public List<ChestPanel> getChestContents() {
        return chestContents;
    }

    public JPanel getContent() {
        return content;
    }

    public JScrollPane getScrollPane() {
        return scrollPane;
    }

    public void updateFirst() {
        this.setTitle(String.format("%s of %s at x:%d z:%d", this.getName(), Str.prettifyDashed(
            this.chestInstance.getFeature().getName()), this.chestInstance.getPos().getX() * 16 + 9, this.chestInstance.getPos().getZ() * 16 + 9));
    }
}

package kaptainwutax.minemap.ui.map.interactive.chest;


import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.minemap.init.Icons;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static kaptainwutax.minemap.util.data.Str.prettifyDashed;

public class ChestFrame extends JFrame {
    private final JPanel content;
    private final ChestInstance chestInstance;
    private final JScrollPane scrollPane;
    private final List<ChestPanel> chestContents = new ArrayList<>();
    private final TopBar topBar;
    private final static int MAX_NUMBER_CHESTS = 32; //TODO find the max we will support
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
        for (int i = 0; i < MAX_NUMBER_CHESTS; i++) {
            chestContents.add(new ChestPanel(new Pair<>(0.7, 0.7)));
        }
        content.add(chestContents.get(0));
        topBar = new TopBar(chestInstance, this);
        this.add(topBar, BorderLayout.NORTH);
        scrollPane = new JScrollPane(content);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
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
        this.setTitle(String.format("%s of %s at x:%d z:%d", this.getName(), prettifyDashed(
            this.chestInstance.getFeature().getName()), this.chestInstance.getPos().getX() * 16 + 9, this.chestInstance.getPos().getZ() * 16 + 9));
    }
}

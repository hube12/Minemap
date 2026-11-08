package com.seedfinding.minemap.util.ui.graphics;

import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.util.data.Str;
import com.seedfinding.minemap.util.ui.buttons.CopyButton;
import com.seedfinding.minemap.util.ui.buttons.JumpButton;
import com.seedfinding.minemap.util.ui.interactive.ListPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;

public class TpPanel {
    public static void makeFrame(List<BPos> bPosList, Feature<?, ?> feature) {
        makeFrame(bPosList, feature, String.format("List of %d %s", bPosList.size(), Str.prettifyDashed(feature.getName())));
    }

    public static void makeFrame(List<BPos> bPosList, Feature<?, ?> feature, String title) {
        makeFrame(bPosList, feature, title, null);
    }

    /** Something the list window can show on the map and hide again (e.g. a route through the listed points). */
    public interface MapToggle {
        boolean isShown();

        void toggle();

        /** Registers a callback for when the shown state changes from outside the window (e.g. sidebar close button). */
        void onRemoved(Runnable listener);
    }

    /**
     * @param mapToggle if not null, adds a button that shows or hides the list on the map
     */
    public static void makeFrame(List<BPos> bPosList, Feature<?, ?> feature, String title, MapToggle mapToggle) {
        // create a new frame
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new java.awt.Dimension(500, 80 + Math.min(350, 70 * bPosList.size())));

        ListPanel content = buildContent(bPosList, feature, mapToggle, frame);
        frame.add(content);

        // display it
        frame.pack();
        // never narrower than what the rows need to keep their buttons inside the box
        java.awt.Insets insets = frame.getInsets();
        frame.setMinimumSize(new java.awt.Dimension(content.getMinimumRowsWidth() + insets.left + insets.right + 8, 200));
        frame.setLocationRelativeTo(MineMap.INSTANCE); // center
        frame.setVisible(true);
    }

    /** The whole content of the list window (rows + buttons). Separate from the frame so it can be laid out and tested alone. */
    static ListPanel buildContent(List<BPos> bPosList, Feature<?, ?> feature, MapToggle mapToggle, Window window) {
        // create the inner list
        final ListPanel listPanel = new ListPanel();
        List<Entry> entries = bPosList.stream().map(e -> new Entry(feature, e, listPanel)).toList();
        // Every row gets the width of the widest one: same black box everywhere, so the buttons line up column-wise.
        int boxWidth = entries.stream().mapToInt(e -> e.getPreferredSize().width).max().orElse(0);
        entries.forEach(e -> {
            int height = e.getPreferredSize().height;
            // the box height is fixed: only its width follows the window (between its minimum and the common width)
            e.setPreferredSize(new java.awt.Dimension(boxWidth, height));
            e.setMaximumSize(new java.awt.Dimension(boxWidth, height));
            e.setMinimumSize(new java.awt.Dimension(e.getMinimumSize().width, height));
            listPanel.addPanel(e);
        });
        listPanel.removeLastBorder();

        JButton copyTPs = new JButton("Copy all TPs");
        copyTPs.addActionListener(event -> {
            StringBuilder copyString = new StringBuilder();
            bPosList.forEach(bPos -> copyString.append(String.format("/tp @p %d ~ %d", bPos.getX(), bPos.getZ())).append("\n"));
            StringSelection stringSelection = new StringSelection(copyString.toString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            copyTPs.setBackground(new Color(50, 255, 84));
            copyTPs.setForeground(Color.WHITE);
        });

        JButton copyLocations = new JButton("Copy all locations");
        copyLocations.addActionListener(event -> {
            StringBuilder copyString = new StringBuilder(String.format("%s\nposX,posZ\n", feature.getName()));
            bPosList.forEach(bPos -> copyString.append(String.format("%d,%d", bPos.getX(), bPos.getZ())).append("\n"));
            StringSelection stringSelection = new StringSelection(copyString.toString());
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);
            copyLocations.setBackground(new Color(50, 255, 84));
            copyLocations.setForeground(Color.WHITE);
        });

        JSplitPane copyPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, copyTPs, copyLocations);
        copyPane.setResizeWeight(1.0);
        if (mapToggle == null) {
            listPanel.add(copyPane, BorderLayout.SOUTH);
        } else {
            JButton drawRoute = new JButton();
            // the route can also be removed from the map elsewhere (sidebar), so always read the real state
            Runnable refresh = () -> drawRoute.setText(mapToggle.isShown() ? "Hide route from map" : "Draw route on map");
            refresh.run();
            mapToggle.onRemoved(() -> SwingUtilities.invokeLater(refresh));
            drawRoute.addActionListener(event -> {
                mapToggle.toggle();
                refresh.run();
            });
            if (window != null) window.addWindowFocusListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowGainedFocus(java.awt.event.WindowEvent e) {
                    refresh.run();
                }
            });
            JPanel south = new JPanel(new GridLayout(0, 1));
            south.add(drawRoute);
            south.add(copyPane);
            listPanel.add(south, BorderLayout.SOUTH);
        }

        return listPanel;
    }

    static class Entry extends RoundedFloatingEntry {
        /** Space above and below the content inside the black box. */
        private static final int VERTICAL_PADDING = 3;
        // Jump, copy and close share one icon box so they line up: 16 * 1.5 = 24 px, centred in the 30 px button.
        private static final int ACTION_ICON_INSET = RoundedFloatingEntry.ACTION_ICON_INSET;
        private static final float ACTION_ICON_FACTOR = RoundedFloatingEntry.ACTION_ICON_FACTOR;

        private final CopyButton copyCoordinate;
        private final JumpButton jumpCoordinate;

        public Entry(Feature<?, ?> feature, BPos pos, ListPanel listPanel) {
            super(" [" + pos.getX() + ", " + pos.getZ() + "] " + Str.formatName(feature.getName()),
                feature.getClass(), panel -> e -> listPanel.removePanel(panel));


            this.copyCoordinate = new CopyButton(16, ACTION_ICON_INSET, ACTION_ICON_FACTOR, true, Color.DARK_GRAY);
            this.copyCoordinate.addActionListener(e -> {
                String myString = String.format("/tp @p %d ~ %d", pos.getX(), pos.getZ());
                StringSelection stringSelection = new StringSelection(myString);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                ListPanel.toggleParentChildren(2, this, component -> component.copyCoordinate.changeBColor(Color.DARK_GRAY));
                this.copyCoordinate.changeBColor(new Color(50, 255, 84));
            });

            this.jumpCoordinate = new JumpButton(16, ACTION_ICON_INSET, ACTION_ICON_FACTOR, true, Color.DARK_GRAY);
            this.jumpCoordinate.addActionListener(e -> {
                MineMap.INSTANCE.worldTabs.getSelectedMapPanel().manager.setCenterPos(pos.getX(), pos.getZ());
                ListPanel.toggleParentChildren(2, this, component -> component.jumpCoordinate.changeBColor(Color.DARK_GRAY));
                this.jumpCoordinate.changeBColor(new Color(50, 255, 84));
            });

            this.stretchText(); // keeps jump, copy and close at the same X in every row, and paints the box at its real size
            // padding so the rounded corners do not bite the icon and buttons, plus the room the box leaves for its shadow
            this.setBorder(BorderFactory.createEmptyBorder(VERTICAL_PADDING, 6, VERTICAL_PADDING + this.shadowGap, 10 + this.shadowGap));
            this.addAtIndex(this.jumpCoordinate, 2);
            this.addAtIndex(this.copyCoordinate, 3);

            this.setBackground(new Color(0, 0, 0, 180));
        }
    }
}

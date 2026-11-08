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

        // create the inner list
        final ListPanel listPanel = new ListPanel();
        bPosList.forEach(e -> listPanel.addPanel(new Entry(feature, e, listPanel)));
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
            frame.addWindowFocusListener(new java.awt.event.WindowAdapter() {
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

        frame.add(listPanel);

        // display it
        frame.pack();
        frame.setLocationRelativeTo(MineMap.INSTANCE); // center
        frame.setVisible(true);
    }

    static class Entry extends RoundedFloatingEntry {
        private final CopyButton copyCoordinate;
        private final JumpButton jumpCoordinate;

        public Entry(Feature<?, ?> feature, BPos pos, ListPanel listPanel) {
            super(" [" + pos.getX() + ", " + pos.getZ() + "] " + Str.formatName(feature.getName()),
                feature.getClass(), panel -> e -> listPanel.removePanel(panel));


            this.copyCoordinate = new CopyButton(16, 6, 1.0F, true, Color.DARK_GRAY);
            this.copyCoordinate.addActionListener(e -> {
                String myString = String.format("/tp @p %d ~ %d", pos.getX(), pos.getZ());
                StringSelection stringSelection = new StringSelection(myString);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                ListPanel.toggleParentChildren(2, this, component -> component.copyCoordinate.changeBColor(Color.DARK_GRAY));
                this.copyCoordinate.changeBColor(new Color(50, 255, 84));
            });

            this.jumpCoordinate = new JumpButton(16, 6, 1.0F, true, Color.DARK_GRAY);
            this.jumpCoordinate.addActionListener(e -> {
                MineMap.INSTANCE.worldTabs.getSelectedMapPanel().manager.setCenterPos(pos.getX(), pos.getZ());
                ListPanel.toggleParentChildren(2, this, component -> component.jumpCoordinate.changeBColor(Color.DARK_GRAY));
                this.jumpCoordinate.changeBColor(new Color(50, 255, 84));
            });

            this.addAtIndex(this.jumpCoordinate, 2);
            this.addAtIndex(this.copyCoordinate, 3);

            this.setBackground(new Color(0, 0, 0, 180));
        }
    }
}

package kaptainwutax.minemap.util.ui.graphics;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.util.data.Str;
import kaptainwutax.minemap.util.ui.buttons.CopyButton;
import kaptainwutax.minemap.util.ui.buttons.JumpButton;
import kaptainwutax.minemap.util.ui.interactive.ListPanel;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.List;

import static kaptainwutax.minemap.util.ui.graphics.Icon.paintImage;

public class TpPanel {
    public static void makeFrame(List<BPos> bPosList, Feature<?, ?> feature) {
        // create a new frame
        JFrame frame = new JFrame(String.format("List of %d %s", bPosList.size(), feature.getName()));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new java.awt.Dimension(500, 80 + Math.min(350, 70 * bPosList.size())));

        // create the inner list
        final ListPanel listPanel = new ListPanel();
        bPosList.forEach(bPos -> listPanel.addPanel(new Entry(feature, bPos)));
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
        listPanel.add(copyPane, BorderLayout.SOUTH);

        frame.add(listPanel);

        // display it
        frame.pack();
        frame.setLocationRelativeTo(MineMap.INSTANCE); // center
        frame.setVisible(true);
    }

    static class Entry extends RoundedPanel {
        private final JComponent iconView;
        private final JLabel positionText;
        private final CopyButton copyCoordinate;
        private final JumpButton jumpCoordinate;

        public Entry(Feature<?, ?> feature, BPos pos) {
            this.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 3, 3, 3);

            this.iconView = new JComponent() {
                @Override
                public java.awt.Dimension getPreferredSize() {
                    return new java.awt.Dimension(30, 30);
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    paintImage(Icons.get(feature.getClass()), g);
                }
            };

            this.positionText = new JLabel(" [" + pos.getX() + ", " + pos.getZ() + "] " + Str.formatName(feature.getName()));
            this.positionText.setFont(new Font(this.positionText.getFont().getName(), Font.PLAIN, 18));
            this.positionText.setBackground(new Color(0, 0, 0, 0));
            this.positionText.setFocusable(false);
            this.positionText.setOpaque(true);
            this.positionText.setForeground(Color.WHITE);

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

            this.add(this.iconView, gbc);
            this.add(this.positionText, gbc);
            this.add(this.jumpCoordinate, gbc);
            this.add(this.copyCoordinate, gbc);

            this.setBackground(new Color(0, 0, 0, 180));
        }
    }
}

package com.seedfinding.minemap.util.ui.interactive;

import javax.swing.*;
import javax.swing.border.MatteBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class ListPanel extends JPanel {
    /** Space between a row's box and the left/right edge of the list. */
    public static final int ROW_MARGIN = 8;
    private final JPanel mainList;
    private final List<JPanel> panels;
    private final List<JPanel> internalPanels = new ArrayList<>();

    public ListPanel() {
        this(new ArrayList<>());
    }

    public ListPanel(List<JPanel> panels) {
        this(panels, new Insets(2, 0, 2, 0));
    }

    public ListPanel(List<JPanel> panels, Insets insets) {
        super();
        this.panels = panels;
        this.setLayout(new BorderLayout());

        // create the main panel
        // Follows the width of the viewport (it only scrolls vertically): without this the rows keep their natural
        // width when the window gets narrow, and their buttons run out of the visible area.
        mainList = new ViewportWidthPanel();

        // create a scrollpane around it
        JScrollPane scrollPane = new JScrollPane(mainList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        // need to resize the element hidden by forcing the validate.
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> panels.forEach(JComponent::revalidate));
        this.add(scrollPane);

        panels.forEach(panel -> this.addPanel(panel, insets));
    }

    /**
     * Lays out its single child at its preferred size, centred horizontally, but never wider than the container.
     * (GridBagLayout collapses a child to its minimum size as soon as the container is narrower than its preferred size.)
     */
    private static class CenteredRowLayout implements LayoutManager {
        @Override
        public void addLayoutComponent(String name, Component comp) {}

        @Override
        public void removeLayoutComponent(Component comp) {}

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            Insets in = parent.getInsets();
            Dimension d = parent.getComponentCount() == 0 ? new Dimension() : parent.getComponent(0).getPreferredSize();
            return new Dimension(d.width + in.left + in.right, d.height + in.top + in.bottom);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            Insets in = parent.getInsets();
            Dimension d = parent.getComponentCount() == 0 ? new Dimension() : parent.getComponent(0).getMinimumSize();
            return new Dimension(d.width + in.left + in.right, d.height + in.top + in.bottom);
        }

        @Override
        public void layoutContainer(Container parent) {
            if (parent.getComponentCount() == 0) return;
            Insets in = parent.getInsets();
            Component child = parent.getComponent(0);
            int available = parent.getWidth() - in.left - in.right;
            int width = Math.max(child.getMinimumSize().width, Math.min(child.getPreferredSize().width, available));
            child.setBounds(in.left + (available - width) / 2, in.top, width, child.getPreferredSize().height);
        }
    }

    private static class ViewportWidthPanel extends JPanel implements Scrollable {
        ViewportWidthPanel() {
            super(new GridBagLayout());
        }

        @Override
        public Dimension getPreferredScrollableViewportSize() {
            return this.getPreferredSize();
        }

        @Override
        public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) {
            return 16;
        }

        @Override
        public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) {
            return orientation == SwingConstants.VERTICAL ? visibleRect.height : visibleRect.width;
        }

        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }

        @Override
        public boolean getScrollableTracksViewportHeight() {
            return false;
        }
    }

    public static List<Component> findAllMatchingChildren(Container container, Predicate<Component> predicate, int step) {
        if (step < 0 || container == null) {
            return new ArrayList<>();
        }
        if (step == 0) {
            return predicate.test(container) ? new ArrayList<>(Collections.singletonList(container)) : new ArrayList<>();
        }
        Component[] containers = container.getComponents();
        List<Component> res = new ArrayList<>();
        for (Component c : containers) {
            if (c instanceof Container) {
                res.addAll(findAllMatchingChildren((Container) c, predicate, step - 1));
            } else if (predicate.test(c)) {
                // this is just because we might want to get node in the tree even if they are not at the same level
                res.add(c);
            }
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    public static <T extends JComponent> void toggleParentChildren(int nParents, T component, Consumer<T> action) {
        Container container = component;
        for (int i = 0; i < nParents; i++) {
            container = container.getParent();
        }
        Component[] components = container.getComponents();
        Arrays.stream(components)
            .map(c -> findAllMatchingChildren((Container) c, comp -> component.getClass().equals(comp.getClass()), nParents - 1))
            .collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll)
            .forEach(e -> action.accept((T) e));
    }

    public void addPanel(JPanel p) {
        addPanel(p, new Insets(2, 0, 2, 0));
    }

    public void addPanel(JPanel p, Insets insets) {
        // the entry keeps its preferred size, centred in the row, and only shrinks when the row is narrower
        JPanel panel = new JPanel(new CenteredRowLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            new MatteBorder(0, 0, 1, 0, Color.GRAY),
            BorderFactory.createEmptyBorder(0, ROW_MARGIN, 0, ROW_MARGIN)));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = insets;
        panel.add(p);

        this.panels.add(p);
        this.mainList.add(panel, gbc, -1);
        this.internalPanels.add(panel);

        this.validate();
        this.repaint();
    }

    public void removeLastBorder() {
        if (internalPanels.size() == 0) return;
        JPanel last = internalPanels.getLast();
        if (last == null) return;
        // no separator line under the last row, but keep the side margin so every row has the same width
        last.setBorder(BorderFactory.createEmptyBorder(0, ROW_MARGIN, 0, ROW_MARGIN));
        this.revalidate();
        this.repaint();
    }

    public void removePanel(JPanel p) {
        removePanel(panels.indexOf(p));
    }

    public void removePanel(int i) {
        this.mainList.remove(i);
        panels.remove(i);
        internalPanels.remove(i);
        revalidate();
        repaint();
    }

    /**
     * The narrowest the list can be while every row still shows all its buttons (rows only shrink their text).
     * Use it to give the window a minimum width.
     */
    public int getMinimumRowsWidth() {
        int widest = this.internalPanels.stream().mapToInt(p -> p.getMinimumSize().width).max().orElse(0);
        return widest + UIManager.getInt("ScrollBar.width");
    }

    public List<JPanel> getPanels() {
        return this.panels;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 350);
    }
}

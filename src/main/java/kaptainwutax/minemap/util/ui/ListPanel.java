package kaptainwutax.minemap.util.ui;

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
        mainList = new JPanel(new GridBagLayout());

        // create a scrollpane around it
        JScrollPane scrollPane = new JScrollPane(mainList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // need to resize the element hidden by forcing the validate.
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> panels.forEach(JComponent::revalidate));
        this.add(scrollPane);

        panels.forEach(panel -> this.addPanel(panel, insets));
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
        JPanel panel = new JPanel();
        panel.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
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
        JPanel last = internalPanels.get(internalPanels.size() - 1);
        if (last == null) return;
        last.setBorder(BorderFactory.createEmptyBorder());
        this.revalidate();
        this.repaint();
    }

    public void removePanel(JPanel p) {
        removePanel(panels.indexOf(p));
    }

    public void removePanel(int i) {
        super.remove(i);
        panels.remove(i);
        internalPanels.remove(i);
        revalidate();
        invalidate();
        repaint();
    }

    public List<JPanel> getPanels() {
        return this.panels;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 350);
    }
}
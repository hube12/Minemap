package kaptainwutax.minemap.util.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.MatteBorder;

public class ListPanel extends JPanel {
    private final JPanel mainList;
    private final List<JPanel> panels;

    public ListPanel() {
        this(new ArrayList<>());
    }

    public ListPanel(List<JPanel> panels) {
        this(panels, 20);
    }

    public ListPanel(List<JPanel> panels, int height) {
        this(panels, height, new Insets(2, 0, 2, 0));
    }

    public ListPanel(List<JPanel> panels, int height, Insets insets) {
        super();
        this.panels = panels;
        this.setLayout(new BorderLayout());

        // create the main panel
        mainList = new JPanel(new GridBagLayout());

        // create a filling panel so each subsequent is added with such compliance
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.weighty = 1;
        mainList.add(new JPanel(), gbc);

        // create a scrollpane around it
        JScrollPane scrollPane=new JScrollPane(mainList);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        // need to resize the element hidden by forcing the validate.
        scrollPane.getVerticalScrollBar().addAdjustmentListener(e-> panels.forEach(JComponent::revalidate));
        this.add(scrollPane);

        panels.forEach(panel -> this.addPanel(panel, height, insets));
    }

    public void addPanel(JPanel p, int height) {
        addPanel(p, height, new Insets(2, 0, 2, 0));
    }

    public void addPanel(JPanel p, int height, Insets insets) {
        JPanel panel = new JPanel();
        panel.setBorder(new MatteBorder(0, 0, 1, 0, Color.GRAY));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.weightx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets=insets;
        panel.add(p);

        panels.add(p);
        mainList.add(panel, gbc, -1);

        validate();
        repaint();
    }

    public void removePanel(JPanel p) {
        removePanel(panels.indexOf(p));
    }

    public void removePanel(int i) {
        super.remove(i);
        panels.remove(i);
        revalidate();
        invalidate();
        repaint();
    }

    public List<JPanel> getPanels() {
        return this.panels;
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(420, 350);
    }

}
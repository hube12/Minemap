package com.seedfinding.minemap.test;

import com.seedfinding.minemap.util.ui.interactive.Dropdown;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.Random;

public class DemoJPane extends JPanel {
    public static void main(String[] args) {
        JFrame frame = new JFrame("");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(500, 500));

        DemoJPane pane = new DemoJPane(ComponentOrientation.LEFT_TO_RIGHT);
        pane.addTab("1", new Panel());
        pane.addTab("2", new Panel());
        pane.addTab("3", new Panel());
        pane.addButton(new Dropdown<>(j -> "t", new JLabel("")), ButtonSide.LEADING);
        frame.add(pane);

        // display it
        frame.pack();
        frame.setLocationRelativeTo(null); // center
        frame.setVisible(true);
    }


    static class Header extends JPanel {
        public Header(String title) {
            this.add(new JLabel(title));
        }
    }

    static class Panel extends JPanel {
        public Panel() {
            add(new JLabel("test"));
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(500, 500);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Random random = new Random();
            g.setColor(getRandomColor(random));
            g.fillRect(0, 0, 100, 100);
        }

        public static Color getRandomColor(Random rand) {
            float r = rand.nextFloat() / 2f + 0.5f;
            float g = rand.nextFloat() / 2f + 0.5f;
            float b = rand.nextFloat() / 2f + 0.5f;
            return new Color(r, g, b);
        }
    }


    public enum ButtonSide {
        LEADING,
        TRAILING;
    }

    private JTabbedPane tabs;
    private JToolBar leadingButtons;
    private JToolBar trailingButtons;
    //private JPanel dropDown;
    private OffsetTabbedPaneUI tabUI;

    public DemoJPane(ComponentOrientation orient) {
        SpringLayout sl = new SpringLayout();
        this.setLayout(sl);

        this.leadingButtons = new JToolBar();
        this.leadingButtons.setBorderPainted(false);
        this.leadingButtons.setFloatable(false);
        this.leadingButtons.setOpaque(false);
        this.leadingButtons.setComponentOrientation(orient);
        this.leadingButtons.setLayout(new FlowLayout(FlowLayout.LEADING, 1, 1));
        sl.putConstraint(SpringLayout.NORTH, this.leadingButtons, 0, SpringLayout.NORTH, this);
        String side = ((orient == ComponentOrientation.RIGHT_TO_LEFT) ? SpringLayout.EAST : SpringLayout.WEST);
        sl.putConstraint(side, this.leadingButtons, 0, side, this);
        this.add(leadingButtons);

        this.trailingButtons = new JToolBar();
        this.trailingButtons.setBorderPainted(false);
        this.trailingButtons.setFloatable(false);
        this.trailingButtons.setOpaque(false);
        this.trailingButtons.setComponentOrientation(orient);
        this.trailingButtons.setLayout(new FlowLayout(FlowLayout.LEADING, 1, 1));
        sl.putConstraint(SpringLayout.NORTH, this.trailingButtons, 0, SpringLayout.NORTH, this);
        side = ((orient == ComponentOrientation.RIGHT_TO_LEFT) ? SpringLayout.WEST : SpringLayout.EAST);
        sl.putConstraint(side, this.trailingButtons, 0, side, this);
        this.add(trailingButtons);

        this.tabs = new JTabbedPane();
        this.tabs.setComponentOrientation(orient);
        sl.putConstraint(SpringLayout.NORTH, this.tabs, 0, SpringLayout.NORTH, this);
        sl.putConstraint(SpringLayout.SOUTH, this.tabs, 0, SpringLayout.SOUTH, this);
        sl.putConstraint(SpringLayout.WEST, this.tabs, 0, SpringLayout.WEST, this);
        sl.putConstraint(SpringLayout.EAST, this.tabs, 0, SpringLayout.EAST, this);
        tabUI = new OffsetTabbedPaneUI();
        this.tabs.setUI(tabUI);
        this.add(tabs);

        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (tabs.getTabCount() > 0) {
                    tabs.getSelectedComponent().requestFocusInWindow();
                }
            }
        });
    }

    /**
     * @return the tabs
     */
    public JTabbedPane getJTabbedPane() {
        return tabs;
    }

    public void addTab(String title, Panel panel) {
        Header header = new Header(title);
        tabs.setTabComponentAt(this.addTabAndGetIndex(title, panel), header);
    }

    public int addTabAndGetIndex(String title, Component component) {
        tabs.addTab(title, component);
        return tabs.getTabCount() - 1;
    }


    public void addButton(JComponent button, ButtonSide side) {
        // button.setBorderPainted(false);
        button.setFocusable(false);
        // button.setMargin(new Insets(1, 1, 1, 1));
        ((side == ButtonSide.LEADING) ? this.leadingButtons : this.trailingButtons).add(button);
        this.tabUI.setMinHeight(
            Math.max(this.leadingButtons.getPreferredSize().height,
                this.trailingButtons.getPreferredSize().height));
        this.tabUI.setLeadingOffset(this.leadingButtons.getPreferredSize().width);
        this.tabUI.setTrailingOffset(this.trailingButtons.getPreferredSize().width);
        this.validate();
    }

    public class OffsetTabbedPaneUI extends BasicTabbedPaneUI {
        private int leadingOffset = 0;
        private int minHeight = 0;
        private int trailingOffset;

        public OffsetTabbedPaneUI() {
            super();
        }

        /* (non-Javadoc)
         * @see javax.swing.plaf.basic.BasicTabbedPaneUI#calculateTabHeight(int, int, int)
         */
        @Override
        protected int calculateTabHeight(int tabPlacement, int tabIndex,
                                         int fontHeight) {
            return Math.max(super.calculateTabHeight(tabPlacement, tabIndex, fontHeight), this.minHeight);
        }

        /* (non-Javadoc)
         * @see javax.swing.plaf.basic.BasicTabbedPaneUI#getTabAreaInsets(int)
         */
        @Override
        protected Insets getTabAreaInsets(int tabPlacement) {
            // ignores tab placement for now
            return new Insets(0, this.leadingOffset, 0, this.trailingOffset);
        }

        /**
         * @param offset the offset to set
         */
        public void setLeadingOffset(int offset) {
            this.leadingOffset = offset;
        }

        /**
         * @param minHeight the minHeight to set
         */
        public void setMinHeight(int minHeight) {
            this.minHeight = minHeight;
        }

        public void setTrailingOffset(int offset) {
            this.trailingOffset = offset;
        }
    }
}

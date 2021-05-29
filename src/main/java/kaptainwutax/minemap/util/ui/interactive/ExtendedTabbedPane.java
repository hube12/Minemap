package kaptainwutax.minemap.util.ui.interactive;

import kaptainwutax.minemap.ui.component.TabHeader;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

public class ExtendedTabbedPane extends JPanel {
    private final JTabbedPane tabbedPane;
    private final JToolBar leadingComponents;
    private final JToolBar trailingComponents;
    private final OffsetTabbedPaneUI tabUI;

    public enum ButtonSide {
        LEADING,
        TRAILING;
    }

    public ExtendedTabbedPane(ComponentOrientation orient) {
        SpringLayout sl = new SpringLayout();
        this.setLayout(sl);

        this.leadingComponents = new JToolBar();
        this.leadingComponents.setBorderPainted(false);
        this.leadingComponents.setFloatable(false);
        this.leadingComponents.setOpaque(false);
        this.leadingComponents.setComponentOrientation(orient);
        this.leadingComponents.setLayout(new FlowLayout(FlowLayout.LEADING, 1, 1));
        sl.putConstraint(SpringLayout.NORTH, this.leadingComponents, 0, SpringLayout.NORTH, this);
        String side = ((orient == ComponentOrientation.RIGHT_TO_LEFT) ? SpringLayout.EAST : SpringLayout.WEST);
        sl.putConstraint(side, this.leadingComponents, 0, side, this);
        this.add(leadingComponents);

        this.trailingComponents = new JToolBar();
        this.trailingComponents.setBorderPainted(false);
        this.trailingComponents.setFloatable(false);
        this.trailingComponents.setOpaque(false);
        this.trailingComponents.setComponentOrientation(orient);
        this.trailingComponents.setLayout(new FlowLayout(FlowLayout.LEADING, 1, 1));
        sl.putConstraint(SpringLayout.NORTH, this.trailingComponents, 0, SpringLayout.NORTH, this);
        side = ((orient == ComponentOrientation.RIGHT_TO_LEFT) ? SpringLayout.WEST : SpringLayout.EAST);
        sl.putConstraint(side, this.trailingComponents, 0, side, this);
        this.add(trailingComponents);

        this.tabbedPane = new JTabbedPane();
        this.tabbedPane.setComponentOrientation(orient);
        sl.putConstraint(SpringLayout.NORTH, this.tabbedPane, 0, SpringLayout.NORTH, this);
        sl.putConstraint(SpringLayout.SOUTH, this.tabbedPane, 0, SpringLayout.SOUTH, this);
        sl.putConstraint(SpringLayout.WEST, this.tabbedPane, 0, SpringLayout.WEST, this);
        sl.putConstraint(SpringLayout.EAST, this.tabbedPane, 0, SpringLayout.EAST, this);
        tabUI = new OffsetTabbedPaneUI();
        //this.tabbedPane.setUI(tabUI);
        this.add(tabbedPane);

        this.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (tabbedPane.getTabCount() > 0) {
                    tabbedPane.getSelectedComponent().requestFocusInWindow();
                }
            }
        });
    }

    // pass through methods
    public int getTabCount() {
        return this.getJTabbedPane().getTabCount();
    }

    public void setSelectedIndex(int index) {
        this.getJTabbedPane().setSelectedIndex(index);
    }

    public int indexOfTab(String title) {
        return this.getJTabbedPane().indexOfTab(title);
    }

    public void setTabComponentAt(int index, Component component) {
        this.getJTabbedPane().setTabComponentAt(index,component);
    }

    public void removeAll() {
        this.getJTabbedPane().removeAll();
    }

    public TabbedPaneHeader getSelectedHeader() {
        if (this.getJTabbedPane().getSelectedIndex() < 0) return null;
        Component c = this.getJTabbedPane().getTabComponentAt(this.getJTabbedPane().getSelectedIndex());
        return c instanceof TabbedPaneHeader ? (TabbedPaneHeader) c : null;
    }

    public Component getSelectedComponent() {
        if (this.tabbedPane.getSelectedIndex() < 0) return null;
        return this.tabbedPane.getComponentAt(this.tabbedPane.getSelectedIndex());
    }

    public JTabbedPane getJTabbedPane() {
        return tabbedPane;
    }

    public void addTab(String title, Component panel, HeaderFactory headerCreator) {
        TabbedPaneHeader header = headerCreator.create(title);
        tabbedPane.setTabComponentAt(this.addTabAndGetIndex(title, panel), header);
    }

    @FunctionalInterface
    public interface HeaderFactory {
        TabbedPaneHeader create(String title);
    }

    public abstract static class  TabbedPaneHeader extends JPanel{

    }

    public int addTabAndGetIndex(String title, Component component) {
        tabbedPane.addTab(title, component);
        return this.getTabCount() - 1;
    }

    public void addComponent(JComponent button, ButtonSide side) {
        // button.setBorderPainted(false);
        button.setFocusable(false);
        // button.setMargin(new Insets(1, 1, 1, 1));
        ((side == ButtonSide.LEADING) ? this.leadingComponents : this.trailingComponents).add(button);
        this.tabUI.setMinHeight(Math.max(
            this.leadingComponents.getPreferredSize().height,
            this.trailingComponents.getPreferredSize().height)
        );
        this.tabUI.setLeadingOffset(this.leadingComponents.getPreferredSize().width);
        this.tabUI.setTrailingOffset(this.trailingComponents.getPreferredSize().width);
        this.validate();
    }

    public static class OffsetTabbedPaneUI extends BasicTabbedPaneUI {
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

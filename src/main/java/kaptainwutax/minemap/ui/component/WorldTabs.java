package kaptainwutax.minemap.ui.component;

import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.util.data.Str;
import kaptainwutax.minemap.util.ui.buttons.CloseButton;
import kaptainwutax.minemap.util.ui.buttons.SquareCloseButton;
import kaptainwutax.minemap.util.ui.graphics.Icon;
import kaptainwutax.minemap.util.ui.interactive.Dropdown;
import kaptainwutax.minemap.util.ui.interactive.ExtendedTabbedPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static kaptainwutax.minemap.MineMap.isDarkTheme;

public class WorldTabs extends ExtendedTabbedPane {
    public static final Color BACKGROUND_COLOR = new Color(60, 63, 65);
    protected final List<TabGroup> tabGroups = new ArrayList<>();
    public final Dropdown<TabGroup> dropdown = new Dropdown<TabGroup>(
        // what is better than a hashcode that can roll? a timestamp!
        j -> String.format("%d [%s]::%s ", j.getWorldSeed(), j.getVersion(), System.nanoTime()),
        value -> value != null ? ((String) value).split("::")[0] : null
    );
    public final JButton closeAllCurrent;
    public TabGroup current;

    @SuppressWarnings("deprecation")
    public WorldTabs() {
        super(ComponentOrientation.LEFT_TO_RIGHT);
        //Copy seed to clipboard.
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getKeyCode() != KeyEvent.VK_C || (e.getModifiersEx() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) == 0) return false;
            MapPanel map = this.getSelectedMapPanel();
            if (map == null) return false;
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(String.valueOf(map.getContext().worldSeed)), null);
            return true;
        });
        this.closeAllCurrent = new JButton(Icon.getIcon(SquareCloseButton.class,28,28,null));
        this.closeAllCurrent.setToolTipText("Close current seed");
        this.closeAllCurrent.addActionListener(e -> closeTabs());
        this.addSideComponent(closeAllCurrent, ButtonSide.TRAILING);
        this.addSideComponent(dropdown, ButtonSide.TRAILING);
        dropdown.addActionListener(e -> {
            if (dropdown.getSelected() != current && dropdown.getSelected() != null) {
                this.cleanSetTabGroup(dropdown.getSelected());
            }
        });
        this.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                WorldTabs.super.repaint();
            }
        });
    }

    @Override
    public TabHeader getSelectedHeader() {
        return (TabHeader) super.getSelectedHeader();
    }

    public TabGroup load(MCVersion version, String worldSeed, int threadCount, Collection<Dimension> dimensions, boolean shouldSwitch) {
        TabGroup tabGroup = new TabGroup(version, worldSeed, threadCount, dimensions, !shouldSwitch);
        if (this.tabGroups.contains(tabGroup)) {
            return null;
        }
        this.tabGroups.add(tabGroup);
        this.dropdown.add(tabGroup);
        if (shouldSwitch) this.cleanSetTabGroup(tabGroup);
        return tabGroup;
    }

    public TabGroup load(MCVersion version, String worldSeed, int threadCount, Collection<Dimension> dimensions) {
        return this.load(version, worldSeed, threadCount, dimensions, true);
    }

    public void cleanSetTabGroup(TabGroup tabGroup) {
        // remove all elements in the jtabbedpane
        this.removeAll();
        if (tabGroup == null) return;
        this.dropdown.setDefault(tabGroup);
        this.current = tabGroup;
        this.setTabGroup(tabGroup);
    }

    private void setTabGroup(TabGroup tabGroup) {
        AtomicBoolean first = new AtomicBoolean(true);

        tabGroup.getPanels().forEach((dimension, mapPanel) -> {
            this.addMapTab(Str.prettifyDashed(dimension.getName()), tabGroup, mapPanel);
            if (first.get()) {
                this.setSelectedIndex(this.getTabCount() - 1);
                first.set(false);
            }
        });
    }

    @Override
    public void remove(Component component) {
        if (component instanceof MapPanel) {
            if (((MapPanel) component).getHeader().isSaved()) return;
            this.tabGroups.forEach(tabGroup -> tabGroup.removeIfPresent((MapPanel) component));
            List<TabGroup> toRemove = this.tabGroups.stream().filter(TabGroup::isEmpty).collect(Collectors.toList());
            toRemove.forEach(this::remove);
        }
        this.getJTabbedPane().remove(component);
    }

    public void remove(TabGroup tabGroup) {
        if (tabGroup==null) return;
        for (MapPanel mapPanel : new ArrayList<>(tabGroup.getMapPanels())) {
            this.remove(mapPanel);
        }
        if (tabGroup.getMapPanels().isEmpty()){
            this.tabGroups.remove(tabGroup);
            this.dropdown.remove(tabGroup);
            current = this.dropdown.getSelected();
            this.repaint();
        }
    }

    public MapPanel getSelectedMapPanel() {
        Component component = this.getSelectedComponent();
        return component instanceof MapPanel ? (MapPanel) component : null;
    }

    public TabGroup getCurrentTabGroup() {
        return current;
    }

    @Override
    public void paintComponent(Graphics g) {
        if (isDarkTheme()) {
            g.setColor(BACKGROUND_COLOR);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        super.paintComponent(g);
    }

    public synchronized void invalidateAll() {
        this.tabGroups.forEach(TabGroup::invalidateAll);
    }

    public static void closeTab() {
        MineMap.INSTANCE.worldTabs.remove(MineMap.INSTANCE.worldTabs.getSelectedComponent());
    }

    public static void closeTabs() {
        MineMap.INSTANCE.worldTabs.remove(MineMap.INSTANCE.worldTabs.getCurrentTabGroup());
    }

    public static void cycle(boolean isRight) {
        MineMap.INSTANCE.worldTabs.cycleSeed(isRight);
    }

    public void cycleSeed(boolean isRight) {
        TabGroup tabGroup = isRight ? this.dropdown.getCycleRight() : this.dropdown.getCycleLeft();
        if (tabGroup == null) return;
        if (tabGroup != current) {
            dropdown.setDefault(tabGroup);
            this.cleanSetTabGroup(dropdown.getSelected());
        }
    }

    public void addMapTab(String title, TabGroup tabGroup, MapPanel mapPanel) {
        mapPanel.updateInteractive();
        if (mapPanel.getHeader() != null) {
            this.addTab(title, mapPanel, mapPanel.getHeader());
        } else {
            TabHeader tabHeader = new TabHeader(title, e -> {
                if (e.isShiftDown()) this.remove(tabGroup);
                else this.remove(mapPanel);
            });

            tabHeader.setComponentPopupMenu(createTabMenu(tabGroup, mapPanel));
            tabHeader.addMouseListener(Events.Mouse.onReleased(e -> {
                if (e.getSource() instanceof TabHeader) {
                    TabHeader source = (TabHeader) e.getSource();
                    this.setSelectedIndex(this.indexOfTab(source.getTabTitle().getText()));
                }
            }));
            mapPanel.setHeader(tabHeader);
            this.addTab(title, mapPanel, tabHeader);
        }

    }

    public JPopupMenu createTabMenu(TabGroup current, MapPanel mapPanel) {
        JPopupMenu popup = new JPopupMenu();

        JMenuItem removeOthers = new JMenuItem("Close Other Tab Groups");
        removeOthers.setBorder(new EmptyBorder(5, 15, 5, 15));

        removeOthers.addMouseListener(Events.Mouse.onReleased(e -> {
            List<TabGroup> others = this.tabGroups.stream().filter(g -> g != current).collect(Collectors.toList());
            for (TabGroup other : others) {
                this.remove(other);
            }
        }));
        popup.add(removeOthers);

        JMenuItem copySeed = new JMenuItem("Copy seed");
        copySeed.setBorder(new EmptyBorder(5, 15, 5, 15));

        copySeed.addMouseListener(Events.Mouse.onReleased(e -> {
            StringSelection content = new StringSelection(String.valueOf(mapPanel.getContext().worldSeed));
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(content, null);
        }));


        popup.add(copySeed);
        return popup;
    }
}



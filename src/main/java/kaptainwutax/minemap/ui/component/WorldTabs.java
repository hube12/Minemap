package kaptainwutax.minemap.ui.component;

import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.util.data.Str;
import kaptainwutax.minemap.util.ui.interactive.ExtendedTabbedPane;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static kaptainwutax.minemap.MineMap.isDarkTheme;

public class WorldTabs extends ExtendedTabbedPane {
    public static final Color BACKGROUND_COLOR = new Color(60, 63, 65);
    protected final List<TabGroup> tabGroups = new ArrayList<>();
    public TabGroup currentTabGroup = null;

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
    }

    @Override
    public TabHeader getSelectedHeader() {
        return (TabHeader)super.getSelectedHeader();
    }

    public TabGroup load(MCVersion version, String worldSeed, int threadCount, Collection<Dimension> dimensions) {
        TabGroup tabGroup = new TabGroup(version, worldSeed, threadCount, dimensions);
        this.tabGroups.add(tabGroup);
        this.removeAll();
        this.setTabGroup(tabGroup);
        currentTabGroup = tabGroup;
        return tabGroup;
    }

    public void setTabGroup(TabGroup tabGroup) {
        String prefix = "[" + tabGroup.getVersion() + "] ";
        AtomicBoolean first = new AtomicBoolean(true);

        tabGroup.getPanels().forEach((dimension, mapPanel) -> {
            String s = Str.prettifyDashed(dimension.getName());
            this.addMapTab(prefix + s + " " + tabGroup.getWorldSeed(), tabGroup, mapPanel);

            if (first.get()) {
                this.setSelectedIndex(this.getTabCount() - 1);
                first.set(false);
            }
        });
    }

    @Override
    public void remove(Component component) {
        if (component instanceof MapPanel) {
            this.tabGroups.forEach(tabGroup -> tabGroup.removeIfPresent((MapPanel) component));
            this.tabGroups.removeIf(TabGroup::isEmpty);
        }

        super.remove(component);
    }

    public void remove(TabGroup tabGroup) {
        for (MapPanel mapPanel : tabGroup.getMapPanels()) {
            super.remove(mapPanel);
        }
        this.tabGroups.remove(tabGroup);
        this.currentTabGroup = this.tabGroups.isEmpty() ? null : this.tabGroups.get(this.tabGroups.size() - 1);
    }

    public MapPanel getSelectedMapPanel() {
        Component component = this.getSelectedComponent();
        return component instanceof MapPanel ? (MapPanel) component : null;
    }

    public TabGroup getCurrentTabGroup() {
        return currentTabGroup;
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

    public void addMapTab(String title, TabGroup tabGroup, MapPanel mapPanel) {
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
        this.setTabComponentAt(this.addTabAndGetIndex(title, mapPanel), tabHeader);
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
        JMenuItem copySeed = new JMenuItem("Copy seed");
        copySeed.setBorder(new EmptyBorder(5, 15, 5, 15));

        copySeed.addMouseListener(Events.Mouse.onReleased(e -> {
            StringSelection content = new StringSelection(String.valueOf(mapPanel.getContext().worldSeed));
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(content, null);
        }));

        popup.add(removeOthers);
        popup.add(copySeed);
        return popup;
    }
}



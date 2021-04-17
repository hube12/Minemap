package kaptainwutax.minemap.ui.component;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.version.MCVersion;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class WorldTabs extends JTabbedPane {

    public static final Color BACKGROUND_COLOR = new Color(60, 63, 65);
    protected final List<TabGroup> tabGroups = new ArrayList<>();

    public WorldTabs() {
        //Copy seed to clipboard.
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getKeyCode() != KeyEvent.VK_C || (e.getModifiersEx() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) == 0) return false;
            MapPanel map = this.getSelectedMapPanel();
            if (map == null) return false;
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(String.valueOf(map.getContext().worldSeed)), null);
            return true;
        });
    }

    public void load(MCVersion version, String worldSeed, int threadCount, Collection<Dimension> dimensions) {
        TabGroup tabGroup = new TabGroup(version, worldSeed, threadCount, dimensions);
        this.tabGroups.add(tabGroup);
        tabGroup.add(this);
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
    }

    public Component getSelectedComponent() {
        if (this.getSelectedIndex() < 0) return null;
        return this.getComponentAt(this.getSelectedIndex());
    }

    public MapPanel getSelectedMapPanel() {
        Component component = this.getSelectedComponent();
        return component instanceof MapPanel ? (MapPanel) component : null;
    }

    public TabHeader getSelectedHeader() {
        if (this.getSelectedIndex() < 0) return null;
        Component c = this.getTabComponentAt(this.getSelectedIndex());
        return c instanceof TabHeader ? (TabHeader) c : null;
    }

    @Override
    public void paintComponent(Graphics g) {
        if (MineMap.lookType.isDark()) {
            g.setColor(BACKGROUND_COLOR);
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        super.paintComponent(g);
    }

    public synchronized void invalidateAll() {
        this.tabGroups.forEach(TabGroup::invalidateAll);
    }

    public int addTabAndGetIndex(String title, Component component) {
        super.addTab(title, component);
        return this.getTabCount() - 1;
    }

    @Override
    public void addTab(String title, Component component) {
        this.setTabComponentAt(this.addTabAndGetIndex(title, component), new TabHeader(title, e -> this.remove(component)));
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

        this.setTabComponentAt(this.addTabAndGetIndex(title, mapPanel), tabHeader);
    }

    public JPopupMenu createTabMenu(TabGroup current, MapPanel mapPanel) {
        JPopupMenu popup = new JPopupMenu();

        JMenuItem removeOthers = new JMenuItem("Close Other Tabs");
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

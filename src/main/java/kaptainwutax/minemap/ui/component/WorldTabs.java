package kaptainwutax.minemap.ui.component;

import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.MCVersion;
import wearblackallday.data.Strings;

import javax.swing.*;
import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WorldTabs extends JTabbedPane {

    protected final List<TabGroup> tabGroups = new ArrayList<>();

    public WorldTabs() {
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
            if (e.getKeyCode() != KeyEvent.VK_C
                    || (e.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) == 0) return false;
            MapPanel map = this.getSelectedMapPanel();
            if (map == null) return false;
            Strings.clipboard(String.valueOf(map.getContext().worldSeed));
            return true;
        });
    }

    public void load(MCVersion version, String worldSeed, int threadCount, Collection<Dimension> dimensions) {
        TabGroup tabGroup = new TabGroup(version, worldSeed, threadCount, dimensions);
        this.tabGroups.add(tabGroup);
        tabGroup.add(this);
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
        this.setTabComponentAt(this.addTabAndGetIndex(title, mapPanel), new TabHeader(title, e -> {
            if (e.isShiftDown()) this.remove(tabGroup);
            else this.remove(mapPanel);
        }));
    }
}

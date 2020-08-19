package kaptainwutax.minemap.ui.component;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.MCVersion;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WorldTabs extends JTabbedPane {

	public static final Color BACKGROUND_COLOR = new Color(60, 63,65);
	protected final List<TabGroup> tabGroups = new ArrayList<>();

	public WorldTabs() {
		//Copy seed to clipboard.
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
			if(e.getKeyCode() != KeyEvent.VK_C
					|| (e.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) == 0)return false;
			MapPanel map = this.getSelectedMapPanel();
			if(map == null)return false;
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(
					new StringSelection(String.valueOf(map.getContext().worldSeed)), null);
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
		if(component instanceof MapPanel) {
			this.tabGroups.forEach(tabGroup -> tabGroup.removeIfPresent((MapPanel)component));
			this.tabGroups.removeIf(TabGroup::isEmpty);
		}

		super.remove(component);
	}

	public void remove(TabGroup tabGroup) {
		for(MapPanel mapPanel: tabGroup.getMapPanels()) {
			super.remove(mapPanel);
		}

		this.tabGroups.remove(tabGroup);
	}

	public Component getSelectedComponent() {
		if(this.getSelectedIndex() < 0)return null;
		return this.getComponentAt(this.getSelectedIndex());
	}

	public MapPanel getSelectedMapPanel() {
		Component component = this.getSelectedComponent();
		return component instanceof MapPanel ? (MapPanel)component : null;
	}

	@Override
	public void paintComponent(Graphics g) {
		if(MineMap.DARCULA) {
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
		this.setTabComponentAt(this.addTabAndGetIndex(title, component), new TabHeader(title, e -> {
			this.remove(component);
		}));
	}

	public void addMapTab(String title, TabGroup tabGroup, MapPanel mapPanel) {
		this.setTabComponentAt(this.addTabAndGetIndex(title, mapPanel), new TabHeader(title, e -> {
			if(e.isShiftDown())this.remove(tabGroup);
			else this.remove(mapPanel);
		}));
	}

}

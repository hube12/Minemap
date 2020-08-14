package kaptainwutax.minemap.ui;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.util.Fragment;

import javax.swing.*;
import java.awt.*;

public class MenuBar extends JMenuBar {

	public MenuBar() {
		this.addFileMenu();
		this.addWorldMenu();
		this.addStyleMenu();
	}

	private void addFileMenu() {
		JMenu fileMenu = new JMenu("File");
		JMenuItem loadSeed = new JMenuItem("New From Seed...");

		loadSeed.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(() -> {
			EnterSeedDialog dialog = new EnterSeedDialog();
			dialog.setVisible(true);
		})));

		fileMenu.add(loadSeed);
		this.add(fileMenu);
	}

	private void addWorldMenu() {
		JMenu worldMenu = new JMenu("World");
		JCheckBoxMenuItem showGrid = new JCheckBoxMenuItem("Show Grid");

		showGrid.addChangeListener(e -> {
			Fragment.DRAW_GRID = showGrid.getState();
			MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
			if(map != null)map.repaint();
		});

		JCheckBoxMenuItem showStructures = new JCheckBoxMenuItem("Show Structures");
		showStructures.setState(true);

		showStructures.addChangeListener(e -> {
			Fragment.DRAW_STRUCTURES = showStructures.getState();
			MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
			if(map != null)map.repaint();
		});

		JMenuItem coordHopper = new JMenuItem();
		coordHopper.setText("Go to Coordinates");

		coordHopper.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(() -> {
			JDialog jumpDialogue = new CoordHopper().mainLogue;
			jumpDialogue.setVisible(true);
		})));

		worldMenu.addMenuListener(Events.Menu.onSelected(e -> {
			coordHopper.setEnabled(MineMap.INSTANCE.worldTabs.getSelectedMapPanel() != null);
		}));

		worldMenu.add(coordHopper);
		worldMenu.add(showGrid);
		worldMenu.add(showStructures);
		this.add(worldMenu);
	}

	private void addStyleMenu() {
		JMenu styleMenu = new JMenu("Style");
		for(String style: Configs.BIOME_COLORS.getStyles()) {
			JMenuItem styleItem = new JMenuItem(style);

			styleItem.addMouseListener(Events.Mouse.onPressed(e -> {
				for(Component c: styleMenu.getMenuComponents()) {
					c.setEnabled(true);
				}

				styleItem.setEnabled(false);
				Configs.USER_PROFILE.setStyle(style);
			}));

			if(Configs.USER_PROFILE.getStyle().equals(style)) {
				styleItem.setEnabled(false);
			}

			styleMenu.add(styleItem);
		}

		this.add(styleMenu);
	}

}

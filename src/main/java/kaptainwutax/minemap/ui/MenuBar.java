package kaptainwutax.minemap.ui;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.config.Config;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.util.Fragment;
import javax.swing.*;
import java.awt.*;
import kaptainwutax.seedutils.mc.Dimension;
import java.util.HashMap;
import java.util.Map;

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

		worldMenu.addMenuListener(Events.Menu.onSelected(e -> coordHopper.setEnabled(MineMap.INSTANCE.worldTabs.getSelectedMapPanel() != null)));


		JCheckBoxMenuItem OWtick = new JCheckBoxMenuItem("Render OverWorld");
		OWtick.setState(Configs.USER_PROFILE.getOWenabled());
		OWtick.addChangeListener(e -> Configs.USER_PROFILE.setOWenabled(OWtick.getState()));

		JCheckBoxMenuItem Ntick = new JCheckBoxMenuItem("Render Nether");
		Ntick.setState(Configs.USER_PROFILE.getNetherenabled());
		Ntick.addChangeListener(e -> Configs.USER_PROFILE.setNetherenabled(Ntick.getState()));

		JCheckBoxMenuItem Etick = new JCheckBoxMenuItem("Render End");
		Etick.setState(Configs.USER_PROFILE.getEndenabled());
		Etick.addChangeListener(e -> Configs.USER_PROFILE.setEndenabled(Etick.getState()));

		worldMenu.add(coordHopper);
		worldMenu.add(showGrid);
		worldMenu.add(showStructures);
		worldMenu.add(OWtick);
		worldMenu.add(Ntick);
		worldMenu.add(Etick);
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

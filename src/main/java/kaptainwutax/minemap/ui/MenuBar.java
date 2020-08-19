package kaptainwutax.minemap.ui;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.dialog.CoordHopperDialog;
import kaptainwutax.minemap.ui.dialog.EnterSeedDialog;
import kaptainwutax.minemap.ui.map.MapPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

		JMenuItem screenshot = new JMenuItem("Screenshot...");

		screenshot.addMouseListener(Events.Mouse.onPressed(mouseEvent -> {
			MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
			if(map == null)return;
			BufferedImage image = map.getScreenshot();

			String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
			File file = new File("screenshots/" + fileName + ".png");
			if(!file.mkdirs())return;

			try {
				ImageIO.write(image, "png", file);
			} catch(IOException e) {
				e.printStackTrace();
			}
		}));

		fileMenu.addMenuListener(Events.Menu.onSelected(e -> screenshot.setEnabled(MineMap.INSTANCE.worldTabs.getSelectedMapPanel() != null)));

		fileMenu.add(loadSeed);
		fileMenu.add(screenshot);
		this.add(fileMenu);
	}

	private void addWorldMenu() {
		JMenu worldMenu = new JMenu("World");
		JCheckBoxMenuItem showGrid = new JCheckBoxMenuItem("Show Grid");

		showGrid.addChangeListener(e -> {
			//TODO: grid toggle
			MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
			if(map != null)map.repaint();
		});

		JCheckBoxMenuItem showStructures = new JCheckBoxMenuItem("Show Structures");
		showStructures.setState(true);

		showStructures.addChangeListener(e -> {
			//TODO: structure toggle
			MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
			if(map != null)map.repaint();
		});

		JMenuItem coordHopper = new JMenuItem();
		coordHopper.setText("Go to Coordinates");

		coordHopper.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(() -> {
			JDialog jumpDialogue = new CoordHopperDialog().mainLogue;
			jumpDialogue.setVisible(true);
		})));

		worldMenu.addMenuListener(Events.Menu.onSelected(e -> coordHopper.setEnabled(MineMap.INSTANCE.worldTabs.getSelectedMapPanel() != null)));
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

package kaptainwutax.minemap.ui;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.feature.SpawnPoint;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.dialog.CoordHopperDialog;
import kaptainwutax.minemap.ui.dialog.EnterSeedDialog;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.ui.map.icon.IconRenderer;
import kaptainwutax.minemap.ui.map.icon.SpawnIcon;
import kaptainwutax.seedutils.mc.pos.BPos;

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
		this.addSettingsMenu();
	}

	private void addSettingsMenu() {
		JMenu settingsMenu = new JMenu("Settings");

		JCheckBoxMenuItem zoom = new JCheckBoxMenuItem("Restrict Maximum Zoom");

		zoom.addChangeListener(e -> {
			Configs.USER_PROFILE.getUserSettings().restrictMaximumZoom = zoom.getState();
			Configs.USER_PROFILE.flush();
		});

		settingsMenu.addMenuListener(Events.Menu.onSelected(e -> {
			zoom.setState(Configs.USER_PROFILE.getUserSettings().restrictMaximumZoom);
		}));

		settingsMenu.add(zoom);
		this.add(settingsMenu);
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
			File dir = new File("screenshots/");
			File file = new File("screenshots/" + fileName + ".png");
			if(!dir.exists() && !dir.mkdirs())return;

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

		JMenuItem coordHopper = new JMenuItem();
		coordHopper.setText("Go to Coordinates");

		coordHopper.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(() -> {
			JDialog jumpDialogue = new CoordHopperDialog().mainLogue;
			jumpDialogue.setVisible(true);
		})));

		JMenuItem goToSpawn = new JMenuItem("Go to Spawn");

		goToSpawn.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(() -> {
			BPos pos = this.getActiveSpawn();

			if(pos != null) {
				MineMap.INSTANCE.worldTabs.getSelectedMapPanel().getManager().setCenterPos(pos.getX(), pos.getZ());
			}
		})));

		worldMenu.addMenuListener(Events.Menu.onSelected(e -> {
			MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
			coordHopper.setEnabled(map != null);
			goToSpawn.setEnabled(map != null && this.getActiveSpawn() != null);
		}));

		worldMenu.add(coordHopper);
		worldMenu.add(goToSpawn);
		this.add(worldMenu);
	}

	private BPos getActiveSpawn() {
		MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
		IconRenderer icon = map.getContext().getIconManager().getFor(SpawnPoint.class);
		return icon instanceof SpawnIcon ? ((SpawnIcon)icon).getPos() : null;
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

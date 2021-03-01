package kaptainwutax.minemap.ui;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.feature.SpawnPoint;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.dialog.DialogStorage;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.ui.map.icon.IconRenderer;
import kaptainwutax.minemap.ui.map.icon.SpawnIcon;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.seed.WorldSeed;
import kaptainwutax.seedutils.util.math.DistanceMetric;
import wearblackallday.swing.SwingUtils;
import wearblackallday.swing.components.MenuBuilder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;

public class MenuBar extends JMenuBar {

	private static final ButtonGroup buttonGroup = new ButtonGroup();

	public MenuBar() {
		this.addFileMenu();
		this.addWorldMenu();
		this.addSettingsMenu();
	}

	//TODO make factory equivalent to ctor Instance
	public static JMenuBar create() {
		return new MenuBuilder(
				new MenuBuilder.Menu("File",
						new MenuBuilder.Item("New from Seed...", (item, e) ->
								SwingUtilities.invokeLater(() -> DialogStorage.ENTER_SEED_DIALOG.setVisible(true))
						),
						new MenuBuilder.Item("Screenshot...", (item, e) -> {
							MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
							if (map == null) return;
							BufferedImage image = map.getScreenshot();

							String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
							File dir = new File("screenshots/");
							File file = new File("screenshots/" + fileName + ".png");
							if (!dir.exists() && !dir.mkdirs()) return;

							try {
								ImageIO.write(image, "png", file);
							} catch (IOException exception) {
								exception.printStackTrace();
							}
						})
				),
				new MenuBuilder.Menu("World",
						new MenuBuilder.Item("Go to Coordinates", (item, e) ->
								SwingUtilities.invokeLater(() -> DialogStorage.COORD_HOPPER_DIALOG.setVisible(true))),
						new MenuBuilder.Item("Load Shadow Seed", (item, e) ->
								SwingUtilities.invokeLater(() -> {
									MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
									MineMap.INSTANCE.worldTabs.load(
											map.getContext().version,
											String.valueOf(WorldSeed.getShadowSeed(map.getContext().worldSeed)),
											map.threadCount, Collections.singletonList(map.getContext().dimension));
								})),
						new MenuBuilder.Item("Change Salts", (item, e) ->
								SwingUtilities.invokeLater(() -> {
									try {
//										new SaltDialog().setVisible(true);
									} catch (Exception exception) {
										exception.printStackTrace();
									}
								}))
				).addSelectedListener((menu, menuEvent) -> {
					for (Component c : menu.getMenuComponents()) {
						c.setEnabled(MineMap.INSTANCE.worldTabs.getSelectedMapPanel() != null);
					}
				}),
				new MenuBuilder.Menu("Settings",
						new MenuBuilder.Menu("Style").run(menu -> {
							ButtonGroup buttonGroup1 = new ButtonGroup();
							for (String style : Configs.BIOME_COLORS.getStyles()) {
								JRadioButtonMenuItem button = new JRadioButtonMenuItem(style);
								button.addActionListener(e -> {
									Configs.USER_PROFILE.getUserSettings().style = style;
									MineMap.INSTANCE.worldTabs.invalidateAll();
									Configs.USER_PROFILE.flush();
								});
								menu.add(button);
								buttonGroup1.add(button);
							}
						}),
						new MenuBuilder.CheckBox("Restrict Maximum Zoom", (item, e) -> {
							Configs.USER_PROFILE.getUserSettings().restrictMaximumZoom = item.getState();
							Configs.USER_PROFILE.flush();
						}),
						new MenuBuilder.Menu("Fragment Metric",
								new MenuBuilder.RadioBox("Euclidean", MenuBar::radioListener),
								new MenuBuilder.RadioBox("Manhattan", MenuBar::radioListener),
								new MenuBuilder.RadioBox("Chebyshev", MenuBar::radioListener))
				));
	}

	private static void radioListener(MenuBuilder.RadioBox item, ActionEvent e) {
		buttonGroup.add(item);
		Configs.USER_PROFILE.getUserSettings().fragmentMetric = item.getText();
		Configs.USER_PROFILE.flush();
		MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
		if (map != null) map.scheduler.scheduledModified.set(true);
	}

	private void addFileMenu() {
		JMenu fileMenu = new JMenu("File");

		JMenuItem loadSeed = new JMenuItem("New From Seed...");

		loadSeed.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(() -> DialogStorage.ENTER_SEED_DIALOG.setVisible(true))));

		JMenuItem screenshot = new JMenuItem("Screenshot...");

		screenshot.addMouseListener(Events.Mouse.onPressed(mouseEvent -> {
			if(!screenshot.isEnabled())return;

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

	private void addSettingsMenu() {
		JMenu settingsMenu = new JMenu("Settings");

		JMenu styleMenu = new JMenu("Style");
		ButtonGroup styleButtons = new ButtonGroup();

		for(String style: Configs.BIOME_COLORS.getStyles()) {
			JRadioButtonMenuItem button = new JRadioButtonMenuItem(style);

			button.addMouseListener(Events.Mouse.onPressed(e -> {
				if(!button.isEnabled())return;

				for(Component c: styleMenu.getMenuComponents()) {
					c.setEnabled(true);
				}

				button.setEnabled(false);
				Configs.USER_PROFILE.getUserSettings().style = style;
				MineMap.INSTANCE.worldTabs.invalidateAll();
				Configs.USER_PROFILE.flush();
			}));

			if(Configs.USER_PROFILE.getUserSettings().style.equals(style)) {
				button.setEnabled(false);
			}

			styleButtons.add(button);
			styleMenu.add(button);
		}

		JCheckBoxMenuItem zoom = new JCheckBoxMenuItem("Restrict Maximum Zoom");

		zoom.addChangeListener(e -> {
			Configs.USER_PROFILE.getUserSettings().restrictMaximumZoom = zoom.getState();
			Configs.USER_PROFILE.flush();
		});

		settingsMenu.addMenuListener(Events.Menu.onSelected(e -> zoom.setState(Configs.USER_PROFILE.getUserSettings().restrictMaximumZoom)));

		JMenu metric = new JMenu("Fragment Metric");
		ButtonGroup group = new ButtonGroup();
		JRadioButtonMenuItem metric1 = new JRadioButtonMenuItem("Euclidean");
		JRadioButtonMenuItem metric2 = new JRadioButtonMenuItem("Manhattan");
		JRadioButtonMenuItem metric3 = new JRadioButtonMenuItem("Chebyshev");
		metric.add(metric1);
		metric.add(metric2);
		metric.add(metric3);
		group.add(metric1);
		group.add(metric2);
		group.add(metric3);

		DistanceMetric m = Configs.USER_PROFILE.getUserSettings().getFragmentMetric();
		if(m == DistanceMetric.EUCLIDEAN_SQ)metric1.setSelected(true);
		else if(m == DistanceMetric.MANHATTAN)metric2.setSelected(true);
		else if(m == DistanceMetric.CHEBYSHEV)metric3.setSelected(true);

		setUserConfig(metric1);
		setUserConfig(metric2);
		setUserConfig(metric3);

		settingsMenu.add(styleMenu);
		settingsMenu.add(metric);
		settingsMenu.add(zoom);
		this.add(settingsMenu);
	}

	private static void setUserConfig(JMenuItem metric) {
		metric.addMouseListener(Events.Mouse.onPressed(mouseEvent -> {
			Configs.USER_PROFILE.getUserSettings().fragmentMetric = metric.getText();
			Configs.USER_PROFILE.flush();
			MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
			if(map != null)map.scheduler.scheduledModified.set(true);
		}));
	}



	private void addWorldMenu() {
		JMenu worldMenu = new JMenu("World");

		JMenuItem goToCoords = new JMenuItem("Go to Coordinates");

		goToCoords.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(() -> {
			if(!goToCoords.isEnabled())return;
			DialogStorage.COORD_HOPPER_DIALOG.setVisible(true);
		})));

		JMenuItem goToSpawn = new JMenuItem("Go to Spawn");

		goToSpawn.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(() -> {
			if(!goToSpawn.isEnabled())return;
			BPos pos = this.getActiveSpawn();
			if(pos != null) {
				MineMap.INSTANCE.worldTabs.getSelectedMapPanel().getManager().setCenterPos(pos.getX(), pos.getZ());
			}
		})));

		JMenuItem loadShadowSeed = new JMenuItem("Load Shadow Seed");

		loadShadowSeed.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(() -> {
			if(!loadShadowSeed.isEnabled())return;
			MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
			MineMap.INSTANCE.worldTabs.load(
					map.getContext().version,
					String.valueOf(WorldSeed.getShadowSeed(map.getContext().worldSeed)),
					map.threadCount, Collections.singletonList(map.getContext().dimension));
		})));

		worldMenu.addMenuListener(Events.Menu.onSelected(e -> {
			MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
			goToCoords.setEnabled(map != null);
			goToSpawn.setEnabled(map != null && this.getActiveSpawn() != null);
			loadShadowSeed.setEnabled(map != null && map.getContext().dimension == Dimension.OVERWORLD);
		}));

		JMenuItem changeSalts = new JMenuItem("Change Salts");

		changeSalts.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(() -> {
//			SaltDialog dialog;
			try {
//				dialog = new SaltDialog();
//				dialog.setVisible(true);
			} catch (Exception exception) {
				exception.printStackTrace();
			}
		})));

		SwingUtils.addSet(worldMenu, goToCoords, goToSpawn, loadShadowSeed, changeSalts);
		this.add(worldMenu);
	}

	private BPos getActiveSpawn() {
		MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
		IconRenderer icon = map.getContext().getIconManager().getFor(SpawnPoint.class);
		return icon instanceof SpawnIcon ? ((SpawnIcon)icon).getPos() : null;
	}
}

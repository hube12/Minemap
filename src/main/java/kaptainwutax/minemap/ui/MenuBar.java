package kaptainwutax.minemap.ui;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.seedutils.mc.seed.WorldSeed;
import wearblackallday.swing.Events;
import wearblackallday.swing.components.MenuBuilder;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;

public class MenuBar {

	private static final ButtonGroup buttonGroup = new ButtonGroup();

	public static JMenuBar create() {
		return new MenuBuilder(
				new MenuBuilder.Menu("File",
						new MenuBuilder.Item("New from Seed...", (item, e) ->
								SwingUtilities.invokeLater(() -> MineMap.ENTER_SEED_DIALOG.setVisible(true))
						),
						new MenuBuilder.Item("Screenshot...", (item, e) -> {
							MapPanel map = MineMap.WORLD_TABS.getSelectedMapPanel();
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
								SwingUtilities.invokeLater(() -> MineMap.COORD_HOPPER_DIALOG.setVisible(true))),
						new MenuBuilder.Item("Load Shadow Seed", (item, e) ->
								SwingUtilities.invokeLater(() -> {
									MapPanel map = MineMap.WORLD_TABS.getSelectedMapPanel();
									MineMap.WORLD_TABS.load(
											map.getContext().version,
											String.valueOf(WorldSeed.getShadowSeed(map.getContext().worldSeed)),
											map.threadCount, Collections.singletonList(map.getContext().dimension));
								})),
						new MenuBuilder.Item("Change Salts", (item, e) ->
								SwingUtilities.invokeLater(() -> MineMap.SALT_DIALOG.setVisible(true)))
				).addSelectedListener((menu, menuEvent) -> {
					for (Component c : menu.getMenuComponents()) {
						c.setEnabled(MineMap.WORLD_TABS.getSelectedMapPanel() != null);
					}
				}),
				new MenuBuilder.Menu("Settings",
						new MenuBuilder.Menu("Style").run(menu -> {
							ButtonGroup buttonGroup1 = new ButtonGroup();
							for (String style : Configs.BIOME_COLORS.getStyles()) {
								JRadioButtonMenuItem button = new JRadioButtonMenuItem(style);
								button.addActionListener(e -> {
									Configs.USER_PROFILE.getUserSettings().style = style;
									MineMap.WORLD_TABS.invalidateAll();
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
		MapPanel map = MineMap.WORLD_TABS.getSelectedMapPanel();
		if (map != null) map.scheduler.scheduledModified.set(true);
	}

	private static void setUserConfig(JMenuItem metric) {
		metric.addMouseListener(Events.Mouse.onPressed(mouseEvent -> {
			Configs.USER_PROFILE.getUserSettings().fragmentMetric = metric.getText();
			Configs.USER_PROFILE.flush();
			MapPanel map = MineMap.WORLD_TABS.getSelectedMapPanel();
			if (map != null) map.scheduler.scheduledModified.set(true);
		}));
	}
}

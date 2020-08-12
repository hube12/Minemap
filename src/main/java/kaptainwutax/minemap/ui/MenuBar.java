package kaptainwutax.minemap.ui;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.util.Fragment;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.function.Consumer;

public class MenuBar extends JMenuBar {

	public MenuBar() {
		JMenu fileMenu = new JMenu("File");
		JMenuItem loadSeed = new JMenuItem("New From Seed...");

		loadSeed.addMouseListener(new MousePressListener(e -> {
			SwingUtilities.invokeLater(() -> {
				EnterSeedDialog dialog = new EnterSeedDialog();
				dialog.setVisible(true);
			});
		}));

		fileMenu.add(loadSeed);
		this.add(fileMenu);

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

		JMenu worldMenu = new JMenu("World");
		JCheckBoxMenuItem showGrid = new JCheckBoxMenuItem("Show Grid");

		showGrid.addChangeListener(e -> {
			Fragment.DRAW_GRID = showGrid.getState();
			Component c = MineMap.INSTANCE.worldTabs.getComponentAt(MineMap.INSTANCE.worldTabs.getSelectedIndex());
			if(!(c instanceof MapPanel))return;
			c.repaint();
		});

		JCheckBoxMenuItem showStructures = new JCheckBoxMenuItem("Show Structures");
		showStructures.setState(true);

		showStructures.addChangeListener(e -> {
			Fragment.DRAW_STRUCTURES = showStructures.getState();
			Component c = MineMap.INSTANCE.worldTabs.getComponentAt(MineMap.INSTANCE.worldTabs.getSelectedIndex());
			if(!(c instanceof MapPanel))return;
			c.repaint();
		});
		//Lara start
		JMenuItem coordHopper = new JMenuItem();
		coordHopper.setText("Go to Coordinates");
		worldMenu.add(coordHopper);
		coordHopper.addActionListener(e -> SwingUtilities.invokeLater(() -> {
			JDialog jumpDialogue = new CoordHopper().mainLogue;
			jumpDialogue.setVisible(true);
		}));
		//Lara end


		worldMenu.add(showGrid);
		worldMenu.add(showStructures);
		this.add(worldMenu);
	}

	public static class MousePressListener implements MouseListener {
		private final Consumer<MouseEvent> onEvent;

		public MousePressListener(Consumer<MouseEvent> onEvent) {
			this.onEvent = onEvent;
		}

		@Override
		public final void mousePressed(MouseEvent e) {
			this.onEvent.accept(e);
		}

		@Override
		public final void mouseClicked(MouseEvent e) {

		}

		@Override
		public final void mouseReleased(MouseEvent e) {

		}

		@Override
		public final void mouseEntered(MouseEvent e) {

		}

		@Override
		public final void mouseExited(MouseEvent e) {

		}
	}

}

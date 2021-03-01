package kaptainwutax.minemap;

import com.formdev.flatlaf.FlatDarkLaf;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.Features;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.ui.MenuBar;
import kaptainwutax.minemap.ui.component.WorldTabs;

import javax.swing.*;
import java.awt.*;

public class MineMap extends JFrame {

	public static MineMap INSTANCE;
	public static boolean DARCULA = false;
	public WorldTabs worldTabs;

	public static void main(String[] args) {
		Features.registerFeatures();
		Icons.registerIcons();
		Configs.registerConfigs();

		INSTANCE = new MineMap();
	}

	public MineMap() {
		FlatDarkLaf.install();
		DARCULA = true;

//		this.setJMenuBar(new MenuBar());
		this.setJMenuBar(MenuBar.create());
		this.add(this.worldTabs = new WorldTabs());

		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setSize(screenSize.width / 2, screenSize.height / 2);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		this.setTitle("MineMap");
		this.setVisible(true);
	}
}

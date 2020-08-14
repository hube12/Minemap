package kaptainwutax.minemap;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatLightLaf;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.ui.MenuBar;
import kaptainwutax.minemap.ui.component.WorldTabs;

import javax.swing.*;
import java.awt.*;

public class MineMap extends JFrame {

	public static MineMap INSTANCE;
	public static boolean DARCULA = false;

	private JMenuBar toolbarPane;
	public WorldTabs worldTabs;

	public static void main(String[] args) {
		Configs.registerConfigs();
		INSTANCE = new MineMap();
		INSTANCE.setVisible(true);
	}

	public MineMap() {
		try {
			FlatLightLaf.install();
			UIManager.setLookAndFeel(new FlatDarculaLaf());
			DARCULA = true;
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}

		BorderLayout layout = new BorderLayout();
		this.setLayout(layout);

		this.initComponents();
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setExtendedState(JFrame.MAXIMIZED_BOTH);
		this.setSize(screenSize.width / 2, screenSize.height / 2);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		System.out.println("Finished initialized things bla bla bla... you get it.");
	}

	private void initComponents() {
		this.toolbarPane = new MenuBar();
		this.add(this.toolbarPane, BorderLayout.NORTH);

		this.worldTabs = new WorldTabs();
		this.add(this.worldTabs);
	}


}

package kaptainwutax.minemap.ui;

import kaptainwutax.biomeutils.source.EndBiomeSource;
import kaptainwutax.biomeutils.source.NetherBiomeSource;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.util.WorldInfo;
import kaptainwutax.seedutils.mc.MCVersion;

import javax.swing.*;
import javax.swing.plaf.metal.MetalIconFactory;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;

public class WorldTabs extends JTabbedPane {

	public static final Color BACKGROUND_COLOR = new Color(60, 63,65);

	public final ByteArrayOutputStream output = new ByteArrayOutputStream();

	public WorldTabs() {
		JTextArea textArea = new JTextArea();
		this.addTab("Console", textArea);

		PrintStream printStream = new PrintStream(this.output) {
			@Override
			public synchronized void println(String x) {
				SwingUtilities.invokeLater(() -> textArea.setText(textArea.getText() + x + "\n"));
			}
		};

		System.setOut(printStream);
		System.setErr(printStream);

		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e -> {
			if(e.getKeyCode() != KeyEvent.VK_C)return false;
			if((e.getModifiers() & Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()) == 0)return false;
			Component c = this.getComponentAt(this.getSelectedIndex());
			if(!(c instanceof MapPanel))return false;
			MapPanel map = (MapPanel)c;
			String s = String.valueOf(map.info.worldSeed);
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(s), null);
			return true;
		});
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if(MineMap.DARCULA) {
			g.setColor(BACKGROUND_COLOR);
			g.fillRect(0, 0, this.getWidth(), this.getHeight());
		}

		super.paintComponent(g);
	}

	public void loadSeed(MCVersion version, String worldSeed, int threadCount) {
		if(worldSeed.isEmpty()) {
			this.loadSeed(version, new Random().nextLong(), threadCount);
			return;
		}

		try {
			this.loadSeed(version, Long.parseLong(worldSeed), threadCount);
		} catch(NumberFormatException e) {
			this.loadSeed(version, worldSeed.hashCode(), threadCount);
		}
	}

	private void loadSeed(MCVersion version, long worldSeed, int threadCount) {
		SwingUtilities.invokeLater(() -> {
			String prefix = "[" + version + "] ";
			this.addTab(prefix + "Overworld " + worldSeed, new MapPanel(new WorldInfo(version, worldSeed, WorldInfo.QUARTER_RES_ID, OverworldBiomeSource::new), threadCount));
			this.setSelectedIndex(this.getTabCount() - 1);
			this.addTab(prefix + "Nether " + worldSeed, new MapPanel(new WorldInfo(version, worldSeed, WorldInfo.QUARTER_RES_ID, NetherBiomeSource::new), threadCount));
			this.addTab(prefix + "End " + worldSeed, new MapPanel(new WorldInfo(version, worldSeed, WorldInfo.QUARTER_RES_ID, EndBiomeSource::new), threadCount));
		});
	}

	public synchronized void invalidateAll() {
		for(int i = 0; i < this.getTabCount(); i++) {
			Component c = this.getComponentAt(i);
			if(!(c instanceof MapPanel))continue;
			((MapPanel)c).restart();
		}
	}

	/* Override Addtab in order to add the close Button everytime */
	@Override
	public void addTab(String title, Icon icon, Component component, String tip) {
		super.addTab(title, icon, component, tip);
		int count = this.getTabCount() - 1;
		setTabComponentAt(count, new CloseButtonTab(component, title, icon));
	}

	@Override
	public void addTab(String title, Icon icon, Component component) {
		addTab(title, icon, component, null);
	}

	@Override
	public void addTab(String title, Component component) {
		addTab(title, null, component);
	}

	/* addTabNoExit */
	public void addTabNoExit(String title, Icon icon, Component component, String tip) {
		super.addTab(title, icon, component, tip);
	}

	public void addTabNoExit(String title, Icon icon, Component component) {
		addTabNoExit(title, icon, component, null);
	}

	public void addTabNoExit(String title, Component component) {
		addTabNoExit(title, null, component);
	}

	/* Button */
	public class CloseButtonTab extends JPanel {
		private Component tab;

		public CloseButtonTab(final Component tab, String title, Icon icon) {
			this.tab = tab;
			setOpaque(false);
			FlowLayout flowLayout = new FlowLayout(FlowLayout.CENTER, 3, 3);
			setLayout(flowLayout);
			JLabel jLabel = new JLabel(title);
			jLabel.setIcon(icon);
			add(jLabel);
			JButton button = new JButton(MetalIconFactory.getInternalFrameCloseIcon(16));
			button.setMargin(new Insets(0, 0, 0, 0));

			button.addMouseListener(Events.Mouse.onClick(e -> {
				if(e.getSource() instanceof JButton){
					JButton clickedButton = (JButton)e.getSource();
					JTabbedPane tabbedPane = (JTabbedPane) clickedButton.getParent().getParent().getParent();
					tabbedPane.remove(tab);
					this.tab.setVisible(false);
				}
			}));

			add(button);
		}
	}

}

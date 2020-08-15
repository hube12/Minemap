package kaptainwutax.minemap.ui;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.config.Config;
import kaptainwutax.minemap.config.UserProfileConfig;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.component.Dropdown;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.MCVersion;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.IntStream;

public class EnterSeedDialog extends JDialog {

	public JTextField seedField;
	public Dropdown<Integer> threadDropdown;
	public Dropdown<MCVersion> versionDropdown;
	public JButton continueButton;

	public EnterSeedDialog() {
		this.setModal(true);
		this.initComponents();
	}

	public void initComponents() {
		int cores = Runtime.getRuntime().availableProcessors();

		Container contentPane = getContentPane();
		contentPane.setLayout(new GridLayout(4, 1));

		this.seedField = new JTextField();
		this.threadDropdown = new Dropdown<>(i -> i + (i == 1 ? " thread" : " threads"), IntStream.rangeClosed(1, cores).boxed());
		this.versionDropdown = new Dropdown<>(Arrays.stream(MCVersion.values()).filter(v -> v.isNewerOrEqualTo(MCVersion.v1_13)));
		this.continueButton = new JButton("Continue");

		this.threadDropdown.selectIfPresent(Configs.USER_PROFILE.getThreadCount(cores));
		this.versionDropdown.selectIfPresent(Configs.USER_PROFILE.getVersion());

		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.versionDropdown, this.threadDropdown);

		contentPane.add(new JLabel("Enter your seed here:"));
		contentPane.add(this.seedField);
		contentPane.add(splitPanel);
		contentPane.add(this.continueButton);

		this.pack();

		this.continueButton.addMouseListener(Events.Mouse.onClick(e -> {
			ArrayList<Dimension> activeDims = new ArrayList<>(3);
			if (Configs.USER_PROFILE.getOWenabled()) {
				activeDims.add(Dimension.OVERWORLD);
			}
			if (Configs.USER_PROFILE.getNetherenabled()) {
				activeDims.add(Dimension.NETHER);
			}
			if (Configs.USER_PROFILE.getEndenabled()) {
				activeDims.add(Dimension.END);
			}
			MineMap.INSTANCE.worldTabs.load(versionDropdown.getSelected(), seedField.getText(), threadDropdown.getSelected(), activeDims);
			Configs.USER_PROFILE.setThreadCount(threadDropdown.getSelected());
			Configs.USER_PROFILE.setVersion(versionDropdown.getSelected());
			continueButton.setEnabled(false);
			setVisible(false);
			dispose();
		}));

		this.setLocation(
				MineMap.INSTANCE.getX() + MineMap.INSTANCE.getWidth() / 2 - this.getWidth() / 2,
				MineMap.INSTANCE.getY() + MineMap.INSTANCE.getHeight() / 2 - this.getHeight() / 2
		);
	}

}

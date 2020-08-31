package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.component.Dropdown;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.MCVersion;
import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.stream.IntStream;

public class EnterSeedDialog extends JDialog {

	public JTextField seedField;
	public Dropdown<Integer> threadDropdown;
	public Dropdown<MCVersion> versionDropdown;
	public JButton continueButton;

	public EnterSeedDialog() {
		this.setModal(true);
		this.initComponents();
		this.setTitle("Load new Seed");
	}

	public void initComponents() {
		int cores = Runtime.getRuntime().availableProcessors();

		Container contentPane = getContentPane();
		contentPane.setLayout(new GridLayout(3, 1));

		this.seedField = new JTextField();
		PromptSupport.setPrompt("Enter your seed here...", this.seedField);

		this.threadDropdown = new Dropdown<>(i -> i + (i == 1 ? " thread" : " threads"), IntStream.rangeClosed(1, cores).boxed());
		this.versionDropdown = new Dropdown<>(Arrays.stream(MCVersion.values()).filter(v -> v.isNewerOrEqualTo(MCVersion.v1_13)));
		this.continueButton = new JButton("Continue");

		this.threadDropdown.selectIfPresent(Configs.USER_PROFILE.getThreadCount(cores));
		this.versionDropdown.selectIfPresent(Configs.USER_PROFILE.getVersion());

		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.versionDropdown, this.threadDropdown);

		JCheckBoxMenuItem[] checkBoxes = Arrays.stream(Dimension.values()).map(dimension -> {
			String s = Character.toUpperCase(dimension.name.charAt(0)) + dimension.name.substring(1);
			JCheckBoxMenuItem check = new JCheckBoxMenuItem("Show " + s);
			check.setState(Configs.USER_PROFILE.isDimensionEnabled(dimension));
			check.addChangeListener(e -> Configs.USER_PROFILE.setDimensionState(dimension, check.getState()));
			return check;
		}).toArray(JCheckBoxMenuItem[]::new);

		contentPane.add(this.seedField);
		contentPane.add(checkBoxes[0]);
		contentPane.add(splitPanel);
		contentPane.add(checkBoxes[1]);
		contentPane.add(this.continueButton);
		contentPane.add(checkBoxes[2]);

		this.pack();

		this.continueButton.addMouseListener(Events.Mouse.onPressed(e -> {
			MineMap.INSTANCE.worldTabs.load(versionDropdown.getSelected(), seedField.getText(),
					threadDropdown.getSelected(), Configs.USER_PROFILE.getEnabledDimensions());

			Configs.USER_PROFILE.setThreadCount(threadDropdown.getSelected());
			Configs.USER_PROFILE.setVersion(versionDropdown.getSelected());
			this.continueButton.setEnabled(false);
			this.dispose();
		}));

		this.setLocation(
				MineMap.INSTANCE.getX() + MineMap.INSTANCE.getWidth() / 2 - this.getWidth() / 2,
				MineMap.INSTANCE.getY() + MineMap.INSTANCE.getHeight() / 2 - this.getHeight() / 2
		);
	}
}

package kaptainwutax.minemap.ui;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.component.Dropdown;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.MCVersion;
import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
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

		JCheckBoxMenuItem OWtick = new JCheckBoxMenuItem("Render OverWorld");
		OWtick.setState(Configs.USER_PROFILE.isDimensionEnabled(Dimension.OVERWORLD));
		OWtick.addChangeListener(e -> Configs.USER_PROFILE.setDimensionState(Dimension.OVERWORLD, OWtick.getState()));

		JCheckBoxMenuItem Ntick = new JCheckBoxMenuItem("Render Nether");
		Ntick.setState(Configs.USER_PROFILE.isDimensionEnabled(Dimension.NETHER));
		Ntick.addChangeListener(e -> Configs.USER_PROFILE.setDimensionState(Dimension.NETHER, Ntick.getState()));

		JCheckBoxMenuItem Etick = new JCheckBoxMenuItem("Render End");
		Etick.setState(Configs.USER_PROFILE.isDimensionEnabled(Dimension.END));
		Etick.addChangeListener(e -> Configs.USER_PROFILE.setDimensionState(Dimension.END, Etick.getState()));

		contentPane.add(this.seedField);
		contentPane.add(OWtick);
		contentPane.add(Ntick);
		contentPane.add(Etick);
		contentPane.add(splitPanel);
		contentPane.add(this.continueButton);

		this.pack();

		this.continueButton.addMouseListener(Events.Mouse.onClick(e -> {
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

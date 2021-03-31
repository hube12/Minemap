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

public class EnterSeedDialog extends Dialog {

	public JTextField seedField;
	public Dropdown<Integer> threadDropdown;
	public Dropdown<MCVersion> versionDropdown;
	public JButton continueButton;

	public EnterSeedDialog(Runnable onExit) {
		super("Load new Seed", new GridLayout(3, 1));
		this.addExitProcedure(onExit);
	}

	@Override
	public void initComponents() {
		int cores = Runtime.getRuntime().availableProcessors();

		this.seedField = new JTextField();
		PromptSupport.setPrompt("Enter your seed here...", this.seedField);

		this.threadDropdown = new Dropdown<>(i -> i + (i == 1 ? " thread" : " threads"), IntStream.rangeClosed(1, cores).boxed());
		this.threadDropdown.selectIfPresent(Configs.USER_PROFILE.getThreadCount(cores));

		this.versionDropdown = new Dropdown<>(Arrays.stream(MCVersion.values())
				.filter(v -> v.isNewerOrEqualTo(MCVersion.v1_0)));
		this.versionDropdown.selectIfPresent(Configs.USER_PROFILE.getVersion());

		JSplitPane splitPanel = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.versionDropdown, this.threadDropdown);

		this.continueButton = new JButton("Continue");

		this.continueButton.addMouseListener(Events.Mouse.onPressed(e -> {
			MineMap.INSTANCE.worldTabs.load(versionDropdown.getSelected(), seedField.getText(),
					threadDropdown.getSelected(), Configs.USER_PROFILE.getEnabledDimensions());

			Configs.USER_PROFILE.setThreadCount(threadDropdown.getSelected());
			Configs.USER_PROFILE.setVersion(versionDropdown.getSelected());
			this.continueButton.setEnabled(false);
			this.dispose();
		}));

		JCheckBoxMenuItem[] checkBoxes = Arrays.stream(Dimension.values()).map(dimension -> {
			String s = Character.toUpperCase(dimension.getName().charAt(0)) + dimension.getName().substring(1);
			JCheckBoxMenuItem check = new JCheckBoxMenuItem("Load " + s);
			check.setState(Configs.USER_PROFILE.isDimensionEnabled(dimension));
			check.addChangeListener(e -> Configs.USER_PROFILE.setDimensionState(dimension, check.getState()));
			return check;
		}).toArray(JCheckBoxMenuItem[]::new);

		this.getContentPane().add(this.seedField);
		this.getContentPane().add(checkBoxes[Dimension.OVERWORLD.ordinal()]);
		this.getContentPane().add(splitPanel);
		this.getContentPane().add(checkBoxes[Dimension.NETHER.ordinal()]);
		this.getContentPane().add(this.continueButton);
		this.getContentPane().add(checkBoxes[Dimension.END.ordinal()]);
	}
}

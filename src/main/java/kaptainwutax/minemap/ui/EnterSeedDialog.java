package kaptainwutax.minemap.ui;

import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.component.Dropdown;
import kaptainwutax.seedutils.mc.MCVersion;

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
		//this.initComponents();
		this.initStuff();
	}

	public void initStuff() {
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
			MineMap.INSTANCE.worldTabs.loadSeed(versionDropdown.getSelected(), seedField.getText(), threadDropdown.getSelected());
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

	private void initComponents() {
		//PYTHON-BEGIN:initComponents
		wutaxlabel = new JLabel();
		wutaxtextfield = new JTextField();
		wutaxcomboBox2 = new JComboBox<>(Arrays.stream(MCVersion.values()).map(Object::toString).toArray(String[]::new));
		wutaxcomboBox1 = new JComboBox<>(IntStream.rangeClosed(1, Runtime.getRuntime().availableProcessors()).boxed()
				.map(i -> i + "                    ").toArray(String[]::new));

		//======== this ========
		Container contentPane = getContentPane();
		contentPane.setLayout(new GridLayoutManager(3, 3, new Insets(0, 0, 0, 0), -1, -1));

		//---- wutaxlabel ----
		wutaxlabel.setText("label");
		contentPane.add(wutaxlabel, new GridConstraints(0, 0, 1, 3,
				GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				null, null, null));

		//---- wutaxtextfield ----
		wutaxtextfield.setText("field");
		contentPane.add(wutaxtextfield, new GridConstraints(1, 0, 1, 3,
				GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				null, null, null));

		Dropdown<Integer> v1 = new Dropdown<>(i -> i + (i == 1 ? " thread" : " threads"), IntStream.rangeClosed(1, Runtime.getRuntime().availableProcessors()).boxed());
		Dropdown<MCVersion> v2 = new Dropdown<>(Arrays.stream(MCVersion.values()).filter(v -> v.isNewerOrEqualTo(MCVersion.v1_13)));


		contentPane.add(wutaxcomboBox1, new GridConstraints(2, 0, 1, 1,
				GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				null, null, null));
		contentPane.add(wutaxcomboBox2, new GridConstraints(2, 2, 1, 1,
				GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_NONE,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW,
				null, null, null));
		pack();
		setLocationRelativeTo(getOwner());
		//PYTHON-END:initComponents
	}

	//PYTHON-BEGIN:variables
	private JLabel wutaxlabel;
	private JTextField wutaxtextfield;
	private JComboBox<String> wutaxcomboBox1;
	private JComboBox<String> wutaxcomboBox2;
	//PYTHON-END:variables

}

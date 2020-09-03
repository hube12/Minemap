package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.component.TabHeader;
import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.*;
import java.awt.*;

public class RenameTabDialog extends Dialog {

	public JTextField nameField;
	public JButton continueButton;

	public RenameTabDialog() {
		super("Rename Tab", new GridLayout(0, 1));
	}

	@Override
	public void initComponents() {
		TabHeader header = MineMap.INSTANCE.worldTabs.getSelectedHeader();

		this.nameField = new JTextField(header.getName());
		PromptSupport.setPrompt("Enter the tab name here...", this.nameField);

		this.continueButton = new JButton("Continue");

		this.continueButton.addMouseListener(Events.Mouse.onPressed(e -> {
			header.setName(this.nameField.getText());
			this.dispose();
		}));

		this.getContentPane().add(this.nameField);
		this.getContentPane().add(this.continueButton);
	}

}

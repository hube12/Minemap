package com.seedfinding.minemap.ui.dialog;

import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.listener.Events;
import com.seedfinding.minemap.ui.component.TabHeader;

import javax.swing.*;
import java.awt.*;

import static com.seedfinding.minemap.util.ui.interactive.Prompt.setPrompt;

public class RenameTabDialog extends Dialog {

    public JTextField nameField;
    public JButton continueButton;
    private TabHeader header;

    public RenameTabDialog(Runnable onExit) {
        super("Rename Tab", new GridLayout(0, 1));
        this.addExitProcedure(onExit);
    }

    @Override
    public void initComponents() {
        header = MineMap.INSTANCE.worldTabs.getSelectedHeader();

        this.nameField = new JTextField(header.getName());
        setPrompt("Enter the tab name here...", this.nameField);

        this.continueButton = new JButton("Continue");

        this.continueButton.addMouseListener(Events.Mouse.onPressed(e -> create()));

        this.getContentPane().add(this.nameField);
        this.getContentPane().add(this.continueButton);
    }


    protected void create() {
        header.setName(this.nameField.getText());
        this.dispose();
    }

    protected void cancel() {
        continueButton.setEnabled(false);
        dispose();
    }

}

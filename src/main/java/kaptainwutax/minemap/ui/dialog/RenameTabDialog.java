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
    private TabHeader header;

    public RenameTabDialog() {
        super("Rename Tab", new GridLayout(0, 1));
    }

    @Override
    public void initComponents() {
        header = MineMap.INSTANCE.worldTabs.getSelectedHeader();

        this.nameField = new JTextField(header.getName());
        PromptSupport.setPrompt("Enter the tab name here...", this.nameField);

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

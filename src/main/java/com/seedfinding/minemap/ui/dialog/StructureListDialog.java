package com.seedfinding.minemap.ui.dialog;

import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.minemap.init.Logger;
import com.seedfinding.minemap.listener.Events;
import com.seedfinding.minemap.util.ui.graphics.TpPanel;
import com.seedfinding.minemap.util.ui.interactive.Prompt;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StructureListDialog extends StructureDialog {
    public JTextField enterN;

    public StructureListDialog(Runnable onExit) {
        super("List N closest structures", new GridLayout(0, 1));
        this.addExitProcedure(onExit);
    }

    @Override
    public void initComponents() {
        super.initComponents();

        this.enterN = new JTextField("1");
        Prompt.setPrompt("Number of structures", this.enterN);
        this.enterN.addKeyListener(Events.Keyboard.onReleased(e -> {
            try {
                Integer.parseInt(this.enterN.getText().trim());
                this.continueButton.setEnabled(true);
            } catch (Exception _e) {
                this.continueButton.setEnabled(false);
            }
        }));

        this.getContentPane().add(this.enterN);
        this.getContentPane().add(super.structureItemDropdown);
        this.getContentPane().add(super.continueButton);
    }


    protected void create() {
        if (!this.continueButton.isEnabled()) return;

        int n;
        try {
            n = Integer.parseInt(this.enterN.getText().trim());
        } catch (NumberFormatException _e) {
            JOptionPane.showMessageDialog(this, String.format("This is not a number: %s", this.enterN.getText().trim()));
            return;
        }
        if (n > 300 || n <= 0) {
            JOptionPane.showMessageDialog(this, String.format("You have chosen a number (%d) outside of the permitted range [1;300]", n));
            return;
        }

        List<BPos> bPosList = getFeatures(n);
        if (bPosList == null || bPosList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "We could not find any feature that match your requirement, report this if you think it is a bug.");
            Logger.LOGGER.info("Could not find a valid feature");
            return;
        };

        // destroy the current container
        this.dispose();

        TpPanel.makeFrame(bPosList, this.structureItemDropdown.getSelected().getFeature());
    }
}

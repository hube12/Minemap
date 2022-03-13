package com.seedfinding.minemap.ui.dialog;

import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.minemap.init.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StructureHopperDialog extends StructureDialog {
    public StructureHopperDialog(Runnable onExit) {
        super("Go to Structure Coordinates", new GridLayout(0, 1));
        this.addExitProcedure(onExit);
    }

    @Override
    public void initComponents() {
        super.initComponents();

        this.getContentPane().add(this.structureItemDropdown);
        this.getContentPane().add(this.continueButton);
    }

    protected void create() {
        if (!this.continueButton.isEnabled()) return;

        List<BPos> bPosList = getFeatures(1);
        if (bPosList == null || bPosList.isEmpty()) {
            JOptionPane.showMessageDialog(this, "We could not find any feature that match your requirement, report this if you think it is a bug.");
            Logger.LOGGER.info("Could not find a valid feature");
            return;
        };

        BPos bPos = bPosList.get(0);
        manager.setCenterPos(bPos.getX(), bPos.getZ());

        this.dispose();
    }
}

package com.seedfinding.minemap.ui.dialog;

import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.listener.Events;
import com.seedfinding.minemap.ui.map.MapPanel;
import com.seedfinding.minemap.util.ui.interactive.Dropdown;
import com.seedfinding.minemap.util.ui.interactive.Prompt;

import javax.swing.*;
import java.awt.*;
import java.util.function.IntUnaryOperator;


public class CoordHopperDialog extends Dialog {

    public JTextField enterX;
    public JTextField enterZ;
    public JSplitPane duo;
    public Dropdown<Type> typeDropdown;
    public JButton continueButton;

    public CoordHopperDialog(Runnable onExit) {
        super("Go to Coordinates", new GridLayout(0, 1));
        this.addExitProcedure(onExit);
    }


    @Override
    public void initComponents() {
        this.enterX = new JTextField("0");
        Prompt.setPrompt("X Coordinate...", this.enterX);

        this.enterX.addKeyListener(Events.Keyboard.onReleased(e -> {
            try {
                Integer.parseInt(this.enterX.getText().trim());
                this.continueButton.setEnabled(true);
            } catch (Exception _e) {
                this.continueButton.setEnabled(false);
            }
        }));

        this.enterZ = new JTextField("0");
        Prompt.setPrompt("Z Coordinate...", this.enterZ);

        this.enterZ.addKeyListener(Events.Keyboard.onReleased(e -> {
            try {
                Integer.parseInt(this.enterX.getText().trim());
                this.continueButton.setEnabled(true);
            } catch (Exception _e) {
                this.continueButton.setEnabled(false);
            }
        }));

        this.typeDropdown = new Dropdown<>(Type::getName, Type.values());

        this.continueButton = new JButton();
        this.continueButton.setText("Continue");

        this.continueButton.addMouseListener(Events.Mouse.onPressed(e -> create()));

        this.duo = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.enterX, this.enterZ);
        duo.setResizeWeight(0.5);
        this.getContentPane().add(duo);
        this.getContentPane().add(this.typeDropdown);
        this.getContentPane().add(this.continueButton);
    }

    protected void create() {
        if (!this.continueButton.isEnabled()) return;
        int x, z;

        try {
            x = Integer.parseInt(this.enterX.getText().trim());
            z = Integer.parseInt(this.enterZ.getText().trim());
        } catch (NumberFormatException _e) {
            return;
        }

        x = this.typeDropdown.getSelected().transform(x);
        z = this.typeDropdown.getSelected().transform(z);
        MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
        if (map != null) map.getManager().setCenterPos(x, z);
        this.dispose();
    }

    protected void cancel() {
        continueButton.setEnabled(false);
        dispose();
    }

    protected enum Type {
        BLOCK("Block Coordinates", i -> i),
        CHUNK("Chunk Coordinates", i -> i << 4),
        REGION_32("Chunk Region Coordinates (32x32)", i -> CHUNK.transform(i) << 5);

        private final String name;
        private final IntUnaryOperator transformation;

        Type(String name, IntUnaryOperator transformation) {
            this.name = name;
            this.transformation = transformation;
        }

        public String getName() {
            return this.name;
        }

        public int transform(int i) {
            return this.transformation.applyAsInt(i);
        }
    }

}

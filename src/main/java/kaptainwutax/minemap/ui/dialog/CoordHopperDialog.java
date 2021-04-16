package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.component.Dropdown;
import kaptainwutax.minemap.ui.map.MapPanel;
import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.*;
import java.awt.*;
import java.util.function.IntUnaryOperator;

public class CoordHopperDialog extends Dialog {

    public JTextField enterX;
    public JTextField enterZ;
    public Dropdown<Type> typeDropdown;
    public JButton continueButton;

    public CoordHopperDialog(Runnable onExit) {
        super("Go to Coordinates", new GridLayout(0, 1));
        this.addExitProcedure(onExit);
    }

    @Override
    public void initComponents() {
        this.enterX = new JTextField("0");
        PromptSupport.setPrompt("X Coordinate...", this.enterX);

        this.enterX.addKeyListener(Events.Keyboard.onReleased(e -> {
            try {
                Integer.parseInt(this.enterX.getText().trim());
                this.continueButton.setEnabled(true);
            } catch (Exception _e) {
                this.continueButton.setEnabled(false);
            }
        }));

        this.enterZ = new JTextField("0");
        PromptSupport.setPrompt("Z Coordinate...", this.enterZ);

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

        JSplitPane duo = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.enterX, this.enterZ);
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

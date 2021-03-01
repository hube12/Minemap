package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.ui.map.MapPanel;
import wearblackallday.swing.components.CustomPanel;
import wearblackallday.swing.components.SelectionBox;

import javax.swing.*;
import java.awt.*;
import java.util.function.IntUnaryOperator;

public class CoordHopperDialog extends JDialog {

    public static final CoordHopperDialog COORD_HOPPER_DIALOG = new CoordHopperDialog();
    public CustomPanel customPanel;
    public CustomPanel.Key<SelectionBox<Type>> typeSelection;

    public CoordHopperDialog() {

        this.setAlwaysOnTop(true);
        this.setModalityType(ModalityType.APPLICATION_MODAL);
        this.setContentPane(this.customPanel = new CustomPanel(new GridLayout(0, 2), 70, 40).
                addTextField("X Coordinate...", "x").
                addTextField("Z Coordinate...", "z").
                addComponent(this.typeSelection, () -> new SelectionBox<>(Type::getName, Type.values())).
                addButton("Continue", e -> this.enter())
        );

        this.setLocation(
                MineMap.INSTANCE.getX() + MineMap.INSTANCE.getWidth() / 2 - this.getWidth() / 2,
                MineMap.INSTANCE.getY() + MineMap.INSTANCE.getHeight() / 2 - this.getHeight() / 2
        );

        this.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        this.pack();
        this.setVisible(false);
    }

    private void enter() {
        try {
            int x, z;
            x = Integer.parseInt(this.customPanel.getText("x").trim());
            z = Integer.parseInt(this.customPanel.getText("z").trim());
            x = this.customPanel.getComponent(this.typeSelection).getSelected().transform(x);
            z = this.customPanel.getComponent(this.typeSelection).getSelected().transform(z);
            MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            if(map != null) map.getManager().setCenterPos(x, z);
            this.setVisible(false);
        } catch (NumberFormatException ignored) {
        }
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

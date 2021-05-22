package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.config.UserSettings;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.util.ui.interactive.Dropdown;
import kaptainwutax.minemap.util.ui.interactive.MultipleSlider;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.stream.IntStream;

import static kaptainwutax.minemap.util.ui.interactive.Prompt.setPrompt;

public class BiomeRiverSizeDialog extends Dialog {

    public MultipleSlider sliders;
    public JButton continueButton;
    public JButton cancelButton;

    public BiomeRiverSizeDialog(Runnable onExit) {
        super("Choose the biome/river size", new GridLayout(3, 1));
        this.addExitProcedure(onExit);
    }

    @Override
    public void initComponents() {
        UserSettings settings=Configs.USER_PROFILE.getUserSettings();




        this.continueButton = new JButton("Continue");
        this.continueButton.addMouseListener(Events.Mouse.onPressed(e -> this.create()));

        this.cancelButton = new JButton("Cancel");
        this.cancelButton.addMouseListener(Events.Mouse.onPressed(e -> this.cancel()));

        JSplitPane splitPanel2 = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.continueButton, this.cancelButton);


        this.getContentPane().add(this.sliders);
        this.getContentPane().add(splitPanel2);
    }

    protected void create() {
        continueButton.setEnabled(false);
        continueButton.setText("Loading...");


    }

    protected void cancel() {
        continueButton.setEnabled(false);
        dispose();
    }
}

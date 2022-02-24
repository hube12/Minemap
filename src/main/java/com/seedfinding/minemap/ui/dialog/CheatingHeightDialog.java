package com.seedfinding.minemap.ui.dialog;

import com.seedfinding.mcterrain.utils.MathHelper;
import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.init.Configs;
import com.seedfinding.minemap.listener.Events;
import com.seedfinding.minemap.util.math.DisplayMaths;
import com.seedfinding.minemap.util.ui.interactive.MultipleSlider;

import javax.swing.*;
import java.awt.*;
import java.util.Hashtable;

public class CheatingHeightDialog extends Dialog {
    private JSlider[] sliders;
    private JButton continueButton;

    public CheatingHeightDialog(Runnable onExit) {
        super("Change Icon Size", new GridLayout(1, 1));
        this.addExitProcedure(onExit);
        this.setResizable(false);
    }

    @Override
    public void initComponents() throws Exception {
        int numberLabels = 1;
        int numberCheatingSize = 4; //meaning from 1 to 2^numberCheatingSize
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        // numberCheatingSize cheating size between 1 and 2^numberCheatingSize
        for (int i = 0; i <= numberCheatingSize; i += 1) {
            labels.put(i, new JLabel(String.valueOf(1<<i)));
        }
        int[] values = new int[numberLabels];
        // Configure the other sliders here !
        values[0] = DisplayMaths.log2nlz((int) MathHelper.clamp(Configs.USER_PROFILE.getUserSettings().cheatingHeight, 1, 16));

        JSplitPane sliderSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        MultipleSlider multipleSlider = new MultipleSlider(values.length, 0, numberCheatingSize, values, labels, JSlider.HORIZONTAL);
        this.sliders = multipleSlider.getSliders();
        if (this.sliders.length != numberLabels) return;
        JPanel labelPanel = new JPanel();

        // Configure for each optimization here
        int opti = 0;
        JLabel label = new JLabel();
        label.setText("Height Scaling");
        JSlider currentSlider = this.sliders[opti];
        currentSlider.addChangeListener(e -> {
            int value = (int) MathHelper.clamp(currentSlider.getValue(), 0, numberCheatingSize);
            Configs.USER_PROFILE.getUserSettings().cheatingHeight = 1 << value;
            Configs.USER_PROFILE.flush();
        });
        labelPanel.add(label);

        sliderSplit.add(labelPanel);
        sliderSplit.add(multipleSlider);
        JSplitPane modalSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        this.continueButton = new JButton("Continue");
        this.continueButton.addMouseListener(Events.Mouse.onPressed(e -> create()));
        modalSplit.add(sliderSplit);
        modalSplit.add(continueButton);
        this.getContentPane().add(modalSplit);
    }

    @Override
    protected void create() {
        this.dispose();
        if (MineMap.INSTANCE!=null && MineMap.INSTANCE.worldTabs!=null && MineMap.INSTANCE.worldTabs.getSelectedMapPanel()!=null){
            MineMap.INSTANCE.worldTabs.getSelectedMapPanel().restart();
        }
    }

    @Override
    protected void cancel() {
        continueButton.setEnabled(false);
        dispose();
    }
}

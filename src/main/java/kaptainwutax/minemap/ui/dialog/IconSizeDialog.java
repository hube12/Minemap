package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.Features;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.util.data.Str;
import kaptainwutax.minemap.util.ui.graphics.Icon;
import kaptainwutax.minemap.util.ui.interactive.MultipleSlider;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class IconSizeDialog extends Dialog {
    private JSlider[] sliders;
    private JButton continueButton;

    public IconSizeDialog(Runnable onExit) {
        super("Change Icon Size", new GridLayout(1, 1));
        this.addExitProcedure(onExit);
        this.setResizable(false);
    }

    @Override
    public void initComponents() throws Exception {
        List<Class<? extends Feature<?, ?>>> features = new ArrayList<>(Features.REGISTRY.keySet());
        Hashtable<Integer, JLabel> labels = new Hashtable<>();
        double steps = 10.0D;
        // 10 possible size between 0.4D and 1.0D
        for (int i = 4; i <= steps; i++) {
            labels.put(i, new JLabel(String.valueOf(i / steps)));
        }
        int[] values = new int[features.size()];
        for (int i = 0; i < features.size(); i++) {
            values[i] = (int) (Configs.ICONS.getSize(features.get(i)) * steps);
        }
        JSplitPane sliderSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        MultipleSlider multipleSlider = new MultipleSlider(features.size(), 4, (int) steps, values, labels);
        this.sliders = multipleSlider.getSliders();
        if (this.sliders.length != features.size()) return;
        JPanel iconPanel = new JPanel();
        double size = 24.0D;
        int scaledSize = 35;
        for (int i = 0; i < features.size(); i++) {
            Class<? extends Feature<?, ?>> feature = features.get(i);
            ImageIcon icon = Icon.getIcon(feature, scaledSize, size,null);
            if (icon == null) {
                iconPanel.add(new JLabel(Str.getInitials(feature.getName())));
                return;
            }
            JLabel label = new JLabel();
            label.setIcon(icon);
            label.setBorder(new BevelBorder(BevelBorder.LOWERED, Color.lightGray, Color.WHITE));
            JSlider currentSlider = this.sliders[i];
            currentSlider.addChangeListener(e -> {
                int value = Math.min(Math.max(currentSlider.getValue(), 4), (int) steps);
                Configs.ICONS.addOverrideEntry(feature, value / steps);
                Configs.ICONS.flush();
                ImageIcon newIcon = Icon.getIcon(feature, scaledSize, size,null);
                label.setIcon(newIcon);
            });
            iconPanel.add(label);
        }
        sliderSplit.add(multipleSlider);
        sliderSplit.add(iconPanel);
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
    }

    @Override
    protected void cancel() {
        continueButton.setEnabled(false);
        dispose();
    }
}

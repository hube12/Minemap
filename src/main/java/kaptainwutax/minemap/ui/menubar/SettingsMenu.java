package kaptainwutax.minemap.ui.menubar;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.KeyShortcuts;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.dialog.ShortcutDialog;
import kaptainwutax.seedutils.util.math.DistanceMetric;

import javax.swing.*;
import java.awt.*;

import static kaptainwutax.minemap.config.KeyboardsConfig.getKeyComboString;

public class SettingsMenu extends Menu {
    private final JMenu lookMenu;
    private final JMenu styleMenu;
    private final JMenu metric;
    private final JCheckBoxMenuItem zoom;
    private final JMenuItem shortcuts;

    public SettingsMenu() {
        this.menu= new JMenu("Settings");

        this.lookMenu = new JMenu("UI Look");
        this.addLookGroup();

        this.styleMenu= new JMenu("Biome Style");
        this.addBiomeGroup();

        this.metric = new JMenu("Fragment Metric");
        this.addMetricGroup();

        this.zoom = new JCheckBoxMenuItem("Restrict Maximum Zoom");
        this.zoom.addChangeListener(e -> {
            Configs.USER_PROFILE.getUserSettings().restrictMaximumZoom = zoom.getState();
            Configs.USER_PROFILE.flush();
        });
        this.menu.addMenuListener(Events.Menu.onSelected(e -> {
            zoom.setState(Configs.USER_PROFILE.getUserSettings().restrictMaximumZoom);
        }));

        this.shortcuts = new JMenuItem("Shortcuts");
        this.shortcuts.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(changeShortcuts())));

        this.menu.add(this.lookMenu);
        this.menu.add(this.styleMenu);
        this.menu.add(this.metric);
        this.menu.add(zoom);
        this.menu.add(this.shortcuts);
    }

    private void addLookGroup(){
        ButtonGroup lookButtons = new ButtonGroup();

        for (MineMap.LookType look : MineMap.LookType.values()) {
            JRadioButtonMenuItem button = new JRadioButtonMenuItem(look.getName());

            button.addMouseListener(Events.Mouse.onPressed(e -> {
                if (!button.isEnabled()) return;
                for (Component c : this.lookMenu.getMenuComponents()) {
                    c.setEnabled(true);
                }
                button.setEnabled(false);
                Configs.USER_PROFILE.getUserSettings().look = look;
                MineMap.lookType = look;
                MineMap.applyStyle();
                Configs.USER_PROFILE.flush();
            }));

            if (Configs.USER_PROFILE.getUserSettings().look.equals(look)) {
                button.setEnabled(false);
            }

            lookButtons.add(button);
            this.lookMenu.add(button);
        }
    }

    private void addBiomeGroup(){
        ButtonGroup styleButtons = new ButtonGroup();

        for (String style : Configs.BIOME_COLORS.getStyles()) {
            JRadioButtonMenuItem button = new JRadioButtonMenuItem(style);

            button.addMouseListener(Events.Mouse.onPressed(e -> {
                if (!button.isEnabled()) return;

                for (Component c : this.styleMenu.getMenuComponents()) {
                    c.setEnabled(true);
                }

                button.setEnabled(false);
                Configs.USER_PROFILE.getUserSettings().style = style;
                MineMap.INSTANCE.worldTabs.invalidateAll();
                Configs.USER_PROFILE.flush();
            }));

            if (Configs.USER_PROFILE.getUserSettings().style.equals(style)) {
                button.setEnabled(false);
            }

            styleButtons.add(button);
            this.styleMenu.add(button);
        }
    }
    private void addMetricGroup(){
        ButtonGroup group = new ButtonGroup();
        JRadioButtonMenuItem metric1 = new JRadioButtonMenuItem("Euclidean");
        JRadioButtonMenuItem metric2 = new JRadioButtonMenuItem("Manhattan");
        JRadioButtonMenuItem metric3 = new JRadioButtonMenuItem("Chebyshev");
        this.metric.add(metric1);
        this.metric.add(metric2);
        this.metric.add(metric3);
        group.add(metric1);
        group.add(metric2);
        group.add(metric3);

        DistanceMetric m = Configs.USER_PROFILE.getUserSettings().getFragmentMetric();
        if (m == DistanceMetric.EUCLIDEAN_SQ) metric1.setSelected(true);
        else if (m == DistanceMetric.MANHATTAN) metric2.setSelected(true);
        else if (m == DistanceMetric.CHEBYSHEV) metric3.setSelected(true);

        metric1.addMouseListener(Events.Mouse.onPressed(mouseEvent -> {
            Configs.USER_PROFILE.getUserSettings().fragmentMetric = metric1.getText();
            Configs.USER_PROFILE.flush();
        }));

        metric2.addMouseListener(Events.Mouse.onPressed(mouseEvent -> {
            Configs.USER_PROFILE.getUserSettings().fragmentMetric = metric2.getText();
            Configs.USER_PROFILE.flush();
        }));

        metric3.addMouseListener(Events.Mouse.onPressed(mouseEvent -> {
            Configs.USER_PROFILE.getUserSettings().fragmentMetric = metric3.getText();
            Configs.USER_PROFILE.flush();
        }));
    }

    public Runnable changeShortcuts() {
        return () -> {
            ShortcutDialog dialog;
            try {
                this.activate.run();
                dialog = new ShortcutDialog(this.deactivate);
                dialog.setVisible(true);
            } catch (Exception exception) {
                this.deactivate.run();
                exception.printStackTrace();
            }
        };
    }

    @Override
    public void doDelayedLabels() {
        this.shortcuts.setText(String.format("Shortcuts (%s)", getKeyComboString(KeyShortcuts.Shortcut.SHORTCUTS)));
    }
}

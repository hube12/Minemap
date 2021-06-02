package kaptainwutax.minemap.ui.menubar;

import kaptainwutax.mcutils.util.math.DistanceMetric;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.KeyShortcuts;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.dialog.ShortcutDialog;
import kaptainwutax.minemap.ui.map.MapManager;
import kaptainwutax.minemap.ui.map.MapPanel;

import javax.swing.*;
import java.awt.event.KeyEvent;

import static kaptainwutax.minemap.config.KeyboardsConfig.getKeyComboString;

public class SettingsMenu extends Menu {
    private final JMenu metric;
    private final JMenu modifierKey;
    private final JMenuItem shortcuts;
    private final JCheckBoxMenuItem zoom;
    private final JCheckBoxMenuItem heightmap;
    private final JCheckBoxMenuItem flashing;
    private final JCheckBoxMenuItem disableStrongholds;

    public SettingsMenu() {
        this.menu = new JMenu("Settings");
        this.menu.setMnemonic(KeyEvent.VK_E);

        this.metric = new JMenu("Fragment Metric");
        this.addMetricGroup();

        this.modifierKey = new JMenu("Layer Switch Key");
        this.addModifierKeyGroup();

        this.zoom = new JCheckBoxMenuItem("Restrict Maximum Zoom");
        this.zoom.addChangeListener(e -> {
            Configs.USER_PROFILE.getUserSettings().restrictMaximumZoom = zoom.getState();
            Configs.USER_PROFILE.flush();
        });
        this.menu.addMenuListener(Events.Menu.onSelected(e -> this.zoom.setState(Configs.USER_PROFILE.getUserSettings().restrictMaximumZoom)));

        this.heightmap = new JCheckBoxMenuItem("Show heightmap");
        this.heightmap.addChangeListener(e -> {
            Configs.USER_PROFILE.getUserSettings().doHeightmap = heightmap.getState();
            Configs.USER_PROFILE.flush();
        });
        this.menu.addMenuListener(Events.Menu.onSelected(e -> this.heightmap.setState(Configs.USER_PROFILE.getUserSettings().doHeightmap)));

        this.flashing = new JCheckBoxMenuItem("Fast startup (Warning Flashes!)");
        this.flashing.addChangeListener(e -> {
            Configs.USER_PROFILE.getUserSettings().allowFlashing = this.flashing.getState();
            Configs.USER_PROFILE.flush();
        });

        this.disableStrongholds = new JCheckBoxMenuItem("Disable stronghold (Even faster!)");
        this.disableStrongholds.addChangeListener(e -> {
            Configs.USER_PROFILE.getUserSettings().disableStronghold = this.disableStrongholds.getState();
            MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            if (!this.disableStrongholds.getState() && map != null) {
                if (map.getContext() != null && map.getContext().getStarts() == null) {
                    map.getContext().calculateStarts(map);
                }
            }
            Configs.USER_PROFILE.flush();
        });

        this.menu.addMenuListener(Events.Menu.onSelected(e -> this.flashing.setState(Configs.USER_PROFILE.getUserSettings().allowFlashing)));

        this.shortcuts = new JMenuItem("Shortcuts");
        this.addMouseAndKeyListener(this.shortcuts, changeShortcuts(), changeShortcuts(), false);

        this.menu.add(this.metric);
        this.menu.add(this.modifierKey);
        this.menu.add(this.zoom);
        this.menu.add(this.heightmap);
        this.menu.add(this.flashing);
        this.menu.add(this.disableStrongholds);
        this.menu.add(this.shortcuts);
    }


    private void addMetricGroup() {
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

    private void addModifierKeyGroup() {
        ButtonGroup group = new ButtonGroup();
        JRadioButtonMenuItem ctrl = new JRadioButtonMenuItem("Ctrl");
        JRadioButtonMenuItem shift = new JRadioButtonMenuItem("Shift");
        JRadioButtonMenuItem alt = new JRadioButtonMenuItem("Alt");
        JRadioButtonMenuItem altgr = new JRadioButtonMenuItem("AltGr");
        JRadioButtonMenuItem meta = new JRadioButtonMenuItem("Meta");
        this.modifierKey.add(ctrl);
        this.modifierKey.add(shift);
        this.modifierKey.add(alt);
        this.modifierKey.add(altgr);
        this.modifierKey.add(meta);
        group.add(ctrl);
        group.add(shift);
        group.add(alt);
        group.add(altgr);
        group.add(meta);

        MapManager.ModifierDown m = Configs.USER_PROFILE.getUserSettings().modifierDown;
        switch (m) {
            case ALT_DOWN:
                alt.setSelected(true);
                break;
            case CTRL_DOWN:
                ctrl.setSelected(true);
                break;
            case META_DOWN:
                meta.setSelected(true);
                break;
            case SHIFT_DOWN:
                shift.setSelected(true);
                break;
            case ALT_GR_DOWN:
                altgr.setSelected(true);
                break;
        }

        alt.addMouseListener(Events.Mouse.onPressed(mouseEvent -> {
            Configs.USER_PROFILE.getUserSettings().modifierDown = MapManager.ModifierDown.ALT_DOWN;
            Configs.USER_PROFILE.flush();
        }));

        ctrl.addMouseListener(Events.Mouse.onPressed(mouseEvent -> {
            Configs.USER_PROFILE.getUserSettings().modifierDown = MapManager.ModifierDown.CTRL_DOWN;
            Configs.USER_PROFILE.flush();
        }));

        meta.addMouseListener(Events.Mouse.onPressed(mouseEvent -> {
            Configs.USER_PROFILE.getUserSettings().modifierDown = MapManager.ModifierDown.META_DOWN;
            Configs.USER_PROFILE.flush();
        }));
        altgr.addMouseListener(Events.Mouse.onPressed(mouseEvent -> {
            Configs.USER_PROFILE.getUserSettings().modifierDown = MapManager.ModifierDown.ALT_GR_DOWN;
            Configs.USER_PROFILE.flush();
        }));
        shift.addMouseListener(Events.Mouse.onPressed(mouseEvent -> {
            Configs.USER_PROFILE.getUserSettings().modifierDown = MapManager.ModifierDown.SHIFT_DOWN;
            Configs.USER_PROFILE.flush();
        }));
    }

    public Runnable changeShortcuts() {
        return () -> {
            this.activate.run();
            JDialog dialog = new ShortcutDialog(this.deactivate);
            dialog.setVisible(true);
        };
    }

    @Override
    public void doDelayedLabels() {
        this.shortcuts.setText(String.format("Shortcuts (%s)", getKeyComboString(KeyShortcuts.ShortcutAction.SHORTCUTS)));
    }
}

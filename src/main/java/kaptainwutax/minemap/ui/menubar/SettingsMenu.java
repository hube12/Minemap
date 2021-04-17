package kaptainwutax.minemap.ui.menubar;

import kaptainwutax.mcutils.util.math.DistanceMetric;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.KeyShortcuts;
import kaptainwutax.minemap.init.Logger;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.dialog.ShortcutDialog;
import kaptainwutax.minemap.ui.map.MapManager;
import kaptainwutax.mcutils.version.MCVersion;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;

import static kaptainwutax.minemap.config.KeyboardsConfig.getKeyComboString;

public class SettingsMenu extends Menu {
    private final JMenu lookMenu;
    private final JMenu styleMenu;
    private final JMenu metric;
    private final JMenu modifierKey;
    private final JCheckBoxMenuItem zoom;
    private final JMenuItem shortcuts;
    private final JMenuItem about;

    public SettingsMenu() {
        this.menu = new JMenu("Settings");

        this.lookMenu = new JMenu("UI Look");
        this.addLookGroup();

        this.styleMenu = new JMenu("Biome Style");
        this.addBiomeGroup();

        this.metric = new JMenu("Fragment Metric");
        this.addMetricGroup();

        this.modifierKey = new JMenu("Layer Switch Key");
        this.addModifierKeyGroup();

        this.zoom = new JCheckBoxMenuItem("Restrict Maximum Zoom");
        this.zoom.addChangeListener(e -> {
            Configs.USER_PROFILE.getUserSettings().restrictMaximumZoom = zoom.getState();
            Configs.USER_PROFILE.flush();
        });
        this.menu.addMenuListener(Events.Menu.onSelected(e -> {
            this.zoom.setState(Configs.USER_PROFILE.getUserSettings().restrictMaximumZoom);
        }));

        this.shortcuts = new JMenuItem("Shortcuts");
        this.shortcuts.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(changeShortcuts())));

        this.about = new JMenuItem("About Minemap");
        this.about.addMouseListener(Events.Mouse.onPressed(e -> {
            JFrame frame = new JFrame("About Minemap " + MineMap.version);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setPreferredSize(new Dimension(500, 400));
            JTextPane textArea = new JTextPane();
            textArea.setContentType("text/html");
            textArea.setEditable(false);
            textArea.setText(getAbout());
            textArea.setFont(new Font("Times", Font.PLAIN, 16));
            textArea.addHyperlinkListener(linkEvent -> {
                if (linkEvent.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                    if (Desktop.isDesktopSupported()) {
                        try {
                            Desktop.getDesktop().browse(linkEvent.getURL().toURI());
                        } catch (IOException | URISyntaxException error) {
                            Logger.LOGGER.warning(String.format("URL could not be opened for %s, error: %s", linkEvent.getURL(), error));
                        }
                    }
                }
            });

            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setPreferredSize(new Dimension(500, 400));

            frame.add(scrollPane);
            frame.pack();
            frame.setLocationRelativeTo(null); // center
            frame.setVisible(true);

        }));


        this.menu.add(this.lookMenu);
        this.menu.add(this.styleMenu);
        this.menu.add(this.metric);
        this.menu.add(this.modifierKey);
        this.menu.add(this.zoom);
        this.menu.add(this.shortcuts);
        this.menu.add(this.about);
    }

    public static String getAbout() {
        StringBuilder sb = new StringBuilder("<html><body>");
        sb.append("This is a program to replace the old amidst with a non Minecraft based one (meaning you can run it without Minecraft installed), ")
                .append("it is also way more efficient since it is fully multithreaded.")
                .append("<br>")
                .append("Minemap supports all official releases of Minecraft from 1.0 to ").append(MCVersion.values()[0].toString())
                .append("<br>")
                .append("The main core part was done by KaptainWutax.")
                .append("<br>")
                .append("The utilities and a lot of the improvements was done by Neil")
                .append("<br>")
                .append("<br>")
                .append("Contributors :<br>")
                .append("<ul>")
                .append("<li>KaptainWutax : Core part of the map system and libs setup</li>")
                .append("<li>Neil : libs enrichment + utilities in Minemap + rich icons</li>")
                .append("<li>Uniquepotatoes : Flat icons design</li>")
                .append("<li>Speedrunning and monkeys discord ppl : input on feature for Minemap</li>")
                .append("</ul>")
                .append("<br>")
                .append("<div style='text-align:center'><a href=\"https://github.com/hube12/Minemap\">Github Link</a></div>");

        return sb.append("</body></html>").toString();
    }

    private void addLookGroup() {
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

    private void addBiomeGroup() {
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
            ShortcutDialog dialog;
            try {
                this.activate.run();
                dialog = new ShortcutDialog(this.deactivate);
                dialog.setVisible(true);
            } catch (Exception exception) {
                this.deactivate.run();
                Logger.LOGGER.severe(exception.toString());
                exception.printStackTrace();
            }
        };
    }

    @Override
    public void doDelayedLabels() {
        this.shortcuts.setText(String.format("Shortcuts (%s)", getKeyComboString(KeyShortcuts.ShortcutAction.SHORTCUTS)));
    }
}

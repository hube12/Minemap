package com.seedfinding.minemap.ui.menubar;

import com.seedfinding.mccore.version.MCVersion;
import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.init.Configs;
import com.seedfinding.minemap.init.Logger;
import com.seedfinding.minemap.listener.Events;
import com.seedfinding.minemap.ui.dialog.IconSizeDialog;
import com.seedfinding.minemap.ui.map.MapPanel;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class AboutMenu extends Menu {

    private final JMenu lookMenu;
    private final JMenu styleMenu;
    private final JCheckBoxMenuItem hideDockableContainers;
    private final JMenuItem iconSize;
    private final JMenuItem settingsFolder;
    private final JMenuItem about;

    public AboutMenu() {
        this.menu = new JMenu("About");
        this.menu.setMnemonic(KeyEvent.VK_B);

        this.lookMenu = new JMenu("UI Look");
        this.addLookGroup();

        this.styleMenu = new JMenu("Biome Style");
        this.addBiomeGroup();

        this.hideDockableContainers = new JCheckBoxMenuItem("Hide dock arrows");
        this.hideDockableContainers.addChangeListener(e -> {
            Configs.USER_PROFILE.getUserSettings().hideDockableContainer = this.hideDockableContainers.getState();
            MapPanel mapPanel = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            if (mapPanel != null) {
                mapPanel.rightBar.searchBox.setVisible(!this.hideDockableContainers.getState());
                mapPanel.rightBar.chestBox.setVisible(!this.hideDockableContainers.getState());
            }
            Configs.USER_PROFILE.flush();
        });
        this.menu.addMenuListener(Events.Menu.onSelected(e -> this.hideDockableContainers.setState(Configs.USER_PROFILE.getUserSettings().hideDockableContainer)));

        this.iconSize = new JMenuItem("Change Icons Size");
        this.addMouseAndKeyListener(this.iconSize, iconSize(), iconSize(), true);

        this.settingsFolder = new JMenuItem("Open Settings Folder");
        this.addMouseAndKeyListener(this.settingsFolder, settingsFolder(), settingsFolder(), true);

        this.about = new JMenuItem("About Minemap");
        this.addMouseAndKeyListener(this.about, aboutPanel(), aboutPanel(), true);

        this.menu.add(this.lookMenu);
        this.menu.add(this.styleMenu);
        this.menu.add(this.hideDockableContainers);
        this.menu.add(this.iconSize);
        this.menu.add(this.settingsFolder);
        this.menu.add(this.about);
    }


    public static String getAbout() {
        @SuppressWarnings("StringBufferReplaceableByString ")
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


    public Runnable aboutPanel() {
        return () -> {
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
            textArea.setCaretPosition(0);
            DefaultCaret caret = (DefaultCaret) textArea.getCaret();
            caret.setUpdatePolicy(DefaultCaret.NEVER_UPDATE);
            JScrollPane scrollPane = new JScrollPane(textArea);
            scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
            scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            scrollPane.setPreferredSize(new Dimension(500, 400));

            frame.add(scrollPane);
            frame.pack();
            frame.setLocationRelativeTo(null); // center
            frame.setVisible(true);
        };
    }

    public Runnable iconSize() {
        return () -> {
            this.activate.run();
            JDialog dialog = new IconSizeDialog(this.deactivate);
            dialog.setVisible(true);
        };
    }

    public Runnable settingsFolder() {
        return () -> {
            Desktop desktop = Desktop.getDesktop();
            File dir = new File(MineMap.ROOT_DIR);
            if (!dir.exists()) return;
            try {
                desktop.open(dir);
            } catch (IOException e) {
                e.printStackTrace();
                Logger.LOGGER.warning("Settings folder could not be opened");
            }
        };
    }

    @Override
    public void doDelayedLabels() {

    }
}

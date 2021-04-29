package kaptainwutax.minemap.ui.menubar;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.KeyShortcuts;
import kaptainwutax.minemap.init.Logger;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.dialog.EnterSeedDialog;
import kaptainwutax.minemap.ui.map.MapPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedList;

import static kaptainwutax.minemap.config.KeyboardsConfig.getKeyComboString;

public class FileMenu extends Menu {
    private final JMenuItem loadSeed;
    private final JMenu recentSeeds;
    private final JMenuItem screenshot;
    private final JMenuItem screenshotFolder;
    private final JMenuItem close;
    public static boolean isClosing=false;

    public FileMenu() {
        this.menu = new JMenu("Home");
        this.menu.setMnemonic(KeyEvent.VK_H);

        this.loadSeed = new JMenuItem("New From Seed");
        this.addMouseAndKeyListener(this.loadSeed, newSeed(), newSeed(), true);

        this.recentSeeds = new JMenu("Recent Seeds");
        this.recentSeeds.addMenuListener(Events.Menu.onSelected(e->this.addRecentSeedGroup()));
        
        this.screenshot = new JMenuItem("Screenshot");
        this.addMouseAndKeyListener(this.screenshot, screenshot(), screenshot(), true);

        this.screenshotFolder = new JMenuItem("Open Screenshot Folder");
        this.addMouseAndKeyListener(this.screenshotFolder, screenshotFolder(), screenshotFolder(), true);

        this.close = new JMenuItem("Close");
        this.addMouseAndKeyListener(this.close, close(true), close(true), true);

        this.menu.addMenuListener(Events.Menu.onSelected(e -> screenshot.setEnabled(MineMap.INSTANCE.worldTabs.getSelectedMapPanel() != null)));

        this.menu.add(this.loadSeed);
        this.menu.add(this.recentSeeds);
        this.menu.add(this.screenshot);
        this.menu.add(this.screenshotFolder);
        this.menu.add(this.close);
    }

    public void addRecentSeedGroup(){
        this.recentSeeds.removeAll();
        for (long seed:Configs.USER_PROFILE.getRecentSeeds()){
            JMenuItem item=new JMenuItem(String.valueOf(seed));
            this.recentSeeds.add(item);
        }
    }

    public Runnable newSeed() {
        return () -> {
            this.activate.run();
            EnterSeedDialog dialog = new EnterSeedDialog(this.deactivate);
            dialog.setVisible(true);
        };
    }

    public Runnable screenshot() {
        return () -> {
            if (!this.screenshot.isEnabled()) return;

            MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            if (map == null) return;
            BufferedImage image = map.getScreenshot();

            String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            File dir = new File(MineMap.SCREENSHOTS_DIR);
            if (!dir.exists() && !dir.mkdirs()) {
                Logger.LOGGER.severe("Screenshot dir doesn't exists yet");
                return;
            }
            File file = new File(MineMap.SCREENSHOTS_DIR + File.separatorChar + fileName + ".png");
            try {
                ImageIO.write(image, "png", file);
            } catch (IOException e) {
                Logger.LOGGER.severe(e.toString());
                e.printStackTrace();
            }
        };
    }

    public Runnable screenshotFolder() {
        return () -> {
            Desktop desktop = Desktop.getDesktop();
            File dir = new File(MineMap.SCREENSHOTS_DIR);
            if (!dir.exists() && !dir.mkdirs()) return;
            try {
                desktop.open(dir);
            } catch (IOException e) {
                e.printStackTrace();
                Logger.LOGGER.warning("Screenshot folder could not be opened");
            }
        };
    }

    public Runnable close(boolean displayMsg) {
        return () -> {
            if (isClosing) return;
            int input;
            if (displayMsg) {
                isClosing=true;
                this.activate.run();
                input = JOptionPane.showConfirmDialog(null, "Do you want to close MineMap?", "Close MineMap", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                this.deactivate.run();
                isClosing=false;
            } else {
                input = 0;
            }
            if (input == 0) {
                for (Frame frame : JFrame.getFrames()) {
                    frame.dispose();
                }
                System.exit(0);
            }

        };
    }

    @Override
    public void doDelayedLabels() {
        this.loadSeed.setText(String.format("New From Seed (%s)", getKeyComboString(KeyShortcuts.ShortcutAction.NEW_SEED)));
        this.screenshot.setText(String.format("Screenshot (%s)", getKeyComboString(KeyShortcuts.ShortcutAction.SCREENSHOT)));
        this.close.setText(String.format("Close (%s)", getKeyComboString(KeyShortcuts.ShortcutAction.CLOSE)));
    }
}

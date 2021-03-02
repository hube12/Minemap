package kaptainwutax.minemap.ui.menubar;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.dialog.EnterSeedDialog;
import kaptainwutax.minemap.ui.map.MapPanel;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FileMenu extends Menu {
    private final JMenuItem screenshot;

    public FileMenu() {
        this.menu = new JMenu("Home");

        JMenuItem loadSeed = new JMenuItem("New From Seed... (Ctrl+N)");
        loadSeed.addMouseListener(Events.Mouse.onPressed(e -> newSeed().run())); // this needs to run immediately

        this.screenshot = new JMenuItem("Screenshot... (Ctrl+S)");
        this.screenshot.addMouseListener(Events.Mouse.onPressed(e -> screenshot().run())); // this needs to run immediately

        JMenuItem close = new JMenuItem("Close (Ctrl+C)");
        close.addMouseListener(Events.Mouse.onPressed(mouseEvent -> close(false).run())); // this needs to run immediately

        this.menu.addMenuListener(Events.Menu.onSelected(e -> screenshot.setEnabled(MineMap.INSTANCE.worldTabs.getSelectedMapPanel() != null)));

        this.menu.add(loadSeed);
        this.menu.add(this.screenshot);
        this.menu.add(close);
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
            File dir = new File("screenshots/");
            File file = new File("screenshots/" + fileName + ".png");
            if (!dir.exists() && !dir.mkdirs()) return;

            try {
                ImageIO.write(image, "png", file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
    }

    public Runnable close(boolean displayMsg) {
        return () -> {
            int input;
            if (displayMsg) {
                this.activate.run();
                input = JOptionPane.showConfirmDialog(null, "Do you want to close MineMap?", "Close MineMap", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                this.deactivate.run();
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

}

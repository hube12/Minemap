package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class Dialog extends JDialog {
    private Runnable runAtExit;

    public Dialog(String title, LayoutManager layout) {
        this.setModal(true);

        this.setTitle(title);
        this.getContentPane().setLayout(layout);
        try {
            this.initComponents();
        } catch (Exception e) {
            Logger.LOGGER.severe(e.toString());
            e.printStackTrace();
        }
        this.registerBindings();
        this.pack();

        this.setLocation(
                MineMap.INSTANCE.getX() + MineMap.INSTANCE.getWidth() / 2 - this.getWidth() / 2,
                MineMap.INSTANCE.getY() + MineMap.INSTANCE.getHeight() / 2 - this.getHeight() / 2
        );

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public abstract void initComponents() throws Exception;

    public void dispose() {
        super.dispose();
        runAtExit.run();
    }

    public void addExitProcedure(Runnable runnable) {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                runnable.run();
            }
        });
        runAtExit = runnable;
    }

    protected abstract void create();

    protected abstract void cancel();

    public void registerBindings() {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "continue");
        actionMap.put("continue", new ButtonContinue());

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "cancel");
        actionMap.put("cancel", new ButtonCancel());
    }

    public class ButtonContinue extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            create();
        }
    }

    public class ButtonCancel extends AbstractAction {
        @Override
        public void actionPerformed(ActionEvent e) {
            cancel();
        }
    }


}

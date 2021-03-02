package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.minemap.MineMap;

import javax.swing.*;
import java.awt.*;
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
            e.printStackTrace();
        }

        this.pack();

        this.setLocation(
                MineMap.INSTANCE.getX() + MineMap.INSTANCE.getWidth() / 2 - this.getWidth() / 2,
                MineMap.INSTANCE.getY() + MineMap.INSTANCE.getHeight() / 2 - this.getHeight() / 2
        );

        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
    }

    public abstract void initComponents() throws Exception;

    public void dispose(){
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
        runAtExit=runnable;
    }
}

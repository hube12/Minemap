package kaptainwutax.minemap.util.ui;

import javax.swing.*;
import java.awt.*;

public class ModalPopup extends JDialog {
    public ModalPopup(JFrame parent, String waitMsg) {
        JPanel p1 = new JPanel(new GridBagLayout());
        p1.add(new JLabel("<html><div style='text-align: center;'>" + waitMsg + "<br>Please wait...</div></html>"));
        this.setUndecorated(true);
        this.getContentPane().add(p1);
        this.pack();
        this.setLocationRelativeTo(parent);
        this.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        this.setModal(true);
    }
}

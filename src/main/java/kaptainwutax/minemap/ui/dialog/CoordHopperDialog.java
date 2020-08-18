package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.ui.map.MapPanel;
import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.*;
import java.awt.*;

public class CoordHopperDialog {
    
    public CoordHopperDialog() {
        super();
        initComponents();
    }

    private void cButtonPressed() {
        int X, Z;

        try {
            X = Integer.parseInt(enterX.getText().trim());
            Z = Integer.parseInt(enterZ.getText().trim());
        } catch (NumberFormatException e) {
            throw new RuntimeException();
        }

        MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
        if(map != null)map.manager.setCenterPos(X, Z);
        mainLogue.dispose();
    }

    private void initComponents() {
        //GEN-BEGIN:initComponents
        mainLogue = new JDialog();
        enterX = new JTextField();
        enterZ = new JTextField();
        PromptSupport.setPrompt("X",enterX);
        cButton = new JButton();
        cLabel = new JLabel();

        //======== mainLogue ========
        {
            mainLogue.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
            Container mainLogueContentPane = mainLogue.getContentPane();
            mainLogueContentPane.setLayout(new BorderLayout());

            //---- enterX ----
            enterX.setMinimumSize(new Dimension(100, 30));
            enterX.setPreferredSize(new Dimension(100, 30));
            PromptSupport.setPrompt("Z",enterZ);
            mainLogueContentPane.add(enterX, BorderLayout.LINE_START);

            //---- enterZ ----
            enterZ.setMinimumSize(new Dimension(100, 30));
            enterZ.setPreferredSize(new Dimension(100, 30));
            mainLogueContentPane.add(enterZ, BorderLayout.LINE_END);

            //---- cButton ----
            cButton.setText("jump to coordinates");
            cButton.addActionListener(e -> cButtonPressed());
            mainLogueContentPane.add(cButton, BorderLayout.PAGE_END);

            //---- cLabel ----
            cLabel.setText("Enter your coordinates here:");
            cLabel.setHorizontalTextPosition(SwingConstants.CENTER);
            mainLogueContentPane.add(cLabel, BorderLayout.PAGE_START);
            mainLogue.pack();
            mainLogue.setLocationRelativeTo(mainLogue.getOwner());
        }
        //GEN-END:initComponents
    }

    //GEN-BEGIN:variables
    public JDialog mainLogue;
    public JTextField enterX;
    public JTextField enterZ;
    public JButton cButton;
    public JLabel cLabel;
    //GEN-END:variables
}

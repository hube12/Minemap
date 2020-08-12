package kaptainwutax.minemap.ui;
import kaptainwutax.minemap.MineMap;
import org.jdesktop.swingx.prompt.PromptSupport;
import javax.swing.*;
import java.awt.*;

public class CoordHopper {
    public CoordHopper() {
        super();
        initComponents();
    }


    private void cButtonPressed() {
        double X,Z;
        try {
            X = Double.parseDouble(enterX.getText());
            Z = Double.parseDouble(enterZ.getText());
        } catch (NumberFormatException e) {
            throw new RuntimeException();
        }
        WorldTabs tabs = MineMap.INSTANCE.worldTabs;
        Component c = tabs.getComponentAt(tabs.getSelectedIndex());
        if((c instanceof MapPanel)){
            MapPanel map = (MapPanel)c;
            map.setcords(X,Z);
        }
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

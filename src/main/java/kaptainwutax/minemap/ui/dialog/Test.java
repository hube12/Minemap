package kaptainwutax.minemap.ui.dialog;



import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.KeyStroke;

public class Test extends JDialog implements ActionListener
{

    public static void main(String[] args) {
        new Test(null,"e","e");
    }
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    Box box_message;
    Box box_buttons;
    Container content_pane;

    JLabel lbl_message;
    JButton btn_yes;
    JButton btn_no;
    JButton btn_cancel;

    String message;

    int result;

    public Test(JFrame parent, String title, String message)
    {
        super(parent, title, true);
        if (parent != null) {
            setLocationRelativeTo(parent);
        } else {
            setLocationRelativeTo(null);
        }

        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        result = 0;


        createObjects();
        setLanguageElements();
        addItemsToLayout();
        pack();
        addCustomKeyMaps();
        setVisible(true);


        btn_yes.addFocusListener(new buttonfocusEventHandler());
    }

    private void setLanguageElements()
    {
        btn_yes.setText("Yes");
        btn_no.setText("No");
        btn_cancel.setText("Cancel");
    }

    private void createObjects()
    {
        content_pane = getContentPane();
        box_message = Box.createHorizontalBox();
        box_buttons = Box.createHorizontalBox();

        box_message.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        box_buttons.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        lbl_message = new JLabel("aaaasadfasdasdgsd");

        btn_yes = new JButton(new ResultYes());
        btn_no = new JButton(new ResultNo());
        btn_cancel = new JButton(new ResultNo());
    }

    private void addItemsToLayout()
    {
        content_pane.add(box_message);
        box_message.add(lbl_message);

        content_pane.add(box_buttons);
        box_buttons.add(btn_yes);
        box_buttons.add(Box.createRigidArea(new Dimension(10,10)));
        box_buttons.add(btn_no);
        box_buttons.add(Box.createRigidArea(new Dimension(10,10)));
        box_buttons.add(btn_cancel);
    }

    public int getResult()
    {
        return result;
    }

    private class ResultYes extends AbstractAction
    {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent arg0) {
            // TODO Auto-generated method stub
            result = 1;
            setVisible(false);
            dispose();
        }

    }

    private class ResultNo extends AbstractAction
    {

        /**
         *
         */
        private static final long serialVersionUID = 1L;

        @Override
        public void actionPerformed(ActionEvent e) {
            // TODO Auto-generated method stub
            result = 0;
            setVisible(false);
            dispose();
        }

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO Auto-generated method stub

    }

    //setVisible(false);
    //dispose();

    private void addCustomKeyMaps()
    {
        InputMap inputMap = getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getRootPane().getActionMap();

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "returnyes");
        actionMap.put("returnyes", new ResultYes());

        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "returnno");
        actionMap.put("returnno", new ResultNo());
    }

    private class buttonfocusEventHandler extends FocusAdapter {

        /** Checks buttons on dialog for focus
         * and makes that button the default
         *
         * @param evt Holds event
         */
        public void focusGained(FocusEvent evt) {

            JButton button = (JButton) evt.getSource();
            if (button instanceof JButton) getRootPane().setDefaultButton(button);

        }
    }  //  end buttonfocusEventHandler
}

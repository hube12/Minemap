import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TestOverlay {
    public static void main(String[] args) {
        JPanel panel = createPanel();
        JFrame frame = createFrame();
        frame.add(panel);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static JPanel createPanel() {
        JPanel mainPanel = new JPanel(){
            
        };
        mainPanel.setLayout(new OverlayLayout(mainPanel));

        JButton button = new JButton("Show Message");
        button.setAlignmentX(0.5f);
        button.setAlignmentY(0.5f);

        GLData data = new GLData();
        data.samples = 4;
        data.swapInterval = 0;
        AWTGLCanvas canvas = new MapCanvas(data);
        canvas.setPreferredSize(new Dimension(400, 400));

        JPanel popupPanel = createPopupPanel(button);
        popupPanel.setAlignmentX(0.1f);
        popupPanel.setAlignmentY(0.1f);

        button.addActionListener(e -> {
            button.setEnabled(false);
            popupPanel.setVisible(true);
        });

        mainPanel.add(popupPanel);
        mainPanel.add(button);
        mainPanel.add(canvas);
        mainPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                canvas.repaint();
            }
        });
        return mainPanel;
    }

    private static JPanel createPopupPanel(JComponent overlapComponent) {
        JPanel popupPanel = new JPanel(new BorderLayout());
        popupPanel.setOpaque(false);
        popupPanel.setMaximumSize(new Dimension(150, 70));
        popupPanel.setBorder(new LineBorder(Color.gray));
        popupPanel.setVisible(false);

        JLabel label = new JLabel("HI there!");
        popupPanel.add(wrapInPanel(label), BorderLayout.CENTER);

        JButton popupCloseButton = new JButton("Close");
        popupPanel.add(wrapInPanel(popupCloseButton), BorderLayout.SOUTH);

        popupCloseButton.addActionListener(e -> {
            overlapComponent.setEnabled(true);
            popupPanel.setVisible(false);
        });

        return popupPanel;
    }

    private static JPanel wrapInPanel(JComponent component) {
        JPanel jPanel = new JPanel();
        jPanel.setBackground(new Color(50, 210, 250, 150));
        jPanel.add(component);
        return jPanel;
    }


    private static JFrame createFrame() {
        JFrame frame = new JFrame("OverlayLayout Example");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(new Dimension(400, 300));
        return frame;
    }
}

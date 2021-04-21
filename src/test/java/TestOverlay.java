import kaptainwutax.mcutils.util.data.Pair;
import org.lwjgl.opengl.awt.GLData;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class TestOverlay {
    private MapCanvas canvas;

    public static void main(String[] args) {
//        Pair<JPanel, MapCanvas> panel = createPanel();
        Pair<JPanel, MapCanvas> panel = createCanvasPanel();
        MapCanvas canvas = panel.getSecond();
        JFrame frame = createFrame();
        frame.add(panel.getFirst());
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

//        JPanel panel2 = new JPanel() {
//            @Override
//            public void repaint() {
//                super.repaint();
//                System.out.println("called glass");
//            }
//
//            @Override
//            protected void paintComponent(Graphics g) {
//                super.paintComponent(g);
//
//                System.out.println("painted glass");
//                canvas.repaint();
//                g = canvas.getGraphics();
//                System.out.println(canvas.getSize());
//                g.setColor(Color.YELLOW);
//                g.fillRect(0, 0, 100, 100);
//
//
//            }
//        };
//        frame.setGlassPane(panel2);
//        frame.getGlassPane().setVisible(true);

    }

    private static Pair<JPanel, MapCanvas> createPanel() {
        JPanel mainPanel = new JPanel() {
            @Override
            public void paint(Graphics g) {
                super.paint(g);
                System.out.println("PAINT PARENT");
                g.setColor(Color.BLUE);
                g.fillRect(0, 0, 200, 200);
            }
        };

        JPanel overlayPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics graphics) {
                super.paintComponent(graphics);
                graphics.setXORMode(Color.BLACK);
                int cx = this.getWidth() / 2, cz = this.getHeight() / 2;
                graphics.fillRect(cx - 120, cz - 30, 240, 60);
                graphics.fillRect(cx - 30, cz - 120, 60, 240);
                graphics.setPaintMode();
            }
        };
        mainPanel.setLayout(new OverlayLayout(mainPanel));

        JButton button = new JButton("Show Message");
        button.setAlignmentX(0.5f);
        button.setAlignmentY(0.5f);

        Pair<JPanel, MapCanvas> canvasPanel = createCanvasPanel();

        JPanel popupPanel = createPopupPanel(button, canvasPanel.getSecond());
        popupPanel.setAlignmentX(0.1f);
        popupPanel.setAlignmentY(0.1f);

        button.addActionListener(e -> {
            button.setEnabled(false);
            popupPanel.setVisible(true);
            canvasPanel.getSecond().repaint();
        });

//        mainPanel.add(popupPanel);
//        mainPanel.add(button);
//        mainPanel.add(overlayPanel);
        mainPanel.add(canvasPanel.getFirst());
        // mainPanel.add(canvas);
        mainPanel.addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                canvasPanel.getFirst().repaint();
            }
        });
        return new Pair<>(mainPanel, canvasPanel.getSecond());
    }

    private static Pair<JPanel, MapCanvas> createCanvasPanel() {
        GLData data = new GLData();
        data.samples = 4;
        data.swapInterval = 0;
        MapCanvas canvas = new MapCanvas(data);
        canvas.setPreferredSize(new Dimension(400, 400));
        JPanel panel = new JPanel() {
            @Override
            public void repaint() {
                super.repaint();
                System.out.println("called");
            }

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                System.out.println("painted");

                canvas.render();
                System.out.println(canvas.getSize());
                g=canvas.getGraphics();
                g.setColor(Color.RED);
                g.fillRect(0, 0, 100, 100);


            }
        };


        panel.add(canvas);
        return new Pair<>(panel, canvas);
    }

    private static JPanel createPopupPanel(JComponent overlapComponent, MapCanvas canvas) {
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
            canvas.repaint();
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

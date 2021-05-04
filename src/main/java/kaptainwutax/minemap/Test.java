package kaptainwutax.minemap;


import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.structure.Stronghold;
import kaptainwutax.mcutils.version.MCVersion;
import org.jdesktop.swingx.HorizontalLayout;

import javax.swing.*;
import java.awt.*;

import static kaptainwutax.mcutils.state.Dimension.OVERWORLD;

public class Test extends JFrame {
    public Test() {
        super("Curve Editor");
        add(buildControlPanel(), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setResizable(false);

        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }


    public static void main(String[] args) {
        MCVersion version = MCVersion.v1_16;
        Stronghold stronghold = new Stronghold(version);
        BiomeSource biomeSource = BiomeSource.of(OVERWORLD, version, 1437905338718953247L);
        //System.out.println(Arrays.toString(Arrays.stream(stronghold.getAllStarts(biomeSource, new JRand(0L))).map(CPos::toBlockPos).toArray()));
        SwingUtilities.invokeLater(() -> new Test().setVisible(true));
    }

    public Component buildControlPanel() {
        return new ControlPanel();
    }

    static class ControlPanel extends JPanel {
        public ControlPanel() {
            super(new HorizontalLayout());
            for (int i = 0; i < 10; i++) {
                JSlider a = new JSlider() {
                    @Override
                    public void paint(Graphics g) {
                        super.paint(g);
                    }
                };
                a.setOrientation(SwingConstants.VERTICAL);

                this.add(a);
            }
        }
    }
}

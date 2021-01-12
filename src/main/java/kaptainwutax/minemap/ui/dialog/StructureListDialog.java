package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.feature.OWBastionRemnant;
import kaptainwutax.minemap.feature.OWFortress;
import kaptainwutax.minemap.feature.StructureHelper;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.component.Dropdown;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.MapManager;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.ui.map.MapSettings;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.pos.BPos;
import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class StructureListDialog extends Dialog {
    public Dropdown<StructureItem> structureItemDropdown;
    public JButton continueButton;
    public JTextField enterN;

    public StructureListDialog() {
        super("List N closest structures", new GridLayout(0, 1));
    }

    @Override
    public void initComponents() {


        MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
        if (map == null) return;
        MapContext context = map.getContext();
        MapSettings settings = context.getSettings();
        MapManager manager = map.getManager();
        ChunkRand chunkRand = new ChunkRand();
        List<Feature<?, ?>> features = settings.getAllFeatures();
        List<StructureItem> structureItems = features.stream()
                .filter(e -> e instanceof RegionStructure)
                .map(e -> new StructureItem((RegionStructure<?, ?>) e))
                .collect(Collectors.toList());

        this.structureItemDropdown = new Dropdown<>(structureItems);

        this.enterN = new JTextField("0");
        PromptSupport.setPrompt("Number of structures", this.enterN);

        this.enterN.addKeyListener(Events.Keyboard.onReleased(e -> {
            try {
                Integer.parseInt(this.enterN.getText().trim());
                this.continueButton.setEnabled(true);
            } catch(Exception _e) {
                this.continueButton.setEnabled(false);
            }
        }));


        this.continueButton = new JButton();
        this.continueButton.setText("Continue");

        this.continueButton.addMouseListener(Events.Mouse.onPressed(e -> {
            if (!this.isEnabled()) return;

            int n;
            try {
                n = Integer.parseInt(this.enterN.getText().trim());
            } catch(NumberFormatException _e) {
                return;
            }
            RegionStructure<?, ?> feature = this.structureItemDropdown.getSelected().getFeature();
            BPos centerPos = manager.getCenterPos();
            BiomeSource biomeSource = context.getBiomeSource();
            int dimCoeff = 0;
            if (feature instanceof OWBastionRemnant || feature instanceof OWFortress) {
                biomeSource = context.getBiomeSource(Dimension.NETHER);
                dimCoeff = 3;
            }
            List<BPos> bPosList = StructureHelper.getClosest(feature, centerPos, context.worldSeed, chunkRand, biomeSource, dimCoeff)
                    .sequential()
                    .limit(n)
                    .collect(Collectors.toList());
            this.dispose();
            TextArea area=new TextArea(Arrays.toString(bPosList.toArray()));
            JFrame frame = new JFrame(String.format("List of %d %s", n,feature.getName()));
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);


            JPanel contentPane = new JPanel();
            contentPane.add(area);
            contentPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            contentPane.setLayout(new CardLayout());
            frame.setContentPane(contentPane);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        }));

        this.getContentPane().add(this.enterN);
        this.getContentPane().add(this.structureItemDropdown);
        this.getContentPane().add(this.continueButton);
    }


    static class StructureItem {

        private final RegionStructure<?, ?> feature;

        StructureItem(RegionStructure<?, ?> feature) {
            this.feature = feature;
        }

        public RegionStructure<?, ?> getFeature() {
            return feature;
        }

        @Override
        public String toString() {
            return feature.getName();
        }
    }
}

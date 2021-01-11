package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.feature.StructureHelper;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.component.Dropdown;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.MapManager;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.ui.map.MapSettings;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.CPos;
import kaptainwutax.seedutils.mc.pos.RPos;
import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.function.IntUnaryOperator;
import java.util.stream.Collectors;

public class FeatureHopperDialog extends Dialog {
    public Dropdown<FeatureItem> featureDropdown;
    public JButton continueButton;


    public FeatureHopperDialog() {
        super("Go to Coordinates", new GridLayout(0, 1));
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
        List<FeatureItem> featureItems = features.stream()
                .filter(e -> e instanceof RegionStructure)
                .map(e -> new FeatureItem((RegionStructure<?, ?>) e))
                .collect(Collectors.toList());

        this.featureDropdown = new Dropdown<>(featureItems);
        this.continueButton = new JButton();
        this.continueButton.setText("Continue");

        this.continueButton.addMouseListener(Events.Mouse.onPressed(e -> {
            if (!this.isEnabled()) return;
            FeatureItem featureItem = this.featureDropdown.getSelected();
            BPos centerPos = manager.getCenterPos();

            List<BPos> bPosList= StructureHelper.getClosest(featureItem.getFeature(),centerPos,context.worldSeed,chunkRand,context.getBiomeSource())
                    .sequential()
                    .limit(1)
                    .collect(Collectors.toList());
            if (!bPosList.isEmpty()) {
                BPos bPos = bPosList.get(0);
                manager.setCenterPos(bPos.getX(),bPos.getZ());
            } else {
                System.out.println("Not found");
            }
            this.dispose();
        }));

        this.getContentPane().add(this.featureDropdown);
        this.getContentPane().add(this.continueButton);
    }


    static class FeatureItem {

        private final RegionStructure<?, ?> feature;

        FeatureItem(RegionStructure<?, ?> feature) {
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

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

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

public class StructureHopperDialog extends Dialog {
    public Dropdown<StructureItem> structureItemDropdown;
    public JButton continueButton;


    public StructureHopperDialog() {
        super("Go to Structure Coordinates", new GridLayout(0, 1));
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
        this.continueButton = new JButton();
        this.continueButton.setText("Continue");

        this.continueButton.addMouseListener(Events.Mouse.onPressed(e -> {
            if (!this.isEnabled()) return;
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
                    .limit(1)
                    .collect(Collectors.toList());
            if (!bPosList.isEmpty()) {
                BPos bPos = bPosList.get(0);
                manager.setCenterPos(bPos.getX(), bPos.getZ());
            } else {
                System.out.println("Not found");
            }
            this.dispose();
        }));

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
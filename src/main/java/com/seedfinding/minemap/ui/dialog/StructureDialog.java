package com.seedfinding.minemap.ui.dialog;

import com.seedfinding.mcbiome.source.BiomeSource;
import com.seedfinding.mccore.rand.ChunkRand;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.structure.EndCity;
import com.seedfinding.mcfeature.structure.RegionStructure;
import com.seedfinding.mcfeature.structure.Stronghold;
import com.seedfinding.mcfeature.structure.Structure;
import com.seedfinding.mcfeature.structure.generator.structure.EndCityGenerator;
import com.seedfinding.mcterrain.TerrainGenerator;
import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.feature.*;
import com.seedfinding.minemap.init.Configs;
import com.seedfinding.minemap.init.Features;
import com.seedfinding.minemap.listener.Events;
import com.seedfinding.minemap.ui.map.MapContext;
import com.seedfinding.minemap.ui.map.MapManager;
import com.seedfinding.minemap.ui.map.MapPanel;
import com.seedfinding.minemap.ui.map.MapSettings;
import com.seedfinding.minemap.util.data.Str;
import com.seedfinding.minemap.util.ui.interactive.Dropdown;
import one.util.streamex.StreamEx;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class StructureDialog extends Dialog {
    protected Dropdown<StructureItem> structureItemDropdown;
    protected JButton continueButton;
    protected JTextField enterN;
    protected MapPanel map;
    protected MapContext context;
    protected MapSettings settings;
    protected MapManager manager;
    protected ChunkRand chunkRand;

    public StructureDialog(String title, LayoutManager layout) {
        super(title, layout);
    }

    @Override
    public void initComponents() {
        this.map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
        if (this.map == null) return;

        this.context = map.getContext();
        this.settings = context.getSettings();
        this.manager = map.getManager();
        this.chunkRand = new ChunkRand();
        List<Feature<?, ?>> features = settings.getAllFeatures();

        List<StructureItem> structureItems = features.stream()
            .filter(e -> e instanceof RegionStructure || e instanceof Stronghold)
            .map(e -> new StructureItem((Structure<?, ?>) e))
            .collect(Collectors.toList());
        if (this.context.getDimension() == Dimension.END) {
            structureItems.add(new StructureItem((Structure<?, ?>) Features.getForVersion(this.context.getVersion()).get(EndCity.class), bPos -> {
                EndCityGenerator endCityGenerator = new EndCityGenerator(this.context.getVersion());
                if (!endCityGenerator.generate(this.context.getTerrainGenerator(), bPos.toChunkPos())) return false;
                return endCityGenerator.hasShip();
            }) {
                @Override
                public String toString() {
                    return "End city with Elytra";
                }
            });
        }

        this.structureItemDropdown = new Dropdown<>(structureItems);

        this.continueButton = new JButton();
        this.continueButton.setText("Continue");
        this.continueButton.addMouseListener(Events.Mouse.onPressed(e -> create()));
    }


    public List<BPos> getFeatures(int limit) {
        Structure<?, ?> feature = this.structureItemDropdown.getSelected().getFeature();
        Function<BPos, Boolean> filter = this.structureItemDropdown.getSelected().getFilter();
        if (!(feature instanceof RegionStructure || feature instanceof Stronghold)) return null;

        BPos centerPos = this.manager.getCenterPos();
        BiomeSource biomeSource = this.context.getBiomeSource();
        TerrainGenerator terrainGenerator = this.context.getTerrainGenerator();

        int dimCoeff = 0;
        if (feature instanceof OWBastionRemnant || feature instanceof OWFortress || feature instanceof OWNERuinedPortal) {
            biomeSource = this.context.getBiomeSource(Dimension.NETHER);
            terrainGenerator = this.context.getTerrainGenerator(Dimension.NETHER);
            dimCoeff = 3;
        }

        if (feature instanceof NEStronghold) {
            biomeSource = this.context.getBiomeSource(Dimension.OVERWORLD);
            terrainGenerator = this.context.getTerrainGenerator(Dimension.OVERWORLD);
            dimCoeff = -3;
        }

        long worldSeedWithSalt = this.context.worldSeed;
        if (Configs.SALTS.getSalt(this.context.version, feature.getName()) != null) {
            worldSeedWithSalt -= Configs.SALTS.getDefaultSalt(this.context.version, feature.getName());
            worldSeedWithSalt += Configs.SALTS.getSalt(this.context.version, feature.getName());
        }

        Stream<BPos> stream = StructureHelper.getClosest(feature, centerPos, worldSeedWithSalt, chunkRand, biomeSource, terrainGenerator, dimCoeff);
        if (stream == null) return null;
        int threads=Math.min(Configs.USER_PROFILE.getThreadCount(),limit);
        return StreamEx.of(stream).parallel(new ForkJoinPool(threads)).filter(e -> filter != null ? filter.apply(e) : true).limit(limit).collect(Collectors.toList());
    }


    protected void cancel() {
        continueButton.setEnabled(false);
        dispose();
    }

    static class StructureItem {

        private final Structure<?, ?> feature;
        private final Function<BPos, Boolean> filter;

        StructureItem(Structure<?, ?> feature) {
            this(feature, null);
        }

        StructureItem(Structure<?, ?> feature, Function<BPos, Boolean> filter) {
            this.feature = feature;
            this.filter = filter;
        }

        public Function<BPos, Boolean> getFilter() {
            return filter;
        }

        public Structure<?, ?> getFeature() {
            return feature;
        }

        @Override
        public String toString() {
            return Str.prettifyDashed(feature.getName());
        }
    }

}

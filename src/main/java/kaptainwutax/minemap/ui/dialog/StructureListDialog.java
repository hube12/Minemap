package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.EndCity;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.featureutils.structure.Stronghold;
import kaptainwutax.featureutils.structure.Structure;
import kaptainwutax.featureutils.structure.generator.structure.EndCityGenerator;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.feature.OWBastionRemnant;
import kaptainwutax.minemap.feature.OWFortress;
import kaptainwutax.minemap.feature.OWNERuinedPortal;
import kaptainwutax.minemap.feature.StructureHelper;
import kaptainwutax.minemap.init.Features;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.MapManager;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.ui.map.MapSettings;
import kaptainwutax.minemap.util.data.Str;
import kaptainwutax.minemap.util.ui.graphics.TpPanel;
import kaptainwutax.minemap.util.ui.interactive.Dropdown;
import kaptainwutax.terrainutils.TerrainGenerator;
import one.util.streamex.StreamEx;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static kaptainwutax.minemap.util.ui.interactive.Prompt.setPrompt;

public class StructureListDialog extends Dialog {
    public Dropdown<StructureItem> structureItemDropdown;
    public JButton continueButton;
    public JTextField enterN;
    private MapPanel map;
    private MapContext context;
    private MapSettings settings;
    private MapManager manager;
    private ChunkRand chunkRand;

    public StructureListDialog(Runnable onExit) {
        super("List N closest structures", new GridLayout(0, 1));
        this.addExitProcedure(onExit);
    }

    @Override
    public void initComponents() {
        map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
        if (map == null) return;

        context = map.getContext();
        settings = context.getSettings();
        manager = map.getManager();
        chunkRand = new ChunkRand();
        List<Feature<?, ?>> features = settings.getAllFeatures();

        List<StructureItem> structureItems = features.stream()
            .filter(e -> e instanceof RegionStructure || e instanceof Stronghold)
            .map(e -> new StructureItem((Structure<?, ?>) e))
            .collect(Collectors.toList());
        if (map.getContext().getDimension()==Dimension.END){
            structureItems.add(new StructureItem((Structure<?, ?>) Features.getForVersion(map.getContext().getVersion()).get(EndCity.class), bPos -> {
                EndCityGenerator endCityGenerator = new EndCityGenerator(map.getContext().getVersion());
                if (!endCityGenerator.generate(map.getContext().getTerrainGenerator(), bPos.toChunkPos())) return false;
                return endCityGenerator.hasShip();
            }) {
                @Override
                public String toString() {
                    return "End city with Elytra";
                }
            });
        }

        this.structureItemDropdown = new Dropdown<>(structureItems);

        this.enterN = new JTextField("1");
        setPrompt("Number of structures", this.enterN);

        this.enterN.addKeyListener(Events.Keyboard.onReleased(e -> {
            try {
                Integer.parseInt(this.enterN.getText().trim());
                this.continueButton.setEnabled(true);
            } catch (Exception _e) {
                this.continueButton.setEnabled(false);
            }
        }));

        this.continueButton = new JButton();
        this.continueButton.setText("Continue");
        this.continueButton.addMouseListener(Events.Mouse.onPressed(e -> create()));

        this.getContentPane().add(this.enterN);
        this.getContentPane().add(this.structureItemDropdown);
        this.getContentPane().add(this.continueButton);
    }


    protected void create() {
        if (!this.continueButton.isEnabled()) return;

        int n;
        try {
            n = Integer.parseInt(this.enterN.getText().trim());
        } catch (NumberFormatException _e) {
            JOptionPane.showMessageDialog(this, String.format("This is not a number: %s", this.enterN.getText().trim()));
            return;
        }

        if (n > 300 || n <= 0) {
            JOptionPane.showMessageDialog(this, String.format("You have chosen a number (%d) outside of the permitted range [1;300]", n));
            return;
        }

        Structure<?, ?> feature = this.structureItemDropdown.getSelected().getFeature();
        Function<BPos, Boolean> filter = this.structureItemDropdown.getSelected().getFilter();
        if (!(feature instanceof RegionStructure || feature instanceof Stronghold)) return;
        BPos centerPos = manager.getCenterPos();
        BiomeSource biomeSource = context.getBiomeSource();
        int dimCoeff = 0;
        if (feature instanceof OWBastionRemnant || feature instanceof OWFortress || feature instanceof OWNERuinedPortal) {
            biomeSource = context.getBiomeSource(Dimension.NETHER);
            dimCoeff = 3;
        }
        TerrainGenerator terrainGenerator = context.getTerrainGenerator();
        if (feature instanceof OWBastionRemnant || feature instanceof OWFortress || feature instanceof OWNERuinedPortal) {
            terrainGenerator = context.getTerrainGenerator(Dimension.NETHER);
        }

        Stream<BPos> stream = StructureHelper.getClosest(feature, centerPos, context.worldSeed, chunkRand, biomeSource, terrainGenerator, dimCoeff);
        assert stream != null;
        List<BPos> bPosList = StreamEx.of(stream)
            .sequential()
            .filter(e -> filter != null ? filter.apply(e) : true)
            .limit(n)
            .collect(Collectors.toList());

        // destroy the current container
        this.dispose();

        TpPanel.makeFrame(bPosList, feature);
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

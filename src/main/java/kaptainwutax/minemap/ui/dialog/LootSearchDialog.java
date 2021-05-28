package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.loot.ILoot;
import kaptainwutax.featureutils.loot.item.Item;
import kaptainwutax.featureutils.loot.item.ItemStack;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.featureutils.structure.Structure;
import kaptainwutax.featureutils.structure.generator.Generator;
import kaptainwutax.featureutils.structure.generator.Generators;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.feature.OWBastionRemnant;
import kaptainwutax.minemap.feature.OWFortress;
import kaptainwutax.minemap.feature.OWNERuinedPortal;
import kaptainwutax.minemap.feature.StructureHelper;
import kaptainwutax.minemap.feature.chests.Chests;
import kaptainwutax.minemap.feature.chests.Loot;
import kaptainwutax.minemap.init.Features;
import kaptainwutax.minemap.init.Logger;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.util.data.Str;
import kaptainwutax.minemap.util.ui.graphics.TpPanel;
import kaptainwutax.minemap.util.ui.interactive.Dropdown;
import kaptainwutax.terrainutils.TerrainGenerator;
import one.util.streamex.StreamEx;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static kaptainwutax.minemap.feature.chests.Loot.getSumWithPredicate;
import static kaptainwutax.minemap.util.ui.interactive.Prompt.setPrompt;


public class LootSearchDialog extends Dialog {

    public JTextField numberOfLoot;
    public Dropdown<Class<? extends Feature<?, ?>>> featureDropdown;
    public Dropdown<Item> itemDropdown;
    public JButton continueButton;
    public MapPanel map;

    public LootSearchDialog(Runnable onExit) {
        super("Find loot", new GridLayout(0, 1));
        this.addExitProcedure(onExit);
    }

    @Override
    public void initComponents() {
        this.numberOfLoot = new JTextField("1");
        setPrompt("Number of chest...", this.numberOfLoot);

        this.numberOfLoot.addKeyListener(Events.Keyboard.onReleased(e -> {
            try {
                Integer.parseInt(this.numberOfLoot.getText().trim());
                this.continueButton.setEnabled(true);
            } catch (Exception _e) {
                this.continueButton.setEnabled(false);
            }
        }));
        map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
        if (map == null) {
            Logger.LOGGER.severe("No map");
            SwingUtilities.invokeLater(this::dispose);
            return;
        }
        List<Class<? extends Feature<?, ?>>> features = Chests.getRegistry().keySet().stream().filter(e -> {
            Class<? extends Feature<?, ?>> superClazz = Chests.getSuperRegistry().get(e);
            if (superClazz == null) return false;
            if (Generators.get(superClazz) == null) return false;
            Feature<?, ?> f = Features.getForVersion(map.getContext().getVersion()).get(e);
            if (f == null) return false;
            return f.isValidDimension(map.getContext().getDimension());
        }).collect(Collectors.toList());
        if (features.size() == 0) {
            Logger.LOGGER.severe("No features");
            SwingUtilities.invokeLater(this::dispose);
            return;
        }

        this.featureDropdown = new Dropdown<>(e -> {
            try {
                Method c = e.getMethod("name");
                return Str.prettifyDashed((String) c.invoke(null));
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException noSuchMethodException) {
                noSuchMethodException.printStackTrace();
                Logger.LOGGER.severe("Impossible to get name for that feature");
            }
            return Feature.name();
        }, features);

        this.featureDropdown.addActionListener(e -> {
            Class<? extends Feature<?, ?>> feature = this.featureDropdown.getSelected();
            Class<? extends Feature<?, ?>> superFeature = Chests.getSuperRegistry().get(feature);
            if (superFeature == null) {
                Logger.LOGGER.severe("Missing super feature " + feature);
                return;
            }
            Generator.GeneratorFactory<?> factory = Generators.get(superFeature);
            if (factory == null) {
                Logger.LOGGER.severe("Missing factory " + feature);
                return;
            }
            Generator generator = factory.create(map.context.getVersion());
            this.continueButton.setEnabled(false);
            this.getContentPane().remove(this.itemDropdown);
            this.getContentPane().remove(this.continueButton);
            this.itemDropdown = new Dropdown<>(Item::getName, generator.getPossibleLootItems());
            this.getContentPane().add(this.itemDropdown);
            this.getContentPane().add(this.continueButton);
            this.revalidate();
            this.repaint();
            this.continueButton.setEnabled(true);
        });

        Class<? extends Feature<?, ?>> first = Chests.getSuperRegistry().get(features.get(0));
        if (first == null) {
            Logger.LOGGER.severe("Missing super feature " + features.get(0));
            SwingUtilities.invokeLater(this::dispose);
            return;
        }
        Generator generator = Generators.get(first).create(map.context.getVersion());
        this.itemDropdown = new Dropdown<>(i -> Str.prettifyDashed(i.getName()), generator.getPossibleLootItems());

        this.continueButton = new JButton();
        this.continueButton.setText("Continue");

        this.continueButton.addMouseListener(Events.Mouse.onPressed(e -> create()));

        this.getContentPane().add(this.numberOfLoot);
        this.getContentPane().add(this.featureDropdown);
        this.getContentPane().add(this.itemDropdown);
        this.getContentPane().add(this.continueButton);
    }

    protected void create() {
        if (!this.continueButton.isEnabled()) return;
        int n;
        try {
            n = Integer.parseInt(this.numberOfLoot.getText().trim());
        } catch (NumberFormatException _e) {
            return;
        }
        Class<? extends Feature<?, ?>> selected = this.featureDropdown.getSelected();
        Class<? extends Feature<?, ?>> superFeature = Chests.getSuperRegistry().get(selected);
        if (superFeature == null) {
            Logger.LOGGER.severe("Missing super feature " + selected);
            return;
        }
        if (map == null) {
            Logger.LOGGER.severe("Missing map");
            return;
        }
        Feature<?, ?> feature = Features.getForVersion(map.getContext().getVersion()).get(selected);
        if (!(feature instanceof ILoot)) return;
        BPos centerPos = map.manager.getCenterPos();
        BiomeSource biomeSource = map.context.getBiomeSource();
        TerrainGenerator chunkGenerator = map.context.getTerrainGenerator();
        int dimCoeff = 0;
        if (feature instanceof OWBastionRemnant || feature instanceof OWFortress || feature instanceof OWNERuinedPortal) {
            biomeSource = map.context.getBiomeSource(Dimension.NETHER);
            chunkGenerator = map.context.getTerrainGenerator(Dimension.NETHER);
            dimCoeff = 3;
        }
        // FIXME make it possible to use any feature (particulary for strongholds)
        if (!(feature instanceof RegionStructure)) return;
        Item selectedItem = this.itemDropdown.getSelected();
        Loot lootGen = Chests.get(selected).create();

        TerrainGenerator finalTerrainGenerator = chunkGenerator;
        long start = System.currentTimeMillis();
        ForkJoinPool forkJoinPool = new ForkJoinPool(Math.max(map.threadCount - 2, 1));
        Stream<BPos> stream = StructureHelper.getClosest((Structure<?, ?>) feature, centerPos, map.context.worldSeed, new ChunkRand(), biomeSource, dimCoeff);
        List<BPos> bPosList = StreamEx.of(Objects.requireNonNull(stream))
            .parallel(forkJoinPool)
            .takeWhile(e -> System.currentTimeMillis() <= start + 20000) // only 20 seconds
            .filter(e -> {
                List<List<ItemStack>> lists = lootGen.getLootAt(map.context.worldSeed, e.toChunkPos(), feature, false, finalTerrainGenerator, map.context.getVersion());
                return getSumWithPredicate(lists, i -> i.getItem().getName().equals(selectedItem.getName())) > 0;
            })
            .limit(n)
            .collect(Collectors.toList());
        this.dispose();
        TpPanel.makeFrame(bPosList, feature);
    }

    protected void cancel() {
        continueButton.setEnabled(false);
        dispose();
    }


}

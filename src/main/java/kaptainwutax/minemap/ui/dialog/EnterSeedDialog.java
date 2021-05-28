package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.map.MapSettings;
import kaptainwutax.minemap.util.data.Str;
import kaptainwutax.minemap.util.ui.graphics.HintTextField;
import kaptainwutax.minemap.util.ui.interactive.Dropdown;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.stream.IntStream;

public class EnterSeedDialog extends Dialog {

    public JTextField seedField;
    public JSpinner biomeSize;
    public JSpinner riverSize;
    public Dropdown<Integer> threadDropdown;
    public Dropdown<MCVersion> versionDropdown;
    public JButton continueButton;
    public JButton cancelButton;

    public EnterSeedDialog(Runnable onExit) {
        super("Load new Seed (<enter> to create)", new GridLayout(4, 2));
        this.addExitProcedure(onExit);
    }

    @Override
    public void initComponents() {
        int cores = Runtime.getRuntime().availableProcessors();

        this.seedField = new HintTextField("Enter your seed here...");

        this.threadDropdown = new Dropdown<>(i -> i + (i == 1 ? " thread" : " threads"), IntStream.rangeClosed(1, cores).boxed());
        this.threadDropdown.selectIfPresent(Configs.USER_PROFILE.getThreadCount(cores));

        this.versionDropdown = new Dropdown<>(Arrays.stream(MCVersion.values()).filter(v -> v.isNewerOrEqualTo(MCVersion.vb1_8_1)));
        this.versionDropdown.selectIfPresent(Configs.USER_PROFILE.getVersion());

        JSplitPane versionThreadSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.versionDropdown, this.threadDropdown);
        versionThreadSplit.setResizeWeight(0.5);
        versionThreadSplit.setEnabled(false);

        this.continueButton = new JButton("Continue");
        this.continueButton.addMouseListener(Events.Mouse.onPressed(e -> this.create()));

        this.cancelButton = new JButton("Cancel");
        this.cancelButton.addMouseListener(Events.Mouse.onPressed(e -> this.cancel()));

        JCheckBoxMenuItem[] checkBoxes = Arrays.stream(Dimension.values()).map(dimension -> {
            JCheckBoxMenuItem check = new JCheckBoxMenuItem("Load " + Str.prettifyDashed(dimension.getName()));
            check.setState(Configs.USER_PROFILE.isDimensionEnabled(dimension));
            check.addChangeListener(e -> Configs.USER_PROFILE.setDimensionState(dimension, check.getState()));
            return check;
        }).toArray(JCheckBoxMenuItem[]::new);

        MapSettings settings = Configs.USER_PROFILE.getDefaultMapSettings()
            .getOrDefault(Dimension.OVERWORLD.getName(), new MapSettings(Dimension.OVERWORLD));

        SpinnerModel biomeModel = new SpinnerNumberModel(settings.getBiomeSize().intValue(), 0, 32, 1);
        this.biomeSize  = new JSpinner(biomeModel);
        JComponent biomeEditor = new JSpinner.NumberEditor(this.biomeSize, "0");
        this.biomeSize.setEditor(biomeEditor);

        SpinnerModel riverModel = new SpinnerNumberModel(settings.getRiverSize().intValue(), 0, 32, 1);
        this.riverSize  = new JSpinner(riverModel);
        JComponent riverEditor = new JSpinner.NumberEditor(this.riverSize, "0");
        this.riverSize.setEditor(riverEditor);

        JSplitPane biomeSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JLabel("Biome Size"), this.biomeSize);
        biomeSplitPane.setResizeWeight(0.5);
        biomeSplitPane.setEnabled(false);
        JSplitPane riverSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, new JLabel("River Size"), this.riverSize);
        riverSplitPane.setResizeWeight(0.5);
        riverSplitPane.setEnabled(false);

        JSplitPane riverBiomeSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, biomeSplitPane, riverSplitPane);
        riverBiomeSplit.setResizeWeight(0.5);
        riverBiomeSplit.setEnabled(false);

        JPanel biomeRiverPanel = new JPanel();
        biomeRiverPanel.add(riverBiomeSplit);

        this.getContentPane().add(this.seedField);
        this.getContentPane().add(checkBoxes[Dimension.OVERWORLD.ordinal()]);
        this.getContentPane().add(versionThreadSplit);
        this.getContentPane().add(checkBoxes[Dimension.NETHER.ordinal()]);
        this.getContentPane().add(riverBiomeSplit);
        this.getContentPane().add(checkBoxes[Dimension.END.ordinal()]);
        this.getContentPane().add(this.continueButton);
        this.getContentPane().add(this.cancelButton);
    }

    protected void create() {
        continueButton.setEnabled(false);
        continueButton.setText("Loading...");
        MCVersion selectedVersion=versionDropdown.getSelected();

        MapSettings settings = Configs.USER_PROFILE.getDefaultMapSettings()
            .getOrDefault(Dimension.OVERWORLD.getName(), new MapSettings(Dimension.OVERWORLD))
            .copyFor(selectedVersion,Dimension.OVERWORLD);
        settings.setRiverSize((Integer) this.riverSize.getValue());
        settings.setBiomeSize((Integer) this.biomeSize.getValue());
        Configs.USER_PROFILE.setDefaultSettings(Dimension.OVERWORLD,settings);

        // Thread t = new Thread(() -> { // not a good idea overall
        MineMap.INSTANCE.worldTabs.load(versionDropdown.getSelected(), seedField.getText(),
            threadDropdown.getSelected(), Configs.USER_PROFILE.getEnabledDimensions());
        Configs.USER_PROFILE.setThreadCount(threadDropdown.getSelected());
        Configs.USER_PROFILE.setVersion(versionDropdown.getSelected());
        dispose();
        // },"Joe");
        // t.start();
//        try{
//            t.join();
//        }catch (Exception e){
//            Logger.LOGGER.severe(String.format("Failed to load seed with error %s",e));
//        }

    }

    protected void cancel() {
        continueButton.setEnabled(false);
        dispose();
    }
}

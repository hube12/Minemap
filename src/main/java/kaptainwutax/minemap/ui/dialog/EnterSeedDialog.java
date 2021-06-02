package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.Logger;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.component.TabGroup;
import kaptainwutax.minemap.ui.map.MapSettings;
import kaptainwutax.minemap.util.data.Assets;
import kaptainwutax.minemap.util.data.Str;
import kaptainwutax.minemap.util.ui.buttons.FileButton;
import kaptainwutax.minemap.util.ui.graphics.Graphic;
import kaptainwutax.minemap.util.ui.graphics.HintTextField;
import kaptainwutax.minemap.util.ui.interactive.Dropdown;
import org.jdesktop.swingx.VerticalLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.stream.IntStream;

public class EnterSeedDialog extends Dialog {

    public HintTextField seedField;
    public FileButton fileButton;
    public JSpinner biomeSize;
    public JSpinner riverSize;
    public Dropdown<Integer> threadDropdown;
    public Dropdown<MCVersion> versionDropdown;
    public JButton continueButton;
    public JButton cancelButton;
    public final ArrayList<String> seeds=new ArrayList<>();

    public EnterSeedDialog(Runnable onExit) {
        super("Load new Seed (<enter> to create)", new BorderLayout());
        this.addExitProcedure(onExit);
        this.setResizable(false);
    }

    @Override
    public void initComponents() {
        int cores = Runtime.getRuntime().availableProcessors();

        this.seedField = new HintTextField("Enter your seed here...");
        this.fileButton = new FileButton(16, 1, 1.7F, false, Color.WHITE, false);
        this.fileButton.setMinimumSize(new java.awt.Dimension(16, 16));
        JSplitPane seedInput = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, this.seedField, this.fileButton);

        seedInput.setEnabled(false);
        seedInput.setResizeWeight(1);

        this.fileButton.addActionListener(e -> {
            File chosenFile = Assets.choseFile(this);
            seeds.clear();
            if (chosenFile != null) {
                try {
                    Scanner myReader = new Scanner(chosenFile);
                    while (myReader.hasNextLine()) {
                        String data = myReader.nextLine();
                        seeds.add(data);
                    }
                    myReader.close();
                } catch (FileNotFoundException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                    Logger.LOGGER.severe("File could not be opened "+e);
                }
            }
            if (!seeds.isEmpty()){
                fileButton.shouldBackground(true);
                fileButton.setBackgroundColor(Color.GREEN);
                fileButton.repaint();
                this.seedField.setHint("File was successfully loaded");
            }else{
                fileButton.shouldBackground(true);
                fileButton.setBackgroundColor(Color.RED);
                fileButton.repaint();
                this.seedField.setHint("File is invalid...");
                Graphic.scheduleAction(3000,()->{
                    this.seedField.setHint("Enter your seed here...");
                    fileButton.shouldBackground(false);
                    fileButton.repaint();
                });
            }
            this.revalidate();
            this.repaint();
        });


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
        this.biomeSize = new JSpinner(biomeModel);
        JComponent biomeEditor = new JSpinner.NumberEditor(this.biomeSize, "0");
        this.biomeSize.setEditor(biomeEditor);

        SpinnerModel riverModel = new SpinnerNumberModel(settings.getRiverSize().intValue(), 0, 32, 1);
        this.riverSize = new JSpinner(riverModel);
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

        JPanel sideA = new JPanel(new VerticalLayout(3));
        sideA.add(seedInput);
        sideA.add(versionThreadSplit);
        sideA.add(riverBiomeSplit);
        sideA.add(this.continueButton);

        JPanel sideB = new JPanel();
        sideB.setLayout(new BoxLayout(sideB, BoxLayout.Y_AXIS));
        sideB.add(checkBoxes[Dimension.OVERWORLD.ordinal()]);
        sideB.add(checkBoxes[Dimension.NETHER.ordinal()]);
        sideB.add(checkBoxes[Dimension.END.ordinal()]);
        sideB.add(this.cancelButton);
        this.cancelButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sideA, sideB);
        splitPane.setEnabled(false);
        splitPane.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.getContentPane().add(splitPane);
    }

    private void doPreparation() {
        continueButton.setEnabled(false);
        continueButton.setText("Loading...");
        MCVersion selectedVersion = versionDropdown.getSelected();

        MapSettings settings = Configs.USER_PROFILE.getDefaultMapSettings()
            .getOrDefault(Dimension.OVERWORLD.getName(), new MapSettings(Dimension.OVERWORLD))
            .copyFor(selectedVersion, Dimension.OVERWORLD);
        settings.setRiverSize((Integer) this.riverSize.getValue());
        settings.setBiomeSize((Integer) this.biomeSize.getValue());
        Configs.USER_PROFILE.setDefaultSettings(Dimension.OVERWORLD, settings);
        Configs.USER_PROFILE.setThreadCount(threadDropdown.getSelected());
        Configs.USER_PROFILE.setVersion(versionDropdown.getSelected());
    }

    protected void create() {
        doPreparation();
        if (!seeds.isEmpty()){
            int index=0;
            for (String seed:seeds){
                MineMap.INSTANCE.worldTabs.load(versionDropdown.getSelected(), seed,
                    threadDropdown.getSelected(), Configs.USER_PROFILE.getEnabledDimensions(),false);
            }
            String text=seedField.getText();
            if (text==null || text.equals("")){
                dispose();
                return;
            }
        }
        TabGroup tabGroup=MineMap.INSTANCE.worldTabs.load(versionDropdown.getSelected(), seedField.getText(),
            threadDropdown.getSelected(), Configs.USER_PROFILE.getEnabledDimensions());
        if (tabGroup==null){
            String title=this.getTitle();
            this.setTitle("Seed/options is invalid or already exists");
            Color bg=this.seedField.getBackground();
            this.seedField.setBackground(Color.RED);
            Graphic.scheduleAction(3000,()->{
                this.setTitle(title);
                this.seedField.setBackground(bg);
            });
            continueButton.setEnabled(true);
            continueButton.setText("Continue");

        }else{
            dispose();
        }

    }

    protected void cancel() {
        continueButton.setEnabled(false);
        dispose();
    }
}

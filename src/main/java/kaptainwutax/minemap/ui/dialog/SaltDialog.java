package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.Logger;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.mcutils.version.MCVersion;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import static kaptainwutax.minemap.util.data.Str.prettifyDashed;

public class SaltDialog extends Dialog {

    // those are cursed but what could I do? Inject myself between super and initcomponent?
    public static Callable<MCVersion> versionCallable = () -> MineMap.INSTANCE.worldTabs.getSelectedMapPanel() != null ? MineMap.INSTANCE.worldTabs.getSelectedMapPanel().context.version : MCVersion.values()[0];
    public static Callable<Integer> numberSaltsCallable = () -> Configs.SALTS.getSalts(versionCallable.call()).size();
    public ArrayList<JSpinner> salts;
    public ArrayList<JLabel> saltsNames;
    public JButton continueButton;
    public JButton resetButton;
    private MCVersion version;
    private int numberSalts;

    public SaltDialog(Runnable onExit) throws Exception {
        super("Change salts", new GridLayout(numberSaltsCallable.call() + 1, 1));
        this.addExitProcedure(onExit);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, (Configs.SALTS.getSalts(MineMap.INSTANCE.worldTabs.getSelectedMapPanel() != null ? MineMap.INSTANCE.worldTabs.getSelectedMapPanel().context.version : MCVersion.values()[0]).size() + 1) * 30);
    }

    @Override
    public void initComponents() {
        this.salts = new ArrayList<>();
        this.saltsNames = new ArrayList<>();
        numberSalts = 0;
        version = MCVersion.values()[0];
        try {
            numberSalts = SaltDialog.numberSaltsCallable.call();
            version = SaltDialog.versionCallable.call();
        } catch (Exception e) {
            Logger.LOGGER.severe(e.toString());
            e.printStackTrace();
        }

        Configs.SALTS.getSalts(version).forEach((name, value) -> {
                    if (value != null) {
                        JLabel saltName = new JLabel(prettifyDashed(name) + " salt");
                        saltName.setHorizontalAlignment(JLabel.CENTER);
                        SpinnerModel saltModel = new SpinnerNumberModel(value.intValue(), Integer.MIN_VALUE, Integer.MAX_VALUE, 1);
                        JSpinner saltSpinner = new JSpinner(saltModel);
                        JComponent saltEditor = new JSpinner.NumberEditor(saltSpinner, "0");
                        saltSpinner.setEditor(saltEditor);
                        this.saltsNames.add(saltName);
                        this.salts.add(saltSpinner);
                        this.getContentPane().add(saltName);
                        this.getContentPane().add(saltSpinner);
                    }
                }
        );


        this.continueButton = new JButton("Continue");
        this.continueButton.addMouseListener(Events.Mouse.onPressed(e -> create()));
        this.resetButton = new JButton("Reset salts for current version");

        this.resetButton.addMouseListener(Events.Mouse.onPressed(e -> {
            Configs.SALTS.resetOverrides(version);
            Configs.SALTS.flush();
            MineMap.INSTANCE.worldTabs.invalidateAll();
            this.resetButton.setEnabled(false);
            this.dispose();
        }));
        this.getContentPane().add(this.continueButton);
        this.getContentPane().add(this.resetButton);
    }

    protected void create() {
        assert (numberSalts == salts.size());
        assert (numberSalts == saltsNames.size());
        for (int i = 0; i < numberSalts; i++) {
            String name = saltsNames.get(i).getText().split(" salt")[0];
            try {
                String previous = ((JSpinner.NumberEditor) salts.get(i).getEditor()).getTextField().getText();
                salts.get(i).commitEdit();
                if (!previous.equals(salts.get(i).getValue().toString())) {
                    JOptionPane.showMessageDialog(this, name + " has an incorrect value, you should " +
                            "only use valid numbers we changed " + "from " + previous + " to " + salts.get(i).getValue().toString());
                }
            } catch (ParseException parseException) {
                JOptionPane.showMessageDialog(this, name + " has an incorrect value, you should only use numbers");
            }
            Integer value = (Integer) salts.get(i).getModel().getValue();
            Configs.SALTS.addOverrideEntry(version, name, value);
        }
        Configs.SALTS.flush();
        MineMap.INSTANCE.worldTabs.invalidateAll();
        this.continueButton.setEnabled(false);
        this.dispose();
    }

    protected void cancel() {
        continueButton.setEnabled(false);
        dispose();
    }
}

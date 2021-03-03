package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.KeyShortcuts;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.component.Dropdown;
import kaptainwutax.seedutils.mc.MCVersion;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class ShortcutDialog extends Dialog {

    public ArrayList<JSpinner> salts;
    public ArrayList<JLabel> saltsNames;
    public JButton continueButton;
    public JButton resetButton;

    public ShortcutDialog(Runnable onExit) {
        super("Change shorcuts", new GridLayout(Configs.KEYBOARDS.getKEYBOARDS().size()+1, 5));
        this.addExitProcedure(onExit);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, (Configs.KEYBOARDS.getKEYBOARDS().size() + 1) * 30);
    }

    @Override
    public void initComponents() {
        Configs.KEYBOARDS.getShortcuts().forEach((key, action) -> {
                    if (action != null) {
                        KeyShortcuts.KeyRegister keyRegister= KeyShortcuts.KeyRegister.initFromString(key);
                        JLabel actionName = new JLabel(action.toString());
                        actionName.setHorizontalAlignment(JLabel.CENTER);
                        Dropdown<KeyShortcuts.KeyRegister.Type> typeDropdown= new Dropdown<>(KeyShortcuts.KeyRegister.Type.values());
                        typeDropdown.setDefault(keyRegister.getType());
                        Dropdown<KeyShortcuts.KeyRegister.Modifier> modifierDropDown= new Dropdown<>(KeyShortcuts.KeyRegister.Modifier.values());
                        modifierDropDown.setDefault(keyRegister.getModifier());
                        Dropdown<KeyShortcuts.KeyRegister.KeyLocation> keyLocationDropdown= new Dropdown<>(KeyShortcuts.KeyRegister.KeyLocation.values());
                        keyLocationDropdown.setDefault(keyRegister.getKeyLocation());
                        JTextField keyText=new JTextField(keyRegister.getKeyText());
                        keyText.setHorizontalAlignment(SwingConstants.CENTER);

                        this.getContentPane().add(actionName);
                        this.getContentPane().add(typeDropdown);
                        this.getContentPane().add(typeDropdown);
                        this.getContentPane().add(modifierDropDown);
                        this.getContentPane().add(keyLocationDropdown);
                        this.getContentPane().add(keyText);
                    }
                }
        );
        this.continueButton = new JButton("Continue");

        this.continueButton.addMouseListener(Events.Mouse.onPressed(e -> {
            
            Configs.KEYBOARDS.flush();
            MineMap.INSTANCE.toolbarPane.doDelayedLabels();
            this.continueButton.setEnabled(false);
            this.dispose();
        }));
        this.resetButton = new JButton("Reset shortcuts");

        this.resetButton.addMouseListener(Events.Mouse.onPressed(e -> {
            Configs.KEYBOARDS.resetOverrides();
            Configs.KEYBOARDS.flush();
            MineMap.INSTANCE.toolbarPane.doDelayedLabels();
            this.resetButton.setEnabled(false);
            this.dispose();
        }));
        this.getContentPane().add(new JLabel());
        this.getContentPane().add(this.continueButton);
        this.getContentPane().add(new JLabel());
        this.getContentPane().add(this.resetButton);
        this.getContentPane().add(new JLabel());
    }

}

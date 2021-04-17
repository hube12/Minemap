package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.KeyShortcuts;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.component.Dropdown;
import kaptainwutax.mcutils.util.data.Pair;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ShortcutDialog extends Dialog {

    public ArrayList<KeyShortcuts.ShortcutAction> shortcutActions;
    public ArrayList<Pair<Dropdown<?>[], JTextField>> keyShortcuts;
    public JButton continueButton;
    public JButton resetButton;

    public ShortcutDialog(Runnable onExit) {
        super("Change shorcuts", new GridLayout(Configs.KEYBOARDS.getKEYBOARDS().size() + 1, 4));
        this.addExitProcedure(onExit);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(600, (Configs.KEYBOARDS.getKEYBOARDS().size() + 1) * 30);
    }

    @Override
    public void initComponents() {
        this.shortcutActions = new ArrayList<>();
        this.keyShortcuts = new ArrayList<>();
        Configs.KEYBOARDS.getShortcuts().forEach((action, key) -> {
                    if (action != null) {
                        KeyShortcuts.KeyRegister keyRegister = KeyShortcuts.KeyRegister.initFromString(key);
                        JLabel actionName = new JLabel(action.toString());
                        actionName.setHorizontalAlignment(JLabel.CENTER);
                        Dropdown<KeyShortcuts.KeyRegister.Type> typeDropdown = new Dropdown<>(KeyShortcuts.KeyRegister.Type.values());
                        typeDropdown.setDefault(keyRegister.getType());
                        Dropdown<KeyShortcuts.KeyRegister.Modifier> modifierDropDown = new Dropdown<>(KeyShortcuts.KeyRegister.Modifier.values());
                        modifierDropDown.setDefault(keyRegister.getModifier());
                        Dropdown<KeyShortcuts.KeyRegister.KeyLocation> keyLocationDropdown = new Dropdown<>(KeyShortcuts.KeyRegister.KeyLocation.values());
                        keyLocationDropdown.setDefault(keyRegister.getKeyLocation());
                        JTextField keyText = new JTextField(keyRegister.getKeyText());
                        keyText.setHorizontalAlignment(SwingConstants.CENTER);

                        keyShortcuts.add(new Pair<>(new Dropdown<?>[] {typeDropdown, modifierDropDown, keyLocationDropdown}, keyText));
                        shortcutActions.add(action);

                        this.getContentPane().add(actionName);
                        this.getContentPane().add(typeDropdown);
                        this.getContentPane().add(modifierDropDown);
                        this.getContentPane().add(keyLocationDropdown);
                        this.getContentPane().add(keyText);
                    }
                }
        );
        this.continueButton = new JButton("Continue");

        this.continueButton.addMouseListener(Events.Mouse.onPressed(e -> create()));
        this.resetButton = new JButton("Reset shortcuts");

        this.resetButton.addMouseListener(Events.Mouse.onPressed(e -> {
            Configs.KEYBOARDS.resetOverrides();
            Configs.KEYBOARDS.flush();
            KeyShortcuts.deRegisterShortcuts();
            KeyShortcuts.registerShortcuts();
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

    protected void create() {
        int numberShortcuts = Configs.KEYBOARDS.getKEYBOARDS().size();
        assert (numberShortcuts == shortcutActions.size());
        assert (numberShortcuts == keyShortcuts.size());
        for (int i = 0; i < numberShortcuts; i++) {
            Pair<Dropdown<?>[], JTextField> keyShortcut = keyShortcuts.get(i);
            KeyShortcuts.ShortcutAction action = shortcutActions.get(i);
            KeyShortcuts.KeyRegister.Type type = (KeyShortcuts.KeyRegister.Type) keyShortcut.getFirst()[0].getSelected();
            KeyShortcuts.KeyRegister.Modifier modifier = (KeyShortcuts.KeyRegister.Modifier) keyShortcut.getFirst()[1].getSelected();
            KeyShortcuts.KeyRegister.KeyLocation keylocation = (KeyShortcuts.KeyRegister.KeyLocation) keyShortcut.getFirst()[2].getSelected();
            Configs.KEYBOARDS.addOverrideEntry(action, new KeyShortcuts.KeyRegister(keyShortcut.getSecond().getText(), type, modifier, keylocation));
        }
        Configs.KEYBOARDS.flush();
        KeyShortcuts.deRegisterShortcuts();
        KeyShortcuts.registerShortcuts();
        MineMap.INSTANCE.toolbarPane.doDelayedLabels();
        this.continueButton.setEnabled(false);
        this.dispose();
    }

    protected void cancel() {
        continueButton.setEnabled(false);
        dispose();
    }
}

package kaptainwutax.minemap.ui.menubar;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.KeyShortcuts;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.dialog.StructureListDialog;
import kaptainwutax.minemap.ui.map.MapPanel;

import javax.swing.*;
import java.awt.event.KeyEvent;

import static kaptainwutax.minemap.config.KeyboardsConfig.getKeyComboString;

public class UtilitiesMenu extends Menu {
    public JMenuItem structureSeedMode;
    public JMenuItem listStructure;

    public UtilitiesMenu() {
        this.menu = new JMenu("Utilities");
        this.menu.setMnemonic(KeyEvent.VK_U);

        this.listStructure = new JMenuItem("List N Structures");
        this.addMouseAndKeyListener(this.listStructure, getNStructure(), getNStructure(), false);

        this.structureSeedMode = new JCheckBoxMenuItem("Structure Seed Mode");
        this.addMouseAndKeyListener(this.structureSeedMode, toggleStructureMode(false), toggleStructureMode(true), true);
        ;
        this.structureSeedMode.setSelected(Configs.USER_PROFILE.getUserSettings().structureMode);

        this.menu.addMenuListener(Events.Menu.onSelected(e -> {
            MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            listStructure.setEnabled(map != null);
        }));

        this.menu.add(listStructure);
        this.menu.add(structureSeedMode);
    }

    public Runnable toggleStructureMode(boolean isKeyboard) {
        return () -> {
            if (isKeyboard) this.structureSeedMode.setSelected(!this.structureSeedMode.isSelected());
            if (!this.structureSeedMode.isEnabled()) return;
            boolean hasJustBeenSelected = !this.structureSeedMode.isSelected();
            Configs.USER_PROFILE.getUserSettings().structureMode = hasJustBeenSelected;
            Configs.USER_PROFILE.flush();
            MineMap.INSTANCE.worldTabs.invalidateAll();
            if (MineMap.INSTANCE.toolbarPane.structureSeedModePopup == null) {
                MineMap.INSTANCE.toolbarPane = new MenuBar();
                System.out.println("This should not happen");
                return;
            }
            MineMap.INSTANCE.toolbarPane.structureSeedModePopup.setVisible(hasJustBeenSelected);
        };
    }

    public Runnable getNStructure() {
        return () -> {
            if (!this.listStructure.isEnabled()) return;
            this.activate.run();
            JDialog dialog = new StructureListDialog(this.deactivate);
            dialog.setVisible(true);
        };
    }

    @Override
    public void doDelayedLabels() {
        this.structureSeedMode.setText(String.format("Structure Seed Mode (%s)", getKeyComboString(KeyShortcuts.ShortcutAction.TOGGLE_STS_MODE)));
    }
}

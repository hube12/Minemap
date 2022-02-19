package com.seedfinding.minemap.ui.menubar;

import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.config.KeyboardsConfig;
import com.seedfinding.minemap.init.Configs;
import com.seedfinding.minemap.init.KeyShortcuts;
import com.seedfinding.minemap.init.Logger;
import com.seedfinding.minemap.listener.Events;
import com.seedfinding.minemap.ui.dialog.LootSearchDialog;
import com.seedfinding.minemap.ui.dialog.StructureListDialog;
import com.seedfinding.minemap.ui.map.MapPanel;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.logging.Level;

public class UtilitiesMenu extends Menu {
    public JMenuItem structureSeedMode;
    public JMenuItem listStructure;
    public JMenuItem lootSearch;

    public UtilitiesMenu() {
        this.menu = new JMenu("Utilities");
        this.menu.setMnemonic(KeyEvent.VK_U);

        this.listStructure = new JMenuItem("List N Structures");
        this.addMouseAndKeyListener(this.listStructure, getNStructure(), getNStructure(), false);

        this.lootSearch = new JMenuItem("Find Chest Loot");
        this.addMouseAndKeyListener(this.lootSearch, getLoot(), getLoot(), false);

        this.structureSeedMode = new JCheckBoxMenuItem("Structure Seed Mode");
        this.addMouseAndKeyListener(this.structureSeedMode, toggleStructureMode(false), toggleStructureMode(true), true);
        this.structureSeedMode.setSelected(Configs.USER_PROFILE.getUserSettings().structureMode);

        this.menu.addMenuListener(Events.Menu.onSelected(e -> {
            MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            listStructure.setEnabled(map != null);
            lootSearch.setEnabled(map != null);
        }));

        this.menu.add(listStructure);
        this.menu.add(lootSearch);
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
                Logger.LOGGER.log(Level.WARNING,"Toggling happened before definition, critical path");
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

    public Runnable getLoot() {
        return () -> {
            if (!this.lootSearch.isEnabled()) return;
            this.activate.run();
            JDialog dialog = new LootSearchDialog(this.deactivate);
            dialog.setVisible(true);
        };
    }

    @Override
    public void doDelayedLabels() {
        this.structureSeedMode.setText(String.format("Structure Seed Mode (%s)", KeyboardsConfig.getKeyComboString(KeyShortcuts.ShortcutAction.TOGGLE_STS_MODE)));
    }
}

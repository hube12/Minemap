package kaptainwutax.minemap.ui.menubar;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.KeyShortcuts;
import kaptainwutax.minemap.init.Logger;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.dialog.StructureListDialog;
import kaptainwutax.minemap.ui.map.MapPanel;

import javax.swing.*;

import static kaptainwutax.minemap.config.KeyboardsConfig.getKeyComboString;

public class UtilitiesMenu extends Menu {
    public JMenuItem structureSeedMode;
    public JMenuItem listStructure;

    public UtilitiesMenu() {
        this.menu = new JMenu("Utilities");

        this.listStructure = new JMenuItem("List N Structures");
        this.listStructure.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(getNStructure())));

        this.structureSeedMode = new JCheckBoxMenuItem("Structure Seed Mode");
        this.structureSeedMode.addActionListener(e -> toggleStructureMode(false).run());
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
            Configs.USER_PROFILE.getUserSettings().structureMode = this.structureSeedMode.isSelected();
            Configs.USER_PROFILE.flush();
            MineMap.INSTANCE.worldTabs.invalidateAll();
            if (MineMap.INSTANCE.toolbarPane.structureSeedModePopup == null) {
                MineMap.INSTANCE.toolbarPane = new MenuBar();
                System.out.println("This should not happen");
                return;
            }
            MineMap.INSTANCE.toolbarPane.structureSeedModePopup.setVisible(this.structureSeedMode.isSelected());
        };
    }

    public Runnable getNStructure() {
        return () -> {
            if (!this.listStructure.isEnabled()) return;
            StructureListDialog dialog;
            try {
                this.activate.run();
                dialog = new StructureListDialog(this.deactivate);
                dialog.setVisible(true);
            } catch (Exception exception) {
                this.deactivate.run();
                Logger.LOGGER.severe(exception.toString());
                exception.printStackTrace();
            }
        };
    }

    @Override
    public void doDelayedLabels() {
        this.structureSeedMode.setText(String.format("Structure Seed Mode (%s)", getKeyComboString(KeyShortcuts.ShortcutAction.TOGGLE_STS_MODE)));
    }
}

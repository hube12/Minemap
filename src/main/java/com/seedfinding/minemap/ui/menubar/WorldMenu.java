package com.seedfinding.minemap.ui.menubar;

import com.seedfinding.mccore.rand.seed.WorldSeed;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mcfeature.misc.SpawnPoint;
import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.config.KeyboardsConfig;
import com.seedfinding.minemap.init.KeyShortcuts;
import com.seedfinding.minemap.init.Logger;
import com.seedfinding.minemap.listener.Events;
import com.seedfinding.minemap.ui.dialog.CoordHopperDialog;
import com.seedfinding.minemap.ui.dialog.SaltDialog;
import com.seedfinding.minemap.ui.dialog.StructureHopperDialog;
import com.seedfinding.minemap.ui.map.MapPanel;
import com.seedfinding.minemap.ui.map.icon.IconRenderer;
import com.seedfinding.minemap.ui.map.icon.SpawnIcon;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.Collections;

public class WorldMenu extends Menu {
    private final JMenuItem goToCoords;
    private final JMenuItem goToSpawn;
    private final JMenuItem loadShadowSeed;
    private final JMenuItem goToStructure;
    private final JMenuItem changeSalts;

    public WorldMenu() {
        this.menu = new JMenu("World");
        this.menu.setMnemonic(KeyEvent.VK_W);

        this.goToCoords = new JMenuItem("Go to Coordinates");
        this.addMouseAndKeyListener(this.goToCoords, goToCoords(), goToCoords(), false);

        this.goToSpawn = new JMenuItem("Go to Spawn");
        this.addMouseAndKeyListener(this.goToSpawn, goToSpawn(), goToSpawn(), false);

        this.goToStructure = new JMenuItem("Go to Structure");
        this.addMouseAndKeyListener(this.goToStructure, goToStructure(), goToStructure(), false);

        this.loadShadowSeed = new JMenuItem("Load Shadow Seed");
        this.addMouseAndKeyListener(this.loadShadowSeed, loadShadowSeed(), loadShadowSeed(), true);

        this.goToCoords.setEnabled(false);
        this.goToSpawn.setEnabled(false);
        this.loadShadowSeed.setEnabled(false);
        this.goToStructure.setEnabled(false);

        this.menu.addMenuListener(Events.Menu.onSelected(e -> {
            MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            this.goToCoords.setEnabled(map != null);
            this.goToSpawn.setEnabled(map != null && this.getActiveSpawn() != null);
            this.loadShadowSeed.setEnabled(map != null && map.getContext().dimension == Dimension.OVERWORLD);
            this.goToStructure.setEnabled(map != null);
        }));

        this.changeSalts = new JMenuItem("Change Salts");
        this.addMouseAndKeyListener(this.changeSalts, changeSalts(), changeSalts(), false);

        this.menu.add(goToCoords);
        this.menu.add(goToSpawn);
        this.menu.add(goToStructure);
        this.menu.add(loadShadowSeed);
        this.menu.add(changeSalts);
    }

    public Runnable goToCoords() {
        return () -> {
            MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            this.goToCoords.setEnabled(map != null);
            if (!this.goToCoords.isEnabled()) return;
            this.activate.run();
            JDialog jumpDialogue = new CoordHopperDialog(this.deactivate);
            jumpDialogue.setVisible(true);
        };
    }

    public Runnable goToSpawn() {
        return () -> {
            MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            this.goToSpawn.setEnabled(map != null && this.getActiveSpawn() != null);
            if (!this.goToSpawn.isEnabled()) return;
            BPos pos = this.getActiveSpawn();
            if (pos != null) {
                MineMap.INSTANCE.worldTabs.getSelectedMapPanel().getManager().setCenterPos(pos.getX(), pos.getZ());
            }
        };
    }

    public Runnable loadShadowSeed() {
        return () -> {
            MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            this.loadShadowSeed.setEnabled(map != null && map.getContext().dimension == Dimension.OVERWORLD);
            if (!this.loadShadowSeed.isEnabled() || map == null) return;
            MineMap.INSTANCE.worldTabs.load(
                map.getContext().version,
                String.valueOf(WorldSeed.getShadowSeed(map.getContext().worldSeed)),
                map.threadCount, Collections.singletonList(map.getContext().dimension));
        };
    }

    public Runnable goToStructure() {
        return () -> {
            MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            this.goToStructure.setEnabled(map != null);
            if (!this.goToStructure.isEnabled()) return;
            this.activate.run();
            JDialog dialog = new StructureHopperDialog(this.deactivate);
            dialog.setVisible(true);
        };
    }

    public Runnable changeSalts() {
        return () -> {
            SaltDialog dialog;
            try {
                this.activate.run();
                dialog = new SaltDialog(this.deactivate);
                dialog.setVisible(true);
            } catch (Exception exception) {
                Logger.LOGGER.severe(exception.toString());
                this.deactivate.run();
                exception.printStackTrace();
            }
        };
    }

    private BPos getActiveSpawn() {
        MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
        IconRenderer icon = map.getContext().getIconManager().getFor(SpawnPoint.class);
        return icon instanceof SpawnIcon ? ((SpawnIcon) icon).getPos() : null;
    }

    @Override
    public void doDelayedLabels() {
        this.goToCoords.setText(String.format("Go to Coordinates (%s)", KeyboardsConfig.getKeyComboString(KeyShortcuts.ShortcutAction.GO_TO_COORDS)));
        this.goToSpawn.setText(String.format("Go to Spawn (%s)", KeyboardsConfig.getKeyComboString(KeyShortcuts.ShortcutAction.GO_TO_SPAWN)));
        this.goToStructure.setText(String.format("Go to Structure (%s)", KeyboardsConfig.getKeyComboString(KeyShortcuts.ShortcutAction.GO_TO_STRUCTURE)));
        this.loadShadowSeed.setText(String.format("Load Shadow Seed (%s)", KeyboardsConfig.getKeyComboString(KeyShortcuts.ShortcutAction.LOAD_SHADOW_SEED)));
        this.changeSalts.setText(String.format("Change Salts (%s)", KeyboardsConfig.getKeyComboString(KeyShortcuts.ShortcutAction.CHANGE_SALTS)));
    }
}

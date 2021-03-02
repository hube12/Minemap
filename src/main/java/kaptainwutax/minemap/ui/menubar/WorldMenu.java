package kaptainwutax.minemap.ui.menubar;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.feature.SpawnPoint;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.dialog.CoordHopperDialog;
import kaptainwutax.minemap.ui.dialog.SaltDialog;
import kaptainwutax.minemap.ui.dialog.StructureHopperDialog;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.ui.map.icon.IconRenderer;
import kaptainwutax.minemap.ui.map.icon.SpawnIcon;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.seed.WorldSeed;

import javax.swing.*;
import java.util.Collections;

public class WorldMenu extends Menu {
    private final JMenuItem goToCoords;
    private final JMenuItem goToSpawn;
    private final JMenuItem loadShadowSeed;
    private final JMenuItem goToStructure;

    public WorldMenu() {
        this.menu = new JMenu("World");

        this.goToCoords = new JMenuItem("Go to Coordinates (Alt+G)");
        this.goToCoords.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(goToCoords()))); // this can wait

        this.goToSpawn = new JMenuItem("Go to Spawn (Alt+P)");
        this.goToSpawn.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(goToSpawn()))); // this can wait

        this.loadShadowSeed = new JMenuItem("Load Shadow Seed (Alt+L)");
        this.loadShadowSeed.addMouseListener(Events.Mouse.onPressed(e -> loadShadowSeed().run())); // this needs to run immediately

        this.goToStructure = new JMenuItem("Go to Structure (Alt+S)");
        this.goToStructure.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(goToStructure()))); // this can wait

        goToCoords.setEnabled(false);
        goToSpawn.setEnabled(false);
        loadShadowSeed.setEnabled(false);
        goToStructure.setEnabled(false);

        this.menu.addMenuListener(Events.Menu.onSelected(e -> {
            MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            goToCoords.setEnabled(map != null);
            goToSpawn.setEnabled(map != null && this.getActiveSpawn() != null);
            loadShadowSeed.setEnabled(map != null && map.getContext().dimension == Dimension.OVERWORLD);
            goToStructure.setEnabled(map != null);
        }));

        JMenuItem changeSalts = new JMenuItem("Change Salts (Alt+C)");
        changeSalts.addMouseListener(Events.Mouse.onPressed(e -> SwingUtilities.invokeLater(changeSalts())));

        this.menu.add(goToCoords);
        this.menu.add(goToSpawn);
        this.menu.add(goToStructure);
        this.menu.add(loadShadowSeed);
        this.menu.add(changeSalts);
    }

    public Runnable goToCoords() {
        return () -> {
            if (!goToCoords.isEnabled()) return;
            this.activate.run();
            JDialog jumpDialogue = new CoordHopperDialog(this.deactivate);
            jumpDialogue.setVisible(true);
        };
    }

    public Runnable goToSpawn() {
        return () -> {
            if (!goToSpawn.isEnabled()) return;
            BPos pos = this.getActiveSpawn();
            if (pos != null) {
                MineMap.INSTANCE.worldTabs.getSelectedMapPanel().getManager().setCenterPos(pos.getX(), pos.getZ());
            }
        };
    }

    public Runnable loadShadowSeed() {
        return () -> {
            if (!loadShadowSeed.isEnabled()) return;
            MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
            MineMap.INSTANCE.worldTabs.load(
                    map.getContext().version,
                    String.valueOf(WorldSeed.getShadowSeed(map.getContext().worldSeed)),
                    map.threadCount, Collections.singletonList(map.getContext().dimension));
        };
    }

    public Runnable goToStructure() {
        return () -> {
            if (!goToStructure.isEnabled()) return;
            this.activate.run();
            JDialog jumpDialogue = new StructureHopperDialog(this.deactivate);
            jumpDialogue.setVisible(true);
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

}

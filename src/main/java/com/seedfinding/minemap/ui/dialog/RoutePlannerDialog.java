package com.seedfinding.minemap.ui.dialog;

import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.minemap.init.Logger;
import com.seedfinding.minemap.listener.Events;
import com.seedfinding.minemap.ui.map.MapManager;
import com.seedfinding.minemap.ui.map.MapPanel;
import com.seedfinding.minemap.ui.map.tool.Route;
import com.seedfinding.minemap.util.data.Str;
import com.seedfinding.minemap.util.tsp.RoutePlanner;
import com.seedfinding.minemap.util.ui.graphics.TpPanel;
import com.seedfinding.minemap.util.ui.interactive.Prompt;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 * Picks the N closest structures of a type, orders them into a short route that starts at the map center,
 * lists the stops in visiting order, with a button to draw the route on the map.
 */
public class RoutePlannerDialog extends StructureDialog {
    private static final int MAX_STOPS = 100;
    private JCheckBox returnToStart;

    public RoutePlannerDialog(Runnable onExit) {
        super("Plan a route through N closest structures", new GridLayout(0, 1));
        this.addExitProcedure(onExit);
    }

    @Override
    public void initComponents() {
        super.initComponents();

        this.enterN = new JTextField("5");
        Prompt.setPrompt("Number of structures", this.enterN);
        this.enterN.addKeyListener(Events.Keyboard.onReleased(e -> this.continueButton.setEnabled(parseN() > 0)));

        this.returnToStart = new JCheckBox("Return to the starting point");

        this.getContentPane().add(this.enterN);
        this.getContentPane().add(this.structureItemDropdown);
        this.getContentPane().add(this.returnToStart);
        this.getContentPane().add(this.continueButton);
    }

    private int parseN() {
        try {
            return Integer.parseInt(this.enterN.getText().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    @Override
    protected void create() {
        if (!this.continueButton.isEnabled()) return;

        int n = this.parseN();
        if (n < 2 || n > MAX_STOPS) {
            JOptionPane.showMessageDialog(this, String.format("Choose a number of structures in [2;%d] (got \"%s\")", MAX_STOPS, this.enterN.getText().trim()));
            return;
        }

        List<BPos> stops = getFeatures(n);
        if (stops == null || stops.isEmpty()) {
            JOptionPane.showMessageDialog(this, "We could not find any feature that match your requirement, report this if you think it is a bug.");
            Logger.LOGGER.info("Could not find a valid feature for the route");
            return;
        }

        BPos center = this.manager.getCenterPos();
        BPos start = new BPos(center.getX(), 0, center.getZ());
        boolean loop = this.returnToStart.isSelected();
        RoutePlanner.Result result = RoutePlanner.plan(start, stops, loop);

        String name = Str.prettifyDashed(this.structureItemDropdown.getSelected().getFeature().getName());
        Route route = new Route(result.order(), name + " route");
        MapManager routeManager = this.manager;
        MapPanel routeMap = this.map;
        TpPanel.MapToggle toggle = new TpPanel.MapToggle() {
            @Override
            public boolean isShown() {
                return routeManager.toolsList.contains(route);
            }

            @Override
            public void onRemoved(Runnable listener) {
                route.addRemovalListener(listener);
            }

            @Override
            public void toggle() {
                if (isShown()) routeManager.removeTool(route);
                else routeManager.toolsList.add(route);
                routeMap.rightBar.tooltip.updateToolsMetrics();
                routeMap.repaint();
            }
        };

        // stops in visiting order, without the start and the optional return leg
        List<BPos> ordered = result.order().subList(1, loop ? result.order().size() - 1 : result.order().size());
        String title = String.format("%s route: %d stops, %.0f blocks", name, ordered.size(), result.length());

        this.dispose();
        TpPanel.makeFrame(ordered, this.structureItemDropdown.getSelected().getFeature(), title, toggle);
    }
}

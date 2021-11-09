package com.seedfinding.minemap.config;

import com.google.gson.annotations.Expose;
import com.seedfinding.mccore.util.math.DistanceMetric;
import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.ui.map.MapManager;

public class UserSettings {

    @Expose
    public String style = BiomeColorsConfig.DEFAULT_STYLE_NAME;
    @Expose
    public MineMap.LookType look = MineMap.LookType.DARCULA;
    @Expose
    public boolean restrictMaximumZoom = true;
    @Expose
    public boolean doHeightmap = false;
    @Expose
    public String fragmentMetric = "Euclidean";
    @Expose
    public boolean structureMode = false;
    @Expose
    public boolean allowFlashing = false;
    @Expose
    public boolean disableStronghold = false;
    @Expose
    public boolean hideDockableContainer = false;
    @Expose
    public MapManager.ModifierDown modifierDown = MapManager.ModifierDown.CTRL_DOWN;

    public DistanceMetric getFragmentMetric() {
        switch (this.fragmentMetric) {
            case "Euclidean":
                return DistanceMetric.EUCLIDEAN_SQ;
            case "Manhattan":
                return DistanceMetric.MANHATTAN;
            case "Chebyshev":
                return DistanceMetric.CHEBYSHEV;
        }
        return null;
    }

}

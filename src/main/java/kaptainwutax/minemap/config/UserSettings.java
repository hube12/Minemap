package kaptainwutax.minemap.config;

import com.google.gson.annotations.Expose;
import kaptainwutax.mcutils.util.math.DistanceMetric;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.ui.map.MapManager;

public class UserSettings {

    @Expose
    public String style = BiomeColorsConfig.DEFAULT_STYLE_NAME;
    @Expose
    public MineMap.LookType look = MineMap.LookType.DARCULA;
    @Expose
    public boolean restrictMaximumZoom = true;
    @Expose
    public String fragmentMetric = "Euclidean";
    @Expose
    public boolean structureMode = false;
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

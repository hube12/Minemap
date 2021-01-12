package kaptainwutax.minemap.config;

import com.google.gson.annotations.Expose;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.seedutils.util.math.DistanceMetric;

public class UserSettings {

    @Expose public String style = BiomeColorsConfig.DEFAULT_STYLE_NAME;
    @Expose public MineMap.LookType look = MineMap.LookType.DARCULA;
    @Expose public boolean restrictMaximumZoom = true;
    @Expose public String fragmentMetric = "Euclidean";

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

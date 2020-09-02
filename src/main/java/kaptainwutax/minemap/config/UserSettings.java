package kaptainwutax.minemap.config;

import com.google.gson.annotations.Expose;
import kaptainwutax.seedutils.util.math.DistanceMetric;

public class UserSettings {

    @Expose public String style = BiomeColorsConfig.DEFAULT_STYLE_NAME;
    @Expose public boolean restrictMaximumZoom = true;
    @Expose public String fragmentMetric = "Euclidean";

    public DistanceMetric getFragmentMetric() {
        if(this.fragmentMetric.equals("Euclidean"))return DistanceMetric.EUCLIDEAN_SQ;
        else if(this.fragmentMetric.equals("Manhattan"))return DistanceMetric.MANHATTAN;
        else if(this.fragmentMetric.equals("Chebyshev"))return DistanceMetric.CHEBYSHEV;
        return null;
    }

}

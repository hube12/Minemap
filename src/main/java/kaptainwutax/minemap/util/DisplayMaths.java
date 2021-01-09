package kaptainwutax.minemap.util;

import kaptainwutax.seedutils.mc.pos.BPos;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;

public class DisplayMaths {
    public static double getAngle(Pair<BPos, BPos> pair) {
        double deltaY=pair.getFirst().getZ() - pair.getSecond().getZ();
        double deltaX= pair.getFirst().getX() - pair.getSecond().getX();
        double angle = Math.toDegrees(Math.atan2(deltaY,deltaX));
        return angle<0?angle+360:angle;
    }

    /*
    Get the offset depending of the angle between 2 points, for instance if angle is 180deg then offset should be 0
    If the angle is 90deg then clearly the two are in diagonal so we should give the maximum offset
     */
    public static double getOffset(double angleBetween, int maxOffset) {
        // make sure the angle is in [0;180]
        angleBetween=angleBetween%180;
        if (angleBetween < 0) {
            angleBetween += 180;
        }
        // shift to [-90;90]
        angleBetween -= 90;
        // shift to [0;90]
        angleBetween = Math.abs(angleBetween);
        // map the range such as [0;90]->[maxOffset;0]
        return reduceToRange(0, 90, maxOffset, 0).apply(angleBetween);
    }

    public static Function<Double, Double> reduceToRange(double minSourceRange, double maxSourceRange, double minTargetRange, double maxTargetRange) {
        if (minSourceRange == maxSourceRange) {
            throw new ArithmeticException("Can not map such range");
        }
        return t -> minTargetRange + (maxTargetRange - minTargetRange) / (maxSourceRange - minSourceRange) * (t - minSourceRange);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}

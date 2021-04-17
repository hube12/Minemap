package kaptainwutax.minemap.util.math;


import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.util.math.DistanceMetric;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DisplayMaths {
    public static final BiPredicate<CPos, List<BPos>> DEFAULT_CPOS_BPOS = (c, l) -> l.contains(c.toBlockPos().add(9, 0, 9));

    public static double getAngle(Pair<BPos, BPos> pair) {
        double deltaY = pair.getFirst().getZ() - pair.getSecond().getZ();
        double deltaX = pair.getFirst().getX() - pair.getSecond().getX();
        double angle = Math.toDegrees(Math.atan2(deltaY, deltaX));
        return angle < 0 ? angle + 360 : angle;
    }

    /*
    Get the offset depending of the angle between 2 points, for instance if angle is 180deg then offset should be 0
    If the angle is 90deg then clearly the two are in diagonal so we should give the maximum offset
     */
    public static double getOffset(double angleBetween, int maxOffset) {
        // make sure the angle is in [0;180]
        angleBetween = angleBetween % 180;
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

    public static double polygonArea(List<BPos> bPosList) {
        double area = 0.0;
        int j = bPosList.size() - 1;
        for (int i = 0; i < bPosList.size(); i++) {
            area += (bPosList.get(j).getX() + bPosList.get(i).getX()) * (bPosList.get(j).getZ() - bPosList.get(i).getZ());
            j = i;
        }
        return Math.abs(area / 2.0);
    }

    public static double circleArea(BPos pos1, BPos pos2) {
        return Math.PI * getDistance2DSquared(pos1, pos2);
    }

    public static double getDistance2D(BPos pos1, BPos pos2) {
        return DistanceMetric.EUCLIDEAN.getDistance(
                pos1.getX() - pos2.getX(),
                pos1.getY() - pos2.getY(),
                pos1.getZ() - pos2.getZ()
        );
    }

    public static double getDistance2DSquared(BPos pos1, BPos pos2) {
        return DistanceMetric.EUCLIDEAN_SQ.getDistance(
                pos1.getX() - pos2.getX(),
                pos1.getY() - pos2.getY(),
                pos1.getZ() - pos2.getZ()
        );
    }

    public static Polygon getPolygon(BPos pos1, BPos pos2, BPos pos3, BPos pos4) {
        return new Polygon(
                new int[] {
                        pos1.getX(),
                        pos2.getX(),
                        pos3.getX(),
                        pos4.getX(),

                        },
                new int[] {
                        pos1.getZ(),
                        pos2.getZ(),
                        pos3.getZ(),
                        pos4.getZ(),
                        },
                4
        );
    }

    public static Polygon getPolygon(BPos pos1, BPos pos2, BPos pos3) {
        return new Polygon(
                new int[] {
                        pos1.getX(),
                        pos2.getX(),
                        pos3.getX(),

                        },
                new int[] {
                        pos1.getZ(),
                        pos2.getZ(),
                        pos3.getZ(),
                        },
                3
        );
    }

    public static Polygon getPolygon(BPos pos1, BPos pos2, int maxOffset) {
        double angle = DisplayMaths.getAngle(new Pair<>(pos1, pos2));
        int offsetX = (int) DisplayMaths.getOffset(angle, maxOffset);
        int offsetY = maxOffset - offsetX;
        return new Polygon(
                new int[] {
                        pos1.getX() + offsetX,
                        pos1.getX() - offsetX,
                        pos2.getX() - offsetX,
                        pos2.getX() + offsetX,

                        },
                new int[] {
                        pos1.getZ() + offsetY,
                        pos1.getZ() - offsetY,
                        pos2.getZ() - offsetY,
                        pos2.getZ() + offsetY,
                        },
                4
        );
    }

    public static Polygon getPolygon(BPos pos1, int maxOffset) {
        return new Polygon(
                new int[] {
                        pos1.getX() - maxOffset,
                        pos1.getX() + maxOffset,
                        pos1.getX() + maxOffset,
                        pos1.getX() - maxOffset,

                        },
                new int[] {
                        pos1.getZ() - maxOffset,
                        pos1.getZ() - maxOffset,
                        pos1.getZ() + maxOffset,
                        pos1.getZ() + maxOffset,
                        },
                4
        );
    }

    public static Ellipse2D getCircle(BPos pos1, BPos pos2) {
        Ellipse2D ellipse = new Ellipse2D.Double();
        double radius = getDistance2D(pos1, pos2);
        ellipse.setFrameFromCenter(pos1.getX(), pos1.getZ(), pos1.getX() + radius, pos1.getZ() + radius);
        return ellipse;
    }

    public static Color getRandomColor() {
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        return new Color(r, g, b);
    }

    public static List<BPos> getPointsInArea(Area area) {
        // TODO actually increase speed by using a proper method, fill flood, raycast or winding number
        Rectangle rectangle = area.getBounds();
        List<BPos> bPosList = new ArrayList<>();
        for (int x = 0; x < rectangle.width; x++) {
            for (int y = 0; y < rectangle.height; y++) {
                int X = rectangle.x + x;
                int Y = rectangle.y + x;
                if (area.contains(X, Y)) {
                    bPosList.add(new BPos(X, 0, Y));
                }
            }
        }
        return bPosList;
    }

    public static List<CPos> getChunkInArea(Area area, BiPredicate<CPos, List<BPos>> predicate) {
        List<BPos> bPosList = getPointsInArea(area);
        Stream<CPos> cPosStream = bPosList.stream().map(BPos::toChunkPos).distinct();
        return cPosStream.filter(c -> predicate.test(c, bPosList)).collect(Collectors.toList());
    }


    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }
}

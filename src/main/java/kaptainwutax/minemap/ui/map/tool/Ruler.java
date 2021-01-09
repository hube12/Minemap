package kaptainwutax.minemap.ui.map.tool;

import kaptainwutax.minemap.util.DisplayMaths;
import kaptainwutax.minemap.util.Pair;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.util.math.DistanceMetric;

import java.awt.*;
import java.util.Random;


public class Ruler extends Tool {
    private BPos pos1 = null;
    private BPos pos2 = null;
    private int pointsTraced = 0;
    private Color color;

    public Ruler(){
        // you could crack that seed ;)
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        color=new Color(r,g,b);
    }

    public boolean addPoint(BPos bpos) {
        switch (pointsTraced) {
            case 0:
                pos1 = bpos;
                break;
            case 1:
                pos2 = bpos;
                break;
            default:
                return false;
        }
        pointsTraced++;
        return true;
    }

    public Polygon getShape() {
        double angle = DisplayMaths.getAngle(new Pair<>(pos1, pos2));
        int offsetX = (int) DisplayMaths.getOffset(angle, 5);
        int offsetY = 5 - offsetX;
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

    public int getPointsTraced() {
        return pointsTraced;
    }

    public boolean isComplete() {
        return this.getPointsTraced() == 2 && pos1 != null && pos2 != null;
    }

    public void reset() {
        pointsTraced = 0;
        pos1 = null;
        pos2 = null;
    }

    public double getMetric() {
        if (this.getPointsTraced() == 2 && pos1 != null && pos2 != null) {
            return DisplayMaths.round(DistanceMetric.EUCLIDEAN.getDistance(
                    pos1.getX() - pos2.getX(),
                    pos1.getY() - pos2.getY(),
                    pos1.getZ() - pos2.getZ()
            ),2) ;
        }
        return 0;
    }

    public String getMetricString(){
       return this.getMetric()+" blocks";
    }

    @Override
    public boolean shouldFill() {
        return true;
    }

    @Override
    public Tool duplicate() {
        return new Ruler();
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color=color;
    }

    @Override
    public String toString() {
        return "Ruler{" +
                "pos1=" + pos1 +
                ", pos2=" + pos2 +
                ", pointsTraced=" + pointsTraced +
                '}';
    }
}

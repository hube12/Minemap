package kaptainwutax.minemap.ui.map.tool;

import kaptainwutax.minemap.util.DisplayMaths;
import kaptainwutax.seedutils.mc.pos.BPos;

import java.awt.*;
import java.util.Random;


public class Area extends Tool {
    private BPos pos1 = null;
    private BPos pos2 = null;
    private BPos pos3 = null;
    private BPos pos4 = null;
    private int pointsTraced = 0;
    private Color color;

    public Area() {
        // you could crack that seed ;)
        Random rand = new Random();
        float r = rand.nextFloat();
        float g = rand.nextFloat();
        float b = rand.nextFloat();
        color = new Color(r, g, b);
    }

    public boolean addPoint(BPos bpos) {
        switch (pointsTraced) {
            case 0:
                pos1 = bpos;
                break;
            case 1:
                pos2 = bpos;
                break;
            case 2:
                pos3 = bpos;
                break;
            case 3:
                pos4 = bpos;
                break;
            default:
                return false;
        }
        pointsTraced++;
        return true;
    }

    public Polygon getShape() {
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

    public int getPointsTraced() {
        return pointsTraced;
    }

    public boolean isComplete() {
        return this.getPointsTraced() == 4 && pos1 != null && pos2 != null;
    }

    public void reset() {
        pointsTraced = 0;
        pos1 = null;
        pos2 = null;
    }

    public double getMetric() {
        if (this.isComplete()) {
            return DisplayMaths.round(DisplayMaths.polygonArea(
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
            ), 2);
        }
        return 0;
    }

    public String getMetricString() {
        return this.getMetric() + " blocks sq";
    }

    @Override
    public boolean shouldFill() {
        return false;
    }

    @Override
    public boolean shouldHideArtefact() {
        return true;
    }

    @Override
    public Tool duplicate() {
        return new Area();
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Square{" +
                "pos1=" + pos1 +
                ", pos2=" + pos2 +
                ", pos3=" + pos3 +
                ", pos4=" + pos4 +
                ", pointsTraced=" + pointsTraced +
                ", color=" + color +
                '}';
    }
}
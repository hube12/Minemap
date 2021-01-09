package kaptainwutax.minemap.ui.map.tool;

import kaptainwutax.minemap.ui.DrawInfo;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.util.math.DistanceMetric;
import randomreverser.util.Pair;


public class LineTool {
    private BPos pos1=null;
    private DrawInfo posScreen1 =null;
    private BPos pos2=null;
    private DrawInfo posScreen2 =null;
    private int pointsTraced = 0;

    public boolean addPoint(BPos bpos,DrawInfo drawInfo) {
        switch (pointsTraced) {
            case 0:
                pos1 = bpos;
                posScreen1 =drawInfo;
                break;
            case 1:
                pos2 = bpos;
                posScreen2 =drawInfo;
                break;
            default:
                return false;
        }
        pointsTraced++;
        return true;
    }

    public Pair<DrawInfo,DrawInfo> getPointsDrawing(){
        return new Pair<>(posScreen1,posScreen2);
    }

    public int getPointsTraced(){
        return pointsTraced;
    }

    public boolean isLine(){
        return pointsTraced==2 && pos1!=null && pos2!=null;
    }

    public void reset(){
        pointsTraced =0;
        pos1=null;
        pos2=null;
        posScreen1 =null;
        posScreen2 =null;
    }

    public double getDistance(){
        if (pointsTraced==2 && pos1!=null && pos2!=null){
            return DistanceMetric.EUCLIDEAN.getDistance(
                    pos1.getX()-pos2.getX(),
                    pos1.getY()-pos2.getY(),
                    pos1.getZ()-pos2.getZ()
            );
        }
        return 0;
    }
}

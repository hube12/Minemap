package kaptainwutax.minemap.util.tsp;

import kaptainwutax.mcutils.util.pos.BPos;

import java.io.*;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Thanks: https://github.com/phil8192/tsp-java
 * Class directory: https://github.com/phil8192/tsp-java/blob/master/src/main/java/net/parasec/tsp/TSPReader.java
 */
public class TspReader {


    static double[] coordinateArray;

    static int counter = 0;

    static double[][] data;
    static double[][] distances;

    static boolean isData = false;
    static double xd, yd;


    public static double[][] getDistances(List<BPos> bPosList) {
        isData = false;
        counter = 0;
        stripCoordinates(bPosList);
        calculateDistances();
        return distances;
    }


    private static void calculateDistances() {
        distances=new double[data.length][data.length];
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data.length; j++) {
                if (i == j) {
                    distances[i][j] = 0;
                } else {
                    xd = data[i][0] - data[j][0];
                    yd = data[i][1] - data[j][1];
                    distances[i][j] = Math.round(Math.sqrt((xd * xd) + (yd * yd)));
                }
            }
        }
    }

    private static void stripCoordinates(List<BPos> bPosList) {
        data=new double[bPosList.size()][2];
        for (BPos bPos:bPosList){
            coordinateArray = new double[2];
            coordinateArray[0] = bPos.getX();
            coordinateArray[1] = bPos.getZ();
            data[counter++] = coordinateArray;
        }
    }

}

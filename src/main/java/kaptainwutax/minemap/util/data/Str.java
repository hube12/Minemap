package kaptainwutax.minemap.util.data;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.stream.Collectors;

public final class Str {

    private final static TreeMap<Integer, String> map = new TreeMap<>();

    static {
        map.put(1000, "M");
        map.put(900, "CM");
        map.put(500, "D");
        map.put(400, "CD");
        map.put(100, "C");
        map.put(90, "XC");
        map.put(50, "L");
        map.put(40, "XL");
        map.put(10, "X");
        map.put(9, "IX");
        map.put(5, "V");
        map.put(4, "IV");
        map.put(1, "I");
    }

    public static String formatName(String name) {
        char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);

        for (int i = 1; i < chars.length; i++) {
            if (chars[i] == '_') {
                chars[i] = ' ';
                chars[i + 1] = Character.toUpperCase(chars[i + 1]);
            }
        }

        return new String(chars);
    }

    public static String capitalize(String s) {
        return capitalize(s, 1);
    }

    public static String capitalize(String s, int n) {
        if (s == null) return null;
        return s.substring(0, n).toUpperCase() + s.substring(n);
    }

    public static String prettifyDashed(String s) {
        if (s == null) return null;
        String[] words = s.toLowerCase().split("_");
        if ((words[0].equals("NE") || words[0].equals("OW"))) {
            words[0] = capitalize(words[0], 2);
        }
        return Arrays.stream(words).map(Str::capitalize).collect(Collectors.joining(" "));
    }

    public static String toRomanNumeral(Integer number) {
        if (number == null) return null;
        int l = map.floorKey(number);
        if (number == l) {
            return map.get(number);
        }
        return map.get(l) + toRomanNumeral(number - l);
    }
}

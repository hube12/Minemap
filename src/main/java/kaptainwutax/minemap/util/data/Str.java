package kaptainwutax.minemap.util.data;

import java.util.Arrays;
import java.util.stream.Collectors;

public final class Str {

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
        String[] words=s.toLowerCase().split("_");
        if ((s.startsWith("NE") || s.startsWith("OW"))){
            words[0]=capitalize(words[0],2);
        }
        return Arrays.stream(words).map(Str::capitalize).collect(Collectors.joining(" "));
    }

}

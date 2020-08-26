package kaptainwutax.minemap.util;

public final class Str {

    public static String formatName(String name) {
        char[] chars = name.toCharArray();
        chars[0] = Character.toUpperCase(chars[0]);

        for(int i = 1; i < chars.length; i++) {
            if(chars[i] == '_') {
                chars[i] = ' ';
                chars[i + 1] = Character.toUpperCase(chars[i + 1]);
            }
        }

        return new String(chars);
    }

}

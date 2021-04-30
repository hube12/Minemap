package kaptainwutax.minemap.util.data;

import java.util.Arrays;
import java.util.stream.Collectors;

public class ListUtil {

    public static <T> java.util.List<T> reindex(java.util.List<T> input, Integer... indexes) {
        if (indexes.length > input.size()) {
            throw new IllegalArgumentException("You should not have more indexes than your input");
        }
        @SuppressWarnings("unchecked")
        T[] temp = (T[]) new Object[input.size()];
        for (int i = 0; i < indexes.length; i++) {
            temp[i] = input.get(indexes[i]);
        }
        for (int i = indexes.length; i < input.size(); i++) {
            temp[i] = input.get(i);
        }
        return Arrays.stream(temp).collect(Collectors.toList());
    }

    public static <T> void reindexInPlace(java.util.List<T> input, Integer... indexes) {
        java.util.List<T> list = reindex(input, indexes);
        input.clear();
        input.addAll(list);
    }

    public static <T> java.util.List<T> cycle(java.util.List<T> input, T initial) {
        int index = input.indexOf(initial);
        if (index < 0) {
            throw new IllegalArgumentException("You should use initial that belong to the set");
        }
        @SuppressWarnings("unchecked")
        T[] temp = (T[]) new Object[input.size()];
        for (int i = 0; i < input.size(); i++) {
            temp[i] = input.get((i + index) % input.size());
        }
        return Arrays.stream(temp).collect(Collectors.toList());
    }

    public static <T> void cycleInPlace(java.util.List<T> input, T initial) {
        java.util.List<T> list = cycle(input, initial);
        input.clear();
        input.addAll(list);
    }
}

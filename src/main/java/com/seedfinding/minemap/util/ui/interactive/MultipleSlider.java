package com.seedfinding.minemap.util.ui.interactive;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Function;

public class MultipleSlider extends JPanel {
    /**
     * Provide an implementation for a multiple slider panel with as many settings as possible
     * Please don't provide a labels table with null Integer.
     */
    private static final String SLIDER_CONTROL_KEY = "Slider.controlKey";
    private final int count;
    private final int orientation;
    private final boolean isInteger;
    private final int min;
    private final int max;
    private final int[] values;
    private final Dictionary<Integer, JLabel> labels;
    private JSlider[] sliders = null;

    public MultipleSlider(int count) {
        this(count, null, null);
    }

    public MultipleSlider(int count, Function<Informations, JSlider> sliderFactory) {
        this(count, null, sliderFactory);
    }

    public MultipleSlider(int count, Dictionary<Integer, JLabel> labels) {
        this(count, 0, 100, labels, JSlider.VERTICAL, null);
    }

    public MultipleSlider(int count, Dictionary<Integer, JLabel> labels, Function<Informations, JSlider> sliderFactory) {
        this(count, 0, 100, labels, JSlider.VERTICAL, sliderFactory);
    }

    public MultipleSlider(int count, int min, int max) {
        this(count, min, max, (Dictionary<Integer, JLabel>) null);
    }

    public MultipleSlider(int count, int min, int max, int[] values) {
        this(count, min, max, values, null);
    }

    public MultipleSlider(int count, int min, int max, int orientation) {
        this(count, min, max, (Dictionary<Integer, JLabel>) null, orientation);
    }

    public MultipleSlider(int count, int min, int max, int[] values, int orientation) {
        this(count, min, max, values, null, orientation);
    }

    public MultipleSlider(int count, int min, int max, Dictionary<Integer, JLabel> labels) {
        this(count, min, max, labels, JSlider.VERTICAL, null);
    }

    public MultipleSlider(int count, int min, int max, int[] values, Dictionary<Integer, JLabel> labels) {
        this(count, min, max, values, labels, JSlider.VERTICAL, null);
    }

    public MultipleSlider(int count, int min, int max, Dictionary<Integer, JLabel> labels, int orientation) {
        this(count, min, max, labels, orientation, null);
    }

    public MultipleSlider(int count, int min, int max, int[] values, Dictionary<Integer, JLabel> labels, int orientation) {
        this(count, min, max, values, labels, orientation, null);
    }

    public MultipleSlider(int count, int min, int max, Dictionary<Integer, JLabel> labels, int orientation, Function<Informations, JSlider> sliderFactory) {
        this(count, min, max, new int[0], labels, orientation, sliderFactory);
    }

    public MultipleSlider(int count, int min, int max, int[] values, Dictionary<Integer, JLabel> labels, int orientation, Function<Informations, JSlider> sliderFactory) {
        this.count = count;
        this.min = min;
        this.max = max;
        this.labels = labels;
        this.isInteger = false;
        this.values = values;
        this.orientation = orientation;
        this.initComponents(sliderFactory == null ? this::makeSlider : sliderFactory);
    }

    public void initComponents(Function<Informations, JSlider> sliderFactory) {
        this.setLayout(new GridBagLayout());
        sliders = makeSliders(sliderFactory);
        for (JSlider slider : sliders) {
            this.add(slider);
        }
    }


    public JSlider[] getSliders() {
        return sliders;
    }

    public JSlider[] makeSliders(Function<Informations, JSlider> sliderFactory) {
        JSlider[] sliders = new JSlider[count];

        for (int index = 0; index < count; index++) {
            Informations informations = new Informations(index, this);
            sliders[index] = sliderFactory.apply(informations);
            sliders[index].putClientProperty(SLIDER_CONTROL_KEY, index);
        }
        return sliders;
    }

    static class Informations {
        public final MultipleSlider parent;
        private final int index;

        public Informations(int index, MultipleSlider parent) {
            this.index = index;
            this.parent = parent;
        }

        public int getMin() {
            return this.parent.min;
        }

        public int getMax() {
            return this.parent.max;
        }

        public int getOrientation() {
            return this.parent.orientation;
        }

        public int[] getValues() {
            return this.parent.values;
        }

        public int getValue() {
            if (this.parent.values.length > index) {
                return this.parent.values[index];
            }
            return (getMax() - getMin()) / 2;
        }

        public Dictionary<Integer, JLabel> getLabels() {
            return this.parent.labels;
        }

        public int getIndex() {
            return this.index;
        }

        public Callable<Integer> spacingLabels() {
            return parent::getSpacingLabels;
        }
    }

    public JSlider makeSlider(Informations informations) {
        JSlider slider = new JSlider(informations.getOrientation(), informations.getMin(), informations.getMax(), informations.getValue());
        slider.setPaintTicks(true);
        try {
            slider.setMajorTickSpacing(informations.spacingLabels().call());
        } catch (Exception e) {
            e.printStackTrace();
        }
        slider.setLabelTable(informations.getLabels());
        slider.setPaintLabels(true);
        slider.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        return slider;
    }

    public int getSpacingLabels() {
        if (this.labels == null) return 0;
        Enumeration<Integer> keys = labels.keys();
        List<Integer> ints = new ArrayList<>();
        while (keys.hasMoreElements()) {
            ints.add(keys.nextElement());
        }
        ints.sort(Integer::compare);
        if (ints.size() < 1) return max - min;
        double average = 0.0D;
        int count = 0;
        int last = ints.remove(0);
        for (int current : ints) {
            average += Math.abs(current - last);
            last = current;
            count++;
        }
        return (int) (average / count);
    }

    public Dictionary<Integer, JLabel> getLabels() {
        return labels;
    }

    public int getCount() {
        return count;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getOrientation() {
        return orientation;
    }

    public boolean isInteger() {
        return isInteger;
    }

    @Override
    public String toString() {
        return "MultipleSlider{" +
            "count=" + count +
            ", orientation=" + orientation +
            ", isInteger=" + isInteger +
            ", min=" + min +
            ", max=" + max +
            ", labels=" + labels +
            '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MultipleSlider)) return false;
        MultipleSlider that = (MultipleSlider) o;
        return count == that.count && orientation == that.orientation && isInteger == that.isInteger && min == that.min && max == that.max && Objects.equals(labels, that.labels);
    }

    @Override
    public int hashCode() {
        return Objects.hash(count, orientation, isInteger, min, max, labels);
    }
}

package kaptainwutax.minemap.util.ui.interactive;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.function.Supplier;

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
    private final Dictionary<Integer, JLabel> labels;

    public MultipleSlider(int count) {
        this(count, null,null);
    }

    public MultipleSlider(int count, Supplier<JSlider> sliderSupplier) {
        this(count, null, sliderSupplier);
    }

    public MultipleSlider(int count, Dictionary<Integer, JLabel> labels) {
        this(count, 0, 100, labels, JSlider.VERTICAL,null);
    }

    public MultipleSlider(int count, Dictionary<Integer, JLabel> labels, Supplier<JSlider> sliderSupplier) {
        this(count, 0, 100, labels, JSlider.VERTICAL, sliderSupplier);
    }

    public MultipleSlider(int count, int min, int max) {
        this(count, min, max, JSlider.VERTICAL);
    }

    public MultipleSlider(int count, int min, int max, int orientation) {
        this(count, min, max, null, orientation);
    }

    public MultipleSlider(int count, int min, int max, Dictionary<Integer, JLabel> labels, int orientation) {
        this(count,min,max,labels,orientation,null);
    }

    public MultipleSlider(int count, int min, int max, Dictionary<Integer, JLabel> labels, int orientation, Supplier<JSlider> sliderSupplier) {
        this.count = count;
        this.min = min;
        this.max = max;
        this.labels = labels;
        this.isInteger = false;
        this.orientation = orientation;
        this.initComponents(sliderSupplier == null ? this::makeSlider : sliderSupplier);
    }

    public void initComponents(Supplier<JSlider> sliderSupplier) {
        this.setLayout(new GridBagLayout());
        JSlider[] sliders = makeSliders(sliderSupplier);
        for (JSlider slider : sliders) {
            this.add(slider);
        }
    }

    public JSlider[] makeSliders(Supplier<JSlider> sliderSupplier) {
        JSlider[] sliders = new JSlider[count];
        for (int index = 0; index < count; index++) {
            sliders[index] = sliderSupplier.get();
            sliders[index].putClientProperty(SLIDER_CONTROL_KEY, index);
        }
        return sliders;
    }

    public JSlider makeSlider() {
        JSlider slider = new JSlider(orientation, min, max, min + (max / 2));
        slider.setPaintTicks(true);
        slider.setMajorTickSpacing(getSpacingLabels());
        slider.setLabelTable(labels);
        slider.setPaintLabels(true);
        slider.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
        return slider;
    }

    public int getSpacingLabels(){
        if (this.labels==null) return 0;
        Enumeration<Integer> keys=labels.keys();
        List<Integer> ints=new ArrayList<>();
        while (keys.hasMoreElements()) {
            ints.add(keys.nextElement());
        }
        ints.sort(Integer::compare);
        if (ints.size()<1)return max-min;
        double average=0.0D;
        int count=0;
        int last=ints.remove(0);
        for (int current:ints){
            average+=Math.abs(current-last);
            last=current;
            count++;
        }
        System.out.println(average+" "+count);
        return (int) (average/count);
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

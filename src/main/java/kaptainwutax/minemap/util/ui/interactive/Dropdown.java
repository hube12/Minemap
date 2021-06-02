package kaptainwutax.minemap.util.ui.interactive;

import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.minemap.init.Logger;
import kaptainwutax.minemap.ui.component.TabGroup;
import kaptainwutax.minemap.util.data.Str;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Dropdown<E> extends JComboBox<String> {

    public final StringMapper<E> mapper;
    public final LinkedList<Pair<E, String>> elements;

    @SafeVarargs
    public Dropdown(E... elements) {
        this(Object::toString, elements);
    }

    public Dropdown(Stream<E> elements) {
        this(Object::toString, elements.collect(Collectors.toList()));
    }

    public Dropdown(Collection<E> elements) {
        this(Object::toString, elements);
    }

    @SafeVarargs
    public Dropdown(StringMapper<E> mapper, E... elements) {
        this(mapper, Arrays.asList(elements));
    }

    public Dropdown(StringMapper<E> mapper, Stream<E> elements) {
        this(mapper, null, elements.collect(Collectors.toList()));
    }

    @SafeVarargs
    public Dropdown(StringMapper<E> mapper, Function<Object, Object> transform, E... elements) {
        this(mapper, transform, Arrays.asList(elements));
    }

    public Dropdown(StringMapper<E> mapper, Collection<E> elements) {
        this(mapper, null, elements);
    }

    public Dropdown(StringMapper<E> mapper, Function<Object, Object> transform, Collection<E> elements) {
        super(elements.stream().map(mapper::map).toArray(String[]::new));
//        this.setEditable(true); // DON'T DO THAT IT CAUSE A LOT OF NPE (STILL A JDK BUG)


        this.mapper = mapper;
        this.elements = elements.stream().map(e -> new Pair<>(e, mapper.map(e))).collect(Collectors.toCollection(LinkedList::new));
        this.setOpaque(true);
        DefaultListCellRenderer listRenderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, transform != null ? transform.apply(value) : value, index, isSelected, cellHasFocus);
            }
        };

        listRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        listRenderer.setVerticalAlignment(SwingConstants.CENTER);
        this.setRenderer(listRenderer);
    }

    public void setDefault(E element) {
        if (element == null) return;
        this.setSelectedItem(elements.stream().filter(e->e.getFirst()==element).map(Pair::getSecond).findFirst().orElse(mapper.map(element)));
    }

    @Override
    public void setSelectedItem(Object item) {
        super.setSelectedItem(item);
        // FIXME, this is so bad
        if (item instanceof String) {
            if (elements!=null){
                List<E> eList=elements.stream().filter(e->e.getSecond().equals(item)).map(Pair::getFirst).collect(Collectors.toList());
                if (eList.size()==1){
                    E first=eList.get(0);
                    if (first instanceof TabGroup){
                        if (((TabGroup) first).isLazyLoaded()) {
                            ((TabGroup) first).loadEffectively();
                        }
                    }
                }
            }
        }
    }

    public void remove(E element) {
        Pair<E, String> toRemove = null;
        for (Pair<E, String> e : this.elements) {
            if (e.getFirst() == element) {
                toRemove = e;
            }
        }
        if (toRemove != null) {
            elements.remove(toRemove);
            super.removeItem(toRemove.getSecond());
        }
    }

    public void add(E element) {
        if (element == null) return;
        String map = mapper.map(element);
        this.elements.add(new Pair<>(element, map));
        this.addItem(map);
    }

    public E getCycleRight() {
        int currentIdx = this.getSelectedIndex();
        if (currentIdx == -1) return null;
        int nextIdx = (currentIdx + 1) % this.getElementsSize();
        try {
            return this.getElement(nextIdx);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.LOGGER.severe("That should not happen for unknown element " + e);
        }
        return null;
    }

    public int getElementsSize() {
        return this.elements.size();
    }

    public E getCycleLeft() {
        int currentIdx = this.getSelectedIndex();
        if (currentIdx == -1) return null;
        int previousIdx = ((currentIdx - 1) % this.getElementsSize() + this.getElementsSize()) % this.getElementsSize();
        try {
            return this.getElement(previousIdx);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.LOGGER.severe("That should not happen for unknown element " + e);
        }
        return null;
    }

    public E getElement(int index) {
        Pair<E, String> e = this.elements.get(index);
        return e == null ? null : e.getFirst();
    }

    public E getSelected() {
        int idx = this.getSelectedIndex();
        if (idx == -1) return null;
        return this.getElement(idx);
    }

    public String getSelectedMapped() {
        return this.mapper.map(this.getSelected());
    }

    public boolean selectIfPresent(E element) {
        return this.selectIfPresent(element, Object::equals);
    }

    public boolean selectIfPresent(E element, BiPredicate<E, E> equals) {
        for (Pair<E, String> e : this.elements) {
            if (equals.test(e.getFirst(), element)) {
                this.setDefault(element);
                return true;
            }
        }

        return false;
    }

    public interface StringMapper<E> {
        String map(E element);
    }

}

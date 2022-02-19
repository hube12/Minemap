package com.seedfinding.minemap.util.ui.interactive;

import com.seedfinding.minemap.init.Logger;
import com.seedfinding.minemap.ui.component.TabGroup;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Dropdown<E> extends JComboBox<String> {

    public final StringMapper<E> mapper;
    public final HashMap<E, String> elements = new HashMap<>();
    public final HashMap<String, E> strings = new HashMap<>();
    public final List<String> order;

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
    public Dropdown(StringMapper<E> mapper, BiFunction<Object, E, Object> transform, E... elements) {
        this(mapper, transform, Arrays.asList(elements));
    }

    public Dropdown(StringMapper<E> mapper, Collection<E> elements) {
        this(mapper, null, elements);
    }

    public Dropdown(StringMapper<E> mapper, BiFunction<Object, E, Object> transform, Collection<E> elements) {
        super(elements.stream().map(mapper::map).toArray(String[]::new));
//        this.setEditable(true); // DON'T DO THAT IT CAUSE A LOT OF NPE (STILL A JDK BUG)

        this.order = elements.stream().map(mapper::map).collect(Collectors.toList());
        this.mapper = mapper;
        for (E element : elements) {
            this.elements.put(element, mapper.map(element));
            this.strings.put(mapper.map(element), element);
        }
        this.setOpaque(true);

        DefaultListCellRenderer listRenderer = new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                return super.getListCellRendererComponent(list, transform != null ? transform.apply(value,
                    (index >= 0 && index < Dropdown.this.order.size() && Dropdown.this.order.get(index) != null ?
                        Dropdown.this.strings.get(Dropdown.this.order.get(index)) : null)) : value, index, isSelected, cellHasFocus);
            }
        };

        listRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        listRenderer.setVerticalAlignment(SwingConstants.CENTER);
        this.setRenderer(listRenderer);
    }

    public void setDefault(E element) {
        if (element == null) return;
        String e = elements.get(element);
        if (e == null) this.setSelectedItem(mapper.map(element));
        this.setSelectedItem(e);
    }

    @Override
    public void setSelectedItem(Object item) {
        super.setSelectedItem(item);
        // FIXME, this is so bad
        if (item instanceof String) {
            if (elements != null) {
                E first = strings.get(item);
                if (first instanceof TabGroup) {
                    if (((TabGroup) first).isLazyLoaded()) {
                        ((TabGroup) first).loadEffectively();
                    }

                }
            }
        }
    }

    public void remove(E element) {
        String toRemove = this.elements.get(element);
        if (toRemove != null) {
            this.elements.remove(element);
            this.strings.remove(toRemove);
            if (this.order.get(this.getSelectedIndex()).equals(toRemove)) {
                this.order.remove(this.getSelectedIndex());
            } else {
                Logger.LOGGER.log(Level.SEVERE,"Missed element in toRemove (Logic error)");
                this.order.remove(toRemove);
            }
            super.removeItem(toRemove);
        }
    }

    public void add(E element) {
        if (element == null) return;
        String map = mapper.map(element);
        this.elements.put(element, map);
        this.strings.put(map, element);
        this.order.add(map);
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
        if (index < this.order.size()) {
            String s = this.order.get(index);
            if (s != null) {
                return this.strings.get(s);
            }
        }
        return null;
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
        String e = this.elements.get(element);
        if (e != null) {
            this.setDefault(element);
            return true;
        }
        return false;
    }

    public interface StringMapper<E> {
        String map(E element);
    }

}

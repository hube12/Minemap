package kaptainwutax.minemap.util.ui.interactive;

import kaptainwutax.minemap.feature.chests.Loot;
import kaptainwutax.minemap.init.Logger;
import kaptainwutax.minemap.ui.component.TabGroup;

import javax.swing.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Dropdown<E> extends JComboBox<String> {

    public final StringMapper<E> mapper;
    public final LinkedList<Object> elements;

    @SafeVarargs
    public Dropdown(E... elements) {
        this(Object::toString, Arrays.asList(elements));
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
        this(mapper, elements.collect(Collectors.toList()));
    }

    public Dropdown(StringMapper<E> mapper, Collection<E> elements) {
        super(elements.stream().map(mapper::map).toArray(String[]::new));
//        this.setEditable(true); // DON'T DO THAT IT CAUSE A LOT OF NPE (STILL A JDK BUG)


        this.mapper = mapper;
        this.elements = new LinkedList<>(elements);
        this.setOpaque(true);
        DefaultListCellRenderer listRenderer = new DefaultListCellRenderer();
        listRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        listRenderer.setVerticalAlignment(SwingConstants.CENTER);
        this.setRenderer(listRenderer);
    }

    public void setDefault(E element) {
        this.setSelectedItem(mapper.map(element));
    }

    public void add(E element) {
        this.elements.add(element);
        this.addItem(mapper.map(element));
    }

    public E getCycleRight() {
        int currentIdx=this.getSelectedIndex();
        if (currentIdx==-1) return null;
        int nextIdx = (currentIdx + 1) % this.getElementsSize();
        try {
            return this.getElement(nextIdx);
        }catch (Exception e){
            e.printStackTrace();
            Logger.LOGGER.severe("That should not happen for unknown element "+e);
        }
        return null;
    }

    public int getElementsSize() {
        return this.elements.size();
    }

    public E getCycleLeft() {
        int currentIdx=this.getSelectedIndex();
        if (currentIdx==-1) return null;
        int previousIdx = ((currentIdx - 1) % this.getElementsSize() + this.getElementsSize()) % this.getElementsSize();
        try {
            return this.getElement(previousIdx);
        }catch (Exception e){
            e.printStackTrace();
            Logger.LOGGER.severe("That should not happen for unknown element "+e);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public E getElement(int index) {
        return (E) this.elements.get(index);
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

    @SuppressWarnings("unchecked")
    public boolean selectIfPresent(E element, BiPredicate<E, E> equals) {
        for (Object e : this.elements) {
            if (equals.test((E) e, element)) {
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

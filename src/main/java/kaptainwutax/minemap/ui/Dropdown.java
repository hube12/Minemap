package kaptainwutax.minemap.ui;

import javafx.scene.control.ComboBox;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Dropdown<E> extends ComboBox<String> {

    public final StringMapper<E> mapper;
    public final Object[] elements;

    public Dropdown(E... elements) {
        this(Object::toString, Arrays.asList(elements));
    }

    public Dropdown(Stream<E> elements) {
        this(Object::toString, elements.collect(Collectors.toList()));
    }

    public Dropdown(Collection<E> elements) {
        this(Object::toString, elements);
    }

    public Dropdown(StringMapper<E> mapper, E... elements) {
        this(mapper, Arrays.asList(elements));
    }

    public Dropdown(StringMapper<E> mapper, Stream<E> elements) {
        this(mapper, elements.collect(Collectors.toList()));
    }

    public Dropdown(StringMapper<E> mapper, Collection<E> elements) {
        elements.stream().map(mapper::map).forEach(this.getItems()::add);

        this.mapper = mapper;
        this.elements = new Object[elements.size()];
        int i = 0;

        for(Iterator<E> it = elements.iterator(); it.hasNext(); i++) {
            this.elements[i] = it.next();
        }

        if(this.elements.length > 0) {
            this.getSelectionModel().select(0);
        }
    }

    public E getElement(int index) {
        return (E)this.elements[index];
    }

    public E getSelected() {
        return this.getElement(this.getSelectionModel().getSelectedIndex());
    }

    public String getSelectedMapped() {
        return this.mapper.map(this.getSelected());
    }

    public boolean selectIfPresent(E element) {
        return this.selectIfPresent(element, Object::equals);
    }

    public boolean selectIfPresent(E element, BiPredicate<E, E> equals) {
        for(int i = 0; i < this.elements.length; i++) {
            if(equals.test(this.getElement(i), element)) {
                this.getSelectionModel().select(i);
                return true;
            }
        }

        return false;
    }

    public interface StringMapper<E> {
        String map(E element);
    }

}

package kaptainwutax.minemap.listener;

import java.awt.event.*;
import java.util.function.Consumer;

public class Events {

    public static final class Mouse implements MouseListener, MouseMotionListener {
        private final Type type;
        private final Consumer<MouseEvent> event;

        public Mouse(Type type, Consumer<MouseEvent> event) {
            this.type = type;
            this.event = event;
        }

        public static Mouse onClick(Consumer<MouseEvent> event) {
            return new Mouse(Type.CLICKED, event);
        }

        public static Mouse onPressed(Consumer<MouseEvent> event) {
            return new Mouse(Type.PRESSED, event);
        }

        public static Mouse onReleased(Consumer<MouseEvent> event) {
            return new Mouse(Type.RELEASED, event);
        }

        public static Mouse onEntered(Consumer<MouseEvent> event) {
            return new Mouse(Type.ENTERED, event);
        }

        public static Mouse onExited(Consumer<MouseEvent> event) {
            return new Mouse(Type.EXITED, event);
        }

        public static Mouse onDragged(Consumer<MouseEvent> event) {
            return new Mouse(Type.DRAGGED, event);
        }

        public static Mouse onMoved(Consumer<MouseEvent> event) {
            return new Mouse(Type.MOVED, event);
        }

        @Override
        public void mouseClicked(MouseEvent e) {
            if(this.type == Type.CLICKED)this.event.accept(e);
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if(this.type == Type.PRESSED)this.event.accept(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if(this.type == Type.RELEASED)this.event.accept(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            if(this.type == Type.ENTERED)this.event.accept(e);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if(this.type == Type.EXITED)this.event.accept(e);
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if(this.type == Type.DRAGGED)this.event.accept(e);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
            if(this.type == Type.MOVED)this.event.accept(e);
        }

        public enum Type {
            CLICKED, PRESSED, RELEASED, ENTERED, EXITED, DRAGGED, MOVED
        }
    }

    public static final class Keyboard implements KeyListener {
        private final Type type;
        private final Consumer<KeyEvent> event;

        public Keyboard(Type type, Consumer<KeyEvent> event) {
            this.type = type;
            this.event = event;
        }

        public static Keyboard onTyped(Consumer<KeyEvent> event) {
            return new Keyboard(Type.TYPED, event);
        }

        public static Keyboard onPressed(Consumer<KeyEvent> event) {
            return new Keyboard(Type.PRESSED, event);
        }

        public static Keyboard onReleased(Consumer<KeyEvent> event) {
            return new Keyboard(Type.RELEASED, event);
        }

        @Override
        public void keyTyped(KeyEvent e) {
            if(this.type == Type.TYPED)this.event.accept(e);
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if(this.type == Type.PRESSED)this.event.accept(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if(this.type == Type.RELEASED)this.event.accept(e);
        }

        public enum Type {
            TYPED, PRESSED, RELEASED
        }
    }


}

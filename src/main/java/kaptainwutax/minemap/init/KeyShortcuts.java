package kaptainwutax.minemap.init;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.ui.menubar.MenuBar;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Arrays;

import static java.awt.event.KeyEvent.getKeyText;

public class KeyShortcuts {
    public static MenuBar menuBar = (MenuBar) MineMap.INSTANCE.toolbarPane;

    public static void registerShortcuts() {
        register(KeyRegister.registerCtrlKey("N"), menuBar.fileMenu.newSeed());
        register(KeyRegister.registerCtrlKey("S"), menuBar.fileMenu.screenshot());
        register(KeyRegister.registerCtrlKey("C"), menuBar.fileMenu.close(true));
    }

    public static void register(KeyRegister keyRegister, Runnable runnable) {
        KeyboardFocusManager.getCurrentKeyboardFocusManager()
                .addKeyEventDispatcher(keyEvent -> {
                    if (keyRegister.check(keyEvent)) {
                        if (!menuBar.isActive()) {
                            runnable.run();
                        } else {
                            System.out.println("You can not open a new popup like that");
                        }

                    }
                    return false;
                });
    }

    public static class KeyRegister {
        private final String keyText;
        private final Type type;
        private final Modifier modifier;
        private final KeyLocation keyLocation;

        public KeyRegister(int keyCode) {
            this(getKeyText(keyCode));
        }

        public KeyRegister(String keyText) {
            this(keyText, Type.ANY, Modifier.ANY, KeyLocation.ANY);
        }

        public KeyRegister(String keyText, Type type) {
            this(keyText, type, Modifier.ANY, KeyLocation.ANY);
        }

        public KeyRegister(String keyText, Modifier modifier) {
            this(keyText, Type.ANY, modifier, KeyLocation.ANY);
        }

        public KeyRegister(String keyText, KeyLocation keyLocation) {
            this(keyText, Type.ANY, Modifier.ANY, keyLocation);
        }

        public KeyRegister(String keyText, Type type, Modifier modifier) {
            this(keyText, type, modifier, KeyLocation.ANY);
        }

        public KeyRegister(String keyText, Type type, KeyLocation keyLocation) {
            this(keyText, type, Modifier.ANY, keyLocation);
        }

        public KeyRegister(String keyText, Modifier modifier, KeyLocation keyLocation) {
            this(keyText, Type.ANY, modifier, keyLocation);
        }

        public KeyRegister(String keyText, Type type, Modifier modifier, KeyLocation keyLocation) {
            this.keyText = keyText;
            this.type = type;
            this.modifier = modifier;
            this.keyLocation = keyLocation;
        }

        public static KeyRegister registerCtrlKey(String keyText) {
            return new KeyRegister(keyText, Type.KEY_PRESSED, Modifier.CTRL, KeyLocation.ANY);
        }

        public static KeyRegister registerAltKey(String keyText) {
            return new KeyRegister(keyText, Type.KEY_PRESSED, Modifier.ALT, KeyLocation.ANY);
        }

        public boolean check(KeyEvent keyEvent) {
            boolean isTextOk = this.keyText.equals(getKeyText(keyEvent.getKeyCode()));
            boolean isTypeOk = true;
            if (this.type != Type.ANY) {
                int id = keyEvent.getID();
                if (id == Type.KEY_PRESSED.getValue() || id == Type.KEY_TYPED.getValue() || id == Type.KEY_RELEASED.getValue()) {
                    isTypeOk = this.type.getValue() == id;
                } else {
                    // if keyEvent is UNKNOWN check that we actually ask for it
                    isTypeOk = this.type == Type.UNKNOWN;
                }
            }
            boolean isModifierOk = true;
            switch (this.modifier) {
                case CTRL:
                    isModifierOk = keyEvent.isControlDown();
                    break;
                case META:
                    isModifierOk = keyEvent.isMetaDown();
                    break;
                case SHIFT:
                    isModifierOk = keyEvent.isShiftDown();
                    break;
                case ALT:
                    isModifierOk = keyEvent.isAltDown();
                    break;
                case ALT_GR:
                    isModifierOk = keyEvent.isAltGraphDown();
                    break;
                case NONE:
                    isModifierOk = !(keyEvent.isControlDown() || keyEvent.isMetaDown() || keyEvent.isShiftDown() || keyEvent.isAltDown() || keyEvent.isAltGraphDown());
                    break;
                case ANY:
                    break;
                default:
                    isModifierOk = false;
                    break;
            }
            boolean isKeyLocationOk = true;
            if (this.keyLocation != KeyLocation.ANY) {
                isKeyLocationOk = keyEvent.getKeyLocation() == this.keyLocation.getValue();
            }
            return isTextOk && isTypeOk && isModifierOk && isKeyLocationOk;
        }

        public enum Type {
            KEY_PRESSED(KeyEvent.KEY_PRESSED),
            KEY_RELEASED(KeyEvent.KEY_RELEASED),
            KEY_TYPED(KeyEvent.KEY_TYPED),
            UNKNOWN(1000),
            ANY(1001);
            public final int id;

            Type(int id) { this.id = id; }

            public int getValue() { return id; }
        }

        public enum KeyLocation {
            KEY_LOCATION_UNKNOWN(KeyEvent.KEY_LOCATION_UNKNOWN),
            KEY_LOCATION_STANDARD(KeyEvent.KEY_LOCATION_STANDARD),
            KEY_LOCATION_LEFT(KeyEvent.KEY_LOCATION_LEFT),
            KEY_LOCATION_RIGHT(KeyEvent.KEY_LOCATION_RIGHT),
            KEY_LOCATION_NUMPAD(KeyEvent.KEY_LOCATION_NUMPAD),
            ANY(-1);
            public final int id;

            KeyLocation(int id) { this.id = id; }

            public int getValue() { return id; }
        }

        public enum Modifier {
            CTRL, META, SHIFT, ALT, ALT_GR, NONE, ANY
        }
    }
}

package kaptainwutax.minemap.init;

import com.google.gson.annotations.SerializedName;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.ui.menubar.MenuBar;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import static kaptainwutax.minemap.ui.map.MapManager.zoom;
import static kaptainwutax.minemap.util.data.Str.prettifyDashed;

public class KeyShortcuts {
    public static final ArrayList<KeyEventDispatcher> currentDispatchers = new ArrayList<>();
    public static MenuBar menuBar = MineMap.INSTANCE.toolbarPane;

    public static void registerShortcuts() {
        Configs.KEYBOARDS.getRegisters().forEach(KeyShortcuts::register);
    }

    public static void deRegisterShortcuts() {
        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
        currentDispatchers.forEach(manager::removeKeyEventDispatcher);
        currentDispatchers.clear();
    }

    public static void register(ShortcutAction shortcutAction, KeyRegister keyRegister) {
        KeyEventDispatcher keyEventDispatcher = keyEvent -> {
            if (keyRegister.check(keyEvent)) {
                if (!menuBar.isActive()) {
                    shortcutAction.action.run();
                } else {
                    System.out.println("You can not open a new popup like that");
                }
            }
            return false;
        };
        currentDispatchers.add(keyEventDispatcher);
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);
    }

    public enum ShortcutAction {
        @SerializedName("NEW_SEED")
        NEW_SEED(menuBar.fileMenu.newSeed()),
        @SerializedName("SCREENSHOT")
        SCREENSHOT(menuBar.fileMenu.screenshot()),
        @SerializedName("CLOSE")
        CLOSE(menuBar.fileMenu.close(true)),
        @SerializedName("GO_TO_COORDS")
        GO_TO_COORDS(menuBar.worldMenu.goToCoords()),
        @SerializedName("GO_TO_SPAWN")
        GO_TO_SPAWN(menuBar.worldMenu.goToSpawn()),
        @SerializedName("LOAD_SHADOW_SEED")
        LOAD_SHADOW_SEED(menuBar.worldMenu.loadShadowSeed()),
        @SerializedName("GO_TO_STRUCTURE")
        GO_TO_STRUCTURE(menuBar.worldMenu.goToStructure()),
        @SerializedName("CHANGE_SALTS")
        CHANGE_SALTS(menuBar.worldMenu.changeSalts()),
        @SerializedName("TOGGLE_STS_MODE")
        TOGGLE_STS_MODE(menuBar.utilitiesMenu.toggleStructureMode(true)),
        @SerializedName("SHORTCUTS")
        SHORTCUTS(menuBar.settingsMenu.changeShortcuts()),
        @SerializedName("ZOOM_IN")
        ZOOM_IN(zoom(false, false)),
        @SerializedName("ZOOM_OUT")
        ZOOM_OUT(zoom(true, false)),
        @SerializedName("LAYER_ZOOM_IN")
        LAYER_ZOOM_IN(zoom(false, true)),
        @SerializedName("LAYER_ZOOM_OUT")
        LAYER_ZOOM_OUT(zoom(true, true)),
        ;

        public Runnable action;

        ShortcutAction(Runnable action) {
            this.action = action;
        }

        @Override
        public String toString() {
            return prettifyDashed(this.name());
        }
    }

    public static class KeyRegister {
        private final String keyText;
        private final Type type;
        private final Modifier modifier;
        private final KeyLocation keyLocation;

        public KeyRegister(int keyCode) {
            this(KeyEvent.getKeyText(keyCode));
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

        public static KeyRegister initFromString(String json) {
            String[] parts = json.split("\\|\\|\\|");
            String keyText = parts[0];
            if (parts.length != 4) {
                return new KeyRegister(keyText);
            }
            Type type;
            Modifier modifier;
            KeyLocation keyLocation;
            try {
                int ord = Integer.parseInt(parts[1]);
                Type[] values = Type.values();
                if (ord < 0 || ord > values.length) {
                    return new KeyRegister(keyText);
                }
                type = values[ord];
            } catch (NumberFormatException e) {
                return new KeyRegister(keyText);
            }
            try {
                int ord = Integer.parseInt(parts[2]);
                Modifier[] values = Modifier.values();
                if (ord < 0 || ord > values.length) {
                    return new KeyRegister(keyText);
                }
                modifier = values[ord];
            } catch (NumberFormatException e) {
                return new KeyRegister(keyText);
            }
            try {
                int ord = Integer.parseInt(parts[3]);
                KeyLocation[] values = KeyLocation.values();
                if (ord < 0 || ord > values.length) {
                    return new KeyRegister(keyText);
                }
                keyLocation = values[ord];
            } catch (NumberFormatException e) {
                return new KeyRegister(keyText);
            }
            return new KeyRegister(keyText, type, modifier, keyLocation);
        }

        public static KeyRegister registerCtrlKey(String keyText) {
            return new KeyRegister(keyText, Type.KEY_PRESSED, Modifier.CTRL, KeyLocation.ANY);
        }

        public static KeyRegister registerAltKey(String keyText) {
            return new KeyRegister(keyText, Type.KEY_PRESSED, Modifier.ALT, KeyLocation.ANY);
        }

        public static String getDisplayRepresentation(KeyRegister key) {
            if (key == null) return "None";
            StringBuilder stringBuilder = new StringBuilder();
            switch (key.keyLocation) {
                case KEY_LOCATION_LEFT:
                    stringBuilder.append("Left_");
                    break;
                case KEY_LOCATION_RIGHT:
                    stringBuilder.append("Right_");
                    break;
                case KEY_LOCATION_NUMPAD:
                    stringBuilder.append("Num_");
                    break;
            }
            switch (key.modifier) {
                case CTRL:
                    stringBuilder.append("Ctrl");
                    break;
                case META:
                    stringBuilder.append("Meta");
                    break;
                case SHIFT:
                    stringBuilder.append("Shift");
                    break;
                case ALT:
                    stringBuilder.append("Alt");
                    break;
                case ALT_GR:
                    stringBuilder.append("AltGr");
                    break;
                default:
                    break;
            }
            if (!key.keyText.isEmpty()) {
                stringBuilder.append("+").append(key.keyText);
            }
            return stringBuilder.toString();
        }

        public KeyLocation getKeyLocation() {
            return keyLocation;
        }

        public Modifier getModifier() {
            return modifier;
        }

        public String getKeyText() {
            return keyText;
        }

        public Type getType() {
            return type;
        }

        public boolean check(KeyEvent keyEvent) {
            boolean isTextOk = this.keyText.equals(KeyEvent.getKeyText(keyEvent.getKeyCode()));
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

        @Override
        public String toString() {
            return String.format("%s|||%d|||%d|||%d", keyText, type.ordinal(), modifier.ordinal(), keyLocation.ordinal());
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
            CTRL(0),
            META(1),
            SHIFT(2),
            ALT(3),
            ALT_GR(4),
            NONE(5),
            ANY(6);
            public final int id;

            Modifier(int id) { this.id = id; }

            public int getValue() { return id; }
        }
    }
}

package kaptainwutax.minemap.config;

import com.google.gson.annotations.Expose;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.KeyShortcuts;
import kaptainwutax.minemap.init.Logger;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class KeyboardsConfig extends Config {
    @Expose
    protected Map<KeyShortcuts.ShortcutAction, String> KEYBOARDS = new LinkedHashMap<>();
    @Expose
    protected Map<KeyShortcuts.ShortcutAction, String> OVERRIDES = new LinkedHashMap<>();

    public static KeyShortcuts.KeyRegister getKeyCombo(KeyShortcuts.ShortcutAction shortcutAction) {
        return Configs.KEYBOARDS.OVERRIDES.entrySet()
                .stream()
                .filter(entry -> entry.getKey().equals(shortcutAction)).map(Map.Entry::getValue)
                .findFirst().map(KeyShortcuts.KeyRegister::initFromString)
                .orElse(
                        Configs.KEYBOARDS.KEYBOARDS.entrySet()
                                .stream()
                                .filter(entry -> entry.getKey().equals(shortcutAction)).map(Map.Entry::getValue)
                                .findFirst().map(KeyShortcuts.KeyRegister::initFromString)
                                .orElse(null)
                );

    }

    public static String getKeyComboString(KeyShortcuts.ShortcutAction shortcutAction) {
        return KeyShortcuts.KeyRegister.getDisplayRepresentation(getKeyCombo(shortcutAction));
    }

    public Map<KeyShortcuts.ShortcutAction, KeyShortcuts.KeyRegister> getKEYBOARDS() {
        return KEYBOARDS.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> KeyShortcuts.KeyRegister.initFromString(entry.getValue())
        ));
    }

    public Map<KeyShortcuts.ShortcutAction, KeyShortcuts.KeyRegister> getOVERRIDES() {
        return OVERRIDES.entrySet().stream().collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> KeyShortcuts.KeyRegister.initFromString(entry.getValue())
        ));
    }

    public Map<KeyShortcuts.ShortcutAction, KeyShortcuts.KeyRegister> getRegisters() {
        Map<KeyShortcuts.ShortcutAction, KeyShortcuts.KeyRegister> shortcuts = new LinkedHashMap<>(this.getKEYBOARDS());
        Map<KeyShortcuts.ShortcutAction, KeyShortcuts.KeyRegister> overrides = this.getOVERRIDES();
        for (KeyShortcuts.ShortcutAction s : overrides.keySet()) {
            shortcuts.put(s, overrides.get(s));
        }
        return shortcuts;
    }

    public Map<KeyShortcuts.ShortcutAction, String> getShortcuts() {
        Map<KeyShortcuts.ShortcutAction, String> shortcuts = new LinkedHashMap<>(this.KEYBOARDS);
        for (KeyShortcuts.ShortcutAction s : this.OVERRIDES.keySet()) {
            shortcuts.put(s, this.OVERRIDES.get(s));
        }
        return shortcuts;
    }

    @Override
    public String getName() {
        return "keyboards";
    }

    @Override
    public void maintainConfig() {
        this.resetConfig();
    }

    @Override
    protected void resetConfig() {
        this.KEYBOARDS.clear();
        this.addDefaultEntry(KeyShortcuts.ShortcutAction.NEW_SEED, KeyShortcuts.KeyRegister.registerCtrlKey("N"));
        this.addDefaultEntry(KeyShortcuts.ShortcutAction.SCREENSHOT, KeyShortcuts.KeyRegister.registerCtrlKey("S"));
        this.addDefaultEntry(KeyShortcuts.ShortcutAction.CLOSE, KeyShortcuts.KeyRegister.registerCtrlKey("Q"));
        this.addDefaultEntry(KeyShortcuts.ShortcutAction.GO_TO_COORDS, KeyShortcuts.KeyRegister.registerAltKey("G"));
        this.addDefaultEntry(KeyShortcuts.ShortcutAction.GO_TO_SPAWN, KeyShortcuts.KeyRegister.registerAltKey("P"));
        this.addDefaultEntry(KeyShortcuts.ShortcutAction.LOAD_SHADOW_SEED, KeyShortcuts.KeyRegister.registerAltKey("L"));
        this.addDefaultEntry(KeyShortcuts.ShortcutAction.GO_TO_STRUCTURE, KeyShortcuts.KeyRegister.registerAltKey("S"));
        this.addDefaultEntry(KeyShortcuts.ShortcutAction.CHANGE_SALTS, KeyShortcuts.KeyRegister.registerAltKey("C"));
        this.addDefaultEntry(KeyShortcuts.ShortcutAction.TOGGLE_STS_MODE, KeyShortcuts.KeyRegister.registerAltKey("A"));
        this.addDefaultEntry(KeyShortcuts.ShortcutAction.SHORTCUTS, KeyShortcuts.KeyRegister.registerAltKey("K"));
        this.addDefaultEntry(KeyShortcuts.ShortcutAction.ZOOM_IN, KeyShortcuts.KeyRegister.registerCtrlKey("NumPad +"));
        this.addDefaultEntry(KeyShortcuts.ShortcutAction.ZOOM_OUT, KeyShortcuts.KeyRegister.registerCtrlKey("NumPad -"));
        this.addDefaultEntry(KeyShortcuts.ShortcutAction.LAYER_ZOOM_IN, KeyShortcuts.KeyRegister.registerAltKey("NumPad +"));
        this.addDefaultEntry(KeyShortcuts.ShortcutAction.LAYER_ZOOM_OUT, KeyShortcuts.KeyRegister.registerAltKey("NumPad -"));
    }

    public void resetOverrides() {
        this.OVERRIDES.clear();
    }

    private void addDefaultEntry(KeyShortcuts.ShortcutAction shortcutAction, KeyShortcuts.KeyRegister keyCombo) {
        KEYBOARDS.put(shortcutAction, keyCombo.toString());
    }

    public void addOverrideEntry(KeyShortcuts.ShortcutAction shortcutAction, KeyShortcuts.KeyRegister keyCombo) {
        OVERRIDES.put(shortcutAction, keyCombo.toString());
    }

    public void flush() {
        try {
            this.writeConfig();
        } catch (IOException e) {
            Logger.LOGGER.severe(e.toString());
            e.printStackTrace();
        }
    }


}

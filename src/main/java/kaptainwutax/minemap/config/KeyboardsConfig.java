package kaptainwutax.minemap.config;

import com.google.gson.annotations.Expose;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.KeyShortcuts;
import kaptainwutax.seedutils.mc.MCVersion;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class KeyboardsConfig extends Config {
    @Expose
    protected Map<String, KeyShortcuts.Shortcut> KEYBOARDS = new LinkedHashMap<>();
    @Expose
    protected Map<String, KeyShortcuts.Shortcut> OVERRIDES = new LinkedHashMap<>();

    public Map<KeyShortcuts.KeyRegister, KeyShortcuts.Shortcut> getKEYBOARDS() {
        return KEYBOARDS.entrySet().stream().collect(Collectors.toMap(
                entry -> KeyShortcuts.KeyRegister.initFromString(entry.getKey()),
                Map.Entry::getValue
        ));
    }

    public Map<String, KeyShortcuts.Shortcut> getShortcuts(){
        Map<String, KeyShortcuts.Shortcut> shortcuts = new LinkedHashMap<>(this.KEYBOARDS);
        for (String s : this.OVERRIDES.keySet()) {
            shortcuts.put(s, this.OVERRIDES.get(s));
        }
        return shortcuts;
    }

    public Map<KeyShortcuts.KeyRegister, KeyShortcuts.Shortcut> getOVERRIDES() {
        return OVERRIDES.entrySet().stream().collect(Collectors.toMap(
                entry -> KeyShortcuts.KeyRegister.initFromString(entry.getKey()),
                Map.Entry::getValue
        ));
    }

    public static KeyShortcuts.KeyRegister getKeyCombo(KeyShortcuts.Shortcut shortcut) {
        return Configs.KEYBOARDS.OVERRIDES.entrySet()
                .stream()
                .filter(entry -> entry.getValue().equals(shortcut)).map(Map.Entry::getKey)
                .findFirst().map(KeyShortcuts.KeyRegister::initFromString)
                .orElse(
                        Configs.KEYBOARDS.KEYBOARDS.entrySet()
                                .stream()
                                .filter(entry -> entry.getValue().equals(shortcut)).map(Map.Entry::getKey)
                                .findFirst().map(KeyShortcuts.KeyRegister::initFromString)
                                .orElse(null)
                );

    }

    public static String getKeyComboString(KeyShortcuts.Shortcut shortcut) {
        return KeyShortcuts.KeyRegister.getDisplayRepresentation(getKeyCombo(shortcut));
    }

    @Override
    public String getName() {
        return "keyboards";
    }

    @Override
    protected void resetConfig() {
        this.KEYBOARDS.clear();
        this.addDefaultEntry(KeyShortcuts.KeyRegister.registerCtrlKey("N"), KeyShortcuts.Shortcut.NEW_SEED);
        this.addDefaultEntry(KeyShortcuts.KeyRegister.registerCtrlKey("S"), KeyShortcuts.Shortcut.SCREENSHOT);
        this.addDefaultEntry(KeyShortcuts.KeyRegister.registerCtrlKey("W"), KeyShortcuts.Shortcut.CLOSE);
        this.addDefaultEntry(KeyShortcuts.KeyRegister.registerAltKey("G"), KeyShortcuts.Shortcut.GO_TO_COORDS);
        this.addDefaultEntry(KeyShortcuts.KeyRegister.registerAltKey("P"), KeyShortcuts.Shortcut.GO_TO_SPAWN);
        this.addDefaultEntry(KeyShortcuts.KeyRegister.registerAltKey("L"), KeyShortcuts.Shortcut.LOAD_SHADOW_SEED);
        this.addDefaultEntry(KeyShortcuts.KeyRegister.registerAltKey("S"), KeyShortcuts.Shortcut.GO_TO_STRUCTURE);
        this.addDefaultEntry(KeyShortcuts.KeyRegister.registerAltKey("C"), KeyShortcuts.Shortcut.CHANGE_SALTS);
        this.addDefaultEntry(KeyShortcuts.KeyRegister.registerAltKey("A"), KeyShortcuts.Shortcut.TOGGLE_STS_MODE);
        this.addDefaultEntry(KeyShortcuts.KeyRegister.registerAltKey("K"), KeyShortcuts.Shortcut.SHORTCUTS);
    }

    public void resetOverrides() {
        this.OVERRIDES.clear();
    }

    private void addDefaultEntry(KeyShortcuts.KeyRegister keyCombo, KeyShortcuts.Shortcut shortcut) {
        KEYBOARDS.put(keyCombo.toString(), shortcut);
    }

    public void addOverrideEntry(KeyShortcuts.KeyRegister keyCombo, KeyShortcuts.Shortcut shortcut) {
        OVERRIDES.put(keyCombo.toString(), shortcut);
    }

    public void flush() {
        try {
            this.writeConfig();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

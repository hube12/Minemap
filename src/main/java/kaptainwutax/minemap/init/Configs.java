package kaptainwutax.minemap.init;

import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.config.*;

public class Configs {

    public static BiomeColorsConfig BIOME_COLORS;
    public static SaltsConfig SALTS;
    public static KeyboardsConfig KEYBOARDS;
    public static IconsConfig ICONS;
    public static UserProfileConfig USER_PROFILE;
    private static boolean shouldDelayedUpdate = false;

    public static void registerConfigs() {
        USER_PROFILE = (UserProfileConfig) new UserProfileConfig().readConfig();
        BIOME_COLORS = (BiomeColorsConfig) new BiomeColorsConfig().readConfig();
        SALTS = (SaltsConfig) new SaltsConfig().readConfig();
        ICONS = (IconsConfig) new IconsConfig().readConfig();
        Configs.updateConfigs();
    }

    public static void registerDelayedConfigs() {
        KEYBOARDS = (KeyboardsConfig) new KeyboardsConfig().readConfig();
        Configs.updateDelayedConfigs();
    }

    public static void updateConfigs() {
        if (USER_PROFILE == null) USER_PROFILE = (UserProfileConfig) new UserProfileConfig().forceGenerateConfig();
        if (BIOME_COLORS == null) BIOME_COLORS = (BiomeColorsConfig) new BiomeColorsConfig().forceGenerateConfig();
        if (SALTS == null) SALTS = (SaltsConfig) new SaltsConfig().forceGenerateConfig();
        if (ICONS == null) ICONS = (IconsConfig) new IconsConfig().forceGenerateConfig();

        if (USER_PROFILE.getMinemapVersion() == null || !USER_PROFILE.getMinemapVersion().equals(MineMap.version)) {
            USER_PROFILE.updateConfig();
            BIOME_COLORS.updateConfig();
            SALTS.updateConfig();
            ICONS.updateConfig();
            shouldDelayedUpdate = true;
        }
    }

    public static void updateDelayedConfigs() {
        if (shouldDelayedUpdate) {
            KEYBOARDS.updateConfig();
            shouldDelayedUpdate = false;
        }
    }
}

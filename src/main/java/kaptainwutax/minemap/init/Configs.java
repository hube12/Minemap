package kaptainwutax.minemap.init;

import kaptainwutax.minemap.config.*;

public class Configs {

	public static BiomeColorsConfig BIOME_COLORS;
	public static SaltsConfig SALTS;
	public static KeyboardsConfig KEYBOARDS;
	public static IconsConfig ICONS;
	public static UserProfileConfig USER_PROFILE;

	public static void registerConfigs() {
		BIOME_COLORS = (BiomeColorsConfig)new BiomeColorsConfig().readConfig();
		SALTS = (SaltsConfig)new SaltsConfig().readConfig();
		ICONS = (IconsConfig) new IconsConfig().readConfig();
		USER_PROFILE = (UserProfileConfig)new UserProfileConfig().readConfig();
	}

	public static void registerDelayedConfigs() {
		KEYBOARDS = (KeyboardsConfig)new KeyboardsConfig().forceGenerateConfig();
	}

}

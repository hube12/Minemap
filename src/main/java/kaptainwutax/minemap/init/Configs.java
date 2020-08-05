package kaptainwutax.minemap.init;

import kaptainwutax.minemap.config.BiomeColorsConfig;
import kaptainwutax.minemap.config.UserProfileConfig;

public class Configs {

	public static BiomeColorsConfig BIOME_COLORS;
	public static UserProfileConfig USER_PROFILE;

	public static void registerConfigs() {
		BIOME_COLORS = (BiomeColorsConfig)new BiomeColorsConfig().readConfig();
		USER_PROFILE = (UserProfileConfig)new UserProfileConfig().readConfig();
	}

}

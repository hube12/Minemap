package kaptainwutax.minemap.config;

import com.google.gson.annotations.Expose;
import kaptainwutax.featureutils.misc.SlimeChunk;
import kaptainwutax.featureutils.structure.Mineshaft;
import kaptainwutax.featureutils.structure.NetherFossil;
import kaptainwutax.minemap.feature.OWBastionRemnant;
import kaptainwutax.minemap.feature.OWFortress;
import kaptainwutax.minemap.ui.map.MapSettings;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.MCVersion;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class UserProfileConfig extends Config {

    @Expose protected int THREAD_COUNT;
    @Expose protected MCVersion MC_VERSION;
    @Expose protected UserSettings USER_SETTINGS;
    @Expose protected Map<String, Boolean> DIMENSIONS = new LinkedHashMap<>();
    @Expose protected Map<String, MapSettings> DEFAULT_MAP_SETTINGS = new LinkedHashMap<>();

    @Override
    public String getName() {
        return "user_profile";
    }

    public int getThreadCount(int cores) {
        if(this.THREAD_COUNT < 1)return 1;
        return Math.min(this.THREAD_COUNT, cores);
    }

    public MCVersion getVersion() {
        return this.MC_VERSION;
    }

    public UserSettings getUserSettings() {
        return USER_SETTINGS;
    }

    public boolean isDimensionEnabled(Dimension dimension) {
        return this.DIMENSIONS.get(dimension.name);
    }

    public List<Dimension> getEnabledDimensions() {
        return this.DIMENSIONS.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey)
                .map(Dimension::fromString).collect(Collectors.toList());
    }

    public MapSettings getMapSettingsCopy(MCVersion version, Dimension dimension) {
        return this.DEFAULT_MAP_SETTINGS.get(dimension.name).copyFor(version, dimension);
    }

    public void setThreadCount(int threadCount) {
        this.THREAD_COUNT = threadCount;
        this.flush();
    }

    public void setVersion(MCVersion version) {
        this.MC_VERSION = version;
        this.flush();
    }

    public void setDefaultSettings(Dimension dimension, MapSettings settings) {
        this.DEFAULT_MAP_SETTINGS.put(dimension.name, settings.copy());
        this.flush();
    }

    public void setDimensionState(Dimension dimension, boolean state) {
        this.DIMENSIONS.put(dimension.name, state);
        this.flush();
    }

    public void flush() {
        try {
            this.writeConfig();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void resetConfig() {
        this.THREAD_COUNT = 1;
        this.MC_VERSION = MCVersion.values()[0];
        this.USER_SETTINGS = new UserSettings();

        for(Dimension dimension: Dimension.values()) {
            this.DIMENSIONS.put(dimension.name, true);
            MapSettings settings = new MapSettings(dimension).refresh();
            settings.hide(SlimeChunk.class, Mineshaft.class, OWBastionRemnant.class, OWFortress.class, NetherFossil.class);
            this.DEFAULT_MAP_SETTINGS.put(dimension.name, settings);
        }
    }

}

package kaptainwutax.minemap.config;

import com.google.gson.annotations.Expose;
import kaptainwutax.featureutils.misc.SlimeChunk;
import kaptainwutax.featureutils.structure.Mineshaft;
import kaptainwutax.featureutils.structure.NetherFossil;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.feature.NEStronghold;
import kaptainwutax.minemap.feature.OWBastionRemnant;
import kaptainwutax.minemap.feature.OWFortress;
import kaptainwutax.minemap.init.Logger;
import kaptainwutax.minemap.ui.map.MapSettings;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

public class UserProfileConfig extends Config {

    @Expose
    protected int THREAD_COUNT;
    @Expose
    protected MCVersion MC_VERSION;
    @Expose
    protected MCVersion ASSETS_VERSION;
    @Expose
    protected String MINEMAP_VERSION;
    @Expose
    protected UserSettings USER_SETTINGS;
    @Expose
    protected Queue<String> RECENT_SEEDS=new LinkedBlockingQueue<>(20);
    @Expose
    protected Map<String, Boolean> DIMENSIONS = new LinkedHashMap<>();
    @Expose
    protected Map<String, MapSettings> DEFAULT_MAP_SETTINGS = new LinkedHashMap<>();

    @Override
    public String getName() {
        return "user_profile";
    }


    public int getThreadCount(int cores) {
        if (this.THREAD_COUNT < 1) return 1;
        return Math.min(this.THREAD_COUNT, cores);
    }

    public String getMinemapVersion() {
        return MINEMAP_VERSION;
    }

    public void setMinemapVersion(String version) {
        this.MINEMAP_VERSION = version;
        this.flush();
    }

    public MCVersion getVersion() {
        return this.MC_VERSION;
    }

    public void setVersion(MCVersion version) {
        this.MC_VERSION = version;
        this.flush();
    }

    public MCVersion getAssetVersion() {
        return this.ASSETS_VERSION;
    }

    public UserSettings getUserSettings() {
        return USER_SETTINGS;
    }

    public boolean isDimensionEnabled(Dimension dimension) {
        return this.DIMENSIONS.get(dimension.getName());
    }

    public List<Dimension> getEnabledDimensions() {
        return this.DIMENSIONS.entrySet().stream().filter(Map.Entry::getValue).map(Map.Entry::getKey)
            .map(Dimension::fromString).collect(Collectors.toList());
    }

    public MapSettings getMapSettingsCopy(MCVersion version, Dimension dimension) {
        return this.DEFAULT_MAP_SETTINGS.get(dimension.getName()).copyFor(version, dimension);
    }

    public void setThreadCount(int threadCount) {
        this.THREAD_COUNT = threadCount;
        this.flush();
    }

    public void setAssetsVersion(MCVersion version) {
        this.ASSETS_VERSION = version;
        this.flush();
    }

    public void setDefaultSettings(Dimension dimension, MapSettings settings) {
        this.DEFAULT_MAP_SETTINGS.put(dimension.getName(), settings.copy());
        this.flush();
    }

    public void setDimensionState(Dimension dimension, boolean state) {
        this.DIMENSIONS.put(dimension.getName(), state);
        this.flush();
    }

    public Queue<String> getRecentSeeds() {
        return RECENT_SEEDS;
    }

    public void addRecentSeed(long seed,MCVersion version){
        String pair=seed+"::"+version;
        if (!RECENT_SEEDS.offer(pair)){
            String head=RECENT_SEEDS.poll();
            if (head==null){
                Logger.LOGGER.severe("Queue has no capacity ? "+ RECENT_SEEDS.peek());
            }
            if (! RECENT_SEEDS.offer(pair)){
                Logger.LOGGER.severe("Queue could not insert after removal: "+ RECENT_SEEDS.peek());
            }
        }
        this.flush();
    }

    public void flush() {
        try {
            this.writeConfig();
        } catch (IOException e) {
            Logger.LOGGER.severe(e.toString());
            e.printStackTrace();
        }
    }

    @Override
    protected void resetConfig() {
        this.THREAD_COUNT = 1;
        this.MC_VERSION = MCVersion.values()[0];
        this.USER_SETTINGS = new UserSettings();
        this.MINEMAP_VERSION = MineMap.version;
        this.ASSETS_VERSION = null; // allowed since I use null as an invalid version

        for (Dimension dimension : Dimension.values()) {
            this.DIMENSIONS.put(dimension.getName(), true);
            MapSettings settings = new MapSettings(dimension).refresh();
            settings.hide(SlimeChunk.class, Mineshaft.class, OWBastionRemnant.class, OWFortress.class, NetherFossil.class, NEStronghold.class);
            this.DEFAULT_MAP_SETTINGS.put(dimension.getName(), settings);
        }
    }

    @Override
    public void maintainConfig() {
        this.THREAD_COUNT = this.THREAD_COUNT == 0 ? 1 : this.THREAD_COUNT;
        this.MC_VERSION = this.MC_VERSION == null ? MCVersion.values()[0] : this.MC_VERSION;
        this.USER_SETTINGS = this.USER_SETTINGS == null ? new UserSettings() : this.USER_SETTINGS;
        this.MINEMAP_VERSION = this.MINEMAP_VERSION == null ? MineMap.version : this.MINEMAP_VERSION;
        //this.ASSET_VERSION=this.ASSET_VERSION; // allowed since I use null as an invalid version
        for (Dimension dimension : Dimension.values()) {
            if (!this.DIMENSIONS.containsKey(dimension.getName())) {
                this.DIMENSIONS.put(dimension.getName(), true);
            }
            if (!this.DEFAULT_MAP_SETTINGS.containsKey(dimension.getName())) {
                MapSettings settings = new MapSettings(dimension).refresh();
                settings.hide(SlimeChunk.class, Mineshaft.class, OWBastionRemnant.class, OWFortress.class, NetherFossil.class, NEStronghold.class);
                this.DEFAULT_MAP_SETTINGS.put(dimension.getName(), settings);
            }
            // TODO hide NEStronghold by default (need versionned config ordered)
        }
    }

}

package kaptainwutax.minemap.config;

import com.google.gson.annotations.Expose;
import kaptainwutax.featureutils.misc.SlimeChunk;
import kaptainwutax.featureutils.structure.Mineshaft;
import kaptainwutax.featureutils.structure.NetherFossil;
import kaptainwutax.mcutils.state.Dimension;
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
    public static int MAX_SIZE = 15;
    @Expose
    protected int THREAD_COUNT;
    @Expose
    protected MCVersion MC_VERSION;
    @Expose
    protected MCVersion ASSETS_VERSION;
    @Expose
    protected String MINEMAP_VERSION;
    @Expose
    protected String OLD_MINEMAP_VERSION;
    @Expose
    protected UserSettings USER_SETTINGS;
    @Expose
    protected LinkedBlockingQueue<String> RECENT_SEEDS = new LinkedBlockingQueue<>(MAX_SIZE);
    @Expose
    protected LinkedBlockingQueue<String> PINNED_SEEDS = new LinkedBlockingQueue<>(MAX_SIZE);
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

    public Map<String, MapSettings> getDefaultMapSettings() {
        return DEFAULT_MAP_SETTINGS;
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

    public void addRecentSeed(long seed, MCVersion version) {
        String pair = seed + "::" + version;
        if (RECENT_SEEDS.contains(pair)) return;
        if (!RECENT_SEEDS.offer(pair)) {
            String head = RECENT_SEEDS.poll();
            if (head == null) {
                Logger.LOGGER.severe("Queue has no capacity ? " + RECENT_SEEDS.peek());
            }
            if (!RECENT_SEEDS.offer(pair)) {
                Logger.LOGGER.severe("Queue could not insert after removal: " + RECENT_SEEDS.peek());
            }
        }
        this.flush();
    }

    public Queue<String> getPinnedSeeds() {
        return PINNED_SEEDS;
    }

    public void addPinnedSeed(long seed, MCVersion version, Dimension dimension) {
        String pair = seed + "::" + version + "::" + dimension.getId();
        if (PINNED_SEEDS.contains(pair)) return;
        if (!PINNED_SEEDS.offer(pair)) {
            String head = PINNED_SEEDS.poll();
            if (head == null) {
                Logger.LOGGER.severe("Queue has no capacity ? " + PINNED_SEEDS.peek());
            }
            if (!PINNED_SEEDS.offer(pair)) {
                Logger.LOGGER.severe("Queue could not insert after removal: " + PINNED_SEEDS.peek());
            }
        }
        this.flush();
    }

    public void removePinnedSeed(long seed, MCVersion version, Dimension dimension) {
        String pair = seed + "::" + version + "::" + dimension.getId();
        if (!PINNED_SEEDS.remove(pair)) {
            Logger.LOGGER.info("This seed was not in the queue " + seed);
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
            settings.resetConfig();
            this.DEFAULT_MAP_SETTINGS.put(dimension.getName(), settings);
        }
    }

    @Override
    public Config readConfig() {
        UserProfileConfig config = (UserProfileConfig) super.readConfig();
        config.RECENT_SEEDS = resizeQueue(config.RECENT_SEEDS, MAX_SIZE);
        config.PINNED_SEEDS = resizeQueue(config.PINNED_SEEDS, MAX_SIZE);
        return config;
    }

    @Override
    public void maintainConfig() {
        this.THREAD_COUNT = this.THREAD_COUNT == 0 ? 1 : this.THREAD_COUNT;
        this.MC_VERSION = this.MC_VERSION == null ? MCVersion.values()[0] : this.MC_VERSION;
        this.USER_SETTINGS = this.USER_SETTINGS == null ? new UserSettings() : this.USER_SETTINGS;
        this.OLD_MINEMAP_VERSION = this.OLD_MINEMAP_VERSION == null ? this.MINEMAP_VERSION : this.OLD_MINEMAP_VERSION;
        this.MINEMAP_VERSION = MineMap.version;
        //this.ASSET_VERSION=this.ASSET_VERSION; // allowed since I use null as an invalid version
        for (Dimension dimension : Dimension.values()) {
            String old = dimension.getName().replace("the_", "");
            if (!this.DIMENSIONS.containsKey(dimension.getName())) {
                // this is a hacky fix for the migration
                this.DIMENSIONS.put(dimension.getName(), this.DIMENSIONS.getOrDefault(old, true));
            }
            if (!this.DEFAULT_MAP_SETTINGS.containsKey(dimension.getName())) {
                MapSettings settings = new MapSettings(dimension).refresh();
                settings.hide(SlimeChunk.class, Mineshaft.class, OWBastionRemnant.class, OWFortress.class, NetherFossil.class, NEStronghold.class);
                this.DEFAULT_MAP_SETTINGS.put(dimension.getName(), this.DEFAULT_MAP_SETTINGS.getOrDefault(old, settings));
            }
            // Cleanup for old values
            if (!old.equals(dimension.getName())) {
                this.DIMENSIONS.remove(old);
                this.DEFAULT_MAP_SETTINGS.remove(old);
            }
            MapSettings settings=this.DEFAULT_MAP_SETTINGS.get(dimension.getName());
            settings.maintainConfig(dimension,this.MC_VERSION);
        }
        this.RECENT_SEEDS = resizeQueue(this.RECENT_SEEDS, MAX_SIZE);
        this.PINNED_SEEDS = resizeQueue(this.PINNED_SEEDS, MAX_SIZE);
    }

    @SuppressWarnings("unchecked")
    private static <T> LinkedBlockingQueue<T> resizeQueue(LinkedBlockingQueue<T> queue, int size) {
        Object[] recentSeeds = queue.toArray();
        queue.clear();
        queue = new LinkedBlockingQueue<>(MAX_SIZE);
        for (int i = 0; i < Math.min(size, recentSeeds.length); i++) {
            if (!queue.offer((T) recentSeeds[i])) Logger.LOGGER.severe("The Queue is not sized correctly " + i + " " + MAX_SIZE + " " + queue.size() + " " + Arrays.toString(queue.toArray()));
        }
        return queue;
    }

}

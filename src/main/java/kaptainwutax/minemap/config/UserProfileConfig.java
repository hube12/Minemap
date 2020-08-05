package kaptainwutax.minemap.config;

import com.google.gson.annotations.Expose;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.seedutils.mc.MCVersion;

import java.io.IOException;

public class UserProfileConfig extends Config {

    @Expose protected int THREAD_COUNT;
    @Expose protected MCVersion MC_VERSION;
    @Expose protected String STYLE;

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

    public String getStyle() {
        return this.STYLE;
    }

    public void setThreadCount(int threadCount) {
        this.THREAD_COUNT = threadCount;
        this.flush();
    }

    public void setVersion(MCVersion version) {
        this.MC_VERSION = version;
        this.flush();
    }

    public void setStyle(String style) {
        if(!this.STYLE.equals(style)) {
            MineMap.INSTANCE.seedTabs.invalidateAll();
            this.STYLE = style;
        }

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
        this.STYLE = "Default";
    }

}

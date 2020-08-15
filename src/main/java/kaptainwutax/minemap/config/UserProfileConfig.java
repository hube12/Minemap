package kaptainwutax.minemap.config;

import com.google.gson.annotations.Expose;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.MCVersion;
import java.io.IOException;
import java.util.Map;

public class UserProfileConfig extends Config {

    @Expose protected int THREAD_COUNT;
    @Expose protected MCVersion MC_VERSION;
    @Expose protected String STYLE;
    @Expose protected Boolean OWenabled;
    @Expose protected Boolean Netherenabled;
    @Expose protected Boolean Endenabled;

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

    public Boolean getOWenabled() {
        return this.OWenabled;
    }

    public Boolean getNetherenabled() {
        return this.Netherenabled;
    }

    public Boolean getEndenabled() {
        return this.Endenabled;
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
            this.STYLE = style;
            MineMap.INSTANCE.worldTabs.invalidateAll();
        }

        this.flush();
    }

    public void setOWenabled(Boolean OWenabled) {
        this.OWenabled = OWenabled;
        this.flush();
    }

    public void setNetherenabled(Boolean Netherenabled) {
        this.Netherenabled = Netherenabled;
        this.flush();
    }

    public void setEndenabled(Boolean Endenabled) {
        this.Endenabled = Endenabled;
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
        this.STYLE = BiomeColorsConfig.DEFAULT_STYLE_NAME;
        this.OWenabled = true;
        this.Netherenabled = true;
        this.Endenabled = true;
    }
}

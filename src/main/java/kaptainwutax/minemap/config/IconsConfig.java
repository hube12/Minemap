package kaptainwutax.minemap.config;

import com.google.gson.annotations.Expose;
import kaptainwutax.featureutils.decorator.EndGateway;
import kaptainwutax.featureutils.misc.SlimeChunk;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.minemap.feature.*;
import kaptainwutax.minemap.init.Logger;
import kaptainwutax.minemap.ui.map.tool.Area;
import kaptainwutax.minemap.ui.map.tool.Circle;
import kaptainwutax.minemap.ui.map.tool.Ruler;
import kaptainwutax.minemap.util.ui.buttons.CloseButton;
import kaptainwutax.minemap.util.ui.buttons.CopyButton;
import kaptainwutax.minemap.util.ui.buttons.InfoButton;
import kaptainwutax.minemap.util.ui.buttons.JumpButton;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

public class IconsConfig extends Config {
    @Expose
    protected Map<String, Double> ICON_SIZES = new LinkedHashMap<>();
    @Expose
    protected Map<String, Double> OVERRIDES = new LinkedHashMap<>();
    @Expose
    protected Map<String, String> CLASS_ICON_PATH = new LinkedHashMap<>();
    @Expose
    protected Map<String, String> USER_CLASS_ICON_PATH = new LinkedHashMap<>();
    @Expose
    protected Map<String, String> OBJECT_ICON_PATH = new LinkedHashMap<>();
    @Expose
    protected Map<String, String> USER_OBJECT_ICON_PATH = new LinkedHashMap<>();

    public <T> Double getSize(Class<T> clazz) {
        if (OVERRIDES.containsKey(clazz.toGenericString())) {
            return OVERRIDES.get(clazz.toGenericString());
        }
        if (ICON_SIZES.containsKey(clazz.toGenericString())) {
            return ICON_SIZES.get(clazz.toGenericString());
        }
        return 1.0D;
    }

    @Override
    public String getName() {
        return "icons";
    }

    @Override
    public void maintainConfig() {
        this.resetSizeConfig();
    }

    @Override
    protected void resetConfig() {
        resetSizeConfig();
    }

    private void resetSizeConfig() {
        this.ICON_SIZES.clear();

        addDefaultEntry(BastionRemnant.class, 1.0);
        addDefaultEntry(BuriedTreasure.class, 0.6);
        addDefaultEntry(DesertPyramid.class, 1.0);
        addDefaultEntry(EndCity.class, 1.0);
        addDefaultEntry(Fortress.class, 1.0);
        addDefaultEntry(Igloo.class, 0.6);
        addDefaultEntry(JunglePyramid.class, 1.0);
        addDefaultEntry(Mansion.class, 1.0);
        addDefaultEntry(Mineshaft.class, 1.0);
        addDefaultEntry(Monument.class, 1.0);
        addDefaultEntry(NetherFossil.class, 1.0);
        addDefaultEntry(OceanRuin.class, 1.0);
        addDefaultEntry(PillagerOutpost.class, 1.0);
        addDefaultEntry(OWRuinedPortal.class, 0.7);
        addDefaultEntry(NERuinedPortal.class, 0.7);
        addDefaultEntry(Shipwreck.class, 1.0);
        addDefaultEntry(SwampHut.class, 1.0);
        addDefaultEntry(Village.class, 1.0);
        addDefaultEntry(Stronghold.class, 1.0);

        addDefaultEntry(OWBastionRemnant.class, 1.0);
        addDefaultEntry(OWFortress.class, 1.0);

        addDefaultEntry(NEStronghold.class, 1.0);

        addDefaultEntry(EndGateway.class, 1.0);
        addDefaultEntry(SlimeChunk.class, 1.0);

        addDefaultEntry(SpawnPoint.class, 1.0);

        addDefaultEntry(Ruler.class, 1.0);
        addDefaultEntry(Area.class, 1.0);
        addDefaultEntry(Circle.class, 1.0);


        addDefaultEntry(CloseButton.class, 1.0);
        addDefaultEntry(CopyButton.class, 1.0);
        addDefaultEntry(JumpButton.class, 1.0);
        addDefaultEntry(InfoButton.class, 1.0);
    }

    public void resetOverrides() {
        this.OVERRIDES.clear();
    }

    private void addDefaultEntry(Class<?> clazz, Double size) {
        ICON_SIZES.put(clazz.toGenericString(), size);
    }

    private void addOverrideEntry(Class<?> clazz, Double size) {
        OVERRIDES.put(clazz.toGenericString(), size);
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

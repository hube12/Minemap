package kaptainwutax.minemap.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import kaptainwutax.minemap.init.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static kaptainwutax.minemap.MineMap.SETTINGS_DIR;

public abstract class Config {

    private static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT)
            .setPrettyPrinting()
            .enableComplexMapKeySerialization()
            .create();
    protected String root = SETTINGS_DIR;
    protected String extension = ".json";

    public void generateConfig() {
        this.resetConfig();

        try {
            this.writeConfig();
        } catch (IOException e) {
            Logger.LOGGER.severe(e.toString());
            e.printStackTrace();
        }
    }

    private File getConfigFile() {
        return new File(this.root + File.separator + this.getName() + this.extension);
    }

    public abstract String getName();

    public Config readConfig() {
        try {
            return GSON.fromJson(new FileReader(this.getConfigFile()), this.getClass());
        } catch (Exception e) {
            this.generateConfig();
        }

        return this;
    }

    public Config forceGenerateConfig() {
        this.generateConfig();
        return this;
    }

    public void updateConfig() {
        this.maintainConfig();

        try {
            this.writeConfig();
        } catch (IOException e) {
            Logger.LOGGER.severe(e.toString());
            e.printStackTrace();
        }
    }

    public abstract void maintainConfig();

    protected abstract void resetConfig();

    public void writeConfig() throws IOException {
        File dir = new File(this.root);
        if (!dir.exists() && !dir.mkdirs()) return;
        if (!this.getConfigFile().exists() && !this.getConfigFile().createNewFile()) return;
        FileWriter writer = new FileWriter(this.getConfigFile());
        GSON.toJson(this, writer);
        writer.flush();
        writer.close();
    }

}

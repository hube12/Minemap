package kaptainwutax.minemap.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;

public abstract class Config {

	private static final Gson GSON = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT).setPrettyPrinting().create();
	protected String root = "configs/";
	protected String extension = ".json";

	public void generateConfig() {
		this.resetConfig();

		try {
			this.writeConfig();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private File getConfigFile() {
		return new File(this.root + this.getName() + this.extension);
	}

	public abstract String getName();

	public Config readConfig() {
		try {
			return GSON.fromJson(new FileReader(this.getConfigFile()), this.getClass());
		} catch (FileNotFoundException e) {
			this.generateConfig();
		}

		return this;
	}

	public Config forceGenerateConfig() {
		this.generateConfig();
		return this;
	}

	protected abstract void resetConfig();

	public void writeConfig() throws IOException {
		File dir = new File(this.root);
		if(!dir.exists() && !dir.mkdirs())return;
		if(!this.getConfigFile().exists() && !this.getConfigFile().createNewFile())return;
		FileWriter writer = new FileWriter(this.getConfigFile());
		GSON.toJson(this, writer);
		writer.flush();
		writer.close();
	}

}

package kaptainwutax.minemap.util.data;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import kaptainwutax.minemap.init.Logger;
import kaptainwutax.seedutils.mc.MCVersion;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;

import static kaptainwutax.minemap.MineMap.DOWNLOAD_DIR;

public class Assets {
    private static final String MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
    private static final File MANIFEST_FILE = new File(DOWNLOAD_DIR + File.separator + "version_manifest.json");

    public final static String DOWNLOAD_DIR_ICONS = DOWNLOAD_DIR + File.separatorChar + "icons";
    public final static String DOWNLOAD_DIR_VERSIONS = DOWNLOAD_DIR + File.separatorChar + "versions";
    public final static String DOWNLOAD_DIR_ASSETS = DOWNLOAD_DIR + File.separatorChar + "assets";

    private URL url;

    public Assets(MCVersion version) {

    }

    public static void createDirs() throws IOException {
        String[] dirs = {DOWNLOAD_DIR_ICONS, DOWNLOAD_DIR_VERSIONS, DOWNLOAD_DIR_ASSETS};
        for (String dir : dirs) {
            Files.createDirectories(Paths.get(dir));
        }
    }

    /**
     * Get the latest version from the global manifest file
     *
     * @return the latest release version or null if the manifest does not exists or is incorrectly formed
     */
    @SuppressWarnings("unchecked")
    public static MCVersion getLatestVersion() {
        JsonReader jsonReader = openJSON(MANIFEST_FILE);
        if (jsonReader == null) {
            return null;
        }
        Map<String, Object> map = new Gson().fromJson(jsonReader, Map.class);
        if (map.containsKey("latest")) {
            Map<String, String> latest = (Map<String, String>) map.get("latest");
            if (latest.containsKey("release")) {
                String version = latest.get("release");
                return MCVersion.fromString(version);
            }
            Logger.LOGGER.warning("Manifest does not contain a release key");
        } else {
            Logger.LOGGER.warning("Manifest does not contain a latest key");
        }
        return null;
    }

    /**
     * Download a specific version manifest, will return false if the global manifest does not exists
     *
     * @param version a specific version (must not be null)
     * @return a boolean specifying if the version manifest was indeed downloaded
     */
    public static boolean downloadVersionManifest(MCVersion version,boolean force) {
        String versionManifestUrl = getVersionManifestUrl(version);
        if (versionManifestUrl == null) {
            Logger.LOGGER.severe(String.format("URL was not found for %s", version));
            return false;
        }
        File versionManifest=new File(DOWNLOAD_DIR_VERSIONS + File.separator + version.name + ".json");
        if (!force && versionManifest.exists()){
            return true;
        }
        return download(versionManifestUrl,versionManifest , null);
    }

    /**
     * Download the manifest from the mojang servers
     *
     * @param version can be null or a specific version to check if the manifest is up to date (aka if the version is not present the manifest is downloaded again
     * @return boolean to say if the manifest is present and up to date
     */
    public static boolean downloadManifest(MCVersion version) {
        if (!manifestExists(version)) {
            if (!download(MANIFEST_URL, MANIFEST_FILE,null)) {
                return false;
            }
            if (version != null && !manifestExists(version)) {
                Logger.LOGGER.severe(String.format("Manifest was incorrectly downloaded or the version does not exists yet %s %s", MANIFEST_FILE.getAbsolutePath(), version.toString()));
                return false;
            }
        }
        return true;
    }

    public static String downloadVersionAssets(MCVersion version,boolean force) {
        JsonReader jsonReader = openJSON(MANIFEST_FILE);
        if (jsonReader == null) {
            return null;
        }
        Pair<String,String> assetIndexURL = getAssetIndexURL(version);
        if (assetIndexURL == null) {
            Logger.LOGGER.severe(String.format("Could not get asset url for version %s", version.toString()));
            return null;
        }
        String[] urlSplit = assetIndexURL.getFirst().split("/");
        if (urlSplit.length < 2) {
            Logger.LOGGER.severe(String.format("Could not get name of asset from url %s for version %s", assetIndexURL, version.toString()));
            return null;
        }
        String name = urlSplit[urlSplit.length - 1];
        File assetManifest=new File(DOWNLOAD_DIR_ASSETS+File.separator+name);
        if (!force && assetManifest.exists() && compareSha1(assetManifest,assetIndexURL.getSecond())){
            return name;
        }
        return download(assetIndexURL.getFirst(),assetManifest,assetIndexURL.getSecond() )?name:null;

    }


    private static boolean download(String url, File out, String sha1) {
        Logger.LOGGER.info(String.format("Downloading %s for file %s",url,out.getName()));
        ReadableByteChannel rbc;
        try {
            rbc = Channels.newChannel(new URL(url).openStream());
        } catch (IOException e) {
            Logger.LOGGER.severe(String.format("Could not open channel to url %s, error: %s", url, e.toString()));
            return false;
        }
        try {
            new FileOutputStream(out).getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            Logger.LOGGER.severe(String.format("Could not download from channel to url %s for file %s, error: %s", url, out.getAbsolutePath(), e.toString()));
            return false;
        }
        return sha1 == null || compareSha1(out, sha1);
    }


    @SuppressWarnings("unchecked")
    private static Pair<String, String> getAssetIndexURL(MCVersion version) {
        JsonReader jsonReader = openJSON(new File(DOWNLOAD_DIR_VERSIONS + File.separator + version.name + ".json"));
        if (jsonReader == null) {
            return null;
        }
        Map<String, Object> map = new Gson().fromJson(jsonReader, Map.class);
        if (map.containsKey("assetIndex")) {
            Map<String, String> assets = (Map<String, String>) map.get("assetIndex");
            if (assets.containsKey("url") && assets.containsKey("sha1")) {
                return new Pair<>(assets.get("url"), assets.get("sha1"));
            }
            Logger.LOGGER.warning(String.format("Version manifest does not contain a url/sha1 key for %s", version));
        } else {
            Logger.LOGGER.warning(String.format("Version manifest does not contain a assetIndex key for %s", version));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static String getVersionManifestUrl(MCVersion version) {
        JsonReader jsonReader = openJSON(MANIFEST_FILE);
        if (jsonReader == null) {
            return null;
        }
        Map<String, Object> map = new Gson().fromJson(jsonReader, Map.class);
        if (map.containsKey("versions")) {
            ArrayList<Map<String, String>> versions = (ArrayList<Map<String, String>>) map.get("versions");
            Optional<Map<String, String>> versionMap = versions.stream().filter(v -> v.containsKey("id") && v.get("id").equals(version.name)).findFirst();
            if (versionMap.isPresent()) {
                if (versionMap.get().containsKey("url")) {
                    return versionMap.get().get("url");
                }
                Logger.LOGGER.warning(String.format("Manifest does not contain the url/sha1 key for %s", version));
            } else {
                Logger.LOGGER.warning(String.format("Manifest does not contain the version array key for %s", version));
            }
        } else {
            Logger.LOGGER.warning("Manifest does not contain a versions key");
        }
        return null;
    }


    private static JsonReader openJSON(File file) {
        FileReader fileReader;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            Logger.LOGGER.severe(String.format("Could not open file at %s, error: %s", file.getAbsolutePath(), e.toString()));
            return null;
        }
        return new JsonReader(fileReader);
    }


    private static boolean manifestExists(MCVersion version) {
        if (MANIFEST_FILE.exists()) {
            JsonReader jsonReader = openJSON(MANIFEST_FILE);
            if (jsonReader == null) {
                return false;
            }
            try {
                if (version == null || versionExists(version, jsonReader)) {
                    return true;
                }
            } catch (IOException e) {
                Logger.LOGGER.severe(String.format("JSON file had an issue %s, error: %s", MANIFEST_FILE.getAbsolutePath(), e.toString()));
                return false;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static boolean versionExists(MCVersion version, JsonReader jsonReader) throws IOException {
        Map<String, Object> map = new Gson().fromJson(jsonReader, Map.class);
        if (map.containsKey("versions")) {
            ArrayList<Map<String, String>> versions = (ArrayList<Map<String, String>>) map.get("versions");
            return versions.stream().anyMatch(v -> v.containsKey("id") && v.get("id").equals(version.name));
        }
        return false;
    }

    private static boolean compareSha1(File file,String sha1){
        if (sha1!=null && file!=null){
            try{
                return getFileChecksum(MessageDigest.getInstance("SHA-1"), file).equals(sha1);
            }catch (NoSuchAlgorithmException e){
                Logger.LOGGER.severe("Could not compute sha1 since algorithm does not exists");
            }
        }
        return false;
    }

    private static String getFileChecksum(MessageDigest digest, File file){
        try {
            FileInputStream fis = new FileInputStream(file);

            byte[] byteArray = new byte[1024];
            int bytesCount;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
            fis.close();
        }catch (IOException e){
            Logger.LOGGER.severe(String.format("Failed to read file for checksum, error : %s",e));
            return "";
        }
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }
}

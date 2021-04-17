package kaptainwutax.minemap.util.data;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.init.Logger;
import kaptainwutax.mcutils.version.MCVersion;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static kaptainwutax.minemap.MineMap.DOWNLOAD_DIR;
import static kaptainwutax.minemap.init.Logger.LOGGER;

public class Assets {
    public final static String DOWNLOAD_DIR_ICONS = DOWNLOAD_DIR + File.separatorChar + "icons";
    public final static String DOWNLOAD_DIR_VERSIONS = DOWNLOAD_DIR + File.separatorChar + "versions";
    public final static String DOWNLOAD_DIR_ASSETS = DOWNLOAD_DIR + File.separatorChar + "assets";
    private static final String MANIFEST_URL = "https://launchermeta.mojang.com/mc/game/version_manifest.json";
    private static final File MANIFEST_FILE = new File(DOWNLOAD_DIR + File.separator + "version_manifest.json");

    public static void createDirs() throws IOException {
        String[] dirs = {DOWNLOAD_DIR_ICONS, DOWNLOAD_DIR_VERSIONS, DOWNLOAD_DIR_ASSETS};
        for (String dir : dirs) {
            Files.createDirectories(Paths.get(dir));
        }
    }

    @SuppressWarnings("unchecked")
    public static Pair<Pair<String, String>, String> shouldUpdate() {
        String data = getDataRestAPI("https://api.github.com/repos/hube12/MineMap/releases/latest");
        if (data == null) {
            return null;
        }
        Map<String, Object> map = new Gson().fromJson(data, Map.class);
        if (map.containsKey("tag_name")) {
            String tagName = (String) map.get("tag_name");
            if (!tagName.equals(MineMap.version)) {
                if (map.containsKey("assets")) {
                    ArrayList<Map<String, Object>> assets = (ArrayList<Map<String, Object>>) map.get("assets");
                    for (Map<String, Object> asset : assets) {
                        if (asset.containsKey("browser_download_url") && asset.containsKey("name") && ((String) asset.get("name")).startsWith("MineMap-")) {
                            String url = (String) asset.get("browser_download_url");
                            String filename = (String) asset.get("name");
                            return new Pair<>(new Pair<>(url, filename), tagName);
                        }
                    }
                    Logger.LOGGER.warning("Github release does not contain a correct release.");
                } else {
                    Logger.LOGGER.warning("Github release does not contain a assets key.");
                }
            } else {
                Logger.LOGGER.info(String.format("Version match so we are not updating current :%s, github :%s", MineMap.version, tagName));
            }
        } else {
            Logger.LOGGER.warning("Github release does not contain a tag_name key.");
        }
        return null;
    }

    public static String downloadLatestMinemap(String url, String filename) {
        if (download(url, new File(filename), null)) {
            return filename;
        }
        Logger.LOGGER.warning(String.format("Failed to download jar from url %s with filename %s", url, filename));
        return null;
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
     * @param force   if force is true then it will download it again even if the file exists
     * @return a boolean specifying if the version manifest was indeed downloaded
     */
    public static boolean downloadVersionManifest(MCVersion version, boolean force) {
        String versionManifestUrl = getVersionManifestUrl(version);
        if (versionManifestUrl == null) {
            Logger.LOGGER.severe(String.format("URL was not found for %s", version));
            return false;
        }
        File versionManifest = new File(DOWNLOAD_DIR_VERSIONS + File.separator + version.name + ".json");
        if (!force && versionManifest.exists()) {
            return true;
        }
        return download(versionManifestUrl, versionManifest, null);
    }

    /**
     * Download the manifest from the mojang servers
     *
     * @param version can be null or a specific version to check if the manifest is up to date (aka if the version is not present the manifest is downloaded again)
     * @return boolean to say if the manifest is present and up to date
     */
    public static boolean downloadManifest(MCVersion version) {
        if (!manifestExists(version)) {
            if (!download(MANIFEST_URL, MANIFEST_FILE, null)) {
                return false;
            }
            if (version != null && !manifestExists(version)) {
                Logger.LOGGER.severe(String.format("Manifest was incorrectly downloaded or the version does not exists yet %s %s", MANIFEST_FILE.getAbsolutePath(), version.toString()));
                return false;
            }
        }
        return true;
    }

    /**
     * Download the assets hash file depending of a version
     *
     * @param version the specific targeted version
     * @param force   if force is true then it will download it again even if the file exists
     * @return the name of the asset as version.json
     */
    public static String downloadVersionAssets(MCVersion version, boolean force) {
        Pair<String, String> assetIndexURL = getAssetIndexURL(version);
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
        File assetManifest = new File(DOWNLOAD_DIR_ASSETS + File.separator + name);
        if (!force && assetManifest.exists() && compareSha1(assetManifest, assetIndexURL.getSecond())) {
            return name;
        }
        return download(assetIndexURL.getFirst(), assetManifest, assetIndexURL.getSecond()) ? name : null;
    }

    /**
     * Download the client jar file depending of a version
     *
     * @param version the specific targeted version
     * @param force   if force is true then it will download it again even if the file exists
     * @return the name of the client jar as version.json
     */
    public static String downloadClientJar(MCVersion version, boolean force) {
        Pair<String, String> clientURL = getClientURL(version);
        if (clientURL == null) {
            Logger.LOGGER.severe(String.format("Could not get client url for version %s", version.toString()));
            return null;
        }
        String[] urlSplit = clientURL.getFirst().split("/");
        if (urlSplit.length < 2) {
            Logger.LOGGER.severe(String.format("Could not get name of client from url %s for version %s", clientURL, version.toString()));
            return null;
        }
        String name = urlSplit[urlSplit.length - 1];
        String versionDir = DOWNLOAD_DIR_VERSIONS + File.separator + version.name;
        try {
            Files.createDirectories(Paths.get(versionDir));
        } catch (IOException e) {
            Logger.LOGGER.severe(String.format("Could not make the directory for the client.jar for version %s", version.toString()));
            return null;
        }
        File clientJar = new File(versionDir + File.separator + name);
        if (!force && clientJar.exists() && compareSha1(clientJar, clientURL.getSecond())) {
            return name;
        }
        return download(clientURL.getFirst(), clientJar, clientURL.getSecond()) ? name : null;
    }

    public static boolean extractJar(MCVersion version, String filename, Predicate<JarEntry> jarEntryPredicate, boolean force) {
        File clientJar = new File(DOWNLOAD_DIR_VERSIONS + File.separator + version.name + File.separator + filename);
        if (!clientJar.exists()) {
            Logger.LOGGER.severe(String.format("Could not get client jar file for version %s", version.toString()));
            return false;
        }
        try {
            extractFromJar(clientJar, DOWNLOAD_DIR_ASSETS + File.separator + version.name, jarEntryPredicate, force);
        } catch (IOException e) {
            Logger.LOGGER.severe(String.format("Could not extract from jar file for version %s", version.toString()));
            return false;
        }
        return true;
    }

    private static void extractFromJar(File jarFile, String pathPrefix, Predicate<JarEntry> jarEntryPredicate, boolean force) throws IOException {
        JarFile jar = new JarFile(jarFile);
        Enumeration<JarEntry> enumEntries = jar.entries();
        while (enumEntries.hasMoreElements()) {
            JarEntry entry = enumEntries.nextElement();
            File extractedFile = new java.io.File(pathPrefix + File.separator + entry.getName());
            if (extractedFile.exists() && !force || !jarEntryPredicate.test(entry)) continue;
            if (entry.isDirectory()) { // if its a directory, create it
                boolean ignored = extractedFile.mkdir();
                continue;
            }
            Files.createDirectories(extractedFile.toPath().getParent());
            InputStream is = jar.getInputStream(entry); // get the input stream
            FileOutputStream fos = new FileOutputStream(extractedFile);
            while (is.available() > 0) {  // write contents of 'is' to 'fos'
                fos.write(is.read());
            }
            fos.close();
            is.close();
        }
        jar.close();
    }


    private static boolean download(String url, File out, String sha1) {
        Logger.LOGGER.info(String.format("Downloading %s for file %s", url, out.getName()));
        ReadableByteChannel rbc;
        try {
            rbc = Channels.newChannel(new URL(url).openStream());
        } catch (IOException e) {
            Logger.LOGGER.severe(String.format("Could not open channel to url %s, error: %s", url, e.toString()));
            return false;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(out);
            fileOutputStream.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
            fileOutputStream.close();
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
            Logger.LOGGER.warning(String.format("Version manifest does not contain a asset url/sha1 key for %s", version));
        } else {
            Logger.LOGGER.warning(String.format("Version manifest does not contain a assetIndex key for %s", version));
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Pair<String, String> getClientURL(MCVersion version) {
        JsonReader jsonReader = openJSON(new File(DOWNLOAD_DIR_VERSIONS + File.separator + version.name + ".json"));
        if (jsonReader == null) {
            return null;
        }
        Map<String, Object> map = new Gson().fromJson(jsonReader, Map.class);
        if (map.containsKey("downloads")) {
            Map<String, Map<String, String>> downloads = (Map<String, Map<String, String>>) map.get("downloads");
            if (downloads.containsKey("client")) {
                Map<String, String> client = downloads.get("client");
                if (client.containsKey("url") && client.containsKey("sha1")) {
                    return new Pair<>(client.get("url"), client.get("sha1"));
                }
                Logger.LOGGER.warning(String.format("Version manifest does not contain a client url/sha1 key for %s", version));
            } else {
                Logger.LOGGER.warning(String.format("Version manifest does not contain a client key for %s", version));
            }
        } else {
            Logger.LOGGER.warning(String.format("Version manifest does not contain a downloads key for %s", version));
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

    private static boolean compareSha1(File file, String sha1) {
        if (sha1 != null && file != null) {
            try {
                return getFileChecksum(MessageDigest.getInstance("SHA-1"), file).equals(sha1);
            } catch (NoSuchAlgorithmException e) {
                Logger.LOGGER.severe("Could not compute sha1 since algorithm does not exists");
            }
        }
        return false;
    }

    private static String getFileChecksum(MessageDigest digest, File file) {
        try {
            FileInputStream fis = new FileInputStream(file);

            byte[] byteArray = new byte[1024];
            int bytesCount;
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
            fis.close();
        } catch (IOException e) {
            Logger.LOGGER.severe(String.format("Failed to read file for checksum, error : %s", e));
            return "";
        }
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static Stream<File> collectAllFiles(File path, Predicate<File> predicate) {
        Stream<File> fileStream = Stream.empty();
        for (File file : Objects.requireNonNull(path.listFiles())) {
            if (predicate != null && predicate.test(file)) {
                fileStream = Stream.concat(fileStream, Stream.of(file));
            }
            if (file.isDirectory()) {
                fileStream = Stream.concat(fileStream, collectAllFiles(file, predicate));
            }
        }
        return fileStream;
    }

    public static List<Path> getFileHierarchical(Path dir, String fileName, String extension) throws IOException {
        return Files.walk(dir).
                filter(file -> Files.isRegularFile(file) && file.toAbsolutePath().getFileName().toString().equals(fileName + extension)).
                collect(Collectors.toList());
    }

    public static List<Pair<String, BufferedImage>> getAsset(Path dir, boolean isJar, String name, String extension, Function<Path, String> fnObjectStorage) {
        List<Path> paths;
        List<Pair<String, BufferedImage>> list = new ArrayList<>();
        try {
            paths = Assets.getFileHierarchical(dir, name, extension);
        } catch (IOException e) {
            LOGGER.severe(String.format("Exception while screening the files for '%s%s' from root %s with error %s", name, extension, dir.toString(), e.toString()));
            System.err.println("Didn't find icon " + name + ".");
            return list;
        }
        for (Path path : paths) {
            try {
                InputStream inputStream = isJar ? Icons.class.getResourceAsStream(path.toString()) : new FileInputStream(path.toString());
                list.add(new Pair<>(fnObjectStorage.apply(path), ImageIO.read(inputStream)));
            } catch (IOException e) {
                LOGGER.severe(String.format("Exception while reading the input stream or getting " +
                        "the file input for %s at %s with error %s", name, dir.toString(), e.toString()));
            }
        }
        if (list.isEmpty()) {
            System.err.println("Didn't find icon " + name + ".");
            LOGGER.severe(String.format("File not found for %s", name));
        } else {
            System.out.println("Found icon " + name + ".");
        }

        return list;
    }

    private static String getDataRestAPI(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                LOGGER.severe(String.format("Failed to fetch URL %s, errorcode : %s", apiUrl, responseCode));
            } else {

                StringBuilder inline = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());
                while (scanner.hasNext()) {
                    inline.append(scanner.nextLine());
                }
                scanner.close();
                return inline.toString();
            }
        } catch (Exception e) {
            LOGGER.severe(String.format("Failed to fetch URL %s, error : %s", apiUrl, e));
        }
        return null;
    }

}

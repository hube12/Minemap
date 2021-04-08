package kaptainwutax.minemap.init;

import kaptainwutax.featureutils.decorator.EndGateway;
import kaptainwutax.featureutils.misc.SlimeChunk;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.feature.*;
import kaptainwutax.minemap.ui.map.interactive.Chest;
import kaptainwutax.minemap.ui.map.tool.Area;
import kaptainwutax.minemap.ui.map.tool.Circle;
import kaptainwutax.minemap.ui.map.tool.Ruler;
import kaptainwutax.minemap.util.data.Assets;
import kaptainwutax.minemap.util.data.Pair;
import kaptainwutax.minemap.util.data.Str;
import kaptainwutax.minemap.util.ui.buttons.CloseButton;
import kaptainwutax.minemap.util.ui.buttons.CopyButton;
import kaptainwutax.minemap.util.ui.buttons.InfoButton;
import kaptainwutax.minemap.util.ui.buttons.JumpButton;
import kaptainwutax.seedutils.mc.MCVersion;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static kaptainwutax.minemap.init.Logger.LOGGER;

public class Icons {

    private static final Map<Class<?>, List<Pair<String, BufferedImage>>> CLASS_REGISTRY = new HashMap<>();
    private static final Map<Object, List<Pair<String, BufferedImage>>> OBJECT_REGISTRY = new HashMap<>();


    public static void registerIcons() {
        String mainPath = "/icon";
        URL url = Icons.class.getResource(mainPath);
        if (url == null) {
            LOGGER.severe(String.format("Url not found for path %s", mainPath));
            return;
        }
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            LOGGER.severe(String.format("Uri was not able to be converted for url %s with error : %s", url, e.toString()));
            return;
        }
        boolean isJar = "jar".equals(uri.getScheme());
        FileSystem fileSystem = null;
        Path dir;
        if (isJar) {
            try {
                fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap(), null);
            } catch (IOException e) {
                LOGGER.severe(String.format("Filesystem was not opened correctly for %s with error : %s", uri, e.toString()));
                return;
            }
            dir = fileSystem.getPath(mainPath);
        } else {
            dir = new File(uri).toPath();
        }
        registerJARIcons(dir, isJar);
        if (fileSystem != null) {
            try {
                fileSystem.close();
            } catch (IOException e) {
                LOGGER.severe(String.format("Filesystem was not cloed correctly for %s with error : %s", uri, e.toString()));
                return;
            }
        }
        if (Assets.downloadManifest(null)){
            MCVersion version=Assets.getLatestVersion();
            if (version!=null){
                if (Assets.downloadVersionManifest(version)){
                    String assetName=Assets.downloadVersionAssets(version);
                    if (assetName!=null){
//                        System.out.println(assetName);
                    }else{
                        Logger.LOGGER.warning("Assets index could not be downloaded");
                    }
                }else{
                    Logger.LOGGER.warning("Version manifest could not be downloaded");
                }
            }else{
                Logger.LOGGER.warning("Manifest does not contain a valid latest release");
            }
        }

        registerInternetIcons();
        cleanDuplicates();
    }

    private static void registerJARIcons(Path dir, boolean isJar) {
        register(BastionRemnant.class, dir, isJar, Structure.getName(BastionRemnant.class));
        register(BuriedTreasure.class, dir, isJar, Structure.getName(BuriedTreasure.class));
        register(DesertPyramid.class, dir, isJar, Structure.getName(DesertPyramid.class));
        register(EndCity.class, dir, isJar, Structure.getName(EndCity.class));
        register(Fortress.class, dir, isJar, Structure.getName(Fortress.class));
        register(Igloo.class, dir, isJar, Structure.getName(Igloo.class));
        register(JunglePyramid.class, dir, isJar, Structure.getName(JunglePyramid.class));
        register(Mansion.class, dir, isJar, Structure.getName(Mansion.class));
        register(Mineshaft.class, dir, isJar, Structure.getName(Mineshaft.class));
        register(Monument.class, dir, isJar, Structure.getName(Monument.class));
        register(OceanRuin.class, dir, isJar, Structure.getName(OceanRuin.class));
        register(PillagerOutpost.class, dir, isJar, Structure.getName(PillagerOutpost.class));
        register(Shipwreck.class, dir, isJar, Structure.getName(Shipwreck.class));
        register(SwampHut.class, dir, isJar, Structure.getName(SwampHut.class));
        register(Village.class, dir, isJar, Structure.getName(Village.class));
        register(Stronghold.class, dir, isJar, Structure.getName(Stronghold.class));

        // special cases
        register(OWRuinedPortal.class, dir, isJar, Structure.getName(RuinedPortal.class));
        register(NERuinedPortal.class, dir, isJar, Structure.getName(RuinedPortal.class));

        register(OWBastionRemnant.class, dir, isJar, Structure.getName(BastionRemnant.class));
        register(OWFortress.class, dir, isJar, Structure.getName(Fortress.class));

        // features
        register(Chest.class, dir, isJar, "treasure_chest");
        register(EndGateway.class, dir, isJar, "end_gateway");
        register(SlimeChunk.class, dir, isJar, "slime");
        register(SpawnPoint.class, dir, isJar, "spawn");
        register(NetherFossil.class, dir, isJar, Structure.getName(NetherFossil.class));

        register(Ruler.class, dir, isJar, "ruler");
        register(Area.class, dir, isJar, "area");
        register(Circle.class, dir, isJar, "circle");


        register(CloseButton.class, dir, isJar, "close");
        register(CopyButton.class, dir, isJar, "copy");
        register(JumpButton.class, dir, isJar, "jump");
        register(InfoButton.class, dir, isJar, "info");

        register(MineMap.class, dir, isJar, "logo");

    }

    public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
        Set<Object> seen = ConcurrentHashMap.newKeySet();
        return t -> seen.add(keyExtractor.apply(t));
    }

    private static void cleanDuplicates() {
        for (Map.Entry<Class<?>, List<Pair<String, BufferedImage>>> pairs : CLASS_REGISTRY.entrySet()) {
            pairs.setValue(pairs.getValue().stream().filter(distinctByKey(Pair::getFirst)).collect(Collectors.toList()));
        }
        for (Map.Entry<Object, List<Pair<String, BufferedImage>>> pairs : OBJECT_REGISTRY.entrySet()) {
            pairs.setValue(pairs.getValue().stream().filter(distinctByKey(Pair::getFirst)).collect(Collectors.toList()));
        }
    }

    private static void registerInternetIcons() {
        //registerItem(Item.ENCHANTED_GOLDEN_APPLE,"apple_golden");
        //registerItem(Item.GOLDEN_APPLE,"apple_golden");
    }

    private static <T> void register(Class<T> clazz, Path dir, boolean isJar, String name, String extension) {
        if (CLASS_REGISTRY.containsKey(clazz)){
            CLASS_REGISTRY.get(clazz).addAll(getIcon(dir, isJar, name, extension));
        }else{
            CLASS_REGISTRY.put(clazz,getIcon(dir, isJar, name, extension));
        }
    }

    private static <T> void register(Class<T> clazz, Path dir, boolean isJar, String name) {
        register(clazz, dir, isJar, name, ".png");
    }

    private static <T> void registerObject(Object object, Path dir, boolean isJar, String name, String extension) {
        if (OBJECT_REGISTRY.containsKey(object)){
            OBJECT_REGISTRY.get(object).addAll(getIcon(dir, isJar, name, extension));
        }else{
            OBJECT_REGISTRY.put(object,getIcon(dir, isJar, name, extension));
        }
    }

    private static <T> void registerObject(Object object, Path dir, boolean isJar, String name) {
        registerObject(object, dir, isJar, name, ".png");
    }

    public static <T> BufferedImage get(Class<T> clazz) {
        List<Pair<String, BufferedImage>> entry = CLASS_REGISTRY.get(clazz);
        if (entry == null) return null;
        if (entry.isEmpty()) return null;
        // TODO make me config dependant
        return entry.get(entry.size() - 1).getSecond();
    }

    public static BufferedImage getObject(Object object) {
        List<Pair<String, BufferedImage>> entry = OBJECT_REGISTRY.get(object);
        if (entry == null) return null;
        if (entry.isEmpty()) return null;
        // TODO make me config dependant
        return entry.get(0).getSecond();
    }

    private static List<Pair<String, BufferedImage>> getIcon(Path dir, boolean isJar, String name, String extension) {
        List<Path> paths;
        List<Pair<String, BufferedImage>> list = new ArrayList<>();
        try {
            paths = getFileHierarchical(dir, name, extension);
        } catch (IOException e) {
            LOGGER.severe(String.format("Exception while screening the files for '%s%s' from root %s with error %s", name, extension, dir.toString(), e.toString()));
            System.err.println("Didn't find icon " + name + ".");
            return list;
        }
        for (Path path : paths) {
            try {
                InputStream inputStream = isJar ? Icons.class.getResourceAsStream(path.toString()) : new FileInputStream(path.toString());
                list.add(new Pair<>(path.toAbsolutePath().toString().split("icon")[1], ImageIO.read(inputStream)));
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
                filter(file -> Files.isRegularFile(file) && file.toAbsolutePath().toString().endsWith(fileName + extension)).
                collect(Collectors.toList());
    }
}

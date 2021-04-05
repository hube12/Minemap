package kaptainwutax.minemap.init;

import kaptainwutax.featureutils.decorator.EndGateway;
import kaptainwutax.featureutils.loot.item.Item;
import kaptainwutax.featureutils.misc.SlimeChunk;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.feature.*;
import kaptainwutax.minemap.ui.map.interactive.Chest;
import kaptainwutax.minemap.ui.map.tool.Area;
import kaptainwutax.minemap.ui.map.tool.Circle;
import kaptainwutax.minemap.ui.map.tool.Ruler;
import kaptainwutax.minemap.util.ui.buttons.CloseButton;
import kaptainwutax.minemap.util.ui.buttons.CopyButton;
import kaptainwutax.minemap.util.ui.buttons.InfoButton;
import kaptainwutax.minemap.util.ui.buttons.JumpButton;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.*;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Icons {

    private static final Map<Class<?>, BufferedImage> REGISTRY = new HashMap<>();
    private static final Map<Item, BufferedImage> REGISTRY_ITEM = new HashMap<>();

    public static void registerIcons() {
        register(BastionRemnant.class, Structure.getName(BastionRemnant.class));
        register(BuriedTreasure.class, Structure.getName(BuriedTreasure.class));
        register(DesertPyramid.class, Structure.getName(DesertPyramid.class));
        register(EndCity.class, Structure.getName(EndCity.class));
        register(Fortress.class, Structure.getName(Fortress.class));
        register(Igloo.class, Structure.getName(Igloo.class));
        register(JunglePyramid.class, Structure.getName(JunglePyramid.class));
        register(Mansion.class, Structure.getName(Mansion.class));
        register(Mineshaft.class, Structure.getName(Mineshaft.class));
        register(Monument.class, Structure.getName(Monument.class));
        register(NetherFossil.class, Structure.getName(NetherFossil.class));
        register(OceanRuin.class, Structure.getName(OceanRuin.class));
        register(PillagerOutpost.class, Structure.getName(PillagerOutpost.class));
        register(OWRuinedPortal.class, Structure.getName(RuinedPortal.class));
        register(NERuinedPortal.class, Structure.getName(RuinedPortal.class));
        register(Shipwreck.class, Structure.getName(Shipwreck.class));
        register(SwampHut.class, Structure.getName(SwampHut.class));
        register(Village.class, Structure.getName(Village.class));
        register(Stronghold.class, Structure.getName(Stronghold.class));

        register(OWBastionRemnant.class, Structure.getName(BastionRemnant.class));
        register(OWFortress.class, Structure.getName(Fortress.class));

        register(EndGateway.class, "end_gateway");
        register(SlimeChunk.class, "slime");

        register(SpawnPoint.class, "spawn");

        register(Ruler.class, "ruler");
        register(Area.class, "area");
        register(Circle.class, "circle");


        register(CloseButton.class, "close");
        register(CopyButton.class, "copy");
        register(JumpButton.class, "jump");
        register(InfoButton.class, "info");

        register(Chest.class, "treasure_chest");
        register(MineMap.class, "logo");

    }

    public static void registerItems(){
        //registerItem(Item.ENCHANTED_GOLDEN_APPLE,"apple_golden");
        //registerItem(Item.GOLDEN_APPLE,"apple_golden");
    }

    public static <T> void register(Class<T> clazz, String name) {
        REGISTRY.put(clazz, getIcon(name));
    }

    public static <T> void registerItem(Item item, String name) {
        REGISTRY_ITEM.put(item, getIcon(name));
    }

    public static <T> BufferedImage get(Class<T> clazz){
        return REGISTRY.get(clazz);
    }

    public static BufferedImage getItem(Item item){
        return REGISTRY_ITEM.get(item);
    }

    private static BufferedImage getIcon(String name) {
        try {
            URI uri = getFileHierarchical("/icon",name);
            if (uri == null) {
                throw new FileNotFoundException();
            }
            System.out.println("Found icon " + name + ".");
            return ImageIO.read(uri.toURL());
        } catch (Exception e) {
            try {
                FileWriter fstream = new FileWriter("error.log", true); //true tells to append data.
                BufferedWriter  out = new BufferedWriter(fstream);
                out.write(e.toString());
                fstream.close();
            }catch (Exception ee){
                ee.printStackTrace();
            }

            e.printStackTrace();
            System.err.println("Didn't find icon " + name + ".");
        }

        return null;
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

    public static URI getFileHierarchical(String mainPath, String fileName) throws URISyntaxException, IOException {
        Path dir;
        FileSystem fileSystem=null;
        URL url = Icons.class.getResource(mainPath);
        if (url == null) return null;
        URI uri = url.toURI();
        if ("jar".equals(uri.getScheme())) {
            try {
                fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap(), null);
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }

            try {
                dir=fileSystem.getPath(mainPath);
            }catch (Exception e ){
                e.printStackTrace();
                fileSystem.close();
                return null;
            }

        } else {
            dir= new File(uri).toPath();
        }
        List<Path> matches = Files.walk(dir).
                filter(file-> Files.isRegularFile(file) && file.toAbsolutePath().toString().endsWith(fileName+".png")).
                collect(Collectors.toList());
        if (fileSystem!=null){
            fileSystem.close();
        }
        if (matches.size() == 0) {
            return null;
        }
        return matches.get(0).toUri();
    }
}

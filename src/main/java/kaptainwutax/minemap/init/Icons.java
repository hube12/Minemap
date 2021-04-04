package kaptainwutax.minemap.init;

import kaptainwutax.featureutils.decorator.EndGateway;
import kaptainwutax.featureutils.misc.SlimeChunk;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.minemap.feature.*;
import kaptainwutax.minemap.ui.map.tool.Area;
import kaptainwutax.minemap.ui.map.tool.Circle;
import kaptainwutax.minemap.ui.map.tool.Ruler;
import kaptainwutax.minemap.util.ui.buttons.CloseButton;
import kaptainwutax.minemap.util.ui.buttons.CopyButton;
import kaptainwutax.minemap.util.ui.buttons.InfoButton;
import kaptainwutax.minemap.util.ui.buttons.JumpButton;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Icons {

    public static final Map<Class<?>, BufferedImage> REGISTRY = new HashMap<>();

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
    }

    public static <T> void register(Class<T> clazz, String name) {
        REGISTRY.put(clazz, getIcon(name));
    }

    public static BufferedImage getIcon(String name) {
        try {
            URI uri = getURI(name);
            if (uri == null) {
                throw new FileNotFoundException();
            }
            System.out.println("Found icon "+name+".");
            return ImageIO.read(uri.toURL());
        } catch (Exception e) {
            System.err.println("Didn't find icon " + name + ".");
        }

        return null;
    }

    public static URI getURI(String name) {
        File dir = getFileFromURL("/icon");
        if (dir == null) {
            return null;
        }
        List<File> matches=collectAllFiles(dir, file -> file.getName().equals(name + ".png")).collect(Collectors.toList());
        if (matches.size() == 0) {
            return null;
        }
        return matches.get(0).toURI();
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

    public static File getFileFromURL(String path) {
        URL url = Icons.class.getResource(path);
        File file;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            file = new File(url.getPath());
        }
        return file;
    }


}

package kaptainwutax.minemap.util.snksynthesis.voxelgame.util;

import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.init.Logger;
import kaptainwutax.minemap.util.data.Assets;
import kaptainwutax.minemap.util.snksynthesis.voxelgame.Visualizer;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWImage;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;
import java.util.function.Function;

import static kaptainwutax.minemap.init.Logger.LOGGER;

public class IOUtils {


    public static <T> T getFromFile(String path, Function<InputStream, T> function) {
        String mainPath = Visualizer.PATH;
        URL url = Icons.class.getResource(mainPath);
        if (url == null) {
            LOGGER.severe(String.format("Url not found for path %s", mainPath));
            return null;
        }
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            LOGGER.severe(String.format("Uri was not able to be converted for url %s with error : %s", url, e));
            return null;
        }
        boolean isJar = "jar".equals(uri.getScheme());
        FileSystem fileSystem = null;
        Path dir;
        if (isJar) {
            try {
                fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap(), null);
            } catch (IOException e) {
                LOGGER.severe(String.format("Filesystem was not opened correctly for %s with error : %s", uri, e));
                return null;
            }
            dir = fileSystem.getPath(mainPath);
        } else {
            dir = new File(uri).toPath();
        }
        java.util.List<Pair<Path, InputStream>> list = Assets.getInputStream(dir, isJar, path, "");

        if (list.size() != 1) {
            LOGGER.severe("List has more than one element " + Arrays.toString(list.toArray()));
            return null;
        }

        T res = function.apply(list.get(0).getSecond());

        if (fileSystem != null) {
            try {
                fileSystem.close();
            } catch (IOException e) {
                LOGGER.severe(String.format("Filesystem was not closed correctly for %s with error : %s", uri, e));
            }
        }
        return res;
    }

    public static ByteBuffer readBufferFromInputStream(InputStream in) {
        ByteBuffer byteBuffer;
        try {
            byteBuffer = BufferUtils.createByteBuffer(in.available());
            Channels.newChannel(in).read(byteBuffer);
            byteBuffer.flip();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.LOGGER.severe("Could not read input stream...");
            return null;
        }
        return byteBuffer;
    }

    public static String readStringFromInputStream(InputStream in) {
        StringBuilder sb = new StringBuilder();
        try (Scanner sc = new Scanner(in)) {
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine());
                sb.append(System.lineSeparator());
            }
        }
        return sb.toString();
    }

    /**
     * Convert the {@link BufferedImage} to the {@link GLFWImage}.
     */
    public static GLFWImage imageToGLFWImage(BufferedImage image) {
        if (image.getType() != BufferedImage.TYPE_INT_ARGB_PRE) {
            final BufferedImage convertedImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
            final Graphics2D graphics = convertedImage.createGraphics();
            final int targetWidth = image.getWidth();
            final int targetHeight = image.getHeight();
            graphics.drawImage(image, 0, 0, targetWidth, targetHeight, null);
            graphics.dispose();
            image = convertedImage;
        }
        final ByteBuffer buffer = BufferUtils.createByteBuffer(image.getWidth() * image.getHeight() * 4);
        for (int i = 0; i < image.getHeight(); i++) {
            for (int j = 0; j < image.getWidth(); j++) {
                int colorSpace = image.getRGB(j, i);
                buffer.put((byte) ((colorSpace << 8) >> 24));
                buffer.put((byte) ((colorSpace << 16) >> 24));
                buffer.put((byte) ((colorSpace << 24) >> 24));
                buffer.put((byte) (colorSpace >> 24));
            }
        }
        buffer.flip();
        final GLFWImage result = GLFWImage.create();
        result.set(image.getWidth(), image.getHeight(), buffer);
        return result;
    }

}

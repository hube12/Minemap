package kaptainwutax.minemap.util.snksynthesis.voxelgame.gfx;

import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.init.Logger;
import kaptainwutax.minemap.util.data.Assets;
import kaptainwutax.minemap.util.snksynthesis.voxelgame.Visualizer;
import org.lwjgl.BufferUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Objects;

import static kaptainwutax.minemap.init.Logger.LOGGER;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.stb.STBImage.*;

/**
 * {@link Texture} is for loading and using textures
 */
public class Texture {

    private final int texture;

    private Texture(String path, boolean rgba) {
        texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, texture);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        IntBuffer channels = BufferUtils.createIntBuffer(1);

        // Flips image vertically so that it is not seen upside down
        stbi_set_flip_vertically_on_load(true);
        ByteBuffer buffer=getByteBuffer(path);
        // heap buffer are not accepted by load_from_memory
        ByteBuffer data = stbi_load_from_memory(Objects.requireNonNull(buffer), width, height, channels, 0);
        if (data != null) {
            if (rgba) {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGBA, GL_UNSIGNED_BYTE, data);
            } else {
                glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, width.get(0), height.get(0), 0, GL_RGB, GL_UNSIGNED_BYTE, data);
            }
            glGenerateMipmap(GL_TEXTURE_2D);
        } else {
            throw new RuntimeException("Couldn't load texture!");
        }
        stbi_image_free(data);
    }

    private ByteBuffer getByteBuffer(String path) {
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
        assert list.size() == 1;
        ByteBuffer byteBuffer;
        try {
            byteBuffer = BufferUtils.createByteBuffer(list.get(0).getSecond().available());
            Channels.newChannel(list.get(0).getSecond()).read(byteBuffer);
            byteBuffer.flip();
        } catch (IOException e) {
            e.printStackTrace();
            Logger.LOGGER.severe("Could not read input stream...");
            return null;
        }
        if (fileSystem != null) {
            try {
                fileSystem.close();
            } catch (IOException e) {
                LOGGER.severe(String.format("Filesystem was not closed correctly for %s with error : %s", uri, e));
            }
        }
        return byteBuffer;
    }

    /**
     * Loads an RGB image.
     *
     * @throws RuntimeException if the texture wasn't able to be loaded
     */
    public static Texture loadRGB(String path) {
        return new Texture(path, false);
    }

    /**
     * Loads an RGBA image
     *
     * @throws RuntimeException if the texture wasn't able to be loaded
     */
    public static Texture loadRGBA(String path) {
        return new Texture(path, true);
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, texture);
    }

    public void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public int getTexture() {
        return texture;
    }
}

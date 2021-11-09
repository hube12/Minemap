package com.seedfinding.minemap.util.snksynthesis.voxelgame.gfx;

import com.seedfinding.minemap.util.snksynthesis.voxelgame.util.IOUtils;
import com.seedfinding.minemap.init.Logger;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Objects;

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
        ByteBuffer buffer = IOUtils.getFromFile(path, IOUtils::readBufferFromInputStream);
        if (buffer == null) {
            Logger.LOGGER.severe("Failed to read texture from " + path);
            return;
        }
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

    // this is meant to be internally used
    protected Texture(int textureId) {
        this.texture = textureId;
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

    public void destroy() {
        glDeleteTextures(texture);
    }

    public int getTexture() {
        return texture;
    }
}

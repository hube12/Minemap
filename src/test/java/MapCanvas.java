import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.minemap.init.Configs;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import javax.swing.*;
import java.awt.*;
import java.nio.ByteBuffer;
import java.util.Random;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public
class MapCanvas extends AWTGLCanvas {

    private final BiomeSource source;
    private final int blockNumber;
    private final int[] textures = new int[4];
    private static final long serialVersionUID = 1L;

    public MapCanvas(GLData data) {
        super(data);
        Configs.registerConfigs();
        this.source = new OverworldBiomeSource(MCVersion.v1_0, 0);
        this.blockNumber = 2;
    }

    @Override
    public void initGL() {
        System.out.println("OpenGL version: " + effective.majorVersion + "." + effective.minorVersion + " (Profile: " + effective.profile + ")");
        createCapabilities();
        glClearColor(1f, 0f, 1f, 1);
        int w = getWidth();
        int h = getHeight();
        for (int y = 0; y < blockNumber; y++) {
            for (int x = 0; x < blockNumber; x++) {
                int width = w / blockNumber;
                int height = h / blockNumber;
                ByteBuffer buffer = genBuffer(x, y, width, height);
                int texture = createTexture(width, height, buffer);
                textures[y * blockNumber + x] = texture;
            }
        }
    }

    public int createTexture(int width, int height, ByteBuffer buffer) {
        int tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, tex);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR); // we also want to sample this texture later
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR); // we also want to sample this texture later
//                        // set wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_EDGE);
        buffer.rewind();
        // copy the buffer data as GL_RGB8 of size (w / blockNumber, h / blockNumber) to be output as GL_RGB of type GL_UNSIGNED_BYTE
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer);

        // create frame buffer
        int fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);
        // bind the texture to the fbo
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, tex, 0);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE) {
            System.out.println("Done");
        }

        // unbind
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        return tex;
    }

    public ByteBuffer genBuffer(int x, int y, int width, int height) {
        Random random = new Random();
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4); // we have to use 4 here (https://www.khronos.org/opengl/wiki/Common_Mistakes#Texture_upload_and_pixel_reads)
        for (int idy = 0; idy < height; idy++) {
            for (int idx = 0; idx < width; idx++) {
                Biome biome = source.getBiome(x * width + idx, 0, y * height + idy);
                Color color = Configs.BIOME_COLORS.get(null, biome);
                int pixel = (int) random.nextLong();
                if (idy < 10 && idx < 10) {
                    pixel = 0;
                }
                byte r = (byte) ((pixel >> 24) & 0xFF);
                byte g = (byte) ((pixel >> 16) & 0xFF);
                byte b = (byte) ((pixel >> 8) & 0xFF);
                r = (byte) color.getRed();
                g = (byte) color.getGreen();
                b = (byte) color.getBlue();
                buffer.put(r);
                buffer.put(g);
                buffer.put(b);
                buffer.put((byte) (0));// no alpha
            }
        }
        buffer.flip();
        return buffer;
    }

    @Override
    public void paintGL() {
        int w = getWidth();
        int h = getHeight();
        for (int y = 0; y < blockNumber; y++) {
            for (int x = 0; x < blockNumber; x++) {
                int tex = textures[y * blockNumber + x];
                // enable 2d context and bind texture
                glEnable(GL_TEXTURE_2D);
                glBindTexture(GL_TEXTURE_2D, tex);
                glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
                glBegin(GL_QUADS);

                glTexCoord2f(0, 0);
                glVertex2f(-1f + (float) x, 1f - (float) y);
                glTexCoord2f(1, 0);
                glVertex2f(0f + (float) x, 1f - (float) y);
                glTexCoord2f(1, 1);
                glVertex2f(0f + (float) x, 0f - (float) y);
                glTexCoord2f(0, 1);
                glVertex2f(-1f + (float) x, 0f - (float) y);
                glEnd();
                glDisable(GL_TEXTURE_2D);
                glBindTexture(GL_TEXTURE_2D, 0);
            }
        }
        swapBuffers();
    }

    @Override
    public void repaint() {
        super.repaint();
        if (SwingUtilities.isEventDispatchThread()) {
            if (!this.isValid()) return;
            render();
        } else {
            SwingUtilities.invokeLater(this::render);
        }
    }
}


package kaptainwutax.minemap.util.snksynthesis.voxelgame.gfx;

import kaptainwutax.minemap.init.Logger;
import kaptainwutax.minemap.util.snksynthesis.voxelgame.util.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.stb.STBTruetype.stbtt_PackEnd;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.system.MemoryUtil.memAllocFloat;

public class Font {
    private final STBTTPackedchar.Buffer charData;
    private final int fontTex;
    private static final int BITMAP_WIDTH = 512;
    private static final int BITMAP_HEIGHT = 512;
    private boolean integerAlign = true;

    private final STBTTAlignedQuad q = STBTTAlignedQuad.malloc();
    private final FloatBuffer xb = memAllocFloat(1);
    private final FloatBuffer yb = memAllocFloat(1);
    private static final float[] scale = {
        24.0f,
        14.0f
    };

    public Font(String filepath) {
        fontTex = glGenTextures();
        charData = STBTTPackedchar.malloc(6 * 128);

        try (STBTTPackContext pc = STBTTPackContext.malloc()) {
            ByteBuffer ttf = IOUtils.getFromFile(filepath, IOUtils::readBufferFromInputStream);
            if (ttf == null) {
                Logger.LOGGER.severe("Failed to read ttf from " + filepath);
                return;
            }
            ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_WIDTH * BITMAP_HEIGHT);

            stbtt_PackBegin(pc, bitmap, BITMAP_WIDTH, BITMAP_HEIGHT, 0, 1, NULL);
            for (int i = 0; i < 2; i++) {
                int p = (i * 3) * 128 + 32;
                charData.limit(p + 95);
                charData.position(p);
                stbtt_PackSetOversampling(pc, 1, 1);
                stbtt_PackFontRange(pc, ttf, 0, scale[i], 32, charData);

                p = (i * 3 + 1) * 128 + 32;
                charData.limit(p + 95);
                charData.position(p);
                stbtt_PackSetOversampling(pc, 2, 2);
                stbtt_PackFontRange(pc, ttf, 0, scale[i], 32, charData);

                p = (i * 3 + 2) * 128 + 32;
                charData.limit(p + 95);
                charData.position(p);
                stbtt_PackSetOversampling(pc, 3, 1);
                stbtt_PackFontRange(pc, ttf, 0, scale[i], 32, charData);
            }
            charData.clear();
            stbtt_PackEnd(pc);

            glBindTexture(GL_TEXTURE_2D, fontTex);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_ALPHA, BITMAP_WIDTH, BITMAP_HEIGHT, 0, GL_ALPHA, GL_UNSIGNED_BYTE, bitmap);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        }
    }

    public void draw(float x, float y, int font, String text) {
        xb.put(0, x);
        yb.put(0, y);

        charData.position(font * 128);

        glEnable(GL_TEXTURE_2D);
        glBindTexture(GL_TEXTURE_2D, fontTex);

        glBegin(GL_QUADS);
        for (int i = 0; i < text.length(); i++) {
            stbtt_GetPackedQuad(charData, BITMAP_WIDTH, BITMAP_HEIGHT, text.charAt(i), xb, yb, q, font == 0 && integerAlign);
            drawBoxTC(
                q.x0(), q.y0(), q.x1(), q.y1(),
                q.s0(), q.t0(), q.s1(), q.t1()
            );
        }
        glEnd();
    }

    private static void drawBoxTC(float x0, float y0, float x1, float y1, float s0, float t0, float s1, float t1) {
        glTexCoord2f(s0, t0);
        glVertex2f(x0, y0);
        glTexCoord2f(s1, t0);
        glVertex2f(x1, y0);
        glTexCoord2f(s1, t1);
        glVertex2f(x1, y1);
        glTexCoord2f(s0, t1);
        glVertex2f(x0, y1);
    }

    public boolean toggleIntegerAlign() {
        this.integerAlign = !this.integerAlign;
        return this.integerAlign;
    }
}

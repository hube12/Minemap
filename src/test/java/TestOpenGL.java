

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.Random;

import static org.lwjgl.opengl.GL.*;
import static org.lwjgl.opengl.GL33.*;
import static org.lwjgl.opengl.GL33.glGenerateMipmap;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class TestOpenGL {
    public static void main(String[] args) {
        JFrame frame = new JFrame("AWT test");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLayout(new BorderLayout());
        frame.setPreferredSize(new Dimension(600, 600));
        GLData data = new GLData();
        data.samples = 4;
        data.swapInterval = 0;
        AWTGLCanvas canvas;

        frame.add(canvas = new AWTGLCanvas(data) {
            private static final long serialVersionUID = 1L;
            int[] ids=new int[4];
            public void initGL() {
                System.out.println("OpenGL version: " + effective.majorVersion + "." + effective.minorVersion + " (Profile: " + effective.profile + ")");
                createCapabilities();
                glClearColor(0f, 0f, 0f, 1);
                int blockNumber = 2;
                for (int y = 0; y < blockNumber; y++) {
                    for (int x = 0; x < blockNumber; x++) {
                        int textureID = glGenTextures(); //Generate texture ID
                        ids[y*blockNumber+x]=textureID;
                        glActiveTexture(GL_TEXTURE0+(y*blockNumber+x));
                        glBindTexture(GL_TEXTURE_2D,textureID);
                    }
                }


                //Setup wrap mode
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_EDGE);

                //Setup texture scaling filtering
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);


                glEnable(GL_TEXTURE_2D);
            }

            public void paintGL() {
                int w = getWidth();
                int h = getHeight();

                int blockNumber = 2;
                for (int y = 0; y < blockNumber; y++) {
                    for (int x = 0; x < blockNumber; x++) {
                        Random random = new Random();
                        ByteBuffer buffer = BufferUtils.createByteBuffer(w / blockNumber * h / blockNumber * 3); //4 for RGBA, 3 for RGB

                        for (int idy = 0; idy < h/blockNumber; idy++) {
                            for (int idx = 0; idx < w/blockNumber; idx++) {
                                int pixel = (int) random.nextLong();
                                buffer.put((byte) ((pixel >> 16) & 0xFF));     // Red component
                                buffer.put((byte) ((pixel >> 8) & 0xFF));      // Green component
                                buffer.put((byte) (pixel & 0xFF));               // Blue component
                            }
                        }
                        glActiveTexture(GL_TEXTURE0+y*blockNumber+x);
                        glBindTexture(GL_TEXTURE_2D,ids[y*blockNumber+x]);
                        buffer.flip();
                        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB8, w/blockNumber, h/blockNumber, 0, GL_RGB, GL_UNSIGNED_BYTE, buffer);
                        glBegin(GL_QUADS);
                        glVertex2f(-1f+(float)x, 1f-(float)y);
                        glVertex2f(0f+(float)x, 1f-(float)y);
                        glVertex2f(0f+(float)x, 0f-(float)y);
                        glVertex2f(-1f+(float)x, 0f-(float)y);

                        glTexCoord2f(0, 0);
                        glTexCoord2f(0, 1);
                        glTexCoord2f(1, 1);
                        glTexCoord2f(1, 0);
                        glEnd();
                    }
                }

                swapBuffers();
            }
        }, BorderLayout.CENTER);
        frame.pack();
        frame.setVisible(true);
        frame.transferFocus();

        Runnable renderLoop = new Runnable() {
            public void run() {
                if (!canvas.isValid())
                    return;
                canvas.render();
                //SwingUtilities.invokeLater(this);
            }
        };
        SwingUtilities.invokeLater(renderLoop);
    }
}
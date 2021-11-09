package com.seedfinding.minemap.util.snksynthesis.voxelgame.gfx;

import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.init.Icons;
import com.seedfinding.minemap.util.snksynthesis.voxelgame.util.IOUtils;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;

import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.Callback;
import org.lwjgl.system.MemoryUtil;

import org.lwjgl.opengl.GL;

import java.awt.image.BufferedImage;
import java.util.Objects;

import static org.lwjgl.opengl.GL33.*;

/**
 * {@link Window} is for creating and managing a window.
 */
public class Window {

    private long window;
    private final String title;
    private int width, height;
    private boolean resized;
    private float deltaTime;
    private float lastFrame;
    private Callback callback;

    public Window(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        resized = false;
    }


    // this will emit PLATFORM_ERROR ON WAYLAND AND NOT RESPECT FOR MACOS
    public void setIcon(BufferedImage bufferedImage) {
        GLFWImage image = IOUtils.imageToGLFWImage(bufferedImage);

        GLFWImage.Buffer images = GLFWImage.malloc(1);
        images.put(0, image);

        glfwSetWindowIcon(window, images);
    }

    /**
     * Creates window
     */
    public void create() {
        // Print error message to System.err
        GLFWErrorCallback.createPrint(System.err).set();
        if (!glfwInit()) { // Initialize GLFW
            throw new RuntimeException("GLFW unintialized!");
        }

        // Configure GLFW window
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);

        // Create window
        window = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        if (window == MemoryUtil.NULL) {
            throw new RuntimeException("Failed to create window!");
        }

        // Center window
        GLFWVidMode vidMode = glfwGetVideoMode(glfwGetPrimaryMonitor());
        assert vidMode != null;
        glfwSetWindowPos(window, (vidMode.width() - width) / 2, (vidMode.height() - height) / 2);

        setIcon(Icons.get(MineMap.class));

        // Make current OpenGL context
        glfwMakeContextCurrent(window);

        // Enable v-sync
        glfwSwapInterval(1);

        glfwShowWindow(window);

        GL.createCapabilities();
        callback = GLUtil.setupDebugMessageCallback(System.err);

        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            glViewport(0, 0, width, height);
            this.width = width;
            this.height = height;
        });

    }

    /**
     * Swap buffer, poll events, and update delta time
     */
    public void update() {

        float currentFrame = (float) glfwGetTime();
        deltaTime = currentFrame - lastFrame;
        lastFrame = currentFrame;

        glfwPollEvents();
        glfwSwapBuffers(window);
    }

    public void destroy() {
        GL.setCapabilities(null);

        if (callback != null) {
            callback.free();
        }

        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);
        glfwTerminate();
        Objects.requireNonNull(glfwSetErrorCallback(null)).free();
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window);
    }

    /**
     * @return Whether window is resized or not
     */
    public boolean isResized() {
        if (resized) {
            resized = false;
            return true;
        } else {
            return false;
        }
    }

    public float getDeltaTime() {
        return deltaTime;
    }

    /**
     * @return Raw GLFW window
     */
    public long getRawWindow() {
        return window;
    }

    public String getTitle() {
        return title;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}

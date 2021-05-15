package kaptainwutax.minemap.util.snksynthesis.voxelgame;

import kaptainwutax.minemap.util.snksynthesis.voxelgame.block.Block;
import kaptainwutax.minemap.util.snksynthesis.voxelgame.block.BlockManager;
import kaptainwutax.minemap.util.snksynthesis.voxelgame.block.BlockType;
import kaptainwutax.minemap.util.snksynthesis.voxelgame.gfx.Shader;
import kaptainwutax.minemap.util.snksynthesis.voxelgame.gfx.Window;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL33.*;

public class Visualizer {
    public static final String PATH = "/others";
    private Window window;
    private Camera cam;
    private Shader shader;
    private Shader lightShader;
    private final BlockManager blockManager;
    private Block light;
    private Vector3f lightPos;

    private void draw(MemoryStack stack) {
        // Bind shader
        shader.bind();

        // Set light color and position
        glUniform3fv(shader.getLocation("lightPos"), lightPos.get(stack.mallocFloat(3)));
        glUniform3f(shader.getLocation("lightColor"), 1.0f, 1.0f, 1.0f);

        // Projection Matrix
        float aspectRatio = (float) window.getWidth() / window.getHeight();
        Matrix4f projection = new Matrix4f().setPerspective((float) Math.toRadians(100.0f), aspectRatio, 0.1f, 100.0f);
        glUniformMatrix4fv(shader.getLocation("projection"), false, projection.get(stack.mallocFloat(16)));

        // View Matrix
        Matrix4f view = cam.getViewMat();
        glUniformMatrix4fv(shader.getLocation("view"), false, view.get(stack.mallocFloat(16)));
        blockManager.checkDestroy();
        blockManager.draw(shader, stack);

        // Unbind shader
        shader.unbind();

        // Bind Light Shader
        lightShader.bind();

        // Set projection and view matrices
        glUniformMatrix4fv(lightShader.getLocation("projection"), false, projection.get(stack.mallocFloat(16)));
        glUniformMatrix4fv(lightShader.getLocation("view"), false, view.get(stack.mallocFloat(16)));

        // Draw light source
        light.draw(lightShader, stack);

        // Unbind light shader
        lightShader.unbind();
    }

    private void init() {
        window = new Window("Visualizer", 650, 650);
        window.create();

        shader = new Shader("vertex.glsl", "fragment.glsl");
        try {
            shader.link();
        } catch (Exception e) {
            e.printStackTrace();
        }

        lightShader = new Shader("light_vertex.glsl", "light_fragment.glsl");
        try {
            lightShader.link();
        } catch (Exception e) {
            e.printStackTrace();
        }

        cam = new Camera();
        cam.addMouseCallback(window);

        lightPos = new Vector3f(blockManager.WIDTH / 2f, 20.5f, blockManager.LENGTH / 2f);
        light = new Block(BlockType.LIGHT);
        light.getModel().translate(lightPos);

        glEnable(GL_DEPTH_TEST);
        glClearColor(0.1607843137254902f, 0.6235294117647059f, 1.0f, 1.0f);
        // glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }

    private void destroy() {
        shader.destroy();
        lightShader.destroy();
        blockManager.destroy();
    }

    private void update() {
        cam.procInput(window);
    }

    public void run() {
        init();

        while (!window.shouldClose()) {
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            update();

            try (MemoryStack stack = MemoryStack.stackPush()) {
                draw(stack);
            }

            window.update();
        }
        destroy();
    }

    public Visualizer(){
        blockManager = new BlockManager();
    }

    public BlockManager getBlockManager() {
        return blockManager;
    }
}

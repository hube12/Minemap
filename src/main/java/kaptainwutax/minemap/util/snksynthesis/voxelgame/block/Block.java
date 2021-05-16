package kaptainwutax.minemap.util.snksynthesis.voxelgame.block;

import kaptainwutax.minemap.util.snksynthesis.voxelgame.gfx.Mesh;
import kaptainwutax.minemap.util.snksynthesis.voxelgame.gfx.Shader;
import kaptainwutax.minemap.util.snksynthesis.voxelgame.gfx.Texture;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.glGetUniformLocation;
import static org.lwjgl.opengl.GL33.glUniformMatrix4fv;

public class Block {

    private Mesh mesh;
    private Texture tex;
    private final BlockType type;
    private final FloatBuffer allocatedMem;
    private final Matrix4f model;

    public Block(BlockType type) {
        switch (type) {
            case GRASS:
                tex = Texture.loadRGBA("soil+grass+stone.png");
                this.mesh = new Mesh(VERTICE_01_10);
                break;
            case STONE:
                tex = Texture.loadRGBA("soil+grass+stone.png");
                this.mesh = new Mesh(VERTICE_1_1);
                break;
            case SOIL:
                tex = Texture.loadRGBA("soil+grass+stone.png");
                this.mesh = new Mesh(VERTICE_0_1);
                break;
            case LIGHT:
                tex = Texture.loadRGBA("light.png");
                this.mesh = new Mesh(LIGHT_CUBE_VERTICES, "LIGHT_SOURCE");
                break;
            case CRYING_OBSIDIAN:
                tex = Texture.loadRGBA("crying+obsidian.png");
                this.mesh = new Mesh(VERTICE_1_1);
                break;
            case OBSIDIAN:
                tex = Texture.loadRGBA("crying+obsidian.png");
                this.mesh = new Mesh(VERTICE_0_0);
                break;

        }
        this.type = type;
        this.model = new Matrix4f();
        allocatedMem = MemoryUtil.memAllocFloat(16);
    }

    /**
     * Copy constructor
     *
     * @param block - Block to be copied
     */
    public Block(Block block) {
        this(block.getType());
    }

    public void draw(Shader shader, MemoryStack stack) {
        int modelLoc = glGetUniformLocation(shader.getProgramId(), "model");
        glUniformMatrix4fv(modelLoc, false, model.get(allocatedMem));
        tex.bind();
        mesh.draw();
        tex.unbind();
    }

    public void destroy() {
        mesh.destroy();
        tex.destroy();
        MemoryUtil.memFree(allocatedMem);
    }

    public BlockType getType() {
        return type;
    }

    public void setPos(Vector3f pos) {
        model.translate(pos);
    }

    public Matrix4f getModel() {
        return model;
    }

    // @formatter:off
    protected final float[] FULL = {
        // Positions            Texture Coords       Normals
        -0.5f, -0.5f, -0.5f,    0.0f, 0.0f,     0.0f,  0.0f, -1.0f,
         0.5f, -0.5f, -0.5f,    1.0f, 0.0f,     0.0f,  0.0f, -1.0f,
         0.5f,  0.5f, -0.5f,    1.0f, 1.0f,     0.0f,  0.0f, -1.0f,
         0.5f,  0.5f, -0.5f,    1.0f, 1.0f,     0.0f,  0.0f, -1.0f,
        -0.5f,  0.5f, -0.5f,    0.0f, 1.0f,     0.0f,  0.0f, -1.0f,
        -0.5f, -0.5f, -0.5f,    0.0f, 0.0f,     0.0f,  0.0f, -1.0f,

        -0.5f, -0.5f,  0.5f,    0.0f, 0.0f,     0.0f,  0.0f, 1.0f,
         0.5f, -0.5f,  0.5f,    1.0f, 0.0f,     0.0f,  0.0f, 1.0f,
         0.5f,  0.5f,  0.5f,    1.0f, 1.0f,     0.0f,  0.0f, 1.0f,
         0.5f,  0.5f,  0.5f,    1.0f, 1.0f,     0.0f,  0.0f, 1.0f,
        -0.5f,  0.5f,  0.5f,    0.0f, 1.0f,     0.0f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,    0.0f, 0.0f,     0.0f,  0.0f, 1.0f,

        -0.5f,  0.5f,  0.5f,    1.0f, 0.0f,     -1.0f,  0.0f,  0.0f,
        -0.5f,  0.5f, -0.5f,    1.0f, 1.0f,     -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f, -0.5f,    0.0f, 1.0f,     -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f, -0.5f,    0.0f, 1.0f,     -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f,  0.5f,    0.0f, 0.0f,     -1.0f,  0.0f,  0.0f,
        -0.5f,  0.5f,  0.5f,    1.0f, 0.0f,     -1.0f,  0.0f,  0.0f,

         0.5f,  0.5f,  0.5f,    1.0f, 0.0f,     1.0f,  0.0f,  0.0f,
         0.5f,  0.5f, -0.5f,    1.0f, 1.0f,     1.0f,  0.0f,  0.0f,
         0.5f, -0.5f, -0.5f,    0.0f, 1.0f,     1.0f,  0.0f,  0.0f,
         0.5f, -0.5f, -0.5f,    0.0f, 1.0f,     1.0f,  0.0f,  0.0f,
         0.5f, -0.5f,  0.5f,    0.0f, 0.0f,     1.0f,  0.0f,  0.0f,
         0.5f,  0.5f,  0.5f,    1.0f, 0.0f,     1.0f,  0.0f,  0.0f,

        -0.5f, -0.5f, -0.5f,    0.0f, 1.0f,     0.0f, -1.0f,  0.0f,
         0.5f, -0.5f, -0.5f,    1.0f, 1.0f,     0.0f, -1.0f,  0.0f,
         0.5f, -0.5f,  0.5f,    1.0f, 0.0f,     0.0f, -1.0f,  0.0f,
         0.5f, -0.5f,  0.5f,    1.0f, 0.0f,     0.0f, -1.0f,  0.0f,
        -0.5f, -0.5f,  0.5f,    0.0f, 0.0f,     0.0f, -1.0f,  0.0f,
        -0.5f, -0.5f, -0.5f,    0.0f, 1.0f,     0.0f, -1.0f,  0.0f,

        -0.5f,  0.5f, -0.5f,    0.0f, 1.0f,     0.0f,  1.0f,  0.0f,
         0.5f,  0.5f, -0.5f,    1.0f, 1.0f,     0.0f,  1.0f,  0.0f,
         0.5f,  0.5f,  0.5f,    1.0f, 0.0f,     0.0f,  1.0f,  0.0f,
         0.5f,  0.5f,  0.5f,    1.0f, 0.0f,     0.0f,  1.0f,  0.0f,
        -0.5f,  0.5f,  0.5f,    0.0f, 0.0f,     0.0f,  1.0f,  0.0f,
        -0.5f,  0.5f, -0.5f,    0.0f, 1.0f,     0.0f,  1.0f,  0.0f
    };
    // @formatter:on

    // @formatter:off
    protected final float[] VERTICE_01_10 = {
        // Positions            Texture Coords       Normals 
        -0.5f, -0.5f, -0.5f,    0.5f, 0.5f,     0.0f,  0.0f, -1.0f,
         0.5f, -0.5f, -0.5f,    1.0f, 0.5f,     0.0f,  0.0f, -1.0f,
         0.5f,  0.5f, -0.5f,    1.0f, 1.0f,     0.0f,  0.0f, -1.0f,
         0.5f,  0.5f, -0.5f,    1.0f, 1.0f,     0.0f,  0.0f, -1.0f,
        -0.5f,  0.5f, -0.5f,    0.5f, 1.0f,     0.0f,  0.0f, -1.0f,
        -0.5f, -0.5f, -0.5f,    0.5f, 0.5f,     0.0f,  0.0f, -1.0f,

        -0.5f, -0.5f,  0.5f,    0.5f, 0.5f,     0.0f,  0.0f, 1.0f, 
         0.5f, -0.5f,  0.5f,    1.0f, 0.5f,     0.0f,  0.0f, 1.0f,
         0.5f,  0.5f,  0.5f,    1.0f, 1.0f,     0.0f,  0.0f, 1.0f,
         0.5f,  0.5f,  0.5f,    1.0f, 1.0f,     0.0f,  0.0f, 1.0f,
        -0.5f,  0.5f,  0.5f,    0.5f, 1.0f,     0.0f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,    0.5f, 0.5f,     0.0f,  0.0f, 1.0f,

        -0.5f,  0.5f,  0.5f,    1.0f, 1.0f,     -1.0f,  0.0f,  0.0f,
        -0.5f,  0.5f, -0.5f,    0.5f, 1.0f,     -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f, -0.5f,    0.5f, 0.5f,     -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f, -0.5f,    0.5f, 0.5f,     -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f,  0.5f,    1.0f, 0.5f,     -1.0f,  0.0f,  0.0f,
        -0.5f,  0.5f,  0.5f,    1.0f, 1.0f,     -1.0f,  0.0f,  0.0f,

         0.5f,  0.5f,  0.5f,    1.0f, 1.0f,     1.0f,  0.0f,  0.0f,
         0.5f,  0.5f, -0.5f,    0.5f, 1.0f,     1.0f,  0.0f,  0.0f,
         0.5f, -0.5f, -0.5f,    0.5f, 0.5f,     1.0f,  0.0f,  0.0f,
         0.5f, -0.5f, -0.5f,    0.5f, 0.5f,     1.0f,  0.0f,  0.0f,
         0.5f, -0.5f,  0.5f,    1.0f, 0.5f,     1.0f,  0.0f,  0.0f,
         0.5f,  0.5f,  0.5f,    1.0f, 1.0f,     1.0f,  0.0f,  0.0f,

        -0.5f, -0.5f, -0.5f,    0.0f, 0.5f,     0.0f, -1.0f,  0.0f,
         0.5f, -0.5f, -0.5f,    0.5f, 0.5f,     0.0f, -1.0f,  0.0f,
         0.5f, -0.5f,  0.5f,    0.5f, 0.0f,     0.0f, -1.0f,  0.0f,
         0.5f, -0.5f,  0.5f,    0.5f, 0.0f,     0.0f, -1.0f,  0.0f,
        -0.5f, -0.5f,  0.5f,    0.0f, 0.0f,     0.0f, -1.0f,  0.0f,
        -0.5f, -0.5f, -0.5f,    0.0f, 0.5f,     0.0f, -1.0f,  0.0f,

        -0.5f,  0.5f, -0.5f,    0.0f, 1.0f,     0.0f,  1.0f,  0.0f,
         0.5f,  0.5f, -0.5f,    0.5f, 1.0f,     0.0f,  1.0f,  0.0f,
         0.5f,  0.5f,  0.5f,    0.5f, 0.5f,     0.0f,  1.0f,  0.0f,
         0.5f,  0.5f,  0.5f,    0.5f, 0.5f,     0.0f,  1.0f,  0.0f,
        -0.5f,  0.5f,  0.5f,    0.0f, 0.5f,     0.0f,  1.0f,  0.0f,
        -0.5f,  0.5f, -0.5f,    0.0f, 1.0f,     0.0f,  1.0f,  0.0f
    };
    // @formatter:on

    // @formatter:off
    protected final float[] VERTICE_0_1 = {
        // Positions          Texture Coords          Normals
        -0.5f, -0.5f, -0.5f,    0.0f, 0.0f,     0.0f,  0.0f, -1.0f,
         0.5f, -0.5f, -0.5f,    0.5f, 0.0f,     0.0f,  0.0f, -1.0f,
         0.5f,  0.5f, -0.5f,    0.5f, 0.5f,     0.0f,  0.0f, -1.0f,
         0.5f,  0.5f, -0.5f,    0.5f, 0.5f,     0.0f,  0.0f, -1.0f,
        -0.5f,  0.5f, -0.5f,    0.0f, 0.5f,     0.0f,  0.0f, -1.0f,
        -0.5f, -0.5f, -0.5f,    0.0f, 0.0f,     0.0f,  0.0f, -1.0f,

        -0.5f, -0.5f,  0.5f,    0.0f, 0.0f,     0.0f,  0.0f, 1.0f,
         0.5f, -0.5f,  0.5f,    0.5f, 0.0f,     0.0f,  0.0f, 1.0f,
         0.5f,  0.5f,  0.5f,    0.5f, 0.5f,     0.0f,  0.0f, 1.0f,
         0.5f,  0.5f,  0.5f,    0.5f, 0.5f,     0.0f,  0.0f, 1.0f,
        -0.5f,  0.5f,  0.5f,    0.0f, 0.5f,     0.0f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,    0.0f, 0.0f,     0.0f,  0.0f, 1.0f,

        -0.5f,  0.5f,  0.5f,    0.5f, 0.0f,     -1.0f,  0.0f,  0.0f,
        -0.5f,  0.5f, -0.5f,    0.5f, 0.5f,     -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f, -0.5f,    0.0f, 0.5f,     -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f, -0.5f,    0.0f, 0.5f,     -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f,  0.5f,    0.0f, 0.0f,     -1.0f,  0.0f,  0.0f,
        -0.5f,  0.5f,  0.5f,    0.5f, 0.0f,     -1.0f,  0.0f,  0.0f,

         0.5f,  0.5f,  0.5f,    0.5f, 0.0f,     1.0f,  0.0f,  0.0f,
         0.5f,  0.5f, -0.5f,    0.5f, 0.5f,     1.0f,  0.0f,  0.0f,
         0.5f, -0.5f, -0.5f,    0.0f, 0.5f,     1.0f,  0.0f,  0.0f,
         0.5f, -0.5f, -0.5f,    0.0f, 0.5f,     1.0f,  0.0f,  0.0f,
         0.5f, -0.5f,  0.5f,    0.0f, 0.0f,     1.0f,  0.0f,  0.0f,
         0.5f,  0.5f,  0.5f,    0.5f, 0.0f,     1.0f,  0.0f,  0.0f,

        -0.5f, -0.5f, -0.5f,    0.0f, 0.5f,     0.0f, -1.0f,  0.0f,
         0.5f, -0.5f, -0.5f,    0.5f, 0.5f,     0.0f, -1.0f,  0.0f,
         0.5f, -0.5f,  0.5f,    0.5f, 0.0f,     0.0f, -1.0f,  0.0f,
         0.5f, -0.5f,  0.5f,    0.5f, 0.0f,     0.0f, -1.0f,  0.0f,
        -0.5f, -0.5f,  0.5f,    0.0f, 0.0f,     0.0f, -1.0f,  0.0f,
        -0.5f, -0.5f, -0.5f,    0.0f, 0.5f,     0.0f, -1.0f,  0.0f,

        -0.5f,  0.5f, -0.5f,    0.0f, 0.5f,     0.0f,  1.0f,  0.0f,
         0.5f,  0.5f, -0.5f,    0.5f, 0.5f,     0.0f,  1.0f,  0.0f,
         0.5f,  0.5f,  0.5f,    0.5f, 0.0f,     0.0f,  1.0f,  0.0f,
         0.5f,  0.5f,  0.5f,    0.5f, 0.0f,     0.0f,  1.0f,  0.0f,
        -0.5f,  0.5f,  0.5f,    0.0f, 0.0f,     0.0f,  1.0f,  0.0f,
        -0.5f,  0.5f, -0.5f,    0.0f, 0.5f,     0.0f,  1.0f,  0.0f
    };
    // @formatter:on

    // @formatter:off
    protected final float[] VERTICE_1_1 = {
        // Positions            Texture Coords         Normals 
        -0.5f, -0.5f, -0.5f,    0.5f, 0.0f,      0.0f,  0.0f, -1.0f,
         0.5f, -0.5f, -0.5f,    1.0f, 0.0f,      0.0f,  0.0f, -1.0f,
         0.5f,  0.5f, -0.5f,    1.0f, 0.5f,      0.0f,  0.0f, -1.0f,
         0.5f,  0.5f, -0.5f,    1.0f, 0.5f,      0.0f,  0.0f, -1.0f,
        -0.5f,  0.5f, -0.5f,    0.5f, 0.5f,      0.0f,  0.0f, -1.0f,
        -0.5f, -0.5f, -0.5f,    0.5f, 0.0f,      0.0f,  0.0f, -1.0f,

        -0.5f, -0.5f,  0.5f,    0.5f, 0.0f,      0.0f,  0.0f, 1.0f,
         0.5f, -0.5f,  0.5f,    1.0f, 0.0f,      0.0f,  0.0f, 1.0f,
         0.5f,  0.5f,  0.5f,    1.0f, 0.5f,      0.0f,  0.0f, 1.0f,
         0.5f,  0.5f,  0.5f,    1.0f, 0.5f,      0.0f,  0.0f, 1.0f,
        -0.5f,  0.5f,  0.5f,    0.5f, 0.5f,      0.0f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,    0.5f, 0.0f,      0.0f,  0.0f, 1.0f,

        -0.5f,  0.5f,  0.5f,    0.5f, 0.0f,      -1.0f,  0.0f,  0.0f,
        -0.5f,  0.5f, -0.5f,    1.0f, 0.0f,      -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f, -0.5f,    1.0f, 0.5f,      -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f, -0.5f,    1.0f, 0.5f,      -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f,  0.5f,    0.5f, 0.5f,      -1.0f,  0.0f,  0.0f,
        -0.5f,  0.5f,  0.5f,    0.5f, 0.0f,      -1.0f,  0.0f,  0.0f,

         0.5f,  0.5f,  0.5f,    0.5f, 0.0f,      1.0f,  0.0f,  0.0f,
         0.5f,  0.5f, -0.5f,    1.0f, 0.0f,      1.0f,  0.0f,  0.0f,
         0.5f, -0.5f, -0.5f,    1.0f, 0.5f,      1.0f,  0.0f,  0.0f,
         0.5f, -0.5f, -0.5f,    1.0f, 0.5f,      1.0f,  0.0f,  0.0f,
         0.5f, -0.5f,  0.5f,    0.5f, 0.5f,      1.0f,  0.0f,  0.0f,
         0.5f,  0.5f,  0.5f,    0.5f, 0.0f,      1.0f,  0.0f,  0.0f,

        -0.5f, -0.5f, -0.5f,    0.5f, 0.0f,      0.0f, -1.0f,  0.0f,
         0.5f, -0.5f, -0.5f,    1.0f, 0.0f,      0.0f, -1.0f,  0.0f,
         0.5f, -0.5f,  0.5f,    1.0f, 0.5f,      0.0f, -1.0f,  0.0f,
         0.5f, -0.5f,  0.5f,    1.0f, 0.5f,      0.0f, -1.0f,  0.0f,
        -0.5f, -0.5f,  0.5f,    0.5f, 0.5f,      0.0f, -1.0f,  0.0f,
        -0.5f, -0.5f, -0.5f,    0.5f, 0.0f,      0.0f, -1.0f,  0.0f,

        -0.5f,  0.5f, -0.5f,    0.5f, 0.0f,      0.0f,  1.0f,  0.0f,
         0.5f,  0.5f, -0.5f,    1.0f, 0.0f,      0.0f,  1.0f,  0.0f,
         0.5f,  0.5f,  0.5f,    1.0f, 0.5f,      0.0f,  1.0f,  0.0f,
         0.5f,  0.5f,  0.5f,    1.0f, 0.5f,      0.0f,  1.0f,  0.0f,
        -0.5f,  0.5f,  0.5f,    0.5f, 0.5f,      0.0f,  1.0f,  0.0f,
        -0.5f,  0.5f, -0.5f,    0.5f, 0.0f,      0.0f,  1.0f,  0.0f
    };
    // @formatter:on


    // @formatter:off
    protected final float[] VERTICE_0_0 = {
        // Positions            Texture Coords         Normals
        -0.5f, -0.5f, -0.5f,    0.0f, 0.5f,      0.0f,  0.0f, -1.0f,
        0.5f, -0.5f, -0.5f,    0.5f, 0.5f,      0.0f,  0.0f, -1.0f,
        0.5f,  0.5f, -0.5f,    0.5f, 1.0f,      0.0f,  0.0f, -1.0f,
        0.5f,  0.5f, -0.5f,    0.5f, 1.0f,      0.0f,  0.0f, -1.0f,
        -0.5f,  0.5f, -0.5f,    0.0f, 1.0f,      0.0f,  0.0f, -1.0f,
        -0.5f, -0.5f, -0.5f,    0.0f, 0.5f,      0.0f,  0.0f, -1.0f,

        -0.5f, -0.5f,  0.5f,    0.0f, 0.5f,       0.0f,  0.0f, 1.0f,
        0.5f, -0.5f,  0.5f,    0.5f, 0.5f,       0.0f,  0.0f, 1.0f,
        0.5f,  0.5f,  0.5f,    0.5f, 1.0f,       0.0f,  0.0f, 1.0f,
        0.5f,  0.5f,  0.5f,    0.5f, 1.0f,       0.0f,  0.0f, 1.0f,
        -0.5f,  0.5f,  0.5f,    0.0f, 1.0f,       0.0f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,    0.0f, 0.5f,       0.0f,  0.0f, 1.0f,

        -0.5f,  0.5f,  0.5f,     0.0f, 0.5f,       -1.0f,  0.0f,  0.0f,
        -0.5f,  0.5f, -0.5f,    0.5f, 0.5f,        -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f, -0.5f,    0.5f, 1.0f,        -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f, -0.5f,    0.5f, 1.0f,        -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f,  0.5f,     0.0f, 1.0f,       -1.0f,  0.0f,  0.0f,
        -0.5f,  0.5f,  0.5f,     0.0f, 0.5f,       -1.0f,  0.0f,  0.0f,

        0.5f,  0.5f,  0.5f,    0.0f, 0.5f,      1.0f,  0.0f,  0.0f,
        0.5f,  0.5f, -0.5f,   0.5f, 0.5f,       1.0f,  0.0f,  0.0f,
        0.5f, -0.5f, -0.5f,   0.5f, 1.0f,       1.0f,  0.0f,  0.0f,
        0.5f, -0.5f, -0.5f,   0.5f, 1.0f,       1.0f,  0.0f,  0.0f,
        0.5f, -0.5f,  0.5f,    0.0f, 1.0f,      1.0f,  0.0f,  0.0f,
        0.5f,  0.5f,  0.5f,    0.0f, 0.5f,      1.0f,  0.0f,  0.0f,

        -0.5f, -0.5f, -0.5f,    0.0f, 0.5f,      0.0f, -1.0f,  0.0f,
        0.5f, -0.5f, -0.5f,    0.5f, 0.5f,      0.0f, -1.0f,  0.0f,
        0.5f, -0.5f,  0.5f,    0.5f, 1.0f,      0.0f, -1.0f,  0.0f,
        0.5f, -0.5f,  0.5f,    0.5f, 1.0f,      0.0f, -1.0f,  0.0f,
        -0.5f, -0.5f,  0.5f,    0.0f, 1.0f,      0.0f, -1.0f,  0.0f,
        -0.5f, -0.5f, -0.5f,    0.0f, 0.5f,      0.0f, -1.0f,  0.0f,

        -0.5f,  0.5f, -0.5f,    0.0f, 0.5f,       0.0f,  1.0f,  0.0f,
        0.5f,  0.5f, -0.5f,    0.5f, 0.5f,       0.0f,  1.0f,  0.0f,
        0.5f,  0.5f,  0.5f,    0.5f, 1.0f,       0.0f,  1.0f,  0.0f,
        0.5f,  0.5f,  0.5f,    0.5f, 1.0f,       0.0f,  1.0f,  0.0f,
        -0.5f,  0.5f,  0.5f,    0.0f, 1.0f,       0.0f,  1.0f,  0.0f,
        -0.5f,  0.5f, -0.5f,    0.0f, 0.5f,       0.0f,  1.0f,  0.0f
    };
    // @formatter:on

    // @formatter:off
    protected final float[] LIGHT_CUBE_VERTICES = {
        // Positions            Texture Coords
        -0.5f, -0.5f, -0.5f,    0.0f, 0.0f,
         0.5f, -0.5f, -0.5f,    1.0f, 0.0f,
         0.5f,  0.5f, -0.5f,    1.0f, 1.0f,
         0.5f,  0.5f, -0.5f,    1.0f, 1.0f,
        -0.5f,  0.5f, -0.5f,    0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,    0.0f, 0.0f,

        -0.5f, -0.5f,  0.5f,    0.0f, 0.0f,
         0.5f, -0.5f,  0.5f,    1.0f, 0.0f,
         0.5f,  0.5f,  0.5f,    1.0f, 1.0f,
         0.5f,  0.5f,  0.5f,    1.0f, 1.0f,
        -0.5f,  0.5f,  0.5f,    0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,    0.0f, 0.0f,

        -0.5f,  0.5f,  0.5f,    1.0f, 0.0f,
        -0.5f,  0.5f, -0.5f,    1.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,    0.0f, 1.0f,
        -0.5f, -0.5f, -0.5f,    0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,    0.0f, 0.0f,
        -0.5f,  0.5f,  0.5f,    1.0f, 0.0f,

         0.5f,  0.5f,  0.5f,    1.0f, 0.0f,
         0.5f,  0.5f, -0.5f,    1.0f, 1.0f,
         0.5f, -0.5f, -0.5f,    0.0f, 1.0f,
         0.5f, -0.5f, -0.5f,    0.0f, 1.0f,
         0.5f, -0.5f,  0.5f,    0.0f, 0.0f,
         0.5f,  0.5f,  0.5f,    1.0f, 0.0f,

        -0.5f, -0.5f, -0.5f,    0.0f, 1.0f,
         0.5f, -0.5f, -0.5f,    1.0f, 1.0f,
         0.5f, -0.5f,  0.5f,    1.0f, 0.0f,
         0.5f, -0.5f,  0.5f,    1.0f, 0.0f,
        -0.5f, -0.5f,  0.5f,    0.0f, 0.0f,
        -0.5f, -0.5f, -0.5f,    0.0f, 1.0f,

        -0.5f,  0.5f, -0.5f,    0.0f, 1.0f,
         0.5f,  0.5f, -0.5f,    1.0f, 1.0f,
         0.5f,  0.5f,  0.5f,    1.0f, 0.0f,
         0.5f,  0.5f,  0.5f,    1.0f, 0.0f,
        -0.5f,  0.5f,  0.5f,    0.0f, 0.0f,
        -0.5f,  0.5f, -0.5f,    0.0f, 1.0f
    };
    // @formatter:on

    // @formatter:off
    protected final float[] COLORED_CUBE_VERTICES = {
        -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f,
        0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f, 
        0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f, 
        0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f, 
        -0.5f,  0.5f, -0.5f,  0.0f,  0.0f, -1.0f, 
        -0.5f, -0.5f, -0.5f,  0.0f,  0.0f, -1.0f, 

        -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
        0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
        0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
        0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
        -0.5f,  0.5f,  0.5f,  0.0f,  0.0f, 1.0f,
        -0.5f, -0.5f,  0.5f,  0.0f,  0.0f, 1.0f,

        -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
        -0.5f,  0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f, -0.5f, -1.0f,  0.0f,  0.0f,
        -0.5f, -0.5f,  0.5f, -1.0f,  0.0f,  0.0f,
        -0.5f,  0.5f,  0.5f, -1.0f,  0.0f,  0.0f,

        0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
        0.5f,  0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
        0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
        0.5f, -0.5f, -0.5f,  1.0f,  0.0f,  0.0f,
        0.5f, -0.5f,  0.5f,  1.0f,  0.0f,  0.0f,
        0.5f,  0.5f,  0.5f,  1.0f,  0.0f,  0.0f,

        -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
        0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,
        0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
        0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
        -0.5f, -0.5f,  0.5f,  0.0f, -1.0f,  0.0f,
        -0.5f, -0.5f, -0.5f,  0.0f, -1.0f,  0.0f,

        -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
        0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f,
        0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
        0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
        -0.5f,  0.5f,  0.5f,  0.0f,  1.0f,  0.0f,
        -0.5f,  0.5f, -0.5f,  0.0f,  1.0f,  0.0f
    };
    // @formatter:on
}

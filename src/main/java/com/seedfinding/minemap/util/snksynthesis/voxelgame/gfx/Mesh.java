package com.seedfinding.minemap.util.snksynthesis.voxelgame.gfx;

import org.lwjgl.system.MemoryUtil;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL33.*;

/**
 * {@link Mesh} is for initializing and drawing meshes
 *
 * @param vertices must be in format
 *
 *                 <pre>
 *                                    {posX, posY, posZ, texCoordX, texCoordY, normalX, normalY, normalZ, ...}
 *                                                 </pre>
 */
public class Mesh {

    private int vaoId; // Vertex Array Object ID
    private int vboId; // Vertex Buffer Object ID

    /**
     * Normal constructor
     */
    public Mesh(float[] vertices) {
        // Allocate memory
        FloatBuffer verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
        verticesBuffer.put(vertices).flip();

        // Create VAO
        vaoId = glGenVertexArrays();
        glBindVertexArray(vaoId);

        // Vertices
        vboId = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboId);
        glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
        int positionSize = 3;
        int textureSize = 2;
        int normalSize = 3;
        int vertexSizeBytes = (positionSize + textureSize + normalSize) * Float.BYTES;
        // Positions
        glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);
        // Texture Coordinates
        glVertexAttribPointer(1, textureSize, GL_FLOAT, false, vertexSizeBytes, positionSize * Float.BYTES);
        glEnableVertexAttribArray(1);
        // Normals
        glVertexAttribPointer(2, normalSize, GL_FLOAT, false, vertexSizeBytes, textureSize * Float.BYTES);
        glEnableVertexAttribArray(2);

        // Unbind VBO and VAO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glBindVertexArray(0);

        // Free memory
        if (verticesBuffer != null) {
            MemoryUtil.memFree(verticesBuffer);
        }
    }

    /**
     * Special constructor for other types of meshes.
     *
     * @param vertices
     * @param type     can be "LIGHT_SOURCE"
     */
    public Mesh(float[] vertices, String type) {
        FloatBuffer verticesBuffer;
        int positionSize;
        int textureSize;
        int vertexSizeBytes;
        switch (type) {
            case "LIGHT_SOURCE":
                // Allocate memory
                verticesBuffer = MemoryUtil.memAllocFloat(vertices.length);
                verticesBuffer.put(vertices).flip();

                // Create VAO
                vaoId = glGenVertexArrays();
                glBindVertexArray(vaoId);

                // Vertices
                vboId = glGenBuffers();
                glBindBuffer(GL_ARRAY_BUFFER, vboId);
                glBufferData(GL_ARRAY_BUFFER, verticesBuffer, GL_STATIC_DRAW);
                positionSize = 3;
                textureSize = 2;
                vertexSizeBytes = (positionSize + textureSize) * Float.BYTES;
                // Positions
                glVertexAttribPointer(0, positionSize, GL_FLOAT, false, vertexSizeBytes, 0);
                glEnableVertexAttribArray(0);
                // Texture Coordinates
                glVertexAttribPointer(1, textureSize, GL_FLOAT, false, vertexSizeBytes, positionSize * Float.BYTES);
                glEnableVertexAttribArray(1);

                // Free memory
                if (verticesBuffer != null) {
                    MemoryUtil.memFree(verticesBuffer);
                }
                break;
        }
    }

    public void draw() {
        // Bind
        glBindVertexArray(vaoId);

        // Draw
        glDrawArrays(GL_TRIANGLES, 0, 36);

        // Unbind
        glBindVertexArray(0);
    }

    public void destroy() {
        // Delete VBO
        glBindBuffer(GL_ARRAY_BUFFER, 0);
        glDeleteBuffers(vboId);
        // Delete VAO
        glBindVertexArray(0);
        glDeleteVertexArrays(vaoId);
    }
}

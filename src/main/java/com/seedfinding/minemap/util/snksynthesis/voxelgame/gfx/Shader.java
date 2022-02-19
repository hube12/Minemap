package com.seedfinding.minemap.util.snksynthesis.voxelgame.gfx;

import com.seedfinding.minemap.init.Logger;
import com.seedfinding.minemap.util.snksynthesis.voxelgame.util.IOUtils;

import static org.lwjgl.opengl.GL33.*;

/**
 * {@link Shader} handles shaders
 */
public class Shader {

    private int programId;
    private int vertexId;
    private int fragId;
    private final String vertexPath;
    private final String fragPath;

    /**
     * Initializes a Shader object
     *
     * @param vertexPath path to vertex shader
     * @param fragPath   path to fragment shader
     */
    public Shader(String vertexPath, String fragPath) {
        this.vertexPath = vertexPath;
        this.fragPath = fragPath;
    }

    /**
     * Link shaders
     *
     * @throws Exception when there is failure in compiling, linking, etc. of
     *                   shaders
     */
    public void link() throws Exception {

        programId = glCreateProgram();
        if (programId == 0) {
            throw new Exception("Unable to create shader program!");
        }

        vertexId = createShader(vertexPath, GL_VERTEX_SHADER);
        fragId = createShader(fragPath, GL_FRAGMENT_SHADER);

        glLinkProgram(programId);
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking shader: " + glGetProgramInfoLog(programId));
        }

        if (vertexId == 0) {
            glDetachShader(programId, vertexId);
        }
        if (fragId == 0) {
            glDetachShader(programId, vertexId);
        }

        glValidateProgram(programId);
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0) {
            Logger.LOGGER.warning(glGetProgramInfoLog(programId));
        }
    }

    public void bind() {
        glUseProgram(programId);
    }

    public void unbind() {
        glUseProgram(0);
    }

    public void destroy() {
        unbind();
        if (programId != 0) {
            glDeleteProgram(programId);
        }
    }

    private int createShader(String path, int shaderType) throws Exception {
        String code = IOUtils.getFromFile(path, IOUtils::readStringFromInputStream);
        if (code == null) {
            Logger.LOGGER.severe("Code could not be read from " + path);
        }
        int shaderId = glCreateShader(shaderType);
        if (shaderId == 0) {
            throw new Exception("Error creating shader: " + glGetShaderInfoLog(shaderId));
        }

        glShaderSource(shaderId, code);
        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error creating shader: " + glGetShaderInfoLog(shaderId));
        }

        glAttachShader(programId, shaderId);

        return shaderId;
    }

    public int getProgramId() {
        return programId;
    }

    public int getLocation(String name) {
        return glGetUniformLocation(programId, name);
    }
}

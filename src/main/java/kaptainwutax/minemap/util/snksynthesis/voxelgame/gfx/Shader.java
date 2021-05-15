package kaptainwutax.minemap.util.snksynthesis.voxelgame.gfx;

import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.util.data.Assets;
import kaptainwutax.minemap.util.snksynthesis.voxelgame.Visualizer;

import static kaptainwutax.minemap.init.Logger.LOGGER;
import static org.lwjgl.opengl.GL33.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

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
     * @param vertexPath path to vertex shader
     * @param fragPath path to fragment shader
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
            System.err.println("Warning: " + glGetProgramInfoLog(programId));
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
        String code = readFile(path);
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

    private String readFile(String path) {
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
        java.util.List<Pair<Path, InputStream>> list=Assets.getInputStream(dir,isJar,path,"");
        if (list.size()!=1)return null;
        StringBuilder sb = new StringBuilder();
        try (Scanner sc = new Scanner(list.get(0).getSecond())) {
            while (sc.hasNextLine()) {
                sb.append(sc.nextLine());
                sb.append(System.lineSeparator());
            }
        }
        if (fileSystem != null) {
            try {
                fileSystem.close();
            } catch (IOException e) {
                LOGGER.severe(String.format("Filesystem was not closed correctly for %s with error : %s", uri, e));
            }
        }
        return sb.toString();
    }

    public int getProgramId() {
        return programId;
    }

    public int getLocation(String name) {
        return glGetUniformLocation(programId, name);
    }
}

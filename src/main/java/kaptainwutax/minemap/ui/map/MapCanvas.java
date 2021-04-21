package kaptainwutax.minemap.ui.map;

import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.minemap.util.data.DrawInfo;
import org.lwjgl.opengl.awt.AWTGLCanvas;
import org.lwjgl.opengl.awt.GLData;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

import static org.lwjgl.opengl.GL.createCapabilities;
import static org.lwjgl.opengl.GL11.glClearColor;

public class MapCanvas extends AWTGLCanvas {
    private final MapPanel map;

    public MapCanvas(MapPanel map) {
        super(generateGLData());
        this.map = map;
    }

    @Override
    public void setSize(Dimension d) {
        super.setSize(d);
        System.out.println("FFF "+d);
    }

    @Override
    public void initGL() {
        System.out.println("OpenGL version: " + effective.majorVersion + "." + effective.minorVersion + " (Profile: " + effective.profile + ")");
        createCapabilities();
        glClearColor(0f, 1f, 1f, 1);
    }

    @Override
    public void paintGL() {
        Map<Fragment, DrawInfo> drawQueue = map.getDrawQueue();
        drawQueue.forEach((fragment, info) -> {
            try {
                fragment.build();
                if (fragment.isBuilt()) fragment.drawBiomes(info, map.getWidth(), map.getHeight());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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

//    @Override
//    public Graphics getGraphics() {
//        Graphics graphics = super.getGraphics();
//        if(graphics instanceof Graphics2D){
//            return new NonClearGraphics2D((Graphics2D) graphics);
//        } else {
//            return new NonClearGraphics(graphics);
//        }
//    }


    public static GLData generateGLData() {
        GLData data = new GLData();
//        data.samples = 4;
//        data.swapInterval = 1;
        return data;
    }
}

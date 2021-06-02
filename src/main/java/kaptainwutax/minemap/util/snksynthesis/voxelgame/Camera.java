package kaptainwutax.minemap.util.snksynthesis.voxelgame;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import static org.lwjgl.glfw.GLFW.*;

import kaptainwutax.minemap.util.snksynthesis.voxelgame.gfx.Window;

public class Camera {

    private Vector3f front;
    private final Vector3f pos;
    private boolean firstMouse;
    private float lastX, lastY, yaw, pitch;

    private static final Vector3f UP = new Vector3f(0.0f, 1.0f, 0.0f);
    private static final Vector3f DOWN = new Vector3f(0.0f, -1.0f, 0.0f);

    public Camera() {
        front = new Vector3f(0.0f, 0.0f, -1.0f);
        pos = new Vector3f(0.0f, 0.0f, 0.0f);
    }

    public void procInput(Window window) {

        // Keyboard input
        final float SPEED = 5.0f * window.getDeltaTime();
        if (glfwGetKey(window.getRawWindow(), GLFW_KEY_W) == GLFW_PRESS) {
            pos.add(new Vector3f(front).mul(SPEED));
        }
        if (glfwGetKey(window.getRawWindow(), GLFW_KEY_S) == GLFW_PRESS) {
            pos.sub(new Vector3f(front).mul(SPEED));
        }
        if (glfwGetKey(window.getRawWindow(), GLFW_KEY_A) == GLFW_PRESS) {
            pos.sub(new Vector3f(front).cross(Camera.UP).normalize().mul(SPEED));
        }
        if (glfwGetKey(window.getRawWindow(), GLFW_KEY_D) == GLFW_PRESS) {
            pos.add(new Vector3f(front).cross(Camera.UP).normalize().mul(SPEED));
        }
        if (glfwGetKey(window.getRawWindow(), GLFW_KEY_SPACE) == GLFW_PRESS) {
            pos.add(new Vector3f(Camera.UP).mul(SPEED)); // Going up
        }
        if (glfwGetKey(window.getRawWindow(), GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            pos.add(new Vector3f(Camera.DOWN).mul(SPEED)); // Going down
        }

        // Toggle cursor mode
        if (glfwGetKey(window.getRawWindow(), GLFW_KEY_Q) == GLFW_PRESS) {
            glfwSetInputMode(window.getRawWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        }
        if (glfwGetKey(window.getRawWindow(), GLFW_KEY_ESCAPE) == GLFW_PRESS) {
            glfwSetInputMode(window.getRawWindow(), GLFW_CURSOR, GLFW_CURSOR_NORMAL);
            firstMouse = true;
        }

        if (glfwGetKey(window.getRawWindow(), GLFW_KEY_0) == GLFW_PRESS) {
            Visualizer.eventManager.process(EventManager.EventAction.KEY_0);
        }
        if (glfwGetKey(window.getRawWindow(), GLFW_KEY_1) == GLFW_PRESS) {
            Visualizer.eventManager.process(EventManager.EventAction.KEY_1);
        }
        if (glfwGetKey(window.getRawWindow(), GLFW_KEY_2) == GLFW_PRESS) {
            Visualizer.eventManager.process(EventManager.EventAction.KEY_2);
        }
        if (glfwGetKey(window.getRawWindow(), GLFW_KEY_3) == GLFW_PRESS) {
            Visualizer.eventManager.process(EventManager.EventAction.KEY_3);
        }
        if (glfwGetKey(window.getRawWindow(), GLFW_KEY_4) == GLFW_PRESS) {
            Visualizer.eventManager.process(EventManager.EventAction.KEY_4);
        }
        if (glfwGetKey(window.getRawWindow(), GLFW_KEY_5) == GLFW_PRESS) {
            Visualizer.eventManager.process(EventManager.EventAction.KEY_5);
        }
        if (glfwGetKey(window.getRawWindow(), GLFW_KEY_6) == GLFW_PRESS) {
            Visualizer.eventManager.process(EventManager.EventAction.KEY_6);
        }
        if (glfwGetKey(window.getRawWindow(), GLFW_KEY_7) == GLFW_PRESS) {
            Visualizer.eventManager.process(EventManager.EventAction.KEY_7);
        }
        if (glfwGetKey(window.getRawWindow(), GLFW_KEY_8) == GLFW_PRESS) {
            Visualizer.eventManager.process(EventManager.EventAction.KEY_8);
        }
        if (glfwGetKey(window.getRawWindow(), GLFW_KEY_9) == GLFW_PRESS) {
            Visualizer.eventManager.process(EventManager.EventAction.KEY_9);
        }

    }

    public void addMouseCallback(Window window) {

        lastX = window.getWidth() / 2f;
        lastY = window.getHeight() / 2f;
        firstMouse = true;

        glfwSetCursorPos(window.getRawWindow(), lastX, lastY);
        glfwSetInputMode(window.getRawWindow(), GLFW_CURSOR, GLFW_CURSOR_DISABLED);

        glfwSetCursorPosCallback(window.getRawWindow(), (_window, xpos, ypos) -> {
            if (glfwGetInputMode(window.getRawWindow(), GLFW_CURSOR) == GLFW_CURSOR_DISABLED) {
                if (firstMouse) {
                    glfwSetCursorPos(window.getRawWindow(), lastX, lastY);
                    // lastX = (float) xpos;
                    // lastY = (float) ypos;
                    firstMouse = false;
                }

                float xOffset = (float) (xpos - lastX);
                float yOffset = (float) (lastY - ypos);
                lastX = (float) xpos;
                lastY = (float) ypos;

                final float SENSITIVITY = 0.1f;

                xOffset *= SENSITIVITY;
                yOffset *= SENSITIVITY;

                yaw += xOffset;
                pitch += yOffset;

                if (pitch > 89.0f) {
                    pitch = 89.0f;
                } else if (pitch < -89.0f) {
                    pitch = -89.0f;
                }

                Vector3f direction = new Vector3f();
                direction.x = (float) Math.cos(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
                direction.y = (float) Math.sin(Math.toRadians(pitch));
                direction.z = (float) Math.sin(Math.toRadians(yaw)) * (float) Math.cos(Math.toRadians(pitch));
                front = direction.normalize();
            }
        });
    }

    public Matrix4f getViewMat() {
        return new Matrix4f().lookAt(pos, new Vector3f(pos).add(front), Camera.UP);
    }
}

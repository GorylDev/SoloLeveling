package engine.core;

import engine.graphics.Camera;
import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.graphics.Texture;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {
    private static Matrix4f projectionMatrix;
    private static Matrix4f viewMatrix;
    private static Matrix4f modelMatrix;
    private long window;
    private float playerX = 0.0f;
    private float playerY = 0.0f;
    private float playerZ = 0.0f;
    private float playerSpeed = 2.0f;

    public void run() {
        init();
        loop();

        glfwDestroyWindow(window);
        glfwTerminate();
    }

    private void init() {
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        window = glfwCreateWindow(800, 600, "Solo Leveling", NULL, NULL);
        if (window == NULL) {
            throw new RuntimeException("Failed to create the GLFW window");
        }

        glfwSetKeyCallback(window, (windowHandle, key, scancode, action, mods) -> {
                String userAction = switch (action) {
                    case GLFW_PRESS -> "pressed";
                    case GLFW_RELEASE -> "released";
                    case GLFW_REPEAT -> "repeated";
                    default -> "unknown action";
                };

                String keyName = glfwGetKeyName(key, scancode);

                switch (key) {
                    case GLFW_KEY_UNKNOWN:
                        keyName = "Unknown Key (code: " + key + ")";
                        break;
                    case GLFW_KEY_W:
                        keyName = "W";
                        break;
                    case GLFW_KEY_A:
                        keyName = "A";
                        break;
                    case GLFW_KEY_S:
                        keyName = "S";
                        break;
                    case GLFW_KEY_D:
                        keyName = "D";
                        break;
                    case GLFW_KEY_ESCAPE:
                        keyName = "ESCAPE";
                        break;
                    default:
                        break;
                }

                System.out.println("System: Key " + keyName + " was " + userAction);
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                glfwSetWindowShouldClose(windowHandle, true); // Close game on ESC
                System.out.println("System: Game closed on action: " + userAction);
            }
            if (key == GLFW_KEY_D && action == GLFW_PRESS) {
                System.out.println("System: Quest Started - Move Right.");
            }
        });

        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glEnable(GL_DEPTH_TEST);
        glDepthFunc(GL_LESS);
        glfwShowWindow(window);
    }

    private void loop() {
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

        double lastTime = glfwGetTime();

        Camera camera = new Camera(0.0f, 0.0f, 5.0f);

        ShaderProgram shaderProgram = new ShaderProgram("shaders/vertex.glsl", "shaders/fragment.glsl");

        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("viewMatrix");
        shaderProgram.createUniform("modelMatrix");

        projectionMatrix = camera.getProjectionMatrix(800, 600);
        viewMatrix = camera.getViewMatrix();

        float[] positions = new float[]{
                -0.5f,  0.5f, 0.0f, // 0: top-left
                -0.5f, -0.5f, 0.0f, // 1: bottom-left
                0.5f, -0.5f, 0.0f, // 2: bottom-right
                0.5f,  0.5f, 0.0f, // 3: top-right
        };

        float[] textCoords = new float[]{
                0.0f, 1.0f, // 0: top-left
                0.0f, 0.0f, // 1: bottom-left
                1.0f, 0.0f, // 2: bottom-right
                1.0f, 1.0f, // 3: top-right
        };

        int[] indices = new int[]{
                0, 1, 3,
                3, 1, 2
        };

        Mesh mesh = new Mesh(positions, textCoords, indices);

        Transform quadTransform = new Transform();

        Texture texture = new Texture("resources/textures/test.jpeg");

        while (!glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            double deltaTime = currentTime - lastTime;
            lastTime = currentTime;

            glfwPollEvents();
            control_wsad(deltaTime, camera);
            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            shaderProgram.bind();

            Matrix4f currentModelMatrix = new Matrix4f();

            quadTransform.getModelMatrix(currentModelMatrix);
            quadTransform.rotation.y += (float) (60.0f * deltaTime);
            quadTransform.rotation.x += (float) (60.0f * deltaTime);
            quadTransform.rotation.z += (float) (60.0f * deltaTime);

            viewMatrix = camera.getViewMatrix();
            shaderProgram.setUniform("modelMatrix", currentModelMatrix);
            shaderProgram.setUniform("viewMatrix", viewMatrix);
            shaderProgram.setUniform("projectionMatrix", projectionMatrix);

            texture.bind();
            mesh.render();
            shaderProgram.unbind();
            glfwSwapBuffers(window);
        }
        shaderProgram.cleanup();
        mesh.cleanup();
        texture.cleanup();
    }

    private void control_wsad(double deltaTime, Camera camera) {
        float moveAmount = (float) (playerSpeed * deltaTime);

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            camera.movePosition(0, 0, -moveAmount); //Move forward
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            camera.movePosition(0, 0, moveAmount);  //Move backward
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            camera.movePosition(-moveAmount, 0, 0); //Strafe left
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            camera.movePosition(moveAmount, 0, 0);  //Strafe righ
        }
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            camera.movePosition(0, moveAmount, 0);  //Fly up
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            camera.movePosition(0, -moveAmount, 0); //Fly down
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }
}
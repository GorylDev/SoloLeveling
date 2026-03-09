package engine.core;

import engine.graphics.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {
    private long window;
    private float playerX = 0.0f;
    private float playerY = 0.0f;
    private float playerZ = 0.0f;

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

        window = glfwCreateWindow(1280, 720, "Solo Leveling", NULL, NULL);
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
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_DEPTH_TEST);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glDepthFunc(GL_LESS);
        glfwShowWindow(window);
    }

    private void loop() {
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        double lastTime = glfwGetTime();

        Camera camera = new Camera(0.0f, 0.0f, 30.0f);
        ShaderProgram shaderProgram = new ShaderProgram("shaders/vertex.glsl", "shaders/fragment.glsl");

        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("viewMatrix");
        shaderProgram.createUniform("modelMatrix");
        shaderProgram.createUniform("textureOffset");
        shaderProgram.createUniform("textureScale");

        Matrix4f projectionMatrix = camera.getProjectionMatrix(1980, 1080);
        Matrix4f viewMatrix;

        Mesh mesh = ModelLoader.loadModel("resources/textures/sung-jin-woo.obj");
        GameObject player = new GameObject(mesh, new Texture("resources/textures/SungJin-Woo.png"));

        while (!glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            double deltaTime = currentTime - lastTime;
            lastTime = currentTime;

            glfwPollEvents();
            control_wsad(deltaTime, player);
            glfwPollEvents();
            player.getTransform().rotation.y += (float) (60.0f * deltaTime); //Rotate player over time
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            shaderProgram.bind();

            Matrix4f currentModelMatrix = new Matrix4f();
            viewMatrix = camera.getViewMatrix();
            shaderProgram.setUniform("viewMatrix", viewMatrix);
            shaderProgram.setUniform("projectionMatrix", projectionMatrix);

            player.getTransform().scale.set(4.0f, 4.0f, 4.0f);
            player.render(shaderProgram, currentModelMatrix);

            shaderProgram.unbind();
            glfwSwapBuffers(window);
        }
        shaderProgram.cleanup();
        mesh.cleanup();
    }

    private void control_wsad(double deltaTime, GameObject player) {
        float playerSpeed = 4.0f;
        float moveAmount = (float) (playerSpeed * deltaTime);
        Vector3f pos = player.getTransform().position;

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            pos.y += moveAmount;//Move forward
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            pos.y -= moveAmount;//Move backward
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            pos.x -= moveAmount;//Strafe left
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            pos.x += moveAmount;//Strafe righ
        }
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            pos.z += moveAmount;//Fly up
        }
        if (glfwGetKey(window, GLFW_KEY_LEFT_SHIFT) == GLFW_PRESS) {
            pos.z -= moveAmount;//Fly down
        }
    }

    public static void main(String[] args) {
        new Main().run();
    }

    public float getPlayerX() {
        return playerX;
    }

    public void setPlayerX(float playerX) {
        this.playerX = playerX;
    }

    public float getPlayerY() {
        return playerY;
    }

    public void setPlayerY(float playerY) {
        this.playerY = playerY;
    }

    public float getPlayerZ() {
        return playerZ;
    }

    public void setPlayerZ(float playerZ) {
        this.playerZ = playerZ;
    }
}
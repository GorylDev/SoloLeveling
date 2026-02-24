package engine.core;

import engine.graphics.Camera;
import org.lwjgl.opengl.GL;

import static java.sql.Types.NULL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Main {
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
                    case NULL:
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

        while (!glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            double deltaTime = currentTime - lastTime;
            lastTime = currentTime;

            control_wsad(deltaTime);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            glfwSwapBuffers(window);
            glfwPollEvents();
        }
    }

    private void control_wsad(double deltaTime) {
        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            playerY += (float) (playerSpeed * deltaTime);
            System.out.println("Move forward");
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            playerY -= (float) (playerSpeed * deltaTime);
            System.out.println("Move backward");
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            playerX -= (float) (playerSpeed * deltaTime);
            System.out.println("Move left");
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            playerX += (float) (playerSpeed * deltaTime);
            System.out.println("Move right");
        }
        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS) {
            for (playerZ = 0.0f; playerZ < 5.0f; playerZ += (float) (playerSpeed * deltaTime)) {
                System.out.println("Jumping... Z=" + playerZ);
            }
            playerZ = 0.0f;
        }

    }

    public static void main(String[] args) {
        new Main().run();
        Camera camera = new Camera(0.0f, 0.0f, 5.0f);
    }
}
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
    private Camera camera;
    private float playerVelocityY = 0.0f;
    private boolean isGrounded = true;
    private float playerX = 0.0f;
    private float playerY = 0.0f;
    private float playerZ = 0.0f;
    private double lastMouseX = -1;
    private double lastMouseY = -1;
    private boolean mouseFirstMoved = false;

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

            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) {
                String esc = "Escape";
                glfwSetWindowShouldClose(windowHandle, true);
                System.out.println("System: Game closed on action: " + esc + " " + userAction);
            }

        });

        glfwSetCursorPosCallback(window, (windowHandle, xpos, ypos) -> {
            if (!mouseFirstMoved) {
                lastMouseX = xpos;
                lastMouseY = ypos;
                mouseFirstMoved = true;
            }

            double deltaX = xpos - lastMouseX;
            double deltaY = ypos - lastMouseY;

            lastMouseX = xpos;
            lastMouseY = ypos;

            float sensitivity = 0.2f;

            if (camera != null) {
                camera.rotate((float) deltaY * sensitivity, (float) deltaX * sensitivity);
            }

        });

        glfwMakeContextCurrent(window);
        GL.createCapabilities();
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glEnable(GL_DEPTH_TEST);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        glDepthFunc(GL_LESS);
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        glfwShowWindow(window);
    }

    private void loop() {
        camera = new Camera(0.0f, 0.0f, 15.0f);
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        double lastTime = glfwGetTime();

        ShaderProgram shaderProgram = new ShaderProgram("shaders/vertex.glsl", "shaders/fragment.glsl");

        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("viewMatrix");
        shaderProgram.createUniform("modelMatrix");

        Matrix4f projectionMatrix = camera.getProjectionMatrix(1980, 1080);
        Matrix4f viewMatrix;

        Mesh mesh = ModelLoader.loadModel("resources/textures/sung-jin-woo.obj");
        GameObject player = new GameObject(mesh, new Texture("resources/textures/SungJin-Woo.png"));

        GameObject statua = new GameObject(mesh, new Texture("resources/textures/SungJin-Woo.png"));

        while (!glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            double deltaTime = currentTime - lastTime;
            lastTime = currentTime;

            glfwPollEvents();
            control_wsad(deltaTime, player, camera);
            camera.updateOrbit(player.getTransform().position);
            glfwPollEvents();
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
            shaderProgram.bind();

            Matrix4f currentModelMatrix = new Matrix4f();
            viewMatrix = camera.getViewMatrix(player.getTransform().position);
            shaderProgram.setUniform("viewMatrix", viewMatrix);
            shaderProgram.setUniform("projectionMatrix", projectionMatrix);

            player.getTransform().scale.set(2.0f, 2.0f, 2.0f);
            player.render(shaderProgram, currentModelMatrix);
            statua.getTransform().position.set(5.0f, 0.0f, 0.0f);
            statua.getTransform().scale.set(4.0f, 4.0f, 4.0f);
            statua.render(shaderProgram, currentModelMatrix);

            shaderProgram.unbind();
            glfwSwapBuffers(window);
        }
        shaderProgram.cleanup();
        mesh.cleanup();
    }

    private void control_wsad(double deltaTime, GameObject player, Camera camera) {
        float playerSpeed = 4.0f;
        float moveAmount = (float) (playerSpeed * deltaTime);
        Vector3f pos = player.getTransform().position;

        float yawRad = (float) Math.toRadians(camera.getYaw());
        float forwardX = (float) Math.sin(yawRad);
        float forwardZ = (float) Math.cos(yawRad);
        float rightX = (float) Math.sin(yawRad + Math.PI / 2);
        float rightZ = (float) Math.cos(yawRad + Math.PI / 2);

        float dx = 0;
        float dz = 0;

        if (glfwGetKey(window, GLFW_KEY_W) == GLFW_PRESS) {
            dx += forwardX * moveAmount;
            dz += forwardZ * moveAmount;
        }
        if (glfwGetKey(window, GLFW_KEY_S) == GLFW_PRESS) {
            dx -= forwardX * moveAmount;
            dz -= forwardZ * moveAmount;
        }
        if (glfwGetKey(window, GLFW_KEY_A) == GLFW_PRESS) {
            dx += rightX * moveAmount;
            dz += rightZ * moveAmount;
        }
        if (glfwGetKey(window, GLFW_KEY_D) == GLFW_PRESS) {
            dx -= rightX * moveAmount;
            dz -= rightZ * moveAmount;
        }

        pos.x += dx;
        pos.z += dz;

        if (dx != 0 || dz != 0) {
            float angle = (float) Math.toDegrees(Math.atan2(dx, dz));
            player.getTransform().rotation.y = angle;
        }

        //Physics and gravity
        float gravity = -20.0f;
        float jumpPower = 8.0f;

        playerVelocityY += gravity * (float) deltaTime;
        pos.y += playerVelocityY * (float) deltaTime;

        if (pos.y <= 0.0f) {
            pos.y = 0.0f;
            playerVelocityY = 0.0f;
            isGrounded = true;
        } else {
            isGrounded = false;
        }

        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS && isGrounded) {
            playerVelocityY = jumpPower;
            isGrounded = false;
        }

        if (isGrounded && (dx != 0 || dz != 0)) {
            float bobbingOffset = (float) (Math.sin(glfwGetTime() * 15.0) * 0.05);
            pos.y = bobbingOffset;
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
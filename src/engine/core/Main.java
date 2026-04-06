package engine.core;

import engine.graphics.*;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL;

import java.util.List;

import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class Main {
    private long window;
    private Camera camera;
    private ChunkManager chunkManager;
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

        //keycallback just in case
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

            double deltaX = lastMouseX - xpos;
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

    //main loop
    private void loop() {
        camera = new Camera(0.0f, 0.0f, 15.0f);
        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        double lastTime = glfwGetTime();

        //defining shaders
        ShaderProgram shaderProgram = new ShaderProgram("shaders/vertex.glsl", "shaders/fragment.glsl");
        ShaderProgram animShaderProgram = new ShaderProgram("shaders/anim_vertex.glsl", "shaders/anim_fragment.glsl");
        ShaderProgram skyboxShader = new ShaderProgram("shaders/skybox_vertex.glsl", "shaders/skybox_fragment.glsl");
        ShaderProgram gateShader = new ShaderProgram("shaders/gate_vertex.glsl", "shaders/gate_fragment.glsl");

        //creating shader uniforms
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("viewMatrix");
        shaderProgram.createUniform("modelMatrix");
        shaderProgram.createUniform("texture_sampler");
        shaderProgram.createUniform("fogColor");

        gateShader.createUniform("projectionMatrix");
        gateShader.createUniform("viewMatrix");
        gateShader.createUniform("modelMatrix");
        gateShader.createUniform("time");
        gateShader.createUniform("gateColor");

        animShaderProgram.createUniform("projectionMatrix");
        animShaderProgram.createUniform("viewMatrix");
        animShaderProgram.createUniform("modelMatrix");

        for (int i = 0; i < 150; i++) {
            animShaderProgram.createUniform("boneMatrices[" + i + "]");
        }

        skyboxShader.createUniform("projectionMatrix");
        skyboxShader.createUniform("viewMatrix");

        Matrix4f projectionMatrix = camera.getProjectionMatrix(1980, 1080);

        chunkManager = new ChunkManager();
        Skybox skybox = new Skybox();

        //defining animations
        AnimatedMesh baseMesh = AnimatedModelLoader.loadModel("resources/models/player_base.fbx");
        AnimatedGameObject player = new AnimatedGameObject(baseMesh, new Texture("resources/textures/SungJin-Woo.png"));
        player.getTransform().scale.set(0.01f, 0.01f, 0.01f);
        Animation runAnimation = AnimationLoader.load("resources/models/anim_run.fbx", player.getMesh().getBoneMap());
        Animation idleAnimation = AnimationLoader.load("resources/models/anim_idle.fbx", player.getMesh().getBoneMap());
        Animation jumpAnimation = AnimationLoader.load("resources/models/anim_jump.fbx", player.getMesh().getBoneMap());
        player.getAnimator().play(idleAnimation);

        //gate system initialization
        Mesh gateMesh = PrimitiveFactory.createQuad();
        float gateX = 0.0f;
        float gateZ = -20.0f;
        float gateY = TerrainGenerator.getProceduralHeight(gateX, gateZ) + 5.0f;
        Gate dungeonGate = new Gate(new Vector3f(gateX, gateY, gateZ), gateMesh);
        Gate exitGate = null;
        boolean inDungeon = false;
        Vector3f overworldReturnPos = new Vector3f();
        EnemyManager enemyManager = new EnemyManager();

        //main while loop
        while (!glfwWindowShouldClose(window)) {
            double currentTime = glfwGetTime();
            double deltaTime = currentTime - lastTime;
            lastTime = currentTime;

            glfwPollEvents();
            control_wsad(deltaTime, player, camera, idleAnimation, runAnimation, jumpAnimation);
            player.update(deltaTime);
            camera.updateOrbit(player.getTransform().position);

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            glDepthFunc(GL_LEQUAL);
            shaderProgram.bind();
            skyboxShader.bind();
            animShaderProgram.bind();
            skyboxShader.setUniform("projectionMatrix", projectionMatrix);
            Matrix4f skyViewMatrix = new Matrix4f(camera.getViewMatrix(player.getTransform().position));
            skyViewMatrix.m30(0);
            skyViewMatrix.m31(0);
            skyViewMatrix.m32(0);
            skyboxShader.setUniform("viewMatrix", skyViewMatrix);
            skybox.getMesh().render();
            skyboxShader.unbind();
            glDepthFunc(GL_LESS);

            animShaderProgram.bind();
            Matrix4f currentModelMatrix = new Matrix4f();
            Matrix4f viewMatrix = camera.getViewMatrix(player.getTransform().position);
            animShaderProgram.setUniform("viewMatrix", viewMatrix);
            animShaderProgram.setUniform("projectionMatrix", projectionMatrix);
            player.render(animShaderProgram, currentModelMatrix);

            enemyManager.update(deltaTime, player.getTransform().position);
            enemyManager.render(animShaderProgram, currentModelMatrix);

            animShaderProgram.unbind();

            shaderProgram.bind();
            shaderProgram.setUniform("viewMatrix", viewMatrix);
            shaderProgram.setUniform("projectionMatrix", projectionMatrix);
            shaderProgram.setUniform("texture_sampler", 0);

            if (inDungeon) {
                shaderProgram.setUniform("fogColor", new Vector3f(0.1f, 0.0f, 0.0f));
            } else {
                shaderProgram.setUniform("fogColor", new Vector3f(0.5f, 0.5f, 0.5f));
            }

            List<GameObject> visibleChunks = chunkManager.update(player.getTransform().position.x, player.getTransform().position.z);
            for (GameObject chunk : visibleChunks) {
                chunk.render(shaderProgram, currentModelMatrix);
            }
            shaderProgram.unbind();

            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            gateShader.bind();
            gateShader.setUniform("projectionMatrix", projectionMatrix);
            gateShader.setUniform("viewMatrix", viewMatrix);
            gateShader.setUniform("time", (float) glfwGetTime());

            if (!inDungeon) {
                gateShader.setUniform("gateColor", new Vector3f(0.0f, 0.5f, 1.0f));
                dungeonGate.render(gateShader, currentModelMatrix);
            } else if (exitGate != null) {
                gateShader.setUniform("gateColor", new Vector3f(1.0f, 0.2f, 0.0f));
                exitGate.render(gateShader, currentModelMatrix);
            }

            dungeonGate.render(gateShader, currentModelMatrix);
            gateShader.unbind();

            glDisable(GL_BLEND);

            if (!inDungeon && dungeonGate.isPlayerEntering(player.getTransform().position)) {
                System.out.println("SYSTEM: Teleporting to Dungeon Dimension...");
                inDungeon = true;
                overworldReturnPos.set(player.getTransform().position);

                float dungeonX = 10000.0f;
                float dungeonZ = 10000.0f;
                float dungeonY = TerrainGenerator.getProceduralHeight(dungeonX, dungeonZ) + 5.0f;

                playerVelocityY = 0.0f;
                player.getTransform().position.set(dungeonX, dungeonY, dungeonZ);

                float exitX = dungeonX;
                float exitZ = dungeonZ - 20.0f;
                float exitY = TerrainGenerator.getProceduralHeight(exitX, exitZ) + 5.0f;
                exitGate = new Gate(new Vector3f(exitX, exitY, exitZ), gateMesh);

            } else if (inDungeon && exitGate.isPlayerEntering(player.getTransform().position)) {
                System.out.println("SYSTEM: Dungeon cleared. Returning to Overworld...");
                inDungeon = false;

                playerVelocityY = 0.0f;
                player.getTransform().position.set(overworldReturnPos.x, overworldReturnPos.y + 1.0f, overworldReturnPos.z + 5.0f);
            }

            glfwSwapBuffers(window);
        }

        shaderProgram.cleanup();
        animShaderProgram.cleanup();
        skyboxShader.cleanup();
        gateShader.cleanup();
        baseMesh.cleanup();
        chunkManager.cleanup();
    }

    //MAIN CONTROLS
    private void control_wsad(double deltaTime, AnimatedGameObject player, Camera camera, Animation idle, Animation run, Animation jump) {
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

        boolean isMoving = (dx != 0 || dz != 0);

        if (isMoving) {
            player.getTransform().rotation.y = (float) Math.toDegrees(Math.atan2(dx, dz));
        }

        double jumpSpeedMultiplier = 2.0;
        float jumpDuration = (float) (jump.getDurationInSeconds() / jumpSpeedMultiplier);
        float maxJumpHeight = 2.0f;

        float gravity = (-10.0f * maxJumpHeight) / (jumpDuration * jumpDuration);
        float jumpPower = (4.0f * maxJumpHeight) / jumpDuration;

        float terrainHeight = TerrainGenerator.getProceduralHeight(pos.x, pos.z);

        playerVelocityY += gravity * (float) deltaTime;
        pos.y += playerVelocityY * (float) deltaTime;

        if (pos.y <= terrainHeight) {
            pos.y = terrainHeight;
            playerVelocityY = 0.0f;
            isGrounded = true;
        } else if (playerVelocityY <= 0.0f && pos.y - terrainHeight < 0.6f) {
            pos.y = terrainHeight;
            playerVelocityY = 0.0f;
            isGrounded = true;
        } else {
            isGrounded = false;
        }

        if (glfwGetKey(window, GLFW_KEY_SPACE) == GLFW_PRESS && isGrounded) {
            playerVelocityY = jumpPower;
            isGrounded = false;
        }

        if (!isGrounded && player.getAnimator().getCurrentAnimation() != jump) {
            player.getAnimator().setSpeedMultiplier(jumpSpeedMultiplier);
            player.getAnimator().play(jump);
        } else if (isGrounded && isMoving && player.getAnimator().getCurrentAnimation() != run) {
            player.getAnimator().setSpeedMultiplier(1.0);
            player.getAnimator().play(run);
        } else if (isGrounded && !isMoving && player.getAnimator().getCurrentAnimation() != idle) {
            player.getAnimator().setSpeedMultiplier(1.0);
            player.getAnimator().play(idle);
        }
    }

    //run
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
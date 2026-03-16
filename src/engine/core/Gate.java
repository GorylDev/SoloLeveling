package engine.core;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;

public class Gate {
    private final GameObject gateObject;
    private final Vector3f position;
    private final float collisionRadiusSquared;

    public Gate(Vector3f position, Mesh mesh) {
        this.position = position;
        this.gateObject = new GameObject(mesh, null);

        this.gateObject.getTransform().position.set(position);
        this.gateObject.getTransform().scale.set(3.0f, 5.0f, 1.0f);

        float radius = 2.5f;
        this.collisionRadiusSquared = radius * radius;
    }

    public void render(ShaderProgram shader, Matrix4f modelMatrix) {
        gateObject.render(shader, modelMatrix);
    }

    public boolean isPlayerEntering(Vector3f playerPos) {
        float dx = playerPos.x - position.x;
        float dz = playerPos.z - position.z;
        float distSquared = (dx * dx) + (dz * dz);

        return distSquared <= collisionRadiusSquared;
    }
}
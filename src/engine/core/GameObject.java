package engine.core;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.graphics.Texture;
import org.joml.Matrix4f;

public class GameObject {
    private final Mesh mesh;
    private final Transform transform;
    private final Texture texture;

    public Transform getTransform() {
        return transform;
    }

    public GameObject(Mesh mesh, Texture texture){
        this.mesh = mesh;
        this.texture = texture;
        this.transform = new Transform();
    }

    public void render(ShaderProgram shader, Matrix4f currentModelMatrix){
        transform.getModelMatrix(currentModelMatrix);
        shader.setUniform("modelMatrix", currentModelMatrix);
        texture.bind();
        mesh.render();
    }

    public void cleanup() {
        mesh.cleanup();
        texture.cleanup();
    }
}

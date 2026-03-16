package engine.core;

import engine.graphics.Mesh;
import engine.graphics.ShaderProgram;
import engine.graphics.Texture;
import org.joml.Matrix4f;

public class GameObject {
    private final Mesh mesh;
    private final Transform transform;
    private final Texture texture;

    public GameObject(Mesh mesh, Texture texture){
        this.mesh = mesh;
        this.texture = texture;
        this.transform = new Transform();
    }

    public Transform getTransform() {
        return transform;
    }

    public void render(ShaderProgram shader, Matrix4f modelMatrix) {
        modelMatrix.identity()
                .translate(transform.position)
                .rotateX(transform.rotation.x)
                .rotateY(transform.rotation.y)
                .rotateZ(transform.rotation.z)
                .scale(transform.scale);

        shader.setUniform("modelMatrix", modelMatrix);

        if (texture != null) {
            texture.bind();
        }

        mesh.render();
    }

    public void cleanup() {
        mesh.cleanup();
        texture.cleanup();
    }
}
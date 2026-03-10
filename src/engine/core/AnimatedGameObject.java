package engine.core;

import engine.graphics.AnimatedMesh;
import engine.graphics.Animator;
import engine.graphics.ShaderProgram;
import engine.graphics.Texture;
import org.joml.Matrix4f;

public class AnimatedGameObject {
    private final AnimatedMesh mesh;
    private final Texture texture;
    private final Transform transform;
    private final Animator animator;

    public AnimatedGameObject(AnimatedMesh mesh, Texture texture) {
        this.mesh = mesh;
        this.texture = texture;
        this.transform = new Transform();
        this.animator = new Animator();
    }

    public AnimatedMesh getMesh() {
        return mesh;
    }

    public Transform getTransform() {
        return transform;
    }

    public Animator getAnimator() {
        return animator;
    }

    public void update(double deltaTime) {
        animator.update(deltaTime);
    }

    public void render(ShaderProgram shader, Matrix4f modelMatrix) {
        modelMatrix.identity().translate(transform.position).
                rotateX((float) Math.toRadians(transform.rotation.x)).
                rotateY((float) Math.toRadians(transform.rotation.y)).
                rotateZ((float) Math.toRadians(transform.rotation.z)).
                scale(transform.scale);

        shader.setUniform("modelMatrix", modelMatrix);

        Matrix4f[] boneTransforms = animator.getBoneTransforms();
        for (int i = 0; i < 150; i++) {
            shader.setUniform("boneMatrices[" + i + "]", boneTransforms[i]);
        }

        texture.bind();
        mesh.render();
    }

    public void cleanup() {
        mesh.cleanup();
        texture.cleanup();
    }
}
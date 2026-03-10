package engine.graphics;

import org.joml.Matrix4f;

public class Frame {
    private final Matrix4f[] boneMatrices;

    public Frame() {
        boneMatrices = new Matrix4f[150];
        for (int i = 0; i < 150; i++) {
            boneMatrices[i] = new Matrix4f().identity();
        }
    }

    public Matrix4f[] getBoneMatrices() {
        return boneMatrices;
    }
}
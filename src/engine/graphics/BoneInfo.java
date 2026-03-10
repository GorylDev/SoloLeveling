package engine.graphics;

import org.joml.Matrix4f;

public class BoneInfo {
    private final int id;
    private final Matrix4f offsetMatrix;

    public BoneInfo(int id, Matrix4f offsetMatrix) {
        this.id = id;
        this.offsetMatrix = offsetMatrix;
    }

    public int getId() {
        return id;
    }

    public Matrix4f getOffsetMatrix() {
        return offsetMatrix;
    }
}
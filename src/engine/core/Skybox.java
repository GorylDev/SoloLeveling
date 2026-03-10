package engine.core;

import engine.graphics.Mesh;

public class Skybox {
    private final Mesh mesh;

    public Skybox() {
        float[] positions = {
                -1,  1, -1,  -1, -1, -1,   1, -1, -1,   1,  1, -1,
                -1,  1,  1,  -1, -1,  1,   1, -1,  1,   1,  1,  1
        };

        int[] indices = {
                //Front
                0, 1, 3, 3, 1, 2,
                //Back
                4, 7, 5, 7, 6, 5,
                //Left
                0, 4, 1, 4, 5, 1,
                //Right
                3, 2, 7, 7, 2, 6,
                //Top
                0, 3, 4, 4, 3, 7,
                //Bottom
                1, 5, 2, 5, 6, 2
        };
        float[] dummyTexCoords = new float[16];
        float[] dummyNormals = new float[24];

        this.mesh = new Mesh(positions, dummyTexCoords, dummyNormals, indices);
    }

    public Mesh getMesh() {
        return mesh;
    }
}
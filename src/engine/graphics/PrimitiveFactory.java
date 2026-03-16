package engine.graphics;

public class PrimitiveFactory {

    public static Mesh createQuad() {
        float[] positions = {
                -1.0f,  1.0f, 0.0f,
                -1.0f, -1.0f, 0.0f,
                1.0f, -1.0f, 0.0f,
                1.0f,  1.0f, 0.0f
        };

        float[] textCoords = {
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f
        };

        float[] normals = {
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f,
                0.0f, 0.0f, 1.0f
        };

        int[] indices = {
                0, 1, 3, 3, 1, 2
        };

        return new Mesh(positions, textCoords, normals, indices);
    }
}
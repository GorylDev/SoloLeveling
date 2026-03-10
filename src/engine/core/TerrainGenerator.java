package engine.core;

import engine.graphics.Mesh;
import org.joml.Vector3f;

public class TerrainGenerator {
    public static final float CHUNK_SIZE = 32.0f;
    private static final int VERTEX_COUNT = 64;

    public static Mesh generateTerrain(int chunkX, int chunkZ) {
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] positions = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] texCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];

        int vertexPointer = 0;

        for (int i = 0; i < VERTEX_COUNT; i++) {
            for (int j = 0; j < VERTEX_COUNT; j++) {
                float worldX = (chunkX * CHUNK_SIZE) + ((float) j / ((float) VERTEX_COUNT - 1) * CHUNK_SIZE) - (CHUNK_SIZE / 2.0f);
                float worldZ = (chunkZ * CHUNK_SIZE) + ((float) i / ((float) VERTEX_COUNT - 1) * CHUNK_SIZE) - (CHUNK_SIZE / 2.0f);

                float worldY = getProceduralHeight(worldX, worldZ);

                positions[vertexPointer * 3] = worldX;
                positions[vertexPointer * 3 + 1] = worldY;
                positions[vertexPointer * 3 + 2] = worldZ;

                Vector3f normal = calculateNormal(worldX, worldZ);
                normals[vertexPointer * 3] = normal.x;
                normals[vertexPointer * 3 + 1] = normal.y;
                normals[vertexPointer * 3 + 2] = normal.z;

                texCoords[vertexPointer * 2] = ((float) j / ((float) VERTEX_COUNT - 1)) * 6.0f;
                texCoords[vertexPointer * 2 + 1] = ((float) i / ((float) VERTEX_COUNT - 1)) * 6.0f;

                vertexPointer++;
            }
        }

        int pointer = 0;
        for (int gz = 0; gz < VERTEX_COUNT - 1; gz++) {
            for (int gx = 0; gx < VERTEX_COUNT - 1; gx++) {
                int topLeft = (gz * VERTEX_COUNT) + gx;
                int topRight = topLeft + 1;
                int bottomLeft = ((gz + 1) * VERTEX_COUNT) + gx;
                int bottomRight = bottomLeft + 1;

                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }

        return new Mesh(positions, texCoords, normals, indices);
    }

    //Procedural algorithms
    public static float getProceduralHeight(float x, float z) {
        float height = (float) (Math.sin(x * 0.03) * Math.cos(z * 0.05)) * 3f;
        height += (float) (Math.sin(x * 0.1) * Math.cos(z * 0.1));
        height += (float) (Math.sin(x * 0.5) * Math.cos(z * 0.5)) * 0.1f;
        return height;
    }

    private static Vector3f calculateNormal(float x, float z) {
        float off = 0.1f;
        float heightL = getProceduralHeight(x - off, z);
        float heightR = getProceduralHeight(x + off, z);
        float heightD = getProceduralHeight(x, z - off);
        float heightU = getProceduralHeight(x, z + off);

        Vector3f normal = new Vector3f(heightL - heightR, 2.0f, heightD - heightU);
        return normal.normalize();
    }
}
package engine.graphics;

import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.assimp.Assimp.*;

public class ModelLoader {

    public static Mesh loadModel(String filePath) {
        AIScene scene = aiImportFile(filePath, aiProcess_Triangulate | aiProcess_JoinIdenticalVertices | aiProcess_GenSmoothNormals);

        if (scene == null || scene.mRootNode() == null || (scene.mFlags() & AI_SCENE_FLAGS_INCOMPLETE) != 0) {
            throw new RuntimeException("Assimp failed to load model: " + filePath + "\n" + aiGetErrorString());
        }

        PointerBuffer aiMeshes = scene.mMeshes();
        assert aiMeshes != null;
        AIMesh aiMesh = AIMesh.create(aiMeshes.get(0));

        float[] positions = processPositions(aiMesh);
        float[] normals = processNormals(aiMesh);
        float[] textCoords = processTextCoords(aiMesh);
        int[] indices = processIndices(aiMesh);

        aiReleaseImport(scene);

        return new Mesh(positions, normals, textCoords, indices);
    }

    public static float[] processNormals(AIMesh aiMesh){
        AIVector3D.Buffer buffer = aiMesh.mNormals();
        assert buffer != null;
        float[] normals = new float[buffer.remaining() * 3];
        for (int i = 0; i < buffer.remaining(); i++) {
            AIVector3D vertex = buffer.get(i);
            normals[i * 3] = vertex.x();
            normals[i * 3 + 1] = vertex.y();
            normals[i * 3 + 2] = vertex.z();
        }
        return normals;
    }

    private static float[] processPositions(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mVertices();
        assert buffer != null;
        float[] positions = new float[buffer.remaining() * 3];
        for (int i = 0; i < buffer.remaining(); i++) {
            AIVector3D vertex = buffer.get(i);
            positions[i * 3] = vertex.x();
            positions[i * 3 + 1] = vertex.y();
            positions[i * 3 + 2] = vertex.z();
        }
        return positions;
    }

    private static float[] processTextCoords(AIMesh aiMesh) {
        AIVector3D.Buffer buffer = aiMesh.mTextureCoords(0);

        if (buffer == null) {
            return new float[aiMesh.mNumVertices() * 2];
        }

        float[] textCoords = new float[buffer.remaining() * 2];
        for (int i = 0; i < buffer.remaining(); i++) {
            AIVector3D textCoord = buffer.get(i);
            textCoords[i * 2] = textCoord.x();
            textCoords[i * 2 + 1] = 1.0f - textCoord.y();
        }
        return textCoords;
    }

    private static int[] processIndices(AIMesh aiMesh) {
        AIFace.Buffer buffer = aiMesh.mFaces();
        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < buffer.remaining(); i++) {
            AIFace face = buffer.get(i);
            IntBuffer faceIndices = face.mIndices();
            while (faceIndices.hasRemaining()) {
                indices.add(faceIndices.get());
            }
        }
        return indices.stream().mapToInt(i -> i).toArray();
    }
}
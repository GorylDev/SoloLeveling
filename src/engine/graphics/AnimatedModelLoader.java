package engine.graphics;

import org.joml.Matrix4f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.assimp.Assimp.*;

public class AnimatedModelLoader {

    public static AnimatedMesh loadModel(String filePath) {
        AIScene scene = aiImportFile(filePath,
                aiProcess_JoinIdenticalVertices |
                        aiProcess_Triangulate |
                        aiProcess_GenSmoothNormals |
                        aiProcess_LimitBoneWeights);

        if (scene == null || scene.mRootNode() == null) {
            throw new RuntimeException(aiGetErrorString());
        }

        AIMesh mesh = AIMesh.create(scene.mMeshes().get(0));
        int vertexCount = mesh.mNumVertices();

        float[] positions = new float[vertexCount * 3];
        float[] textCoords = new float[vertexCount * 2];
        float[] normals = new float[vertexCount * 3];
        int[] boneIds = new int[vertexCount * 4];
        float[] weights = new float[vertexCount * 4];

        AIVector3D.Buffer vertices = mesh.mVertices();
        for (int i = 0; i < vertexCount; i++) {
            AIVector3D vertex = vertices.get(i);
            positions[i * 3] = vertex.x();
            positions[i * 3 + 1] = vertex.y();
            positions[i * 3 + 2] = vertex.z();
        }

        AIVector3D.Buffer aiNormals = mesh.mNormals();
        if (aiNormals != null) {
            for (int i = 0; i < vertexCount; i++) {
                AIVector3D normal = aiNormals.get(i);
                normals[i * 3] = normal.x();
                normals[i * 3 + 1] = normal.y();
                normals[i * 3 + 2] = normal.z();
            }
        }

        AIVector3D.Buffer aiTextCoords = mesh.mTextureCoords(0);
        if (aiTextCoords != null) {
            for (int i = 0; i < vertexCount; i++) {
                AIVector3D textCoord = aiTextCoords.get(i);
                textCoords[i * 2] = textCoord.x();
                textCoords[i * 2 + 1] = 1.0f - textCoord.y();
            }
        }

        int faceCount = mesh.mNumFaces();
        AIFace.Buffer faces = mesh.mFaces();
        List<Integer> indicesList = new ArrayList<>();
        for (int i = 0; i < faceCount; i++) {
            AIFace face = faces.get(i);
            IntBuffer indicesBuffer = face.mIndices();
            while (indicesBuffer.hasRemaining()) {
                indicesList.add(indicesBuffer.get());
            }
        }

        int[] indices = new int[indicesList.size()];
        for (int i = 0; i < indicesList.size(); i++) {
            indices[i] = indicesList.get(i);
        }

        Map<String, BoneInfo> boneMapping = new HashMap<>();
        int boneCount = 0;
        int[] vertexBoneCounts = new int[vertexCount];

        int numBones = mesh.mNumBones();
        PointerBuffer aiBones = mesh.mBones();
        if (aiBones != null) {
            for (int i = 0; i < numBones; i++) {
                AIBone bone = AIBone.create(aiBones.get(i));
                String boneName = bone.mName().dataString();

                int boneId;
                if (!boneMapping.containsKey(boneName)) {
                    boneId = boneCount++;
                    Matrix4f offset = toMatrix4f(bone.mOffsetMatrix());
                    boneMapping.put(boneName, new BoneInfo(boneId, offset));
                } else {
                    boneId = boneMapping.get(boneName).getId();
                }

                int numWeights = bone.mNumWeights();
                AIVertexWeight.Buffer aiWeights = bone.mWeights();
                for (int j = 0; j < numWeights; j++) {
                    AIVertexWeight weight = aiWeights.get(j);
                    int vertexId = weight.mVertexId();
                    float weightValue = weight.mWeight();

                    int currentBoneIndex = vertexBoneCounts[vertexId];
                    if (currentBoneIndex < 4) {
                        boneIds[vertexId * 4 + currentBoneIndex] = boneId;
                        weights[vertexId * 4 + currentBoneIndex] = weightValue;
                        vertexBoneCounts[vertexId]++;
                    }
                }
            }
        }

        return new AnimatedMesh(positions, textCoords, normals, boneIds, weights, indices, boneMapping);
    }

    private static Matrix4f toMatrix4f(AIMatrix4x4 aiMat) {
        return new Matrix4f(
                aiMat.a1(), aiMat.b1(), aiMat.c1(), aiMat.d1(),
                aiMat.a2(), aiMat.b2(), aiMat.c2(), aiMat.d2(),
                aiMat.a3(), aiMat.b3(), aiMat.c3(), aiMat.d3(),
                aiMat.a4(), aiMat.b4(), aiMat.c4(), aiMat.d4()
        );
    }
}
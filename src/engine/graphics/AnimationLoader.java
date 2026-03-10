package engine.graphics;

import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.PointerBuffer;
import org.lwjgl.assimp.*;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.assimp.Assimp.*;

public class AnimationLoader {

    public static Animation load(String filePath, Map<String, BoneInfo> boneMap) {
        AIScene scene = aiImportFile(filePath, aiProcess_JoinIdenticalVertices | aiProcess_Triangulate);

        if (scene == null || scene.mRootNode() == null) {
            throw new RuntimeException(aiGetErrorString());
        }

        AIAnimation aiAnimation = AIAnimation.create(scene.mAnimations().get(0));

        double duration = aiAnimation.mDuration();
        double ticksPerSecond = aiAnimation.mTicksPerSecond() != 0.0 ? aiAnimation.mTicksPerSecond() : 25.0;
        double durationInSeconds = duration / ticksPerSecond;

        int numChannels = aiAnimation.mNumChannels();
        PointerBuffer aiChannels = aiAnimation.mChannels();

        Map<String, AINodeAnim> nodeAnims = new HashMap<>();
        int maxFrames = 0;

        for (int i = 0; i < numChannels; i++) {
            AINodeAnim nodeAnim = AINodeAnim.create(aiChannels.get(i));
            nodeAnims.put(nodeAnim.mNodeName().dataString(), nodeAnim);

            if (nodeAnim.mNumPositionKeys() > maxFrames) maxFrames = nodeAnim.mNumPositionKeys();
            if (nodeAnim.mNumRotationKeys() > maxFrames) maxFrames = nodeAnim.mNumRotationKeys();
            if (nodeAnim.mNumScalingKeys() > maxFrames) maxFrames = nodeAnim.mNumScalingKeys();
        }

        Frame[] frames = new Frame[maxFrames];
        for (int i = 0; i < maxFrames; i++) {
            frames[i] = new Frame();
            buildFrameMatrices(scene.mRootNode(), new Matrix4f(), nodeAnims, frames[i], i, boneMap);
        }

        return new Animation(frames, durationInSeconds);
    }

    private static void buildFrameMatrices(AINode node, Matrix4f parentTransform, Map<String, AINodeAnim> nodeAnims, Frame frame, int frameIndex, Map<String, BoneInfo> boneMap) {
        String nodeName = node.mName().dataString();
        AINodeAnim nodeAnim = nodeAnims.get(nodeName);
        Matrix4f nodeTransform = new Matrix4f();

        if (nodeAnim != null) {
            Vector3f posVec = new Vector3f();
            if (nodeAnim.mNumPositionKeys() > 0) {
                int posIndex = Math.min(frameIndex, nodeAnim.mNumPositionKeys() - 1);
                AIVectorKey posKey = AIVectorKey.create(nodeAnim.mPositionKeys().address() + ((long) posIndex * AIVectorKey.SIZEOF));
                posVec.set(posKey.mValue().x(), posKey.mValue().y(), posKey.mValue().z());
            }

            Quaternionf rotQuat = new Quaternionf();
            if (nodeAnim.mNumRotationKeys() > 0) {
                int rotIndex = Math.min(frameIndex, nodeAnim.mNumRotationKeys() - 1);
                AIQuatKey rotKey = AIQuatKey.create(nodeAnim.mRotationKeys().address() + ((long) rotIndex * AIQuatKey.SIZEOF));
                rotQuat.set(rotKey.mValue().x(), rotKey.mValue().y(), rotKey.mValue().z(), rotKey.mValue().w());
            }

            Vector3f scaleVec = new Vector3f(1.0f, 1.0f, 1.0f);
            if (nodeAnim.mNumScalingKeys() > 0) {
                int scaleIndex = Math.min(frameIndex, nodeAnim.mNumScalingKeys() - 1);
                AIVectorKey scaleKey = AIVectorKey.create(nodeAnim.mScalingKeys().address() + ((long) scaleIndex * AIVectorKey.SIZEOF));
                scaleVec.set(scaleKey.mValue().x(), scaleKey.mValue().y(), scaleKey.mValue().z());
            }

            nodeTransform.translate(posVec).rotate(rotQuat).scale(scaleVec);
        } else {
            nodeTransform.set(toMatrix4f(node.mTransformation()));
        }

        Matrix4f globalTransform = new Matrix4f(parentTransform).mul(nodeTransform);

        BoneInfo boneInfo = boneMap.get(nodeName);
        if (boneInfo != null) {
            Matrix4f finalTransform = new Matrix4f(globalTransform).mul(boneInfo.getOffsetMatrix());
            frame.getBoneMatrices()[boneInfo.getId()] = finalTransform;
        }

        PointerBuffer children = node.mChildren();
        if (children != null) {
            for (int j = 0; j < node.mNumChildren(); j++) {
                buildFrameMatrices(AINode.create(children.get(j)), globalTransform, nodeAnims, frame, frameIndex, boneMap);
            }
        }
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
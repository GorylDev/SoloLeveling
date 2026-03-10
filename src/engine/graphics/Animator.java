package engine.graphics;

import org.joml.Matrix4f;

public class Animator {
    private Animation currentAnimation;
    private double animationTime;

    public void play(Animation animation) {
        this.currentAnimation = animation;
        this.animationTime = 0.0;
    }

    public void update(double deltaTime) {
        if (currentAnimation == null) return;

        animationTime += deltaTime;
        if (animationTime >= currentAnimation.getDurationInSeconds()) {
            animationTime %= currentAnimation.getDurationInSeconds();
        }
    }

    public Matrix4f[] getBoneTransforms() {
        if (currentAnimation == null || currentAnimation.getFrames().length == 0) {
            Matrix4f[] empty = new Matrix4f[150];
            for (int i = 0; i < 150; i++) {
                empty[i] = new Matrix4f().identity();
            }
            return empty;
        }

        double progress = animationTime / currentAnimation.getDurationInSeconds();
        int frameIndex = (int) (progress * currentAnimation.getFrames().length);
        if (frameIndex >= currentAnimation.getFrames().length) {
            frameIndex = currentAnimation.getFrames().length - 1;
        }
        return currentAnimation.getFrames()[frameIndex].getBoneMatrices();
    }
}
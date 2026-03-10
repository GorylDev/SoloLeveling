package engine.graphics;

public class Animation {
    private final Frame[] frames;
    private final double durationInSeconds;

    public Animation(Frame[] frames, double durationInSeconds) {
        this.frames = frames;
        this.durationInSeconds = durationInSeconds;
    }

    public Frame[] getFrames() {
        return frames;
    }

    public double getDurationInSeconds() {
        return durationInSeconds;
    }
}
package engine.core;

import engine.graphics.Texture;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChunkManager {
    private final Map<String, GameObject> activeChunks = new HashMap<>();
    private final Texture terrainTexture;
    private final int viewDistance = 2;

    public ChunkManager() {
        this.terrainTexture = new Texture("resources/textures/grass.png");
    }

    public List<GameObject> update(float playerX, float playerZ) {
        List<GameObject> chunksToRender = new ArrayList<>();
        int currentChunkX = Math.round(playerX / TerrainGenerator.CHUNK_SIZE);
        int currentChunkZ = Math.round(playerZ / TerrainGenerator.CHUNK_SIZE);

        for (int xOffset = -viewDistance; xOffset <= viewDistance; xOffset++) {
            for (int zOffset = -viewDistance; zOffset <= viewDistance; zOffset++) {
                int chunkX = currentChunkX + xOffset;
                int chunkZ = currentChunkZ + zOffset;
                String key = chunkX + "_" + chunkZ;

                if (!activeChunks.containsKey(key)) {
                    GameObject newChunk = new GameObject(TerrainGenerator.generateTerrain(chunkX, chunkZ), terrainTexture);
                    newChunk.getTransform().position.set(0.0f, 0.0f, 0.0f);
                    activeChunks.put(key, newChunk);
                }
                chunksToRender.add(activeChunks.get(key));
            }
        }

        return chunksToRender;
    }

    public void cleanup() {
        for (GameObject chunk : activeChunks.values()) {
            chunk.cleanup();
        }
        activeChunks.clear();
    }
}
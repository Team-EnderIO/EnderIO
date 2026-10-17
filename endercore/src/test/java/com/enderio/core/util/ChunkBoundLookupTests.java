package com.enderio.core.util;

import com.enderio.core.common.util.ChunkBoundLookup;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ChunkBoundLookupTests {
    @Test
    public void addToChunk() {
        // Arrange.
        var lookup = new ChunkBoundLookup<String>();

        // Act.
        lookup.addToChunk(new ChunkPos(0, 0), "This is my chunk data");

        // Assert.
        var chunkData = lookup.getForChunk(new ChunkPos(0, 0));
        Assertions.assertNotNull(chunkData);
        Assertions.assertEquals(1, chunkData.size());
        Assertions.assertTrue(chunkData.contains("This is my chunk data"));
        Assertions.assertTrue(lookup.values().contains("This is my chunk data"));
    }

    @Test
    public void addToChunkMultipleTimes() {
        // Arrange.
        var lookup = new ChunkBoundLookup<String>();

        // Act.
        lookup.addToChunk(new ChunkPos(0, 0), "This is my chunk data");
        lookup.addToChunk(new ChunkPos(0, 0), "This is more data");
        lookup.addToChunk(new ChunkPos(0, 0), "Yay more data");

        // Assert.
        var chunkData = lookup.getForChunk(new ChunkPos(0, 0));
        Assertions.assertNotNull(chunkData);
        Assertions.assertEquals(3, chunkData.size());

        Assertions.assertTrue(chunkData.contains("This is my chunk data"));
        Assertions.assertTrue(chunkData.contains("This is more data"));
        Assertions.assertTrue(chunkData.contains("Yay more data"));

        var allValues = lookup.values();
        Assertions.assertTrue(allValues.contains("This is my chunk data"));
        Assertions.assertTrue(allValues.contains("This is more data"));
        Assertions.assertTrue(allValues.contains("Yay more data"));
    }

    @Test
    public void removeFromChunk() {
        // Arrange.
        var lookup = new ChunkBoundLookup<String>();
        lookup.addToChunk(new ChunkPos(0, 0), "This is my chunk data");

        // Act.
        lookup.removeFromChunk(new ChunkPos(0, 0), "This is my chunk data");

        // Assert.
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(0, 0)).isEmpty());
        Assertions.assertTrue(lookup.values().isEmpty());
    }

    @Test
    public void addForBlockRadius() {
        // Arrange.
        var lookup = new ChunkBoundLookup<String>();

        // Act.
        lookup.addForBlockRadius(new BlockPos(0, 0, 0), 16, "This is my chunk data");

        // Assert.
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(1, 0)).contains("This is my chunk data"));
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(0, 1)).contains("This is my chunk data"));
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(1, 1)).contains("This is my chunk data"));
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(0, 0)).contains("This is my chunk data"));

        // Spot check for something out of range.
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(1, 2)).isEmpty());
    }

    @Test
    public void updateForBlockRadius() {
        // Arrange.
        var lookup = new ChunkBoundLookup<String>();

        // Ensure there's data out of the range to update
        lookup.addForBlockRadius(new BlockPos(16, 0, 16), 16, "This is my chunk data");

        // Ensure other data is kept
        lookup.addToChunk(new ChunkPos(0, 0), "Other data");

        // Act.
        lookup.updateForBlockRadius(new BlockPos(0, 0, 0), 16, "This is my chunk data");

        // Assert.
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(1, 0)).contains("This is my chunk data"));
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(0, 1)).contains("This is my chunk data"));
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(1, 1)).contains("This is my chunk data"));
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(0, 0)).contains("This is my chunk data"));

        // Ensure the old range no longer holds the value
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(1, 2)).isEmpty());

        // Ensure no other data was displaced
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(0, 0)).contains("Other data"));
    }

    @Test
    public void addForChunkRadius() {
        // Arrange.
        var lookup = new ChunkBoundLookup<String>();

        // Act.
        lookup.addForChunkRadius(new ChunkPos(0, 0), 1, "This is my chunk data");

        // Assert.
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(1, 0)).contains("This is my chunk data"));
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(0, 1)).contains("This is my chunk data"));
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(1, 1)).contains("This is my chunk data"));
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(0, 0)).contains("This is my chunk data"));

        // Spot check for something out of range.
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(1, 2)).isEmpty());
    }

    @Test
    public void updateForChunkRadius() {
        // Arrange.
        var lookup = new ChunkBoundLookup<String>();

        // Ensure there's data out of the range to update
        lookup.addForChunkRadius(new ChunkPos(1, 1), 1, "This is my chunk data");

        // Ensure other data is kept
        lookup.addToChunk(new ChunkPos(0, 0), "Other data");

        // Act.
        lookup.updateForChunkRadius(new ChunkPos(0, 0), 1, "This is my chunk data");

        // Assert.
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(1, 0)).contains("This is my chunk data"));
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(0, 1)).contains("This is my chunk data"));
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(1, 1)).contains("This is my chunk data"));
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(0, 0)).contains("This is my chunk data"));

        // Ensure the old range no longer holds the value
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(1, 2)).isEmpty());

        // Ensure no other data was displaced
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(0, 0)).contains("Other data"));
    }

    @Test
    public void remove() {
        // Arrange.
        var lookup = new ChunkBoundLookup<String>();
        lookup.addToChunk(new ChunkPos(0, 0), "This is my chunk data");
        lookup.addToChunk(new ChunkPos(0, 1), "This is my chunk data");

        // Act.
        lookup.remove("This is my chunk data");

        // Assert.
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(0, 0)).isEmpty());
        Assertions.assertTrue(lookup.getForChunk(new ChunkPos(0, 1)).isEmpty());
        Assertions.assertTrue(lookup.values().isEmpty());
    }
}

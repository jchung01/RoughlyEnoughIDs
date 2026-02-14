package org.dimdev.jeid.api.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

public interface BiomeAccessor {
    int size();

    Chunk getChunk();

    int[] getBiomes();

    int getBiomeId(BlockPos pos);

    int getBiomeId(int relativeX, int relativeZ);
}

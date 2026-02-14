package org.dimdev.jeid.impl;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import com.google.common.base.Preconditions;
import org.dimdev.jeid.api.BiomeApi;
import org.dimdev.jeid.api.REIDApi;
import org.dimdev.jeid.api.biome.BiomeAccessor;
import org.dimdev.jeid.impl.type.BiomeContainer;
import org.dimdev.jeid.impl.type.BiomeStorage;

public class BiomeApiImpl implements BiomeApi {
    public BiomeContainer getBiomeContainer(Chunk chunk) {
        return ((BiomeStorage) chunk).reid$getBiomes();
    }

    @Override
    public BiomeAccessor getBiomeAccessor(Chunk chunk) {
        return getBiomeContainer(chunk);
    }

    public static int[] getInternalBiomeArray(Chunk chunk) {
        if (BiomeApi.INSTANCE instanceof BiomeApiImpl) {
            BiomeContainer biomeContainer = ((BiomeApiImpl) BiomeApi.INSTANCE).getBiomeContainer(chunk);
            return biomeContainer.getInternalBiomes();
        }
        // Fallback, shouldn't be reachable
        REIDApi.logger.warn("BiomeApi's implementation was overwritten to {}!", BiomeApi.INSTANCE.getClass().getName());
        return BiomeApi.INSTANCE.getBiomeAccessor(chunk).getBiomes();
    }

    @Override
    public void updateBiome(Chunk chunk, BlockPos pos, int biomeId) {
        getBiomeContainer(chunk).setBiome(pos, biomeId);
        chunk.markDirty();
    }

    @Override
    public void replaceBiomes(Chunk chunk, int[] biomeIds) {
        BiomeContainer biomeContainer = getBiomeContainer(chunk);
        Preconditions.checkArgument(biomeIds.length == biomeContainer.size(),
                "Failed to replace biomes for chunk ({}, {}), Expected array length: {}, Actual: {}",
                chunk.x, chunk.z, biomeContainer.size(), biomeIds.length);

        biomeContainer.setBiomes(biomeIds);
        chunk.markDirty();
    }

    @Override
    public void fillWithBiome(Chunk chunk, int biomeId) {
        getBiomeContainer(chunk).fill(biomeId);
        chunk.markDirty();
    }

    @Override
    public int getIdentityMask() {
        return 0xFFFFFFFF;
    }

    @Override
    public int getMaxBiomeId() {
        return Integer.MAX_VALUE - 1;
    }
}

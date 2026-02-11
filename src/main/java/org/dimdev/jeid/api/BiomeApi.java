package org.dimdev.jeid.api;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import org.dimdev.jeid.api.biome.BiomeAccessor;

public interface BiomeApi {
    BiomeApi INSTANCE = REIDApi.loadService(BiomeApi.class);

    BiomeAccessor getBiomeAccessor(Chunk chunk);

    void updateBiome(Chunk chunk, BlockPos pos, int biomeId);

    void replaceBiomes(Chunk chunk, int[] biomeIds);

    void fillWithBiome(Chunk chunk, int biomeId);

    int getIdentityMask();

    int getMaxBiomeId();
}

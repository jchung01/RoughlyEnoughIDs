package org.dimdev.jeid.api.type;

import net.minecraft.util.math.BlockPos;

import org.dimdev.jeid.api.biome.BiomeAccessor;

public interface IBiomeContainer extends BiomeAccessor {
    void setBiome(BlockPos pos, int biomeId);

    void setBiome(int relativeX, int relativeZ, int biomeId);

    void setBiomes(int[] biomes);

    void fill(int biomeId);
}

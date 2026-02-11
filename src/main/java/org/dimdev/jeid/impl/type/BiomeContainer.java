package org.dimdev.jeid.impl.type;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import org.dimdev.jeid.api.biome.BiomeAccessor;
import org.dimdev.jeid.init.REIDBiomes;

import java.util.Arrays;

public class BiomeContainer implements BiomeAccessor {
    private static final int NUM_BIOMES = 16 * 16;
    private final Chunk chunk;
    private final int[] biomes;

    public BiomeContainer(Chunk chunk) {
        this.chunk = chunk;

        biomes = new int[NUM_BIOMES];
        Arrays.fill(biomes, -1);
    }

    private int getIndex(int x, int z) {
        return (z << 4) | x;
    }

    public int[] getInternalBiomes() {
        return biomes;
    }

    @Override
    public int[] getBiomes() {
        return Arrays.copyOf(biomes, biomes.length);
    }

    @Override
    public Chunk getChunk() {
        return chunk;
    }

    @Override
    public int size() {
        return NUM_BIOMES;
    }

    @Override
    public int getBiomeId(BlockPos pos) {
        return getBiomeId(pos.getX() & 0xF, pos.getZ() & 0xF);
    }

    @Override
    public int getBiomeId(int relativeX, int relativeZ) {
        return biomes[getIndex(relativeX, relativeZ)];
    }

    public void setBiome(BlockPos pos, int biomeId) {
        setBiome(pos.getX() & 0xF, pos.getZ() & 0xF, biomeId);
    }

    public void setBiome(int relativeX, int relativeZ, int biomeId) {
        biomes[getIndex(relativeX, relativeZ)] = biomeId;
    }

    public void setBiome(int index, int biomeId) {
        biomes[index] = biomeId;
    }

    public void setBiomes(int[] biomes) {
        System.arraycopy(biomes, 0, this.biomes, 0, this.biomes.length);
    }

    public void fill(int biomeId) {
        Arrays.fill(biomes, biomeId);
    }

    public byte[] dummy() {
        byte[] dummy = new byte[NUM_BIOMES];
        Arrays.fill(dummy, (byte) REIDBiomes.ERROR.getId());
        return dummy;
    }
}

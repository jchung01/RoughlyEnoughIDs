package org.dimdev.jeid.impl.type;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;

import com.google.common.base.Preconditions;
import org.dimdev.jeid.api.biome.BiomeAccessor;
import org.dimdev.jeid.init.REIDBiomes;
import org.dimdev.jeid.util.Lazy;

import java.util.Arrays;

public class BiomeContainer implements BiomeAccessor {
    private final Chunk chunk;
    private final int numBiomes;
    private final Lazy<int[]> biomes;

    public BiomeContainer(Chunk chunk, int numBiomes) {
        this.chunk = chunk;
        this.numBiomes = numBiomes;

        biomes = Lazy.of(() -> {
            int[] biomes = new int[numBiomes];
            Arrays.fill(biomes, -1);
            return biomes;
        });
    }

    private int getIndex(int x, int z) {
        return (z << 4) | x;
    }

    public boolean isInitialized() {
        return biomes.isInitialized();
    }

    public int[] getInternalBiomes() {
        return biomes.get();
    }

    @Override
    public int size() {
        return numBiomes;
    }

    @Override
    public Chunk getChunk() {
        Preconditions.checkNotNull(chunk);

        return chunk;
    }

    @Override
    public int[] getBiomes() {
        return Arrays.copyOf(biomes.get(), size());
    }

    @Override
    public int getBiomeId(BlockPos pos) {
        return getBiomeId(pos.getX() & 0xF, pos.getZ() & 0xF);
    }

    @Override
    public int getBiomeId(int relativeX, int relativeZ) {
        return getBiomeId(getIndex(relativeX, relativeZ));
    }

    public int getBiomeId(int index) {
        return biomes.get()[index];
    }

    public void setBiome(BlockPos pos, int biomeId) {
        setBiome(pos.getX() & 0xF, pos.getZ() & 0xF, biomeId);
    }

    public void setBiome(int relativeX, int relativeZ, int biomeId) {
        biomes.get()[getIndex(relativeX, relativeZ)] = biomeId;
    }

    public void setBiome(int index, int biomeId) {
        biomes.get()[index] = biomeId;
    }

    public void setBiomes(int[] biomes) {
        System.arraycopy(biomes, 0, this.biomes.get(), 0, size());
    }

    public void fill(int biomeId) {
        Arrays.fill(biomes.get(), biomeId);
    }

    public byte[] dummy() {
        byte[] dummy = new byte[size()];
        Arrays.fill(dummy, (byte) REIDBiomes.ERROR.getId());
        return dummy;
    }
}

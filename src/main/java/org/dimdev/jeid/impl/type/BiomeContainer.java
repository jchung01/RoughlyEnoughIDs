package org.dimdev.jeid.impl.type;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.ints.IntLists;
import org.dimdev.jeid.api.type.IBiomeContainer;
import org.dimdev.jeid.biome.BiomeError;

import java.util.Arrays;

public class BiomeContainer implements IBiomeContainer {
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
    public int getSize() {
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

    @Override
    public void setBiome(BlockPos pos, int biomeId) {
        setBiome(pos.getX() & 0xF, pos.getZ() & 0xF, biomeId);
    }

    @Override
    public void setBiome(int relativeX, int relativeZ, int biomeId) {
        biomes[getIndex(relativeX, relativeZ)] = biomeId;
    }

    public void setBiome(int index, int biomeId) {
        biomes[index] = biomeId;
    }

    @Override
    public void setBiomes(int[] biomes) {
        System.arraycopy(biomes, 0, this.biomes, 0, this.biomes.length);
    }

    @Override
    public void fill(int biomeId) {
        Arrays.fill(biomes, biomeId);
    }

    public byte[] dummy() {
        byte[] dummy = new byte[NUM_BIOMES];
        Arrays.fill(dummy, (byte) Biome.REGISTRY.getIDForObject(BiomeError.getInstance()));
        return dummy;
    }
}

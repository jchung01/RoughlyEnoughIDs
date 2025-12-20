package org.dimdev.jeid.core;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.IChunkGenerator;

import org.dimdev.jeid.ducks.ICustomBiomesForGeneration;
import org.dimdev.jeid.ducks.IModSupportsJEID;
import org.dimdev.jeid.ducks.INewChunk;

public class REIDHooks {
    private static final Biome[] reusableBiomeList = new Biome[256];

    /**
     * Initialize biome array after any calls to {@link net.minecraft.world.gen.IChunkGenerator#generateChunk}.
     * This guarantees the correct biomes even for modded chunk generators.
     */
    public static void initializeBiomeArray(Chunk chunk, IChunkGenerator chunkGenerator) {
        if (chunkGenerator instanceof IModSupportsJEID) {
            return;
        }
        Biome[] biomes;
        if (chunkGenerator instanceof ICustomBiomesForGeneration) {
            // Some chunk generators modify the biomes beyond those returned by the BiomeProvider.
            biomes = ((ICustomBiomesForGeneration) chunkGenerator).getBiomesForGeneration();
        }
        else {
            biomes = chunk.getWorld().getBiomeProvider().getBiomes(reusableBiomeList, chunk.x * 16, chunk.z * 16, 16, 16);
        }
        INewChunk newChunk = (INewChunk) chunk;
        int[] intBiomeArray = newChunk.getIntBiomeArray();
        for (int i = 0; i < intBiomeArray.length; ++i) {
            intBiomeArray[i] = Biome.getIdForBiome(biomes[i]);
        }
    }

    /**
     * Set the int biome id for a certain block position.
     * It is not this method's responsibility to make sure the change is synced to the client.
     * @param biomeId the biome id
     * @param world the world
     * @param pos the block position
     * @return the set biome id
     */
    public static int setBiomeId(int biomeId, World world, BlockPos pos) {
        Chunk chunk = world.getChunk(pos);
        int[] intBiomeArray = ((INewChunk) chunk).getIntBiomeArray();
        intBiomeArray[(pos.getZ() & 0xF) << 4 | pos.getX() & 0xF] = biomeId;
        chunk.markDirty();
        return biomeId;
    }
}

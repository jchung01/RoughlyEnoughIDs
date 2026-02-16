package org.dimdev.jeid.ducks;

import org.dimdev.jeid.impl.type.BiomeStorage;

/**
 * Duck interface for Chunk mixins.
 */
public interface INewChunk extends BiomeStorage {
    /**
     * @deprecated Use {@link BiomeStorage#reid$getBiomes()} instead.
     */
    @Deprecated
    int[] getIntBiomeArray();

    /**
     * @deprecated Use {@link BiomeStorage#reid$getBiomes()} instead.
     */
    @Deprecated
    void setIntBiomeArray(int[] intBiomeArray);
}

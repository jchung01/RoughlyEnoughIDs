package org.dimdev.jeid.ducks;

import org.dimdev.jeid.impl.type.BiomeContainer;

/**
 * Duck interface for Chunk mixins.
 */
public interface INewChunk {
    BiomeContainer reid$getBiomes();

    @Deprecated
    int[] getIntBiomeArray();

    @Deprecated
    void setIntBiomeArray(int[] intBiomeArray);
}

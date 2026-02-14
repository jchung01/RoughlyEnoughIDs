package org.dimdev.jeid.ducks;

import org.dimdev.jeid.impl.type.BiomeStorage;

/**
 * Duck interface for Chunk mixins.
 */
public interface INewChunk extends BiomeStorage {
    @Deprecated
    int[] getIntBiomeArray();

    @Deprecated
    void setIntBiomeArray(int[] intBiomeArray);
}

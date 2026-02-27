package org.dimdev.jeid.ducks;

import net.minecraft.world.chunk.NibbleArray;

/**
 * Duck interface for BlockStateContainer mixin.
 */
public interface INewBlockStateContainer {
    void setTemporaryPalette(int[] temporaryPalette);
    int[] getTemporaryPalette();
    void setLegacyAdd2(NibbleArray add2);
    boolean reid$isLegacyFormat();
}

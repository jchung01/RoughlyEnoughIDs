package org.dimdev.jeid.mixin.modsupport.cubicchunks;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;

import io.github.opencubicchunks.cubicchunks.api.worldgen.CubePrimer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import javax.annotation.Nonnull;

@Mixin(value = CubePrimer.class, remap = false)
@SuppressWarnings("deprecation")
public class MixinCubePrimer {
    @Shadow
    @Final
    public static IBlockState DEFAULT_STATE;
    @Shadow
    @Final
    private char[] data;
    @Unique
    private char[] extIntData = null;

    @Shadow
    private static int getBlockIndex(int x, int y, int z) {
        return 0;
    }

    /**
     * @author Exsolutus
     * @reason Support int biome ids
     */
    @Overwrite
    public IBlockState getBlockState(int x, int y, int z) {
        int idx = getBlockIndex(x, y, z);
        int block = this.data[idx];
        if (extIntData != null) {
            block |= extIntData[idx] << 16;
        }
        IBlockState iblockstate = Block.BLOCK_STATE_IDS.getByValue(block);
        return iblockstate == null ? DEFAULT_STATE : iblockstate;
    }

    /**
     * @author Exsolutus
     * @reason Support int biome ids
     */
    @Overwrite
    public void setBlockState(int x, int y, int z, @Nonnull IBlockState state) {
        int value = Block.BLOCK_STATE_IDS.get(state);
        char lsb = (char) value;
        int idx = getBlockIndex(x, y, z);
        this.data[idx] = lsb;
        if (value > 0xFFFF) {
            if (extIntData == null) {
                extIntData = new char[4096];
            }
            extIntData[idx] = (char) (value >>> 16);
        }
    }
}

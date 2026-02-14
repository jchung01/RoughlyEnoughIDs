package org.dimdev.jeid.mixin.modsupport.cubicchunks;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.chunk.BlockStateContainer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import io.github.opencubicchunks.cubicchunks.core.world.cube.Cube;
import org.dimdev.jeid.impl.type.BiomeStorage;
import org.dimdev.jeid.ducks.INewBlockStateContainer;
import org.dimdev.jeid.impl.BiomeApiImpl;
import org.dimdev.jeid.impl.type.BiomeContainer;
import org.dimdev.jeid.mixin.core.world.MixinBlockStateContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "io.github.opencubicchunks.cubicchunks.core.server.chunkio.IONbtWriter", remap = false)
public class MixinIONbtWriter {
    /**
     * @author Exsolutus, jchung01
     * @reason Support int biome ids
     */
    @Overwrite
    private static void writeBiomes(Chunk column, NBTTagCompound nbt) {// column biomes
        nbt.setIntArray("Biomes", BiomeApiImpl.getInternalBiomeArray(column));
    }

    /**
     * @author Exsolutus, jchung01
     * @reason Support int biome ids
     */
    @Overwrite
    private static void writeBiomes(Cube cube, NBTTagCompound nbt) {// cube biomes
        BiomeContainer biomeContainer = ((BiomeStorage) cube).reid$getBiomes();
        if (biomeContainer.isInitialized()) {
            nbt.setIntArray("Biomes3D", biomeContainer.getInternalBiomes());
        }
    }

    /**
     * Delegate to JEID version of {@link BlockStateContainer#getDataForNBT(byte[], NibbleArray)}.
     *
     * @see MixinBlockStateContainer#reid$newGetDataForNBT(byte[], NibbleArray, CallbackInfoReturnable) delegated logic
     */
    @Expression("? < 4096")
    @ModifyExpressionValue(method = "writeBlocks", at = @At("MIXINEXTRAS:EXPRESSION"))
    private static boolean reid$setNBTPalette(boolean original, @Local(name = "ebs") ExtendedBlockStorage ebs, @Local(name = "section") NBTTagCompound section,
                                              @Local(name = "abyte") byte[] abyte, @Local(name = "data") NibbleArray data, @Local(name = "add") LocalRef<NibbleArray> add) {
        add.set(ebs.getData().getDataForNBT(abyte, data));
        int[] palette = ((INewBlockStateContainer) ebs.getData()).getTemporaryPalette();
        if (palette != null) section.setIntArray("Palette", palette);
        // Skip loop
        return false;
    }
}

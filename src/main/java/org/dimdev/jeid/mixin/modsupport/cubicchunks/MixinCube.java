package org.dimdev.jeid.mixin.modsupport.cubicchunks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import io.github.opencubicchunks.cubicchunks.api.util.Coords;
import io.github.opencubicchunks.cubicchunks.api.world.IColumn;
import io.github.opencubicchunks.cubicchunks.core.CubicChunks;
import io.github.opencubicchunks.cubicchunks.core.util.AddressTools;
import io.github.opencubicchunks.cubicchunks.core.world.cube.Cube;
import org.dimdev.jeid.ducks.modsupport.cubicchunks.INewCube;
import org.dimdev.jeid.init.REIDBiomes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Arrays;

@Mixin(value = Cube.class, remap = false)
@Implements(@Interface(iface = INewCube.class, prefix = "int$"))
public abstract class MixinCube {
    @Shadow
    @Final
    private World world;
    @Unique
    private static final byte ERROR_BIOME_ID = (byte) REIDBiomes.ERROR.getId();
    @Unique
    @Nullable
    private int[] intBiomeArray;

    @SuppressWarnings("deprecation")
    @Shadow
    public abstract <T extends Chunk & IColumn> T getColumn();

    /**
     * @author Exsolutus
     * @reason Support int biome ids
     */
    @Overwrite
    public Biome getBiome(BlockPos pos) {
        if (this.intBiomeArray == null) {
            return this.getColumn().getBiome(pos, world.getBiomeProvider());
        }
        int biomeX = Coords.blockToLocalBiome3d(pos.getX());
        int biomeY = Coords.blockToLocalBiome3d(pos.getY());
        int biomeZ = Coords.blockToLocalBiome3d(pos.getZ());
        int biomeId = this.intBiomeArray[AddressTools.getBiomeAddress3d(biomeX, biomeY, biomeZ)];
        return Biome.getBiome(biomeId);
    }

    @Inject(method = "getBiomeArray", at = @At(value = "RETURN"), cancellable = true)
    private void reid$returnErrorBiomeArray(CallbackInfoReturnable<byte[]> cir) {
        byte[] arr = new byte[256];
        Arrays.fill(arr, ERROR_BIOME_ID);
        cir.setReturnValue(arr);
    }

    /**
     * @author Exsolutus
     * @reason Support int biome ids
     */
    @Overwrite
    public void setBiome(int localBiomeX, int localBiomeY, int localBiomeZ, Biome biome) {
        if (this.intBiomeArray == null)
            this.intBiomeArray = new int[64];

        this.intBiomeArray[AddressTools.getBiomeAddress3d(localBiomeX, localBiomeY, localBiomeZ)] = Biome.REGISTRY.getIDForObject(biome);
    }

    @Nullable
    public int[] int$getBiomeArray() {
        return this.intBiomeArray;
    }

    public void int$setBiomeArray(int[] biomeArray) {
        if (this.intBiomeArray == null)
            this.intBiomeArray = biomeArray;

        if (this.intBiomeArray.length != biomeArray.length) {
            CubicChunks.LOGGER.warn("Could not set level cube biomes, array length is {} instead of {}", biomeArray.length, this.intBiomeArray.length);
        } else {
            System.arraycopy(biomeArray, 0, this.intBiomeArray, 0, this.intBiomeArray.length);
        }
    }
}

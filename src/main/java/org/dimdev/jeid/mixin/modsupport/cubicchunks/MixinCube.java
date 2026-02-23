package org.dimdev.jeid.mixin.modsupport.cubicchunks;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import io.github.opencubicchunks.cubicchunks.api.util.Coords;
import io.github.opencubicchunks.cubicchunks.api.world.IColumn;
import io.github.opencubicchunks.cubicchunks.core.util.AddressTools;
import io.github.opencubicchunks.cubicchunks.core.world.cube.Cube;
import org.dimdev.jeid.impl.type.BiomeStorage;
import org.dimdev.jeid.impl.type.BiomeContainer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Cube.class, remap = false)
public abstract class MixinCube implements BiomeStorage {
    @Shadow
    @Final
    private World world;
    @Unique
    private BiomeContainer reid$biomeContainer = new BiomeContainer(getColumn(), Coords.BIOMES_PER_CUBE);

    @Shadow
    public abstract <T extends Chunk & IColumn> T getColumn();

    // TODO: Consider if CC-specific API is necessary
    @Unique
    @Override
    public BiomeContainer reid$getBiomes() {
        return reid$biomeContainer;
    }

    /**
     * @return A biome array filled with REID's error biome to better identify mods that aren't supported yet.
     */
    @ModifyReturnValue(method = "getBiomeArray", at = @At(value = "RETURN"))
    private byte[] reid$returnErrorBiomeArray(byte[] original) {
        if (!reid$biomeContainer.isInitialized()) {
            return null;
        }
        return reid$biomeContainer.dummy();
    }

    /**
     * @author Exsolutus, jchung01
     * @reason Support int biome ids
     */
    @Overwrite
    public Biome getBiome(BlockPos pos) {
        // No 3D biomes, fallback to 2D
        if (!reid$biomeContainer.isInitialized()) {
            return this.getColumn().getBiome(pos, world.getBiomeProvider());
        }
        int biomeX = Coords.blockToLocalBiome3d(pos.getX());
        int biomeY = Coords.blockToLocalBiome3d(pos.getY());
        int biomeZ = Coords.blockToLocalBiome3d(pos.getZ());
        int biomeId = reid$biomeContainer.getBiomeId(AddressTools.getBiomeAddress3d(biomeX, biomeY, biomeZ));
        return Biome.getBiome(biomeId);
    }

    /**
     * @author Exsolutus, jchung01
     * @reason Support int biome ids
     */
    @Overwrite
    public void setBiome(int localBiomeX, int localBiomeY, int localBiomeZ, Biome biome) {
        reid$biomeContainer.setBiome(AddressTools.getBiomeAddress3d(localBiomeX, localBiomeY, localBiomeZ),
                Biome.getIdForBiome(biome));
    }

    /**
     * Skip setting original biome array.
     */
    @Inject(method = "setBiomeArray", at = @At("HEAD"), cancellable = true)
    private void reid$cancelSetBiomeArray(byte[] biomeArray, CallbackInfo ci) {
        ci.cancel();
    }
}

package org.dimdev.jeid.mixin.modsupport.twilightforest;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import org.dimdev.jeid.ducks.INewChunk;
import org.dimdev.jeid.network.BiomeChangeMessage;
import org.dimdev.jeid.network.MessageManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import twilightforest.biomes.TFBiomes;
import twilightforest.block.BlockTFMagicLogSpecial;

@Mixin(value = BlockTFMagicLogSpecial.class, remap = false)
public class MixinBlockTFMagicLogSpecial {
    /**
     * @reason For versions 3.9 and later
     */
    @Inject(method = "sendChangedBiome", at = @At(value = "HEAD"), cancellable = true)
    private void reid$rewriteSendChangedBiome(World world, BlockPos pos, Biome biome, CallbackInfo ci) {
        IMessage message = new BiomeChangeMessage(pos.getX(), pos.getZ(), Biome.getIdForBiome(biome));
        MessageManager.CHANNEL.sendToAllAround(message, new NetworkRegistry.TargetPoint(world.provider.getDimension(), pos.getX(), 128.0D, pos.getZ(), 128.0D));
        ci.cancel();
    }

    @Inject(method = "doTreeOfTransformationEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Chunk;getBiomeArray()[B", remap = true))
    private void reid$toIntBiomeArray(CallbackInfo ci, @Local(ordinal = 1) BlockPos dPos, @Local Chunk chunk) {
        ((INewChunk) chunk).getIntBiomeArray()[(dPos.getZ() & 15) << 4 | (dPos.getX() & 15)] = Biome.getIdForBiome(TFBiomes.enchantedForest);
        chunk.markDirty();
    }
}
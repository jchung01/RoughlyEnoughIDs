package org.dimdev.jeid.mixin.core.misc;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public class MixinBlock {
    @Shadow
    public static int getIdFromBlock(Block blockIn) {
        return 0;
    }

    /**
     * @reason Use JustEnoughIDs ID format (id, meta rather than meta, id) for
     * blocks with an ID larger than 4096.
     **/
    // @ModifyReturnValue doesn't seem to work here
    @Inject(method = "getStateId", at = @At(value = "RETURN"), cancellable = true)
    private static void reid$getJEIDStateId(IBlockState state, CallbackInfoReturnable<Integer> cir, @Local Block block) {
        int id = getIdFromBlock(block);
        int meta = block.getMetaFromState(state);
        if ((id & 0xFFFFF000) == 0) {
            // Use vanilla 4 bit meta + 12 bit ID
            return;
        }
        // Use JEID 28 bit ID + 4 bit meta
        cir.setReturnValue((id << 4) | meta);
    }

    /**
     * @reason Use JustEnoughIDs ID format (id, meta rather than meta, id) for blocks with
     * an ID larger than 4096 stored in JEID format (state ID is larger than 65536)
     */
    @ModifyArg(method = "getStateById", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getBlockById(I)Lnet/minecraft/block/Block;"), index = 0)
    private static int reid$useJEIDId(int vanillaId, @Local(ordinal = 0, argsOnly = true) int stateId) {
        if ((stateId & 0xFFFF0000) == 0) {
            return vanillaId;
        } else {
            return (stateId >> 4) & 0xFFFFFFF;
        }
    }

    /**
     * @reason See {@link #reid$useJEIDId}
     */
    @ModifyArg(method = "getStateById", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getStateFromMeta(I)Lnet/minecraft/block/state/IBlockState;"), index = 0)
    private static int reid$useJEIDMeta(int vanillaMeta, @Local(ordinal = 0, argsOnly = true) int stateId) {
        if ((stateId & 0xFFFF0000) == 0) {
            return vanillaMeta;
        } else {
            return stateId & 0xF;
        }
    }
}

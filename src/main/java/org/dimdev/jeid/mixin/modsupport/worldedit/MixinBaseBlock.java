package org.dimdev.jeid.mixin.modsupport.worldedit;

import com.llamalad7.mixinextras.expression.Expression;
import com.sk89q.worldedit.blocks.BaseBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = BaseBlock.class, remap = false)
public class MixinBaseBlock {
    @Unique
    private int reid$intId = 0;

    @Inject(method = "getId", at = @At(value = "RETURN"), cancellable = true)
    private void reid$getIntId(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(reid$intId);
    }

    @ModifyConstant(method = "internalSetId", constant = @Constant(intValue = 4095))
    private int reid$getMaxBlockId(int oldValue) {
        return Integer.MAX_VALUE - 1;
    }

    @Expression("@((short) ?)")
    @Inject(method = "internalSetId", at = @At("MIXINEXTRAS:EXPRESSION"))
    private void reid$setIntId(int id, CallbackInfo ci) {
        reid$intId = id;
    }
}

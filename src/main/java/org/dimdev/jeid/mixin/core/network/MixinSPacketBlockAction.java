package org.dimdev.jeid.mixin.core.network;

import net.minecraft.network.play.server.SPacketBlockAction;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(value = SPacketBlockAction.class)
public class MixinSPacketBlockAction {
    @ModifyConstant(method = {"readPacketData", "writePacketData"}, constant = @Constant(intValue = 4095))
    private int reid$getMaxBlockId(int oldValue) {
        return Integer.MAX_VALUE - 1;
    }
}

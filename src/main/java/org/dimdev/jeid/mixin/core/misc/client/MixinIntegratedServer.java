package org.dimdev.jeid.mixin.core.misc.client;

import net.minecraft.server.integrated.IntegratedServer;

import org.dimdev.jeid.util.compat.CompatHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = IntegratedServer.class)
public class MixinIntegratedServer {
    @Inject(method = "init", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/integrated/IntegratedServer;loadAllWorlds(Ljava/lang/String;Ljava/lang/String;JLnet/minecraft/world/WorldType;Ljava/lang/String;)V"), cancellable = true)
    private void reid$handleServerAboutToStartCancellable(CallbackInfoReturnable<Boolean> cir) {
        if (CompatHandler.handleNEID((IntegratedServer) (Object) this)) {
            cir.setReturnValue(false);
        }
    }
}

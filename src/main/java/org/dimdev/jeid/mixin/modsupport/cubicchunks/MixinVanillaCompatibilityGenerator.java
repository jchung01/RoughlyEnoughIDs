package org.dimdev.jeid.mixin.modsupport.cubicchunks;

import net.minecraft.world.chunk.Chunk;

import io.github.opencubicchunks.cubicchunks.core.worldgen.generator.vanilla.VanillaCompatibilityGenerator;
import org.dimdev.jeid.core.REIDHooks;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = VanillaCompatibilityGenerator.class, remap = false)
public class MixinVanillaCompatibilityGenerator {
    @Inject(method = "generateColumn", at = @At("HEAD"), cancellable = true)
    public void reid$generateBiomes(Chunk column, CallbackInfo ci) {
        REIDHooks.initializeBiomeArray(column, null);
        ci.cancel();
    }
}

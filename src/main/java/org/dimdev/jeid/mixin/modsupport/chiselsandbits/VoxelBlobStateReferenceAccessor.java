package org.dimdev.jeid.mixin.modsupport.chiselsandbits;

import mod.chiselsandbits.chiseledblock.data.VoxelBlobStateInstance;
import mod.chiselsandbits.chiseledblock.data.VoxelBlobStateReference;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.lang.ref.WeakReference;
import java.util.Map;

@Mixin(value = VoxelBlobStateReference.class, remap = false)
public interface VoxelBlobStateReferenceAccessor {
    @Invoker("getRefs")
    static Map<VoxelBlobStateInstance, WeakReference<VoxelBlobStateInstance>> reid$getRefs() {
        throw new AssertionError();
    }
}

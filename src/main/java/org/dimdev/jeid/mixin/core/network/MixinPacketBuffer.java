package org.dimdev.jeid.mixin.core.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PacketBuffer.class)
public abstract class MixinPacketBuffer {
    @Shadow
    public abstract int readVarInt();

    @Shadow
    public abstract PacketBuffer writeVarInt(int input);

    @Redirect(method = "writeItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;writeShort(I)Lio/netty/buffer/ByteBuf;", ordinal = 0, remap = false))
    private ByteBuf reid$writeIntItemId(PacketBuffer packetBuffer, int p_writeShort_1_) {
        return writeVarInt(p_writeShort_1_);
    }

    @Redirect(method = "writeItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;writeShort(I)Lio/netty/buffer/ByteBuf;", ordinal = 1, remap = false))
    private ByteBuf reid$writeIntItemId1(PacketBuffer packetBuffer, int p_writeShort_1_) {
        return writeVarInt(p_writeShort_1_);
    }

    /**
     * @reason Disable default id read logic to prevent advancing readerIndex.
     */
    @Redirect(method = "readItemStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;readShort()S", ordinal = 0, remap = false))
    private short reid$defaultReadId(PacketBuffer instance) {
        return 0;
    }

    @ModifyVariable(method = "readItemStack", at = @At(value = "STORE"), ordinal = 0)
    private int reid$readIntId(int original) {
        return readVarInt();
    }
}

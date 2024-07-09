package org.dimdev.jeid.mixin.core.potion;

import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

import io.netty.buffer.ByteBuf;
import org.dimdev.jeid.ducks.IStoredEffectInt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Mixin to read/write effect ids as ints. Converted from JEIDTransformer#transformSPacketEntityEffect
 *
 * @see org.dimdev.jeid.core.JEIDTransformer
 */
@Mixin(value = SPacketEntityEffect.class)
public class MixinSPacketEntityEffect implements IStoredEffectInt {
    @Unique
    private int reid$effectInt = 0;

    @Final
    @Inject(method = "<init>(ILnet/minecraft/potion/PotionEffect;)V", at = @At(value = "RETURN"))
    private void reid$initEffectInt(int entityIdIn, PotionEffect effect, CallbackInfo ci) {
        reid$effectInt = Potion.getIdFromPotion(effect.getPotion());
    }

    /**
     * @reason Redirect instead of wrap to avoid advancing index.
     */
    @Final
    @Redirect(method = "readPacketData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;readByte()B", ordinal = 0))
    private byte reid$readEffectInt(PacketBuffer buf) {
        reid$effectInt = buf.readVarInt();
        return (byte) (reid$effectInt & 255);
    }

    /**
     * @reason Redirect instead of wrap to avoid advancing index.
     */
    @Final
    @Redirect(method = "writePacketData", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/PacketBuffer;writeByte(I)Lio/netty/buffer/ByteBuf;", ordinal = 0))
    private ByteBuf reid$writeEffectInt(PacketBuffer buf, int ignored) {
        buf.writeVarInt(reid$effectInt);
        return null;
    }

    @Override
    public int getEffectInt() {
        return reid$effectInt;
    }
}

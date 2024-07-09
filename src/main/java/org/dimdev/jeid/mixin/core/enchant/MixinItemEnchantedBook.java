package org.dimdev.jeid.mixin.core.enchant;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.item.ItemEnchantedBook;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import org.dimdev.jeid.JEID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ItemEnchantedBook.class)
public class MixinItemEnchantedBook {
    @ModifyArg(method = "addEnchantment", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getEnchantmentByID(I)Lnet/minecraft/enchantment/Enchantment;"))
    private static int reid$getIntEnchIdForAdd(int original, @Local(ordinal = 0) NBTTagCompound nbtTagCompound) {
        return nbtTagCompound.getInteger("id");
    }

    // LVT gets modified by above mixin, let's just overwrite the short id
    @ModifyArg(method = "addEnchantment", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagList;appendTag(Lnet/minecraft/nbt/NBTBase;)V", ordinal = 0))
    private static NBTBase reid$setIntEnchIdForAdd(NBTBase nbt, @Local(argsOnly = true) EnchantmentData data) {
        if (!(nbt instanceof NBTTagCompound)) {
            throw new AssertionError(JEID.MODID + " :: NBTTagList#appendTag argument of addEnchantment isn't \"NBTTagCompound\"");
        }
        NBTTagCompound nbtTagCompound = (NBTTagCompound) nbt;
        nbtTagCompound.setInteger("id", Enchantment.getEnchantmentID(data.enchantment));
        return nbtTagCompound;
    }
}

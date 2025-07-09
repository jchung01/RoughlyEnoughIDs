package org.dimdev.jeid.mixin.core.enchant;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import com.llamalad7.mixinextras.sugar.Local;
import org.dimdev.jeid.JEID;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantmentHelper.class)
public class MixinEnchantmentHelper {
    @ModifyArg(method = "getEnchantmentLevel", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getEnchantmentByID(I)Lnet/minecraft/enchantment/Enchantment;"))
    private static int reid$getIntEnchIdForLevel(int original, @Local NBTTagCompound nbtTagCompound) {
        return nbtTagCompound.getInteger("id");
    }

    @ModifyArg(method = "getEnchantments", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/Enchantment;getEnchantmentByID(I)Lnet/minecraft/enchantment/Enchantment;"))
    private static int reid$getIntEnchIdForMap(int id, @Local NBTTagCompound nbtTagCompound) {
        return nbtTagCompound.getInteger("id");
    }

    @Redirect(method = "setEnchantments", at = @At(value = "INVOKE", target = "Lnet/minecraft/nbt/NBTTagCompound;setShort(Ljava/lang/String;S)V", ordinal = 0))
    private static void reid$setIntEnchId(NBTTagCompound instance, String key, short value, @Local Enchantment enchant) {
        if (!key.equals("id")) throw new AssertionError(JEID.MODID + " :: Ordinal 0 of setEnchantments isn't \"id\"");
        instance.setInteger("id", Enchantment.getEnchantmentID(enchant));
    }

    @ModifyVariable(method = "applyEnchantmentModifier", at = @At(value = "STORE"), ordinal = 1)
    private static int reid$getIntEnchIdForModifier(int id, @Local NBTTagList nbtTagList, @Local(ordinal = 0) int index) {
        // Ints on LVT: (ordinal = 0) = int i, (ordinal = 1) = int j
        return nbtTagList.getCompoundTagAt(index).getInteger("id");
    }
}
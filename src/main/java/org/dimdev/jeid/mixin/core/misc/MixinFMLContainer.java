package org.dimdev.jeid.mixin.core.misc;

import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraftforge.fml.common.FMLContainer;
import org.dimdev.jeid.JEID;
import org.dimdev.jeid.JEIDLogger;
import org.dimdev.jeid.proxy.IProxy;
import org.dimdev.jeid.util.Mods;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Map;

@Mixin(value = FMLContainer.class, remap = false)
public class MixinFMLContainer {
    @Definition(id = "getString", method = "Lnet/minecraft/nbt/NBTTagCompound;getString(Ljava/lang/String;)Ljava/lang/String;", remap = true)
    @Expression("?.getString('ModId')")
    @ModifyExpressionValue(method = "readData", at = @At("MIXINEXTRAS:EXPRESSION"))
    private String reid$checkNEID(String modId, SaveHandler handler, WorldInfo info, Map<String, NBTBase> propertyMap) {
        if (modId.equals(IProxy.NEID)) {
            JEIDLogger.LOGGER.warn("This world was saved with NEID; REID will try to migrate the save!");
            propertyMap.put(Mods.NEID_MIGRATION_KEY, new NBTTagByte((byte) 1));
        }
        return modId;
    }
}

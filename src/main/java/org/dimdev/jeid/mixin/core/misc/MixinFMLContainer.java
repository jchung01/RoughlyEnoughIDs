package org.dimdev.jeid.mixin.core.misc;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraftforge.fml.common.FMLContainer;
import org.dimdev.jeid.JEID;
import org.dimdev.jeid.JEIDLogger;
import org.dimdev.jeid.proxy.IProxy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(value = FMLContainer.class, remap = false)
public class MixinFMLContainer {
    @Definition(id = "getString", method = "Lnet/minecraft/nbt/NBTTagCompound;getString(Ljava/lang/String;)Ljava/lang/String;", remap = true)
    @Expression("?.getString('ModId')")
    @ModifyExpressionValue(method = "readData", at = @At("MIXINEXTRAS:EXPRESSION"))
    private String reid$checkNEID(String modId) {
        if (modId.equals(IProxy.NEID)) {
            JEIDLogger.LOGGER.warn("This world was saved with NEID; REID will try to migrate the save!");
            JEID.worldSavedWithNEID = true;
        }
        return modId;
    }
}

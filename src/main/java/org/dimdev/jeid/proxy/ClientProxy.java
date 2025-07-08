package org.dimdev.jeid.proxy;

import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.LoaderException;
import org.dimdev.jeid.util.IncompatibleModsException;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ClientProxy implements IProxy {
    @Override
    public void checkIncompatibleMods() {
        if (Loader.isModLoaded(NEID)) {
            throw new LoaderException(new IncompatibleModsException(getErrorMessage(NEID), NEID));
        }
    }

    @Override
    public List<String> getErrorMessage(String modId) {
        List<String> message = new ArrayList<>();
        if (modId.equals(NEID)) {
            message.add(new TextComponentTranslation("msg.reid.neidcompat.warning1").getFormattedText());
            message.add(new TextComponentTranslation("msg.reid.neidcompat.warning2").getFormattedText());
            message.add(new TextComponentTranslation("msg.reid.neidcompat.warning3")
                    .setStyle(new Style().setColor(TextFormatting.RED))
                    .getFormattedText());
            message.add(new TextComponentTranslation("msg.reid.neidcompat.warning4").getFormattedText());
        }
        return message;
    }
}

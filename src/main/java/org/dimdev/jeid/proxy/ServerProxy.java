package org.dimdev.jeid.proxy;

import net.minecraft.crash.CrashReport;
import net.minecraft.util.ReportedException;
import net.minecraft.util.text.TextComponentTranslation;

import net.minecraftforge.fml.common.Loader;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ServerProxy implements IProxy {
    @Override
    public void checkIncompatibleMods() {
        if (Loader.isModLoaded(NEID)) {
            CrashReport crashreport = CrashReport.makeCrashReport(
                    new RuntimeException(String.join("\n", getErrorMessage(NEID))),
                    "NotEnoughIDs (NEID) is incompatible - see RuntimeException below");
            throw new ReportedException(crashreport);
        }
    }

    @Override
    public List<String> getErrorMessage(String modId) {
        List<String> message = new ArrayList<>();
        if (modId.equals(NEID)) {
            message.add(new TextComponentTranslation("msg.reid.neidcompat.warning1").getUnformattedText());
            message.add(new TextComponentTranslation("msg.reid.neidcompat.warning2").getUnformattedText());
            message.add(new TextComponentTranslation("msg.reid.neidcompat.warning3").getUnformattedText());
            message.add(new TextComponentTranslation("msg.reid.neidcompat.warning4").getUnformattedText());
        }
        return message;
    }
}

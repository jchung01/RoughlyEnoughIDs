package org.dimdev.jeid.util.compat;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.crash.CrashReport;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ReportedException;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import org.dimdev.jeid.JEIDLogger;

public class CompatHandler {
    /**
     * @return true if NEID was detected, false otherwise
     */
    public static boolean handleNEID(MinecraftServer server) {
        if (!Loader.isModLoaded("neid")) return false;
        List<String> neidMessage = neidCompatMessage();
        for (String line : neidMessage) {
            JEIDLogger.LOGGER.error(line);
        }
        if (server.isDedicatedServer()) {
            String detailMessage = "REID detected NEID is installed. Please remove NEID to avoid world load issues and let REID convert the save. See errors in log for more details.";
            CrashReport crashreport = CrashReport.makeCrashReport(new RuntimeException(detailMessage), "NotEnoughIDs (NEID) is incompatible - see RuntimeException below");
            throw new ReportedException(crashreport);
        }
        else {
            displayNEIDCompatScreen(neidMessage);
        }
        return true;
    }

    private static List<String> neidCompatMessage() {
        final List<String> message = new ArrayList<>();
        message.add(new TextComponentTranslation("msg.reid.neidcompat.warning1").getFormattedText());
        message.add(new TextComponentTranslation("msg.reid.neidcompat.warning2").getFormattedText());
        message.add(new TextComponentTranslation("msg.reid.neidcompat.warning3").getFormattedText());
        message.add(new TextComponentTranslation("msg.reid.neidcompat.warning4").getFormattedText());
        return message;
    }

    @SideOnly(Side.CLIENT)
    public static void displayNEIDCompatScreen(List<String> message) {
        Minecraft.getMinecraft().addScheduledTask(() ->{
           Minecraft.getMinecraft().displayGuiScreen(new CompatScreen(message));
        });
    }
}

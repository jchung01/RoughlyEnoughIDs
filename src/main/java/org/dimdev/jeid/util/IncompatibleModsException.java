package org.dimdev.jeid.util;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiErrorScreen;

import net.minecraftforge.fml.client.CustomModLoadingErrorDisplayException;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class IncompatibleModsException extends CustomModLoadingErrorDisplayException {
    private final List<String> messages;
    private int textHeight;

    public IncompatibleModsException(List<String> messages, String modId) {
        super("Incompatible mod: " + modId, null);
        this.messages = messages;
    }

    @Override
    public void initGui(GuiErrorScreen errorScreen, FontRenderer fontRenderer) {
        this.textHeight = this.messages.size() * fontRenderer.FONT_HEIGHT;
    }

    @Override
    public void drawScreen(GuiErrorScreen errorScreen, FontRenderer fontRenderer, int mouseRelX, int mouseRelY, float tickTime) {
        int i = errorScreen.height / 2 - this.textHeight / 2;
        for (String s : this.messages) {
            errorScreen.drawCenteredString(fontRenderer, s, errorScreen.width / 2, i, 16777215);
            i += fontRenderer.FONT_HEIGHT;
        }
    }
}

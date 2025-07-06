package org.dimdev.jeid.config;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.dimdev.jeid.JEID;

@Config(modid = JEID.MODID, name = "RoughlyEnoughIDs")
@Config.LangKey(value = "cfg.reid.mainTitle")
public class ConfigHandler {
    @Config.LangKey("cfg.reid.debug")
    @Config.Name("Debug")
    public static final DebugCategory DEBUG = new DebugCategory();

    @Config.LangKey("cfg.reid.transformer")
    @Config.Name("Transformers")
    public static final TransformerCategory TRANSFORMER = new TransformerCategory();

    public static class DebugCategory {
        @Config.RequiresMcRestart
        @Config.Name("Biomes")
        @Config.Comment("Enable this to register extra biomes for debugging/testing.")
        public boolean reidDebugBiomesToggle = false;

        @Config.RequiresMcRestart
        @Config.Name("Biomes - Amount")
        @Config.Comment("The amount of biomes to register.")
        public int reidDebugBiomesAmt = 300;

        @Config.RequiresMcRestart
        @Config.Name("Blocks")
        @Config.Comment("Enable this to register extra blocks for debugging/testing.")
        public boolean reidDebugBlocksToggle = false;

        @Config.RequiresMcRestart
        @Config.Name("Blocks - Amount")
        @Config.Comment("The amount of blocks to register.")
        public int reidDebugBlocksAmt = 5_000;

        @Config.RequiresMcRestart
        @Config.Name("Enchantments")
        @Config.Comment("Enable this to register extra enchantments for debugging/testing.")
        public boolean reidDebugEnchantsToggle = false;

        @Config.RequiresMcRestart
        @Config.Name("Enchantments - Amount")
        @Config.Comment("The amount of enchantments to register.")
        public int reidDebugEnchantsAmt = Short.MAX_VALUE;

        @Config.RequiresMcRestart
        @Config.Name("Items")
        @Config.Comment("Enable this to register extra items for debugging/testing.")
        public boolean reidDebugItemsToggle = false;

        @Config.RequiresMcRestart
        @Config.Name("Items - Amount")
        @Config.Comment("The amount of items to register.")
        public int reidDebugItemsAmt = 40_000;

        @Config.RequiresMcRestart
        @Config.Name("Potions")
        @Config.Comment("Enable this to register extra potions for debugging/testing.")
        public boolean reidDebugPotionsToggle = false;

        @Config.RequiresMcRestart
        @Config.Name("Potions - Amount")
        @Config.Comment("The amount of potions to register.")
        public int reidDebugPotionsAmt = 300;
    }

    public static class TransformerCategory {
        @Config.RequiresMcRestart
        @Config.Name("Patch Targets: Chunk biome initialization")
        @Config.Comment
            ({
                "Some mods may generate chunks outside of the expected class and need REID to inject its biome initialization.",
                "The issue generally manifests as random patches of plains biomes in chunks.",
                "Specify classes here by their fully qualified class name."
            })
        public String[] chunkBiomeInitPatchList = new String[] {
            "pregenerator.impl.processor.generator.ChunkHelper"
        };
    }

    @Mod.EventBusSubscriber(modid = JEID.MODID)
    public static class EventHandler {
        @SubscribeEvent
        public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(JEID.MODID)) {
                ConfigManager.sync(JEID.MODID, Config.Type.INSTANCE);
            }
        }
    }
}

package org.dimdev.jeid.init;

import net.minecraft.world.biome.Biome;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;
import org.dimdev.jeid.JEID;
import org.dimdev.jeid.biome.BiomeError;

@ObjectHolder(JEID.MODID)
public class REIDBiomes {
    @ObjectHolder(BiomeError.NAME)
    public static final BiomeError ERROR = new BiomeError();

    @SubscribeEvent
    public static void onBiomeRegister(RegistryEvent.Register<Biome> event) {
        IForgeRegistry<Biome> registry = event.getRegistry();
        ERROR.register(registry);
    }
}

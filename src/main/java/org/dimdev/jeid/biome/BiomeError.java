package org.dimdev.jeid.biome;

import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeVoid;

import net.minecraftforge.fml.common.event.FMLModIdMappingEvent;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistry;
import org.dimdev.jeid.JEID;

import java.util.List;

public class BiomeError extends BiomeVoid {
    public static final String NAME = "error_biome";
    private int id = -1;

    public BiomeError() {
        super(new BiomeProperties("A mod doesn't support extended biome IDs -- report to REID").setRainDisabled());
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public void register(IForgeRegistry<Biome> registry) {
        registry.register(this.setRegistryName(new ResourceLocation(JEID.MODID, NAME)));
        setId(Biome.getIdForBiome(this));
    }

    public void updateMapping(FMLModIdMappingEvent event) {
        List<FMLModIdMappingEvent.ModRemapping> remaps = event.getRemaps(GameData.BIOMES);
        if (remaps == null) return;

        remaps.stream()
                .filter(remap -> remap.key.equals(new ResourceLocation(JEID.MODID, NAME)))
                .findFirst()
                .ifPresent(remap -> setId(remap.newId));
    }
}

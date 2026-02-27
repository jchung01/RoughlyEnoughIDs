package org.dimdev.jeid.util;

import net.minecraft.util.ResourceLocation;

import net.minecraftforge.fml.common.Loader;
import org.dimdev.jeid.JEID;

public enum Mods {
    ADVANCED_ROCKETRY("advancedrocketry"),
    ABYSSALCRAFT("abyssalcraft"),
    ATUM("atum"),
    BIOMES_O_PLENTY("biomesoplenty"),
    BIOME_STAFF("biomestaff"),
    BIOME_TWEAKER("biometweaker"),
    BOOKSHELF("bookshelf"),
    COMPACT_MACHINES("compactmachines3"),
    CREEPING_NETHER("creepingnether"),
    CHISELS_AND_BITS("chiselsandbits"),
    CHUNK_PREGENERATOR("chunkpregenerator"),
    CUBIC_CHUNKS("cubicchunks"),
    CYCLOPS_CORE("cyclopscore"),
    EXTRA_UTILS("extrautils2"),
    GAIA_DIMENSION("gaiadimension"),
    GEOGRAPHICRAFT("geographicraft"),
    HAMMERLIB("hammercore"),
    JOURNEYMAP("journeymap"),
    KATHAIRIS("kathairis"),
    MORE_PLANETS("moreplanets"),
    MYSTCRAFT("mystcraft"),
    NATURES_COMPASS("naturescompass"),
    REALISTIC_TERRAIN_GEN("rtg"),
    SCAPE_RUN_PARASITES("srparasites"),
    THAUMCRAFT("thaumcraft"),
    BETWEENLANDS("thebetweenlands"),
    TOFUCRAFT("tofucraft"),
    TROPICRAFT("tropicraft"),
    TWILIGHT_FOREST("twilightforest"),
    WORLD_EDIT("worldedit"),
    WYRMS_OF_NYRUS("wyrmsofnyrus"),
    ;

    public static final String NEID_MIGRATION_KEY = "migratedNEIDToREID";

    public final String modId;
    private final ResourceLocation registryKey;

    Mods(String modId) {
        this.modId = modId;
        registryKey = new ResourceLocation(JEID.MODID, modId);
    }

    public boolean isLoaded() {
        return Loader.isModLoaded(modId);
    }
}

package org.dimdev.jeid.mixin.core.misc;

import com.llamalad7.mixinextras.sugar.Local;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatCrafting;
import net.minecraft.stats.StatList;
import org.dimdev.jeid.JEID;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Rewrite most of the class to support an unlimited number of IDs (map rather than array).
 **/
@Mixin(value = StatList.class, priority = 500)
public final class MixinStatList {
    @Final
    @Shadow
    public static List<StatBase> ALL_STATS;
    @Final
    @Shadow
    public static List<StatBase> BASIC_STATS;
    @Final
    @Shadow
    public static List<StatCrafting> MINE_BLOCK_STATS;
    // All vanilla arrays are shrunk to size 1 to act as single stat holders,
    // passing to their map equivalents.
    @Final
    @Shadow
    private static StatBase[] BLOCKS_STATS;
    @Final
    @Shadow
    private static StatBase[] OBJECT_USE_STATS;
    @Final
    @Shadow
    private static StatBase[] CRAFTS_STATS;
    @Final
    @Shadow
    private static StatBase[] OBJECT_BREAK_STATS;
    @Final
    @Shadow
    private static StatBase[] OBJECTS_PICKED_UP_STATS;
    @Final
    @Shadow
    private static StatBase[] OBJECTS_DROPPED_STATS;
    @Unique
    private static final Map<Block, StatBase> BLOCKS_STATS_MAP = new Reference2ObjectOpenHashMap<>();
    @Unique
    private static final Map<Item, StatBase> CRAFTS_STATS_MAP = new Reference2ObjectOpenHashMap<>();
    @Unique
    private static final Map<Item, StatBase> OBJECT_USE_STATS_MAP = new Reference2ObjectOpenHashMap<>();
    @Unique
    private static final Map<Item, StatBase> OBJECT_BREAK_STATS_MAP = new Reference2ObjectOpenHashMap<>();
    @Unique
    private static final Map<Item, StatBase> OBJECTS_PICKED_UP_STATS_MAP = new Reference2ObjectOpenHashMap<>();
    @Unique
    private static final Map<Item, StatBase> OBJECTS_DROPPED_STATS_MAP = new Reference2ObjectOpenHashMap<>();

    /**
     * @reason Reduce memory footprint of stat arrays
     */
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 4096))
    private static int reid$shrinkArray(int original) {
        return 1;
    }

    /**
     * @reason Reduce memory footprint of stat arrays
     */
    @ModifyConstant(method = "<clinit>", constant = @Constant(intValue = 32000))
    private static int reid$shrinkArrayShort(int original) {
        return 1;
    }

    // region GET_BLOCK_STATS

    /**
     * @reason Prevent IndexOutOfBoundsException
     */
    @Redirect(method = "getBlockStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/block/Block;getIdFromBlock(Lnet/minecraft/block/Block;)I"))
    private static int reid$defaultBlockStatsIndex(Block blockIn) {
        return 0;
    }

    @Inject(method = "getBlockStats", at = @At(value = "RETURN"), cancellable = true)
    private static void reid$getBlockStatsFromMap(Block block, CallbackInfoReturnable<StatBase> cir) {
        cir.setReturnValue(BLOCKS_STATS_MAP.get(block));
    }
    // endregion

    // region GET_CRAFT_STATS

    /**
     * @reason Prevent IndexOutOfBoundsException
     */
    @Redirect(method = "getCraftStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getIdFromItem(Lnet/minecraft/item/Item;)I"))
    private static int reid$defaultCraftStatsIndex(Item itemIn) {
        return 0;
    }

    @Inject(method = "getCraftStats", at = @At(value = "RETURN"), cancellable = true)
    private static void reid$getCraftStatsFromMap(Item item, CallbackInfoReturnable<StatBase> cir) {
        cir.setReturnValue(CRAFTS_STATS_MAP.get(item));
    }
    // endregion

    // region GET_OBJECT_USE_STATS

    /**
     * @reason Prevent IndexOutOfBoundsException
     */
    @Redirect(method = "getObjectUseStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getIdFromItem(Lnet/minecraft/item/Item;)I"))
    private static int reid$defaultObjectUseStatsIndex(Item itemIn) {
        return 0;
    }

    @Inject(method = "getObjectUseStats", at = @At(value = "RETURN"), cancellable = true)
    private static void reid$getObjectUseStatsFromMap(Item item, CallbackInfoReturnable<StatBase> cir) {
        cir.setReturnValue(OBJECT_USE_STATS_MAP.get(item));
    }
    // endregion

    // region GET_OBJECT_BREAK_STATS

    /**
     * @reason Prevent IndexOutOfBoundsException
     */
    @Redirect(method = "getObjectBreakStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getIdFromItem(Lnet/minecraft/item/Item;)I"))
    private static int reid$defaultObjectBreakStatsIndex(Item itemIn) {
        return 0;
    }

    @Inject(method = "getObjectBreakStats", at = @At(value = "RETURN"), cancellable = true)
    private static void reid$getObjectBreakStatsFromMap(Item item, CallbackInfoReturnable<StatBase> cir) {
        cir.setReturnValue(OBJECT_BREAK_STATS_MAP.get(item));
    }
    // endregion

    // region GET_OBJECTS_PICKED_UP_STATS

    /**
     * @reason Prevent IndexOutOfBoundsException
     */
    @Redirect(method = "getObjectsPickedUpStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getIdFromItem(Lnet/minecraft/item/Item;)I"))
    private static int reid$defaultObjectsPickedUpStatsIndex(Item itemIn) {
        return 0;
    }

    @Inject(method = "getObjectsPickedUpStats", at = @At(value = "RETURN"), cancellable = true)
    private static void reid$getObjectsPickedUpStatsFromMap(Item item, CallbackInfoReturnable<StatBase> cir) {
        cir.setReturnValue(OBJECTS_PICKED_UP_STATS_MAP.get(item));
    }
    // endregion

    // region GET_DROPPED_OBJECT_STATS

    /**
     * @reason Prevent IndexOutOfBoundsException
     */
    @Redirect(method = "getDroppedObjectStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/Item;getIdFromItem(Lnet/minecraft/item/Item;)I"))
    private static int reid$defaultObjectsDroppedStatsIndex(Item itemIn) {
        return 0;
    }

    @Inject(method = "getDroppedObjectStats", at = @At(value = "RETURN"), cancellable = true)
    private static void reid$getDroppedObjectStatsFromMap(Item item, CallbackInfoReturnable<StatBase> cir) {
        cir.setReturnValue(OBJECTS_DROPPED_STATS_MAP.get(item));
    }
    // endregion

    // region MINING STATS
    /**
     * @reason Use default array as single stat holder
     */
    @SuppressWarnings("all")
    @ModifyVariable(method = "initMiningStats", at = @At(value = "STORE", ordinal = 0))
    private static int reid$defaultMiningStatIndex(int index, @Local Block block) {
        if (index != Block.getIdFromBlock(block)) {
            throw new AssertionError(JEID.MODID + " :: Ordinal 0 of STORE int of StatList#initMiningStats isn't \"block id\"");
        }
        return 0;
    }

    /**
     * @reason Add to MINE_BLOCK_STATS (vanilla) and BLOCKS_STATS_MAP
     */
    @ModifyArg(method = "initMiningStats", at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", remap = false), index = 0)
    private static Object reid$addMiningStat(Object original, @Local Block key) {
        BLOCKS_STATS_MAP.put(key, (StatCrafting) original);
        return original;
    }

    @Redirect(method = "initMiningStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/StatList;replaceAllSimilarBlocks([Lnet/minecraft/stats/StatBase;Z)V", remap = false))
    private static void reid$replaceSimilarMiningBlocks(StatBase[] stat, boolean useItemIds) {
        replaceAllSimilarBlocks(BLOCKS_STATS_MAP);
    }
    // endregion

    // region USE STATS
    /**
     * @reason Use default array as single stat holder
     */
    @SuppressWarnings("all")
    @ModifyVariable(method = "initStats", at = @At(value = "STORE", ordinal = 0))
    private static int reid$defaultUseStatIndex(int index, @Local Item item) {
        if (index != Item.getIdFromItem(item)) {
            throw new AssertionError(JEID.MODID + " :: Ordinal 0 of STORE int of StatList#initStats isn't \"item id\"");
        }
        return 0;
    }

    /**
     * @reason Add to OBJECT_USE_STATS_MAP
     */
    @Redirect(method = "initStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/StatCrafting;registerStat()Lnet/minecraft/stats/StatBase;"))
    private static StatBase reid$addUseStat(StatCrafting instance, @Local Item item) {
        // Don't actually redirect
        instance.registerStat();
        OBJECT_USE_STATS_MAP.put(item, instance);
        return instance;
    }

    @Redirect(method = "initStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/StatList;replaceAllSimilarBlocks([Lnet/minecraft/stats/StatBase;Z)V", remap = false))
    private static void reid$replaceSimilarUseBlocks(StatBase[] stat, boolean useItemIds) {
        replaceAllSimilarBlocksItem(OBJECT_USE_STATS_MAP);
    }
    // endregion

    // region CRAFTABLE STATS

    /**
     * @reason Use default array as single stat holder
     */
    @SuppressWarnings("all")
    @ModifyVariable(method = "initCraftableStats", at = @At(value = "STORE", ordinal = 0))
    private static int reid$defaultCraftIndex(int index, @Local Item item) {
        if (index != Item.getIdFromItem(item)) {
            throw new AssertionError(JEID.MODID + " :: Ordinal 0 of STORE int of StatList#initCraftableStats isn't \"item id\"");
        }
        return 0;
    }

    /**
     * @reason Add to CRAFTS_STATS_MAP
     */
    @Redirect(method = "initCraftableStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/StatCrafting;registerStat()Lnet/minecraft/stats/StatBase;"))
    private static StatBase reid$addCraftStat(StatCrafting instance, @Local Item item) {
        // Don't actually redirect
        instance.registerStat();
        CRAFTS_STATS_MAP.put(item, instance);
        return instance;
    }

    @Redirect(method = "initCraftableStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/StatList;replaceAllSimilarBlocks([Lnet/minecraft/stats/StatBase;Z)V", remap = false))
    private static void reid$replaceSimilarCraftBlocks(StatBase[] stat, boolean useItemIds) {
        replaceAllSimilarBlocksItem(CRAFTS_STATS_MAP);
    }
    // endregion

    // region BREAK STATS

    /**
     * @reason Use default array as single stat holder
     */
    @SuppressWarnings("all")
    @ModifyVariable(method = "initItemDepleteStats", at = @At(value = "STORE", ordinal = 0))
    private static int reid$defaultBreakIndex(int index, @Local Item item) {
        if (index != Item.getIdFromItem(item)) {
            throw new AssertionError(JEID.MODID + " :: Ordinal 0 of STORE int of StatList#initItemDepleteStats isn't \"item id\"");
        }
        return 0;
    }

    /**
     * @reason Add to OBJECT_BREAK_STATS_MAP
     */
    @Redirect(method = "initItemDepleteStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/StatCrafting;registerStat()Lnet/minecraft/stats/StatBase;"))
    private static StatBase reid$addBreakStat(StatCrafting instance, @Local Item item) {
        // Don't actually redirect
        instance.registerStat();
        OBJECT_BREAK_STATS_MAP.put(item, instance);
        return instance;
    }

    @Redirect(method = "initItemDepleteStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/StatList;replaceAllSimilarBlocks([Lnet/minecraft/stats/StatBase;Z)V", remap = false))
    private static void reid$replaceSimilarBreakBlocks(StatBase[] stat, boolean useItemIds) {
        replaceAllSimilarBlocksItem(OBJECT_BREAK_STATS_MAP);
    }
    // endregion

    // region PICKED UP/DROPPED STATS

    /**
     * @reason Use default arrays as single stat holders
     */
    @SuppressWarnings("all")
    @ModifyVariable(method = "initPickedUpAndDroppedStats", at = @At(value = "STORE", ordinal = 0))
    private static int reid$defaultDropIndex(int index, @Local Item item) {
        if (index != Item.getIdFromItem(item)) {
            throw new AssertionError(JEID.MODID + " :: Ordinal 0 of STORE int of StatList#initPickedUpAndDroppedStats isn't \"item id\"");
        }
        return 0;
    }

    /**
     * @reason Add to OBJECTS_PICKED_UP_STATS_MAP
     */
    @Redirect(method = "initPickedUpAndDroppedStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/StatCrafting;registerStat()Lnet/minecraft/stats/StatBase;", ordinal = 0))
    private static StatBase reid$addPickupStat(StatCrafting instance, @Local Item item) {
        // Don't actually redirect
        instance.registerStat();
        OBJECTS_PICKED_UP_STATS_MAP.put(item, instance);
        return instance;
    }

    /**
     * @reason Add to OBJECTS_DROPPED_STATS_MAP
     */
    @Redirect(method = "initPickedUpAndDroppedStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/StatCrafting;registerStat()Lnet/minecraft/stats/StatBase;", ordinal = 1))
    private static StatBase reid$addDropStat(StatCrafting instance, @Local Item item) {
        // Don't actually redirect
        instance.registerStat();
        OBJECTS_DROPPED_STATS_MAP.put(item, instance);
        return instance;
    }

    @Redirect(method = "initPickedUpAndDroppedStats", at = @At(value = "INVOKE", target = "Lnet/minecraft/stats/StatList;replaceAllSimilarBlocks([Lnet/minecraft/stats/StatBase;Z)V", remap = false))
    private static void reid$replaceSimilarPickupDropBlocks(StatBase[] stat, boolean useItemIds) {
        replaceAllSimilarBlocksItem(OBJECT_BREAK_STATS_MAP);
    }
    // endregion

    @Inject(method = "reinit", at = @At(value = "HEAD"), remap = false)
    private static void reid$clearArrays(CallbackInfo ci) {
        BLOCKS_STATS[0] = null;
        OBJECT_USE_STATS[0] = null;
        CRAFTS_STATS[0] = null;
        OBJECT_BREAK_STATS[0] = null;
        OBJECTS_PICKED_UP_STATS[0] = null;
        OBJECTS_DROPPED_STATS[0] = null;
    }

    @Redirect(method = "reinit", at = @At(value = "INVOKE", target = "Lcom/google/common/collect/Lists;newArrayList(Ljava/lang/Iterable;)Ljava/util/ArrayList;"), remap = false)
    private static ArrayList<StatBase> reid$getUnknownStats(Iterable<StatBase> allStats) {
        int requiredCapacity = BLOCKS_STATS_MAP.size() + CRAFTS_STATS_MAP.size()
                + OBJECT_USE_STATS_MAP.size() + OBJECT_BREAK_STATS_MAP.size()
                + OBJECTS_PICKED_UP_STATS_MAP.size() + OBJECTS_DROPPED_STATS_MAP.size();
        Set<StatBase> knownStats = new ObjectOpenHashSet<>(requiredCapacity + 1);
        knownStats.addAll(BLOCKS_STATS_MAP.values());
        knownStats.addAll(CRAFTS_STATS_MAP.values());
        knownStats.addAll(OBJECT_USE_STATS_MAP.values());
        knownStats.addAll(OBJECT_BREAK_STATS_MAP.values());
        knownStats.addAll(OBJECTS_PICKED_UP_STATS_MAP.values());
        knownStats.addAll(OBJECTS_DROPPED_STATS_MAP.values());
        ArrayList<StatBase> unknownStats = new ArrayList<>();
        for (StatBase stat : allStats) {
            if (!knownStats.contains(stat)) {
                unknownStats.add(stat);
            }
        }
        BLOCKS_STATS_MAP.clear();
        CRAFTS_STATS_MAP.clear();
        OBJECT_USE_STATS_MAP.clear();
        OBJECT_BREAK_STATS_MAP.clear();
        OBJECTS_PICKED_UP_STATS_MAP.clear();
        OBJECTS_DROPPED_STATS_MAP.clear();
        return unknownStats;
    }

    @SuppressWarnings("SameParameterValue")
    @Unique
    private static void replaceAllSimilarBlocks(Map<Block, StatBase> stat) {
        mergeStatBasesBlock(stat, Blocks.WATER, Blocks.FLOWING_WATER);
        mergeStatBasesBlock(stat, Blocks.LAVA, Blocks.FLOWING_LAVA);
        mergeStatBasesBlock(stat, Blocks.LIT_PUMPKIN, Blocks.PUMPKIN);
        mergeStatBasesBlock(stat, Blocks.LIT_FURNACE, Blocks.FURNACE);
        mergeStatBasesBlock(stat, Blocks.LIT_REDSTONE_ORE, Blocks.REDSTONE_ORE);
        mergeStatBasesBlock(stat, Blocks.POWERED_REPEATER, Blocks.UNPOWERED_REPEATER);
        mergeStatBasesBlock(stat, Blocks.POWERED_COMPARATOR, Blocks.UNPOWERED_COMPARATOR);
        mergeStatBasesBlock(stat, Blocks.REDSTONE_TORCH, Blocks.UNLIT_REDSTONE_TORCH);
        mergeStatBasesBlock(stat, Blocks.LIT_REDSTONE_LAMP, Blocks.REDSTONE_LAMP);
        mergeStatBasesBlock(stat, Blocks.DOUBLE_STONE_SLAB, Blocks.STONE_SLAB);
        mergeStatBasesBlock(stat, Blocks.DOUBLE_WOODEN_SLAB, Blocks.WOODEN_SLAB);
        mergeStatBasesBlock(stat, Blocks.DOUBLE_STONE_SLAB2, Blocks.STONE_SLAB2);
        mergeStatBasesBlock(stat, Blocks.GRASS, Blocks.DIRT);
        mergeStatBasesBlock(stat, Blocks.FARMLAND, Blocks.DIRT);
    }

    @Unique
    private static void replaceAllSimilarBlocksItem(Map<Item, StatBase> stat) {
        mergeStatBasesItem(stat, Blocks.WATER, Blocks.FLOWING_WATER);
        mergeStatBasesItem(stat, Blocks.LAVA, Blocks.FLOWING_LAVA);
        mergeStatBasesItem(stat, Blocks.LIT_PUMPKIN, Blocks.PUMPKIN);
        mergeStatBasesItem(stat, Blocks.LIT_FURNACE, Blocks.FURNACE);
        mergeStatBasesItem(stat, Blocks.LIT_REDSTONE_ORE, Blocks.REDSTONE_ORE);
        mergeStatBasesItem(stat, Blocks.POWERED_REPEATER, Blocks.UNPOWERED_REPEATER);
        mergeStatBasesItem(stat, Blocks.POWERED_COMPARATOR, Blocks.UNPOWERED_COMPARATOR);
        mergeStatBasesItem(stat, Blocks.REDSTONE_TORCH, Blocks.UNLIT_REDSTONE_TORCH);
        mergeStatBasesItem(stat, Blocks.LIT_REDSTONE_LAMP, Blocks.REDSTONE_LAMP);
        mergeStatBasesItem(stat, Blocks.DOUBLE_STONE_SLAB, Blocks.STONE_SLAB);
        mergeStatBasesItem(stat, Blocks.DOUBLE_WOODEN_SLAB, Blocks.WOODEN_SLAB);
        mergeStatBasesItem(stat, Blocks.DOUBLE_STONE_SLAB2, Blocks.STONE_SLAB2);
        mergeStatBasesItem(stat, Blocks.GRASS, Blocks.DIRT);
        mergeStatBasesItem(stat, Blocks.FARMLAND, Blocks.DIRT);
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Unique
    private static void mergeStatBasesBlock(Map<Block, StatBase> statBaseIn, Block block1, Block block2) {
        StatBase stat1 = statBaseIn.get(block1);
        StatBase stat2 = statBaseIn.get(block2);
        if (stat1 != null && stat2 == null) {
            statBaseIn.put(block2, stat1);
        } else {
            ALL_STATS.remove(stat1);
            BASIC_STATS.remove(stat1);
            MINE_BLOCK_STATS.remove(stat1);
            statBaseIn.put(block1, statBaseIn.get(block2));
        }
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    @Unique
    private static void mergeStatBasesItem(Map<Item, StatBase> statBaseIn, Block block1, Block block2) {
        Item item1 = Item.getItemFromBlock(block1);
        Item item2 = Item.getItemFromBlock(block2);
        StatBase stat1 = statBaseIn.get(item1);
        StatBase stat2 = statBaseIn.get(item2);
        if (stat1 != null && stat2 == null) {
            statBaseIn.put(item2, statBaseIn.get(item1));
        } else {
            ALL_STATS.remove(stat1);
            BASIC_STATS.remove(stat1);
            MINE_BLOCK_STATS.remove(stat1);
            statBaseIn.put(item1, statBaseIn.get(item2));
        }
    }
}

package org.dimdev.jeid.mixin.modsupport.chiselsandbits;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByte;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import mod.chiselsandbits.chiseledblock.BlockChiseled;
import mod.chiselsandbits.chiseledblock.NBTBlobConverter;
import mod.chiselsandbits.chiseledblock.TileEntityBlockChiseled;
import mod.chiselsandbits.chiseledblock.data.VoxelBlob;
import mod.chiselsandbits.chiseledblock.data.VoxelBlobStateInstance;
import mod.chiselsandbits.chiseledblock.data.VoxelBlobStateReference;
import mod.chiselsandbits.helpers.ModUtil;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.util.Constants;
import org.dimdev.jeid.JEIDLogger;
import org.dimdev.jeid.util.Mods;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;
import java.util.function.IntUnaryOperator;

@Mixin(value = TileEntityBlockChiseled.class, remap = false)
public abstract class MixinTileEntityBlockChiseled extends TileEntity {
    @Unique
    private static final String reid$CONVERTED_STATE_ID = "rc";

    @Unique
    private boolean reid$converted = false;
    @Unique
    private boolean reid$isCrossworld;

    @Shadow
    public abstract IExtendedBlockState getBasicState();

    @Shadow
    public abstract void setState(IExtendedBlockState state);

    @Override
    public void onLoad() {
        // Cross-world format uses strings, which are stable
        if (reid$isCrossworld) return;
        // Only convert once, on server-side
        if (world.isRemote || reid$converted) return;

        IExtendedBlockState oldState = getBasicState();
        VoxelBlobStateReference oldRef = oldState.getValue(BlockChiseled.UProperty_VoxelBlob);
        Integer oldId = oldState.getValue(BlockChiseled.UProperty_Primary_BlockState);
        if (oldRef == null || oldId == null) return;

        int newId;
        VoxelBlobStateReference newRef;
        NBTBase worldSavedWithNEID = world.getWorldInfo().getAdditionalProperty(Mods.NEID_MIGRATION_KEY);
        if (Objects.equals(worldSavedWithNEID, new NBTTagByte((byte) 1))) {
            newId = reid$convertNEIDStateId(oldId);
            // Shouldn't happen, but try fallback
            if (newId == 0) {
                newId = reid$convertStateId(oldId);
            }
            newRef = reid$recreateBlob(oldRef, MixinTileEntityBlockChiseled::reid$convertNEIDStateId);
        }
        else {
            newId = reid$convertStateId(oldId);
            newRef = reid$recreateBlob(oldRef, MixinTileEntityBlockChiseled::reid$convertStateId);
        }

        // Something went wrong with the conversion, fallback to cobblestone
        if (newId == 0) {
            JEIDLogger.LOGGER.warn("Failed to convert chiseled block at ({}, {}, {}) - old id {}",
                    getPos().getX(), getPos().getY(), getPos().getZ(), oldId);
            newId = ModUtil.getStateId(Blocks.COBBLESTONE.getDefaultState());
        }
        // Set the new state
        if (newId != oldId || newRef != oldRef) {
            IExtendedBlockState newState = getBasicState().withProperty(BlockChiseled.UProperty_Primary_BlockState, newId)
                    .withProperty(BlockChiseled.UProperty_VoxelBlob, newRef);
            setState(newState);
        }
        reid$converted = true;
    }

    @Unique
    private static int reid$convertNEIDStateId(int oldId) {
        // 4-bit meta + 16-bit ID
        int i = oldId & 65535;
        int j = oldId >> 16 & 15;
        IBlockState state = Block.getBlockById(i).getStateFromMeta(j);
        return Block.getStateId(state);
    }

    @Unique
    private static int reid$convertStateId(int oldId) {
        IBlockState state = Block.getStateById(oldId);
        return Block.getStateId(state);
    }

    @Unique
    private VoxelBlobStateReference reid$recreateBlob(VoxelBlobStateReference blobRef, IntUnaryOperator converter) {
        VoxelBlobStateInstance blobInstance = blobRef.getInstance();
        // Clear cached blob
        VoxelBlobStateReferenceAccessor.reid$getRefs().remove(blobInstance);

        // Convert blob ids
        VoxelBlob blob = blobInstance.getBlob();
        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; y++) {
                for (int z = 0; z < 16; z++) {
                    int newId = converter.applyAsInt(blob.get(x, y, z));
                    blob.set(x, y, z, newId);
                }
            }
        }
        // Write new blob
        byte[] voxelBytes = blob.blobToBytes(0);
        return new VoxelBlobStateReference(voxelBytes, blobRef.weight);
    }

    @Inject(method = "readChisleData", at = @At("RETURN"))
    private void reid$checkConversionConditions(NBTTagCompound compound, CallbackInfoReturnable<Boolean> cir) {
        reid$isCrossworld = compound.hasKey(NBTBlobConverter.NBT_PRIMARY_STATE, Constants.NBT.TAG_STRING);
        reid$converted = compound.getBoolean(reid$CONVERTED_STATE_ID);
    }

    @Inject(method = "writeToNBT", at = @At("RETURN"))
    private void reid$markConverted(NBTTagCompound compound, CallbackInfoReturnable<NBTTagCompound> cir) {
        compound.setBoolean(reid$CONVERTED_STATE_ID, true);
    }
}

package com.enderio.machines.common.blockentity;

import com.enderio.base.EIONBTKeys;
import com.enderio.base.common.blockentity.IPaintableBlockEntity;
import com.enderio.base.common.blockentity.SinglePaintedBlockEntity;
import com.enderio.base.common.util.PaintUtils;
import com.enderio.machines.common.init.MachineBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class PaintedTravelAnchorBlockEntity extends TravelAnchorBlockEntity implements IPaintableBlockEntity {

    @Nullable
    private Block paint;

    @Nullable
    public Block getPaint() {
        return paint;
    }

    public PaintedTravelAnchorBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(MachineBlockEntities.PAINTED_TRAVEL_ANCHOR.get(), pWorldPosition, pBlockState);
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(SinglePaintedBlockEntity.PAINT, paint).build();
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        Block oldPaint = paint;
        CompoundTag tag = pkt.getTag();
        if (tag == null) {
            return;
        }

        handleUpdateTag(tag);
        if (oldPaint != paint) {
            requestModelDataUpdate();
            if (level != null) {
                level.setBlock(getBlockPos(), level.getBlockState(getBlockPos()), 9);
            }
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag syncData) {
        readPaint(syncData);
        super.handleUpdateTag(syncData);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        readPaint(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        writePaint(nbt);
        return nbt;
    }

    // TODO: HOUSEKEEPING?: This should probably be converted to a capability.
    protected void readPaint(CompoundTag tag) {
        if (tag.contains(EIONBTKeys.PAINT)) {
            paint = PaintUtils.getBlockFromRL(tag.getString(EIONBTKeys.PAINT));
            if (level != null) {
                if (level.isClientSide) {
                    requestModelDataUpdate();
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(),
                        Block.UPDATE_NEIGHBORS + Block.UPDATE_CLIENTS);
                }
            }
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        writePaint(tag);
    }

    protected void writePaint(CompoundTag tag) {
        if (paint != null) {
            tag.putString(EIONBTKeys.PAINT, Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(paint)).toString());
        }
    }
}

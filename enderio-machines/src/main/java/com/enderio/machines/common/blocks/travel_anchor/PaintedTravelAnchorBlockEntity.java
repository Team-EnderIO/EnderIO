package com.enderio.machines.common.blocks.travel_anchor;

import com.enderio.base.EIONBTKeys;
import com.enderio.base.common.paint.PaintUtils;
import com.enderio.base.common.paint.blockentity.PaintedBlockEntity;
import com.enderio.base.common.paint.blockentity.SinglePaintedBlockEntity;
import com.enderio.machines.common.init.MachineBlockEntities;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

public class PaintedTravelAnchorBlockEntity extends TravelAnchorBlockEntity implements PaintedBlockEntity {

    @Nullable
    private Block paint;

    public PaintedTravelAnchorBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(MachineBlockEntities.PAINTED_TRAVEL_ANCHOR.get(), pWorldPosition, pBlockState);
    }

    @Override
    public Optional<Block> getPrimaryPaint() {
        return Optional.ofNullable(paint);
    }

    @Override
    public void setPrimaryPaint(Block paint) {
        this.paint = paint;
        setChanged();
    }

    @SuppressWarnings("DataFlowIssue")
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
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt,
            HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
        Block oldPaint = paint;
        CompoundTag tag = pkt.getTag();

        handleUpdateTag(tag, lookupProvider);
        if (oldPaint != paint) {
            requestModelDataUpdate();
            if (level != null) {
                level.setBlock(getBlockPos(), level.getBlockState(getBlockPos()), 9);
            }
        }
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(tag, lookupProvider);
        readPaint(tag);
    }

    @Override
    public void handleUpdateTag(CompoundTag syncData, HolderLookup.Provider lookupProvider) {
        super.handleUpdateTag(syncData, lookupProvider);
        readPaint(syncData);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
        CompoundTag nbt = super.getUpdateTag(lookupProvider);
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
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(tag, lookupProvider);
        writePaint(tag);
    }

    protected void writePaint(CompoundTag tag) {
        if (paint != null) {
            tag.putString(EIONBTKeys.PAINT, Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(paint)).toString());
        }
    }
}

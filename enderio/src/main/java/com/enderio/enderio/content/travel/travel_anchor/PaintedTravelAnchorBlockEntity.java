package com.enderio.enderio.content.travel.travel_anchor;

import com.enderio.enderio.content.paint.PaintUtils;
import com.enderio.enderio.content.paint.block.entity.PaintedBlockEntity;
import com.enderio.enderio.content.paint.block.entity.SinglePaintedBlockEntity;
import com.enderio.enderio.foundation.EIONBTKeys;
import com.enderio.enderio.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class PaintedTravelAnchorBlockEntity extends TravelAnchorBlockEntity implements PaintedBlockEntity {

    @Nullable
    private Block paint;

    public PaintedTravelAnchorBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.PAINTED_TRAVEL_ANCHOR.get(), worldPosition, blockState);
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
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        Block oldPaint = paint;
        CompoundTag tag = pkt.getTag();

        handleUpdateTag(tag);
        if (oldPaint != paint) {
            requestModelDataUpdate();
            if (level != null) {
                level.setBlock(getBlockPos(), level.getBlockState(getBlockPos()), 9);
            }
        }
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        readPaint(tag);
    }

    @Override
    public void handleUpdateTag(CompoundTag syncData) {
        super.handleUpdateTag(syncData);
        readPaint(syncData);
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

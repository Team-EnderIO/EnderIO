package com.enderio.base.common.paint.blockentity;

import com.enderio.base.EIONBTKeys;
import com.enderio.base.common.init.EIOBlockEntities;
import com.enderio.base.common.paint.PaintUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class SinglePaintedBlockEntity extends BlockEntity implements PaintedBlockEntity {

    @Nullable
    protected Block paint;

    public static final ModelProperty<Block> PAINT = PaintedBlockEntity.createAndRegisterModelProperty();

    public SinglePaintedBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(EIOBlockEntities.SINGLE_PAINTED.get(), pWorldPosition, pBlockState);
    }

    protected SinglePaintedBlockEntity(BlockEntityType<?> blockEntityType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(blockEntityType, pWorldPosition, pBlockState);
    }

    public void setPrimaryPaint(@Nullable Block paint) {
        this.paint = paint;
        setChanged();
    }

    @Override
    public Optional<Block> getPrimaryPaint() {
        return Optional.ofNullable(paint);
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public ModelData getModelData() {
        return ModelData.builder()
            .with(PAINT, paint)
            .build();
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
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
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(tag, lookupProvider);
        readPaint(tag);
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
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(tag, lookupProvider);
        writePaint(tag);
    }

    protected void writePaint(CompoundTag tag) {
        if (paint != null) {
            tag.putString(EIONBTKeys.PAINT, Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(paint)).toString());
        }
    }
}

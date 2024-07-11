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
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class DoublePaintedBlockEntity extends SinglePaintedBlockEntity {

    @Nullable
    private Block paint2;

    public static final ModelProperty<Block> PAINT2 = PaintedBlockEntity.createAndRegisterModelProperty();

    public DoublePaintedBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(EIOBlockEntities.DOUBLE_PAINTED.get(), pWorldPosition, pBlockState);
    }

    @Override
    public boolean hasSecondaryPaint() {
        return true;
    }

    @Override
    public Optional<Block> getSecondaryPaint() {
        return Optional.ofNullable(paint2);
    }

    public void setSecondaryPaint(@Nullable Block paint) {
        this.paint2 = paint;
        setChanged();
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    public ModelData getModelData() {
        return ModelData.builder()
            .with(PAINT, paint)
            .with(PAINT2, paint2)
            .build();
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        Block oldPaint = getSecondaryPaint().orElse(null);
        super.onDataPacket(net, pkt, lookupProvider);

        if (oldPaint != paint2) {
            requestModelDataUpdate();
            if (level != null) {
                level.setBlock(getBlockPos(), level.getBlockState(getBlockPos()), 9);
            }
        }
    }

    @Override
    protected void readPaint(CompoundTag tag) {
        super.readPaint(tag);

        if (tag.contains(EIONBTKeys.PAINT_2)) {
            paint2 = PaintUtils.getBlockFromRL(tag.getString(EIONBTKeys.PAINT_2));
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
    protected void writePaint(CompoundTag tag) {
        super.writePaint(tag);

        if (paint2 != null) {
            tag.putString(EIONBTKeys.PAINT_2, Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(paint2)).toString());
        }
    }
}

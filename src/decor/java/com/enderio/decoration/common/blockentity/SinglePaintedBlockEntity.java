package com.enderio.decoration.common.blockentity;

import com.enderio.decoration.common.util.PaintUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public class SinglePaintedBlockEntity extends BlockEntity implements IPaintableBlockEntity {

    private Block paint;

    public Block getPaint() {
        return paint;
    }

    public static final ModelProperty<Block> PAINT = new ModelProperty<>();

    public SinglePaintedBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    @Nonnull
    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(PAINT, paint).build();
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
        handleUpdateTag(tag);
        if (oldPaint != paint) {
            requestModelDataUpdate();
            if (level != null) {
                level.setBlock(getBlockPos(), level.getBlockState(getBlockPos()), 9);
            }
        }
    }

    @Override
    public void load(@Nonnull CompoundTag tag) {
        super.load(tag);
        readPaint(tag);
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        writePaint(nbt);
        return nbt;
    }

    // TODO: HOUSEKEEPING?: This should probably be converted to a capability.
    protected void readPaint(CompoundTag tag) {
        if (tag.contains("paint")) {
            paint = PaintUtils.getBlockFromRL(tag.getString("paint"));
            if (level != null) {
                if (level.isClientSide) {
                    requestModelDataUpdate();
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(),
                        Block.UPDATE_NEIGHBORS + Block.UPDATE_CLIENTS);
                }
            }
        }
    }

    @Nonnull
    @Override
    public void saveAdditional(@Nonnull CompoundTag tag) {
        super.saveAdditional(tag);
        writePaint(tag);
    }

    protected void writePaint(CompoundTag tag) {
        if (paint != null) {
            tag.putString("paint", Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(paint)).toString());
        }
    }
}

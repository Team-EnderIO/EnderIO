package com.enderio.base.common.blockentity;

import com.enderio.base.EIONBTKeys;
import com.enderio.base.common.util.PaintUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class DoublePaintedBlockEntity extends SinglePaintedBlockEntity {

    @Nullable
    private Block paint2;

    @Nullable
    public Block getPaint2() {
        return paint2;
    }

    @Override
    public Block[] getPaints() {
        return new Block[] { getPaint(), getPaint2() };
    }

    public static final ModelProperty<Block> PAINT2 = IPaintableBlockEntity.createAndRegisterModelProperty();

    public DoublePaintedBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    @Override
    public ModelData getModelData() {
        return ModelData.builder().with(PAINT, getPaint()).with(PAINT2, paint2).build();
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        Block oldPaint = getPaint2();
        super.onDataPacket(net, pkt);
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
            tag.putString(EIONBTKeys.PAINT_2, Objects.requireNonNull(ForgeRegistries.BLOCKS.getKey(paint2)).toString());
        }
    }
}

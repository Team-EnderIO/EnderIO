package com.enderio.enderio.content.paint.block.entity;

import com.enderio.enderio.content.paint.PaintUtils;
import com.enderio.enderio.foundation.EIONBTKeys;
import com.enderio.enderio.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.model.data.ModelData;
import net.neoforged.neoforge.model.data.ModelProperty;
import org.jspecify.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class DoublePaintedBlockEntity extends SinglePaintedBlockEntity {

    @Nullable
    private Block paint2;

    public static final ModelProperty<Block> PAINT2 = PaintedBlockEntity.createAndRegisterModelProperty();

    public DoublePaintedBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.DOUBLE_PAINTED.get(), worldPosition, blockState);
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
    public void onDataPacket(Connection net, ValueInput valueInput) {
        Block oldPaint = getSecondaryPaint().orElse(null);
        super.onDataPacket(net, valueInput);

        if (oldPaint != paint2) {
            requestModelDataUpdate();
            if (level != null) {
                level.setBlock(getBlockPos(), level.getBlockState(getBlockPos()), 9);
            }
        }
    }

    @Override
    protected void readPaint(ValueInput input) {
        super.readPaint(input);

        input.read(EIONBTKeys.PAINT_2, Identifier.CODEC).ifPresent(rl -> {
            paint2 = PaintUtils.getBlockFromRL(rl);
            if (level != null && level.isClientSide()) {
                requestModelDataUpdate();
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(),
                    Block.UPDATE_NEIGHBORS + Block.UPDATE_CLIENTS);
            }
        });


    }

    @Override
    protected void writePaint(ValueOutput output) {
        super.writePaint(output);

        if (paint2 != null) {
            output.store(EIONBTKeys.PAINT_2, Identifier.CODEC, BuiltInRegistries.BLOCK.getKey(this.paint2));
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

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
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Optional;

public class PaintedTravelAnchorBlockEntity extends TravelAnchorBlockEntity implements PaintedBlockEntity {

    @Nullable
    private Block paint;

    public PaintedTravelAnchorBlockEntity(BlockPos pWorldPosition, BlockState pBlockState) {
        super(EIOBlockEntities.PAINTED_TRAVEL_ANCHOR.get(), pWorldPosition, pBlockState);
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
    public void onDataPacket(Connection net, ValueInput valueInput) {
        super.onDataPacket(net, valueInput);
        Block oldPaint = paint;

        handleUpdateTag(valueInput);
        if (oldPaint != paint) {
            requestModelDataUpdate();
            if (level != null) {
                level.setBlock(getBlockPos(), level.getBlockState(getBlockPos()), 9);
            }
        }
    }

    @Override
    public void handleUpdateTag(ValueInput input) {
        super.handleUpdateTag(input);
        readPaint(input);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
        CompoundTag nbt = super.getUpdateTag(lookupProvider);
        writePaint(nbt);
        return nbt;
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        readPaint(input);
    }

    // TODO: HOUSEKEEPING?: This should probably be converted to a capability.
    protected void readPaint(ValueInput input) {
        input.read(EIONBTKeys.PAINT, Identifier.CODEC).ifPresent(rl -> {
            paint = PaintUtils.getBlockFromRL(rl);
            if (level != null && level.isClientSide()) {
                requestModelDataUpdate();
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(),
                    Block.UPDATE_NEIGHBORS + Block.UPDATE_CLIENTS);
            }
        });
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        writePaint(output);
    }

    protected void writePaint(ValueOutput output) {
        if (paint != null) {
            output.store(EIONBTKeys.PAINT, Identifier.CODEC, BuiltInRegistries.BLOCK.getKey(this.paint));
        }
    }

    //TODO why is tag still a thing?
    protected void writePaint(CompoundTag tag) {
        if (paint != null) {
            tag.putString(EIONBTKeys.PAINT, Objects.requireNonNull(BuiltInRegistries.BLOCK.getKey(paint)).toString());
        }
    }
}

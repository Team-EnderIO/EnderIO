package com.enderio.enderio.content.machines.capacitor_bank;

import com.enderio.core.common.graph.INetworkNode;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CapacitorBankNode implements INetworkNode<CapacitorBankNetwork, CapacitorBankNode> {

    public static final Codec<CapacitorBankNode> CODEC = RecordCodecBuilder.create(i ->
        i.group(BlockPos.CODEC.fieldOf("pos").forGetter(CapacitorBankNode::getPos),
                Codec.LONG.fieldOf("capacity").forGetter(CapacitorBankNode::getCapacity))
            .apply(i, CapacitorBankNode::new)
    );

    @Nullable
    private CapacitorBankBlockEntity blockEntity;
    private CapacitorBankNetwork network;
    private BlockPos pos;

    //Used to for unloaded nodes
    private long savedCapacity;

    public CapacitorBankNode(CapacitorBankBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.savedCapacity = blockEntity.getTier().getStorageCapacity();
        this.pos = blockEntity.getBlockPos();
        this.network = new CapacitorBankNetwork(this);
    }

    public CapacitorBankNode(BlockPos pos, long savedCapacity) {
        this.pos = pos;
        this.savedCapacity = savedCapacity;
    }

    public void attach(CapacitorBankBlockEntity blockEntity) {
        this.blockEntity = blockEntity;
        this.pos = blockEntity.getBlockPos();
        this.getNetwork().init(this);
        this.savedCapacity = blockEntity.getTier().getStorageCapacity();
    }

    public int getMaxEnergyStored() {
        return blockEntity.getTier().getStorageCapacity();
    }

    @Nullable
    public CapacitorBankBlockEntity getBlockEntity() {
        return blockEntity;
    }

    public boolean hasBlockEntity() {
        return blockEntity != null;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    protected long getCapacity() {
        return savedCapacity;
    }

    public void markDirty() {
        if (hasBlockEntity()) {
            blockEntity.setChanged();
            blockEntity.getLevel().sendBlockUpdated(blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity.getBlockState(), Block.UPDATE_ALL);
        }
    }

    @Override
    public boolean isValid() {
        return network != null;
    }

    @Override
    public CapacitorBankNetwork getNetwork() {
        return Objects.requireNonNull(network);
    }

    @Override
    public void setNetwork(@Nullable CapacitorBankNetwork network) {
        this.network = network;
    }
}

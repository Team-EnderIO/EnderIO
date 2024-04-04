package com.enderio.conduits.common.integrations.refinedstorage;

import com.enderio.EnderIO;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.INetworkNodeVisitor;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class RSNetworkNode implements INetworkNode, INetworkNodeVisitor {

    public static final ResourceLocation ID = EnderIO.loc("rs_conduit");
    private final Level level;
    private final BlockPos pos;
    private INetwork network;
    private UUID owner;

    public RSNetworkNode(Level level, BlockPos pos) {
        this.level = level;
        this.pos = pos;
    }

    @NotNull
    @Override
    public ItemStack getItemStack() {
        return new ItemStack(RSIntegration.NORMAL_ITEM.get());
    }

    @Override
    public void onConnected(INetwork network) {
        this.network = network;
    }

    @Override
    public void onDisconnected(INetwork network) {
        this.network = null;
    }

    @Override
    public boolean isActive() {
        return true;
    }

    @Nullable
    @Override
    public INetwork getNetwork() {
        return network;
    }

    @Override
    public void update() {

    }

    @Override
    public CompoundTag write(CompoundTag compoundTag) {
        if (owner != null)
            compoundTag.putUUID("owner", owner);
        return compoundTag;
    }

    public void read(CompoundTag compoundTag) {
        if (compoundTag.contains("owner"))
            owner = compoundTag.getUUID("owner");
    }

    @Override
    public BlockPos getPos() {
        return pos;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public void markDirty() {
        if (!level.isClientSide())
            RSNodeHost.RSAPI.getNetworkNodeManager((ServerLevel) level).markForSaving();
    }

    @Override
    public int getEnergyUsage() {
        return RS.SERVER_CONFIG.getCable().getUsage();
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Nullable
    @Override
    public UUID getOwner() {
        return owner;
    }

    @Override
    public void setOwner(@Nullable UUID uuid) {
        this.owner = uuid;
    }

    @Override
    public void visit(Operator operator) {
        for (Direction value : Direction.values()) {
            if (canConduct(value)) {
                operator.apply(level, pos.relative(value), value);
            }
        }
    }
}

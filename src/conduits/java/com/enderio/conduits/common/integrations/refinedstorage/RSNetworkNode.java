package com.enderio.conduits.common.integrations.refinedstorage;

import com.enderio.EnderIO;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RSNetworkNode extends NetworkNode {

    public static final ResourceLocation ID = EnderIO.loc("rs_conduit");

    public RSNetworkNode(Level level, BlockPos pos) {
        super(level, pos);
    }

    @NotNull
    @Override
    public ItemStack getItemStack() {
        return new ItemStack(RSIntegration.NORMAL_ITEM.get());
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
    public int getEnergyUsage() {
        return RS.SERVER_CONFIG.getCable().getUsage();
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

}

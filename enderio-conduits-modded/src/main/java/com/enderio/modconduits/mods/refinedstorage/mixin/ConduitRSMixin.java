package com.enderio.modconduits.mods.refinedstorage.mixin;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.common.conduit.ConduitBundle;
import com.enderio.conduits.common.conduit.block.ConduitBundleBlockEntity;
import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.modconduits.mods.refinedstorage.RSConduitsModule;
import com.enderio.modconduits.mods.refinedstorage.RSNetworkHost;
import com.refinedmods.refinedstorage.platform.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage.platform.api.support.network.NetworkNodeContainerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Set;

@Mixin(ConduitBundleBlockEntity.class)
public abstract class ConduitRSMixin extends EnderBlockEntity implements NetworkNodeContainerBlockEntity {

    @Shadow
    public abstract void setLevel(Level pLevel);

    public ConduitRSMixin(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
    }

    @Override
    public Set<InWorldNetworkNodeContainer> getContainers() {
        ConduitBundle bundle = ((ConduitBundleBlockEntity) (Object) this).getBundle();
        Holder<Conduit<?>> rsConduit = this.getLevel().holderOrThrow(RSConduitsModule.RS2);
        if (bundle.hasType(rsConduit)) {
            RSNetworkHost data = bundle.getNodeFor(rsConduit).getOrCreateData(RSConduitsModule.DATA.get());
            data.setup(this.getLevel(), this.worldPosition);
            return data.getConnections();
        }
        return Set.of();
    }

    @Override
    public boolean canBuild(ServerPlayer player) {
        ConduitBundle bundle = ((ConduitBundleBlockEntity) (Object) this).getBundle();
        Holder<Conduit<?>> rsConduit = this.getLevel().holderOrThrow(RSConduitsModule.RS2);
        RSNetworkHost data = bundle.getNodeFor(rsConduit).getOrCreateData(RSConduitsModule.DATA.get());
        return data.canBuild(player);
    }
}

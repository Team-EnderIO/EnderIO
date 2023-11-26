package com.enderio.conduits.common.types;

import com.enderio.EnderIO;
import com.enderio.api.conduit.IClientConduitData;
import com.enderio.api.conduit.IConduitMenuData;
import com.enderio.api.conduit.NodeIdentifier;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.init.EnderConduitTypes;
import com.enderio.conduits.common.integrations.ae2.AE2InWorldConduitNodeHost;
import com.enderio.conduits.common.tag.ConduitTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.common.capabilities.Capability;
import net.neoforged.neoforge.common.capabilities.ForgeCapabilities;
import net.neoforged.neoforge.common.util.LazyOptional;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EnergyConduitType extends SimpleConduitType<EnergyExtendedData> {
    public EnergyConduitType() {
        super(EnderIO.loc("block/conduit/energy"), new EnergyConduitTicker(), EnergyExtendedData::new,
            new IClientConduitData.Simple<>(EnderConduitTypes.ICON_TEXTURE, new Vector2i(0, 24)), IConduitMenuData.ENERGY);
    }

    @Override
    public ConduitConnectionData getDefaultConnection(Level level, BlockPos pos, Direction direction) {
        BlockEntity blockEntity = level.getBlockEntity(pos.relative(direction));
        if (blockEntity != null) {
            LazyOptional<IEnergyStorage> capability = blockEntity.getCapability(ForgeCapabilities.ENERGY, direction.getOpposite());
            if (capability.isPresent()) {
                IEnergyStorage storage = capability.orElseThrow(() -> new RuntimeException("present capability was not found"));
                return new ConduitConnectionData(storage.canReceive(), storage.canExtract(), RedstoneControl.ALWAYS_ACTIVE);

            }
        }
        return super.getDefaultConnection(level, pos, direction);
    }

    @Override
    public <K> Optional<LazyOptional<K>> proxyCapability(Capability<K> cap, EnergyExtendedData extendedConduitData, Level level, BlockPos pos, @Nullable Direction direction, Optional<NodeIdentifier.IOState> state) {
        if (ForgeCapabilities.ENERGY == cap
            && state.map(NodeIdentifier.IOState::isExtract).orElse(true)
            && (direction == null || !level.getBlockState(pos.relative(direction)).is(ConduitTags.Blocks.ENERGY_CABLE))) {
                return Optional.of(extendedConduitData.selfCap.cast());

        }
        return Optional.empty();
    }

}

package com.enderio.conduits.common.types;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ClientConduitData;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.NodeIdentifier;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.init.EnderConduitTypes;
import com.enderio.conduits.common.tag.ConduitTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EnergyConduitType extends SimpleConduitType<EnergyExtendedData> {
    public EnergyConduitType() {
        super(EnderIO.loc("block/conduit/energy"), new EnergyConduitTicker(), EnergyExtendedData::new,
            new ClientConduitData.Simple<>(EnderConduitTypes.ICON_TEXTURE, new Vector2i(0, 24)), ConduitMenuData.ENERGY);
    }

    @Override
    public ConduitConnectionData getDefaultConnection(Level level, BlockPos pos, Direction direction) {
//        BlockEntity blockEntity = level.getBlockEntity(pos.relative(direction));
//        if (blockEntity != null) { TODO: NEO-PORT: non be caps
            IEnergyStorage capability = level.getCapability(Capabilities.EnergyStorage.BLOCK, pos.relative(direction), direction.getOpposite());
            if (capability != null) {
                return new ConduitConnectionData(capability.canReceive(), capability.canExtract(), RedstoneControl.ALWAYS_ACTIVE);

            }
//        }
        return super.getDefaultConnection(level, pos, direction);
    }

    @Override
    public <K> Optional<K> proxyCapability(BlockCapability<K, Direction> cap, EnergyExtendedData extendedConduitData, Level level, BlockPos pos, @Nullable Direction direction, Optional<NodeIdentifier.IOState> state) {
        if (Capabilities.EnergyStorage.BLOCK == cap
            && state.map(NodeIdentifier.IOState::isExtract).orElse(true)
            && (direction == null || !level.getBlockState(pos.relative(direction)).is(ConduitTags.Blocks.ENERGY_CABLE))) {
                return (Optional<K>) Optional.ofNullable(extendedConduitData.getSelfCap()); //TODO remove optional?

        }
        return Optional.empty();
    }

}

package com.enderio.conduits.common.conduit.type.energy;

import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.conduits.common.conduit.type.SimpleConduitType;
import com.enderio.conduits.common.tag.ConduitTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EnergyConduitType extends SimpleConduitType<EnergyConduitData> {
    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(false, false, false, false, false, true);

    public EnergyConduitType() {
        super(new EnergyConduitTicker(), EnergyConduitData::new, MENU_DATA);
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
    public <K> Optional<LazyOptional<K>> proxyCapability(Capability<K> cap, EnergyConduitData extendedConduitData, Level level, BlockPos pos, @Nullable Direction direction, @Nullable ConduitNode.IOState state) {
        if (ForgeCapabilities.ENERGY == cap
            && (state == null || state.isExtract())
            && (direction == null || !level.getBlockState(pos.relative(direction)).is(ConduitTags.Blocks.ENERGY_CABLE))) {
            return Optional.of(extendedConduitData.getSelfCap().cast());

        }
        return Optional.empty();
    }

}


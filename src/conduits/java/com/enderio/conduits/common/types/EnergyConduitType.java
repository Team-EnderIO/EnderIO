package com.enderio.conduits.common.types;

import com.enderio.EnderIO;
import com.enderio.api.conduit.IClientConduitData;
import com.enderio.api.conduit.IConduitMenuData;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.init.EnderConduitTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;

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
}

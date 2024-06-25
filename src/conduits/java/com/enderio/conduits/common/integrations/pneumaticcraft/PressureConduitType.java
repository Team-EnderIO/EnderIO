package com.enderio.conduits.common.integrations.pneumaticcraft;

import com.enderio.api.conduit.ConduitData;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.conduit.ConduitNode;
import com.enderio.conduits.common.conduit.type.SimpleConduitType;
import me.desht.pneumaticcraft.api.PNCCapabilities;
import me.desht.pneumaticcraft.api.pressure.PressureTier;
import me.desht.pneumaticcraft.common.capabilities.MachineAirHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PressureConduitType extends SimpleConduitType<ConduitData.EmptyConduitData> {

    public static final ConduitMenuData MENUDATA = new ConduitMenuData.Simple(false, false, false, false, false, false);
    public static final MachineAirHandler MACHINE_AIR_HANDLER = new MachineAirHandler(PressureTier.TIER_TWO, 0);
    public static final LazyOptional<MachineAirHandler> LAZY = LazyOptional.of(() -> MACHINE_AIR_HANDLER);

    public PressureConduitType() {
        super(PressureTicker.INSTANCE, () -> ConduitData.EMPTY, MENUDATA);
    }

    @Override
    public <K> Optional<LazyOptional<K>> proxyCapability(Capability<K> cap, ConduitData.EmptyConduitData extendedConduitData, Level level, BlockPos pos,
        @Nullable Direction direction, ConduitNode.@Nullable IOState state) {
        if (cap == PNCCapabilities.AIR_HANDLER_MACHINE_CAPABILITY) {
            return Optional.of(LAZY.cast()); //One shared cap should:tm: be fine
        }
        return super.proxyCapability(cap, extendedConduitData, level, pos, direction, state);
    }
}

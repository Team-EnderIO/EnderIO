package com.enderio.enderio.client.content.machines.renderer.blockentity;

import com.enderio.enderio.content.machines.capacitor_bank.DisplayMode;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.EnumMap;
import java.util.Map;
import java.util.UUID;

public class CapacitorBankRenderState extends BlockEntityRenderState {

    public BlockState blockState;
    public Level level;
    public UUID uuid;
    Map<Direction, DisplayMode> map = new EnumMap<>(Direction.class);


    public void addDisplaymode(Direction direction, DisplayMode mode) {
        map.put(direction, mode);
    }

    public DisplayMode getDisplaymode(Direction direction) {
        return map.get(direction);
    }
}

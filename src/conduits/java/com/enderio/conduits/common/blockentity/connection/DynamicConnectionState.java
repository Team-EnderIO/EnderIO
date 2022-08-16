package com.enderio.conduits.common.blockentity.connection;

import com.enderio.api.UseOnly;
import com.enderio.base.common.blockentity.RedstoneControl;
import com.enderio.core.common.blockentity.ColorControl;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

public record DynamicConnectionState(@Nullable ColorControl in, @Nullable ColorControl out, RedstoneControl control, ColorControl redstoneChannel, @UseOnly(LogicalSide.SERVER) ItemStack filter) implements IConnectionState {

    /**
     * @return a simple dynamic connection state used for simpler renderingtesting
     */
    public static DynamicConnectionState ofInput() {
        return new DynamicConnectionState(ColorControl.GREEN, null, RedstoneControl.ACTIVE_WITH_SIGNAL, ColorControl.RED, ItemStack.EMPTY);
    }

    @Override
    public boolean isConnection() {
        return true;
    }
}

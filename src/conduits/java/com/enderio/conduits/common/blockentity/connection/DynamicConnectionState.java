package com.enderio.conduits.common.blockentity.connection;

import com.enderio.api.UseOnly;
import com.enderio.base.common.blockentity.RedstoneControl;
import com.enderio.core.common.blockentity.ColorControl;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public record DynamicConnectionState(@Nullable ColorControl in, @Nullable ColorControl out, RedstoneControl control, ColorControl redstoneChannel, @UseOnly(LogicalSide.SERVER) ItemStack filter) implements IConnectionState {

    /**
     * @return a simple dynamic connection state used for simpler renderingtesting
     */
    public static DynamicConnectionState ofInput() {
        return new DynamicConnectionState(ColorControl.GREEN, null, RedstoneControl.ACTIVE_WITH_SIGNAL, ColorControl.RED, ItemStack.EMPTY);
    }

    private static int connection = 0;
    //TODO Remove
    public static DynamicConnectionState random() {
        Random r = new Random();
        @Nullable
        ColorControl in = null;
        @Nullable
        ColorControl out = null;
        RedstoneControl control = random(r, RedstoneControl.class);
        ColorControl redstoneChannel = random(r, ColorControl.class);
        //switch (r.nextInt(3)) {
        //    case 0 -> {in = random(r, ColorControl.class); out = random(r, ColorControl.class);}
        //    case 1 -> in = random(r, ColorControl.class);
        //    default -> out = random(r, ColorControl.class);
        //}
        switch (connection) {
            case 0,1 -> in = ColorControl.BLUE;
            default -> out = ColorControl.BLUE;
        }
        connection++;
        return new DynamicConnectionState(in, out, control, redstoneChannel, ItemStack.EMPTY);
    }

    @Override
    public boolean isConnection() {
        return true;
    }

    private static <T extends Enum<T>> T random(Random r, Class<T> clazz) {
        return clazz.getEnumConstants()[r.nextInt(clazz.getEnumConstants().length)];
    }
}

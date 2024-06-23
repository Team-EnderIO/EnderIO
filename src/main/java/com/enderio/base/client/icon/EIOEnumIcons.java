package com.enderio.base.client.icon;

import com.enderio.EnderIO;
import com.enderio.api.misc.RedstoneControl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Locale;

public class EIOEnumIcons {

    // region Dye Color

    public static ResourceLocation getIcon(DyeColor value) {
        return DYE_COLOR_ICONS.get(value);
    }

    private static final EnumMap<DyeColor, ResourceLocation> DYE_COLOR_ICONS = new EnumMap<>(DyeColor.class);

    // endregion

    // region Redstone Control

    public static ResourceLocation getIcon(RedstoneControl redstoneControl) {
        return REDSTONE_CONTROL_ICONS.get(redstoneControl);
    }

    private static final EnumMap<RedstoneControl, ResourceLocation> REDSTONE_CONTROL_ICONS = new EnumMap<>(RedstoneControl.class);

    // endregion

    static {
        Arrays.stream(RedstoneControl.values())
            .forEach(control -> REDSTONE_CONTROL_ICONS.put(control, EnderIO.loc("icons/redstone_control/" + control.name().toLowerCase(Locale.ROOT))));

        Arrays.stream(DyeColor.values())
            .forEach(control -> DYE_COLOR_ICONS.put(control, EnderIO.loc("icons/dye_color/" + control.name().toLowerCase(Locale.ROOT))));
    }
}

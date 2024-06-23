package com.enderio.base.client.icon;

import com.enderio.EnderIO;
import com.enderio.api.misc.RedstoneControl;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Locale;

public class EIOEnumIcons {

    // region Redstone Control

    public static ResourceLocation getIcon(RedstoneControl redstoneControl) {
        return REDSTONE_CONTROL_ICONS.get(redstoneControl);
    }

    private static final EnumMap<RedstoneControl, ResourceLocation> REDSTONE_CONTROL_ICONS = new EnumMap<>(RedstoneControl.class);

    // endregion

    static {
        Arrays.stream(RedstoneControl.values())
            .forEach(control -> REDSTONE_CONTROL_ICONS.put(control, EnderIO.loc("icons/redstone_control/" + control.name().toLowerCase(Locale.ROOT))));
    }
}

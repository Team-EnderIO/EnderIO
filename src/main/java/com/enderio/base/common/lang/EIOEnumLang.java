package com.enderio.base.common.lang;

import com.enderio.api.misc.RedstoneControl;
import net.minecraft.network.chat.Component;
import org.apache.commons.lang3.NotImplementedException;

public class EIOEnumLang {
    public static Component getDescription(RedstoneControl redstoneControl) {
        return switch (redstoneControl) {
            case ALWAYS_ACTIVE -> EIOLang.REDSTONE_ALWAYS_ACTIVE;
            case ACTIVE_WITH_SIGNAL -> EIOLang.REDSTONE_ACTIVE_WITH_SIGNAL;
            case ACTIVE_WITHOUT_SIGNAL -> EIOLang.REDSTONE_ACTIVE_WITHOUT_SIGNAL;
            case NEVER_ACTIVE -> EIOLang.REDSTONE_NEVER_ACTIVE;
            default -> throw new NotImplementedException();
        };
    }
}

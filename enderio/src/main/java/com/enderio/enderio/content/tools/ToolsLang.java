package com.enderio.enderio.content.tools;

import com.enderio.enderio.EnderIO;
import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class ToolsLang {

    // region Coordinate Selector

    public static final MutableComponent COORDINATE_SELECTOR_NO_PAPER = message("coordinate_selector/no_paper");
    public static final MutableComponent COORDINATE_SELECTOR_NO_BLOCK = message("coordinate_selector/no_block");

    // endregion

    // region Glider

    public static final MutableComponent GLIDER_DISABLED = message("glider/disable");
    public static final MutableComponent GLIDER_DISABLED_FALL_FLYING = message("glider/disable/fall_flying");

    // endregion

    // region Vials

    public static final MutableComponent SOUL_VIAL_ERROR_PLAYER = message("soul_vial/error/player");
    public static final MutableComponent SOUL_VIAL_ERROR_BOSS = message("soul_vial/error/boss");
    public static final MutableComponent SOUL_VIAL_ERROR_BLACKLISTED = message("soul_vial/error/blacklisted");
    public static final MutableComponent SOUL_VIAL_ERROR_FAILED = message("soul_vial/error/failed");
    public static final MutableComponent SOUL_VIAL_ERROR_DEAD = message("soul_vial/error/dead");

    // Has two string params
    public static final MutableComponent SOUL_VIAL_TOOLTIP_HEALTH = tooltip("soul_vial/health");

    // endregion

    private static MutableComponent tooltip(String path) {
        return create("tooltip", path);
    }

    private static MutableComponent message(String path) {
        return create("message", path);
    }

    private static MutableComponent create(String type, String path) {
        return Component.translatable(Util.makeDescriptionId(type, EnderIO.rl("tool/" + path)));
    }
}

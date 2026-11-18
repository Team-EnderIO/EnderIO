package com.enderio.enderio.content.armory.toggles;

import com.enderio.enderio.EnderIO;
import net.minecraft.resources.ResourceLocation;

/**
 * Keybind channels for dark steel toggles. A toggle names one of these in its {@code key} field.
 * <p>
 * Each channel needs a matching {@link net.minecraft.client.KeyMapping} that sends a
 * {@link com.enderio.enderio.foundation.network.packets.ServerboundDarkSteelTogglePacket} with its id,
 * see {@code KeymapHandler}. Addons can add their own channels the same way, or bind their toggles to these.
 */
public final class DarkSteelToggleKeys {
    public static final ResourceLocation NIGHT_VISION = EnderIO.rl("night_vision");
    public static final ResourceLocation ELYTRA = EnderIO.rl("elytra");
    public static final ResourceLocation STEP_ASSIST = EnderIO.rl("step_assist");
    public static final ResourceLocation DOUBLE_JUMP = EnderIO.rl("double_jump");
    public static final ResourceLocation EXPLOSIVE = EnderIO.rl("explosive");
    public static final ResourceLocation SPEED = EnderIO.rl("speed");

    private DarkSteelToggleKeys() {
    }
}

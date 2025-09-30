package com.enderio.enderio.client.gui.icon;

import com.enderio.core.client.icon.EnumIconMap;
import com.enderio.enderio.api.EnderIOAPI;
import com.enderio.enderio.api.io.RedstoneControl;
import com.enderio.enderio.common.block.glass.GlassCollisionPredicate;
import com.enderio.enderio.common.block.glass.GlassLighting;
import com.enderio.enderio.common.filter.item.general.DamageFilterMode;
import net.minecraft.world.item.DyeColor;

public class EIOEnumIcons {

    public static final EnumIconMap<DyeColor> DYE_COLOR = createAll(DyeColor.class, "dye_color");

    public static final EnumIconMap<RedstoneControl> REDSTONE_CONTROL = createAll(RedstoneControl.class,
            "redstone_control");

    public static final EnumIconMap<GlassCollisionPredicate> GLASS_COLLISION_PREDICATE = builder(
            GlassCollisionPredicate.class, "glass_collision").addAll().remove(GlassCollisionPredicate.NONE).build();

    public static final EnumIconMap<GlassLighting> GLASS_LIGHTING = builder(GlassLighting.class, "glass_lighting")
            .addAll()
            .remove(GlassLighting.NONE)
            .build();

    public static final EnumIconMap<DamageFilterMode> DAMAGE_FILTER_MODE = createAll(DamageFilterMode.class,
            "damage_filter_mode");

    private static <T extends Enum<T>> EnumIconMap<T> createAll(Class<T> enumClass, String iconFolder) {
        return new EnumIconMap<>(EnderIOAPI.MOD_ID, enumClass, iconFolder);
    }

    private static <T extends Enum<T>> EnumIconMap.Builder<T> builder(Class<T> enumClass, String iconFolder) {
        return new EnumIconMap.Builder<>(EnderIOAPI.MOD_ID, enumClass, iconFolder);
    }
}

package com.enderio.base.client.gui.icon;

import com.enderio.EnderIOBase;
import com.enderio.base.api.misc.RedstoneControl;
import com.enderio.base.common.block.glass.GlassCollisionPredicate;
import com.enderio.base.common.block.glass.GlassLighting;
import com.enderio.core.client.icon.EnumIconMap;
import net.minecraft.world.item.DyeColor;

public class EIOEnumIcons {

    public static final EnumIconMap<DyeColor> DYE_COLOR = createAll(DyeColor.class, "dye_color");

    public static final EnumIconMap<RedstoneControl> REDSTONE_CONTROL = createAll(RedstoneControl.class, "redstone_control");

    public static final EnumIconMap<GlassCollisionPredicate> GLASS_COLLISION_PREDICATE = builder(GlassCollisionPredicate.class, "glass_collision")
            .addAll()
            .remove(GlassCollisionPredicate.NONE)
            .build();

    public static final EnumIconMap<GlassLighting> GLASS_LIGHTING = builder(GlassLighting.class, "glass_lighting")
            .addAll()
            .remove(GlassLighting.NONE)
            .build();

    private static <T extends Enum<T>> EnumIconMap<T> createAll(Class<T> enumClass, String iconFolder) {
        return new EnumIconMap<>(EnderIOBase.REGISTRY_NAMESPACE, enumClass, iconFolder);
    }

    private static <T extends Enum<T>> EnumIconMap.Builder<T> builder(Class<T> enumClass, String iconFolder) {
        return new EnumIconMap.Builder<>(EnderIOBase.REGISTRY_NAMESPACE, enumClass, iconFolder);
    }
}

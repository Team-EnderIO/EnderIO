package com.enderio.base.client.gui.icon;

import com.enderio.EnderIO;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.base.common.block.glass.GlassCollisionPredicate;
import com.enderio.base.common.block.glass.GlassLighting;
import com.enderio.core.client.icon.EnumIconMap;
import net.minecraft.world.item.DyeColor;

public class EIOEnumIcons {

    public final static EnumIconMap<DyeColor> DYE_COLOR = createAll(DyeColor.class, "dye_color");

    public final static EnumIconMap<RedstoneControl> REDSTONE_CONTROL = createAll(RedstoneControl.class, "redstone_control");

    public final static EnumIconMap<GlassCollisionPredicate> GLASS_COLLISION_PREDICATE = builder(GlassCollisionPredicate.class, "glass_collision")
            .addAll()
            .remove(GlassCollisionPredicate.NONE)
            .build();

    public final static EnumIconMap<GlassLighting> GLASS_LIGHTING = builder(GlassLighting.class, "glass_lighting")
            .addAll()
            .remove(GlassLighting.NONE)
            .build();

    private static <T extends Enum<T>> EnumIconMap<T> createAll(Class<T> enumClass, String iconFolder) {
        return new EnumIconMap<>(EnderIO.MODID, enumClass, iconFolder);
    }

    private static <T extends Enum<T>> EnumIconMap.Builder<T> builder(Class<T> enumClass, String iconFolder) {
        return new EnumIconMap.Builder<>(EnderIO.MODID, enumClass, iconFolder);
    }
}

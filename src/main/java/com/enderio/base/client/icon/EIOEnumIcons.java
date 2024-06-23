package com.enderio.base.client.icon;

import com.enderio.EnderIO;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.base.common.block.glass.GlassCollisionPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Locale;
import java.util.stream.Collectors;

public class EIOEnumIcons {

    public static EnumIconHolder<DyeColor> DYE_COLOR = new EnumIconHolder<>(DyeColor.class, "dye_color");
    public static EnumIconHolder<RedstoneControl> REDSTONE_CONTROL = new EnumIconHolder<>(RedstoneControl.class, "redstone_control");
    public static EnumIconHolder<GlassCollisionPredicate> GLASS_COLLISION_PREDICATE = new EnumIconHolder<>(GlassCollisionPredicate.class, "glass_collision");

    public static class EnumIconHolder<T extends Enum<T>> {
        private final EnumMap<T, ResourceLocation> icons;

        public EnumIconHolder(Class<T> enumClass, String iconFolder) {
            //noinspection Convert2Diamond
            icons = new EnumMap<T, ResourceLocation>(Arrays.stream(enumClass.getEnumConstants())
                .collect(Collectors.toMap(e -> e,
                    e -> EnderIO.loc("icons/" + iconFolder + "/" + e.name().toLowerCase(Locale.ROOT)))));
        }

        public ResourceLocation get(T value) {
            return icons.get(value);
        }
    }
}

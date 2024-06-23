package com.enderio.base.client.icon;

import com.enderio.EnderIO;
import com.enderio.api.misc.RedstoneControl;
import com.enderio.base.common.block.glass.GlassCollisionPredicate;
import com.enderio.base.common.block.glass.GlassLighting;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Locale;
import java.util.stream.Collectors;

public class EIOEnumIcons {

    public static EnumIconHolder<DyeColor> DYE_COLOR = new EnumIconHolder<>(DyeColor.class, "dye_color");

    public static EnumIconHolder<RedstoneControl> REDSTONE_CONTROL = new EnumIconHolder<>(RedstoneControl.class, "redstone_control");

    public static EnumIconHolder<GlassCollisionPredicate> GLASS_COLLISION_PREDICATE =
        new EnumIconHolder.Builder<>(GlassCollisionPredicate.class, "glass_collision")
            .addAll()
            .remove(GlassCollisionPredicate.NONE)
            .build();

    public static EnumIconHolder<GlassLighting> GLASS_LIGHTING =
        new EnumIconHolder.Builder<>(GlassLighting.class, "glass_lighting")
            .addAll()
            .remove(GlassLighting.NONE)
            .build();

    public static class EnumIconHolder<T extends Enum<T>> {
        private final EnumMap<T, ResourceLocation> icons;

        public EnumIconHolder(Class<T> enumClass, String iconFolder) {
            //noinspection Convert2Diamond
            icons = new EnumMap<T, ResourceLocation>(Arrays.stream(enumClass.getEnumConstants())
                .collect(Collectors.toMap(e -> e,
                    e -> createFor(iconFolder, e))));
        }

        private EnumIconHolder(EnumMap<T, ResourceLocation> icons) {
            this.icons = icons;
        }

        @Nullable
        public ResourceLocation get(T value) {
            return icons.get(value);
        }

        private static <T extends Enum<T>> ResourceLocation createFor(String iconFolder, T value) {
            return EnderIO.loc("icons/" + iconFolder + "/" + value.name().toLowerCase(Locale.ROOT));
        }

        public static class Builder<T extends Enum<T>> {
            private final Class<T> enumClass;
            private final String iconFolder;

            private final EnumMap<T, ResourceLocation> icons;

            public Builder(Class<T> enumClass, String iconFolder) {
                this.enumClass = enumClass;
                this.iconFolder = iconFolder;
                icons = new EnumMap<>(enumClass);
            }

            public Builder<T> add(T value) {
                icons.put(value, createFor(iconFolder, value));
                return this;
            }

            public Builder<T> add(T value, ResourceLocation icon) {
                icons.put(value, icon);
                return this;
            }

            public Builder<T> addAll() {
                for (T value : enumClass.getEnumConstants()) {
                    icons.put(value, createFor(iconFolder, value));
                }
                return this;
            }

            public Builder<T> remove(T value) {
                icons.remove(value);
                return this;
            }

            public EnumIconHolder<T> build() {
                return new EnumIconHolder<>(icons);
            }
        }
    }
}

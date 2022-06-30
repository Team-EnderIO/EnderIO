package com.enderio.api.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;

/**
 * This class is in this package, because it's not only used by the item, but also by machines
 */

public record CoordinateSelection(ResourceLocation level, BlockPos pos) {

    /**
     * Create a coordinate selection using a {@link Level} rather than a {@link ResourceLocation}.
     */
    public static CoordinateSelection of(Level level, BlockPos pos) {
        return new CoordinateSelection(level.dimension().location(), pos);
    }

    /**
     * Get the name of the given level.
     */
    // TODO: Does this belong here/need to be static?
    public static String getLevelName(ResourceLocation level) {
        return level.getNamespace().equals("minecraft") ? level.getPath() : level.toString();
    }

    /**
     * Get the name of the level this points to.
     */
    public String getLevelName() {
        return getLevelName(level());
    }

    /**
     * Only call on Serverside, the only Level the Client knows about is {@link net.minecraft.client.Minecraft#level}
     * @return the level of this Selection or null if no level is found
     */
    public @Nullable Level getLevelInstance() {
        return ServerLifecycleHooks.getCurrentServer().getLevel(ResourceKey.create(Registry.DIMENSION_REGISTRY, level()));
    }
}

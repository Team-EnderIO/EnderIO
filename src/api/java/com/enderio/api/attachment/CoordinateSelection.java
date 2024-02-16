package com.enderio.api.attachment;

import com.enderio.api.UseOnly;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.util.INBTSerializable;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * This class is in this package, because it's not only used by the item, but also by machines
 */

public class CoordinateSelection implements INBTSerializable<CompoundTag> {

    private ResourceLocation level;
    private BlockPos pos;

    // TODO: NEO-PORT: Not happy with this class having an "empty" constructor
    //       Either needs to be removed somehow, or make this safe to have nulls.
    public CoordinateSelection() {
    }

    public CoordinateSelection(ResourceLocation level, BlockPos pos) {
        this.level = level;
        this.pos = pos;
    }

    public CoordinateSelection(Level level, BlockPos pos) {
        this.level = level.dimension().location();
        this.pos = pos;
    }

    /**
     * Create a coordinate selection using a {@link Level} rather than a {@link ResourceLocation}.
     */
    @Deprecated(forRemoval = true)
    public static CoordinateSelection of(Level level, BlockPos pos) {
        return new CoordinateSelection(level.dimension().location(), pos);
    }

    public ResourceLocation getLevel() {
        return level;
    }

    public BlockPos getPos() {
        return pos;
    }

    /**
     * Get the name of the given level.
     */
    public static String getLevelName(ResourceLocation level) {
        return level.getNamespace().equals("minecraft") ? level.getPath() : level.toString();
    }

    /**
     * Get the name of the level this points to.
     */
    public String getLevelName() {
        return getLevelName(level);
    }

    // TODO: NEO-PORT: Needed?
    /**
     * @return the level of this Selection or null if no level is found
     */
    @Nullable
    @UseOnly(LogicalSide.SERVER)
    public Level getLevelInstance() {
        return ServerLifecycleHooks.getCurrentServer().getLevel(ResourceKey.create(Registries.DIMENSION, level));
    }

    @NotNull
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("level", level.toString());
        tag.put("pos", NbtUtils.writeBlockPos(pos));
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        level = new ResourceLocation(nbt.getString("level"));
        pos = NbtUtils.readBlockPos(nbt.getCompound("pos"));
    }
}

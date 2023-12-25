package com.enderio.core.common.registry;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.registries.DeferredHolder;

import javax.annotation.Nullable;

public class EnderDeferredBlockEntity<T extends BlockEntity> extends DeferredHolder<BlockEntityType<? extends BlockEntity>, BlockEntityType<T>> {

    /**
     * Creates a new DeferredHolder with a ResourceKey.
     *
     * <p>Attempts to bind immediately if possible.
     *
     * @param key The resource key of the target object.
     * @see #create(ResourceKey, ResourceLocation)
     * @see #create(ResourceLocation, ResourceLocation)
     * @see #create(ResourceKey)
     */
    protected EnderDeferredBlockEntity(ResourceKey<BlockEntityType<? extends BlockEntity>> key) {
        super(key);
    }

    public static <T extends BlockEntity> EnderDeferredBlockEntity<T> createBlockEntity(DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> holder)
    {
        return new EnderDeferredBlockEntity<>(holder.getKey());
    }

    @Nullable
    public T create(BlockPos pos, BlockState state) {
        return this.get().create(pos, state);
    }
}

package com.enderio.core.common.registry;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class EnderBlockEntityRegistry extends DeferredRegister<BlockEntityType<?>> {
    protected EnderBlockEntityRegistry(String namespace) {
        super(BuiltInRegistries.BLOCK_ENTITY_TYPE.key(), namespace);
    }

    public <T extends BlockEntity> EnderDeferredBlockEntity<T> registerBlockEntity(String name, BlockEntityType.BlockEntitySupplier<T> sup, Block... blocks) {
        DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> holder = this.register(name, () -> BlockEntityType.Builder.of(sup, blocks).build(null));
        return EnderDeferredBlockEntity.createBlockEntity(holder);
    }

    @SafeVarargs
    public final <T extends BlockEntity> EnderDeferredBlockEntity<T> registerBlockEntity(String name, BlockEntityType.BlockEntitySupplier<T> sup,
        Supplier<? extends Block>... blocks) {
        List<? extends Block> blockList = Arrays.stream(blocks).map(Supplier::get).toList();
        DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> holder = this.register(name, () -> BlockEntityType.Builder.of(sup, blockList.toArray(new Block[] {})).build(null));
        return EnderDeferredBlockEntity.createBlockEntity(holder);
    }

    public static EnderBlockEntityRegistry create(String modid) {
        return new EnderBlockEntityRegistry(modid);
    }

}

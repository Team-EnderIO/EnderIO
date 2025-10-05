package com.enderio.enderio.init;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.content.conduits.bundle.ConduitBundleBlockEntity;
import com.enderio.enderio.content.paint.block.entity.DoublePaintedBlockEntity;
import com.enderio.enderio.content.paint.block.entity.SinglePaintedBlockEntity;
import com.enderio.enderio.foundation.block.entity.EnderSkullBlockEntity;
import com.enderio.regilite.holder.RegiliteBlockEntity;
import com.enderio.regilite.registry.BlockEntityRegistry;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Arrays;
import java.util.function.Supplier;

public class EIOBlockEntities {
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, EnderIO.MOD_ID);

    // region Painting

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SinglePaintedBlockEntity>> SINGLE_PAINTED = register("single_painted",
        SinglePaintedBlockEntity::new, EIOBlocks.PAINTED_FENCE::get, EIOBlocks.PAINTED_FENCE_GATE::get, EIOBlocks.PAINTED_SAND::get,
        EIOBlocks.PAINTED_STAIRS::get, EIOBlocks.PAINTED_CRAFTING_TABLE::get, EIOBlocks.PAINTED_REDSTONE_BLOCK::get, EIOBlocks.PAINTED_TRAPDOOR::get,
        EIOBlocks.PAINTED_WOODEN_PRESSURE_PLATE::get, EIOBlocks.PAINTED_GLOWSTONE::get, EIOBlocks.PAINTED_WALL::get);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<DoublePaintedBlockEntity>> DOUBLE_PAINTED = register("double_painted",
        DoublePaintedBlockEntity::new, EIOBlocks.PAINTED_SLAB::get);

    // endregion

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ConduitBundleBlockEntity>> CONDUIT = register("conduit",
        ConduitBundleBlockEntity::new, ConduitBlocks.CONDUIT::get);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EnderSkullBlockEntity>> ENDER_SKULL = register("ender_skull",
        EnderSkullBlockEntity::new, EIOBlocks.WALL_ENDERMAN_HEAD::get, EIOBlocks.ENDERMAN_HEAD::get);

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY_TYPES.register(eventBus);
    }

    @SafeVarargs
    private static <T extends BlockEntity> DeferredHolder<BlockEntityType<?>, BlockEntityType<T>> register(String name,
        BlockEntityType.BlockEntitySupplier<? extends T> factory, Supplier<Block>... validBlocks) {
        //noinspection DataFlowIssue
        return BLOCK_ENTITY_TYPES.register(name, () -> BlockEntityType.Builder.<T>of(factory,
            Arrays.stream(validBlocks).map(Supplier::get).toArray(Block[]::new)).build(null));
    }
}

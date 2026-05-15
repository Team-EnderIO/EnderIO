package com.enderio.enderio.api.farm;

import com.enderio.core.annotations.UseOnly;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jetbrains.annotations.ApiStatus;
import org.jspecify.annotations.Nullable;

@ApiStatus.Experimental
public interface FarmingMachine {

    ResourceSlotId<ItemResource> seeds(BlockPos pos);

    ResourceSlotId<ItemResource> axe();

    ResourceSlotId<ItemResource> hoe();

    ResourceSlotId<ItemResource> shears();

    ItemResource getResource(ResourceSlotId<ItemResource> slot);

    @UseOnly(LogicalSide.SERVER)
    FakePlayer getPlayer();

    BlockPos getPosition();

    int getFarmingRange();

    boolean consumeBonemeal();

    @Nullable
    Level getLevel();

    boolean handleDrops(BlockState plant, BlockPos pos, BlockPos soil, BlockEntity blockEntity, ItemResource stack);

    @Nullable
    EntityType<?> getEntityType();

    InteractionResult useStack(BlockPos soil, ItemResource resource, ResourceSlotId<ItemResource> slot);

    void mineBlock(ResourceSlotId<ItemResource> slot, BlockState state, BlockPos pos);
}

package com.enderio.base.common.blockentity;

import com.enderio.api.capability.IOwner;
import com.enderio.base.common.capability.Owner;
import com.enderio.base.common.init.EIOCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import org.jetbrains.annotations.Nullable;
import java.util.Collection;

public class GraveBlockEntity extends BlockEntity {
    private final Owner owner = new Owner();
    private final LazyOptional<IOwner> ownerLazy = LazyOptional.of(() -> owner);
    private final GraveItemStackHandler itemHandler = new GraveItemStackHandler();
    private final LazyOptional<IItemHandler> itemLazy = LazyOptional.of(() -> itemHandler);

    public GraveBlockEntity(BlockEntityType<?> type, BlockPos pWorldPosition, BlockState pBlockState) {
        super(type, pWorldPosition, pBlockState);
    }

    // region Items

    public void addDrops(Collection<ItemEntity> drops) {
        NonNullList<ItemStack> stacks = NonNullList.create();
        drops.forEach(entity -> stacks.add(entity.getItem()));
        this.itemHandler.setItems(stacks);
    }

    public Collection<ItemStack> getItems() {
        return this.itemHandler.getItems();
    }

    // endregion

    // region Capabilities

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        ownerLazy.invalidate();
        itemLazy.invalidate();
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        if (cap == EIOCapabilities.OWNER) {
            return this.ownerLazy.cast();
        }
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return this.itemLazy.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void load(CompoundTag pTag) {
        owner.deserializeNBT(pTag.getCompound(owner.getSerializedName()));
        itemHandler.deserializeNBT(pTag.getCompound("Items"));
        super.load(pTag);
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put(owner.getSerializedName(), owner.serializeNBT());
        pTag.put("Items", itemHandler.serializeNBT());
    }

    // endregion

    // region Networking

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        var tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }

    // endregion

    private static class GraveItemStackHandler extends ItemStackHandler {

        public void setItems(NonNullList<ItemStack> items) {
            this.stacks = items;
        }

        public NonNullList<ItemStack> getItems() {
            return stacks;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            return stack;
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }
    }
}

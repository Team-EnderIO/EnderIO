package com.enderio.machines.common.blockentity;

import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.IOMode;
import com.enderio.machines.common.io.FixedIOConfig;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.menu.EnchanterMenu;
import com.enderio.api.recipe.EnchanterRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EnchanterBlockEntity extends MachineBlockEntity {

    public EnchanterBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
    }

    @Override
    public MachineTier getTier() {
        return MachineTier.Standard;
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .addInput((slot, stack) -> stack.getItem() == Items.WRITABLE_BOOK)
            .addInput()
            .addInput((slot, stack) -> stack.is(Tags.Items.GEMS_LAPIS))
            .addOutput()
            .build();
    }

    @Override
    public boolean supportsRedstoneControl() {
        return false;
    }

    @Override
    protected IIOConfig createIOConfig() {
        // No IO support for this block.
        return new FixedIOConfig(IOMode.DISABLED);
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new EnchanterMenu(this, pInventory, pContainerId);
    }

    @Override
    protected MachineInventory createItemHandler(MachineInventoryLayout layout) {
        return new MachineInventory(getIOConfig(), layout) {
            protected void onContentsChanged(int slot) {
                if (slot != 3) {
                    Optional<EnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.Types.ENCHANTING, getRecipeWrapper(), level);
                    if (recipe.isPresent()) {
                        getInventory().setStackInSlot(3, recipe.get().assemble(getRecipeWrapper()));
                    }
                    else {
                        getInventory().setStackInSlot(3, ItemStack.EMPTY);
                    }
                }
                setChanged();
            }

            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot == 3 && !isServer()) {
                    return ItemStack.EMPTY;
                }
                return super.extractItem(slot, amount, simulate);
            }
        };
    }
    
}

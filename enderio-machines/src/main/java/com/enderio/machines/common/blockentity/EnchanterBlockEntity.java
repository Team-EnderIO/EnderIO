package com.enderio.machines.common.blockentity;

import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.IOMode;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.FixedIOConfig;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.EnchanterMenu;
import com.enderio.machines.common.recipe.EnchanterRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class EnchanterBlockEntity extends MachineBlockEntity {

    private final RecipeWrapper container;

    public EnchanterBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
        container = new RecipeWrapper(getInventory());
    }

    @Override
    public MachineTier getTier() {
        return MachineTier.STANDARD;
    }

    public RecipeWrapper getContainer() {
        return container;
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder(false)
            .inputSlot((slot, stack) -> stack.getItem() == Items.WRITABLE_BOOK)
            .inputSlot()
            .inputSlot((slot, stack) -> stack.is(Tags.Items.GEMS_LAPIS))
            .outputSlot()
            .build();
    }

    // region Machine config

    @Override
    public boolean supportsRedstoneControl() {
        return false;
    }

    @Override
    protected IIOConfig createIOConfig() {
        // No IO support for this block.
        return new FixedIOConfig(IOMode.DISABLED);
    }

    // endregion

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
        return new EnchanterMenu(this, pInventory, pContainerId);
    }

    @Override
    protected MachineInventory createMachineInventory(MachineInventoryLayout layout) {
        // Custom behaviour as this works more like a crafting table than a machine.
        return new MachineInventory(getIOConfig(), layout) {
            protected void onContentsChanged(int slot) {
                if (slot != 3) {
                    Optional<EnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.Types.ENCHANTING, container, level);
                    if (recipe.isPresent()) {
                        getInventory().setStackInSlot(3, recipe.get().assemble(container));
                    } else {
                        getInventory().setStackInSlot(3, ItemStack.EMPTY);
                    }
                }

                onInventoryContentsChanged(slot);
                setChanged();
            }

            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (slot == 3 && isClientSide()) {
                    return ItemStack.EMPTY;
                }
                return super.extractItem(slot, amount, simulate);
            }
        };
    }
    
}

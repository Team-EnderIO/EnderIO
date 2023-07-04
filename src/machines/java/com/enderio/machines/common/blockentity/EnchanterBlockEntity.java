package com.enderio.machines.common.blockentity;

import com.enderio.api.io.IIOConfig;
import com.enderio.api.io.IOMode;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.FixedIOConfig;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.SingleSlotAccess;
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

import java.util.Optional;

public class EnchanterBlockEntity extends MachineBlockEntity {

    private final RecipeWrapper container;

    public static final SingleSlotAccess BOOK = new SingleSlotAccess();
    public static final SingleSlotAccess CATALYST = new SingleSlotAccess();
    public static final SingleSlotAccess LAPIS = new SingleSlotAccess();
    public static final SingleSlotAccess OUTPUT = new SingleSlotAccess();

    public EnchanterBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);
        container = new RecipeWrapper(getInventory());
    }

    public RecipeWrapper getContainer() {
        return container;
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .inputSlot((slot, stack) -> stack.getItem() == Items.WRITABLE_BOOK)
            .slotAccess(BOOK)
            .inputSlot()
            .slotAccess(CATALYST)
            .inputSlot((slot, stack) -> stack.is(Tags.Items.GEMS_LAPIS))
            .slotAccess(LAPIS)
            .outputSlot()
            .slotAccess(OUTPUT)
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
                if (level == null) {
                    return;
                }

                if (!OUTPUT.isSlot(slot)) {
                    Optional<EnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.ENCHANTING.type().get(), container, level);
                    if (recipe.isPresent()) {
                        OUTPUT.setStackInSlot(this, recipe.get().assemble(container, level.registryAccess()));
                    } else {
                        OUTPUT.setStackInSlot(this, ItemStack.EMPTY);
                    }
                }

                onInventoryContentsChanged(slot);
                setChanged();
            }

            public ItemStack extractItem(int slot, int amount, boolean simulate) {
                if (level == null) {
                    return ItemStack.EMPTY;
                }

                if (OUTPUT.isSlot(slot) && level.isClientSide()) {
                    return ItemStack.EMPTY;
                }
                return super.extractItem(slot, amount, simulate);
            }
        };
    }
    
}

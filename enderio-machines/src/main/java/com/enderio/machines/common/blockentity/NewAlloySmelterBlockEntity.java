package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.machines.recipes.IAlloySmeltingRecipe;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PoweredTaskMachineEntity;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.init.MachineCapacitorKeys;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.AlloySmelterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class NewAlloySmelterBlockEntity extends PoweredTaskMachineEntity<PoweredCraftingTask<IAlloySmeltingRecipe, Container>> {
    public static class Standard extends NewAlloySmelterBlockEntity {

        public Standard(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(AlloySmelterMode.ALL,
                MachineCapacitorKeys.ALLOY_SMELTER_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.ALLOY_SMELTER_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.ALLOY_SMELTER_ENERGY_CONSUME.get(),
                pType, pWorldPosition, pBlockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.STANDARD;
        }
    }

    private final AlloySmelterMode defaultMode;
    private AlloySmelterMode mode;
    private boolean inventoryChanged = true;

    public NewAlloySmelterBlockEntity(AlloySmelterMode mode, CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey energyUseKey,
        BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(capacityKey, transferKey, energyUseKey, pType, pWorldPosition, pBlockState);

        this.defaultMode = mode;
        this.mode = mode;
    }

    public AlloySmelterMode getMode() {
        // Lock to default mode if this is a simple machine.
        return getTier() == MachineTier.SIMPLE ? defaultMode : mode;
    }

    public void setMode(AlloySmelterMode mode) {
        this.mode = mode;
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        // Setup item slots
        return MachineInventoryLayout.builder(getTier() != MachineTier.SIMPLE)
            .inputSlot(3, this::acceptSlotInput)
            .outputSlot()
            .build();
    }

    private boolean acceptSlotInput(int slot, ItemStack stack) {
        // Ensure we don't break automation by inserting items that'll break the current recipe.
        PoweredCraftingTask currentTask = getCurrentTask();
        if (currentTask != null) {
            MachineInventory inventory = getInventory();
            ItemStack currentContents = inventory.getStackInSlot(slot);
            inventory.setStackInSlot(slot, stack);

            boolean accept = currentTask.getRecipe().matches(getRecipeWrapper(), level);

            inventory.setStackInSlot(slot, currentContents);
            return accept;
        }
        return true;
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        inventoryChanged = true; // TODO: 28/05/2022 This kind of thing might be a good idea for a base crafter class?
        super.onInventoryContentsChanged(slot);
    }

    @Override
    protected boolean hasNextTask() {
        return inventoryChanged;
    }

    @Override
    protected @Nullable PoweredCraftingTask getNextTask() {
        if (level == null)
            return null;

        // If there's no items, don't waste tick time

        // Search for an alloy recipe.
        if (getMode().canAlloy()) {
            PoweredCraftingTask task = level
                .getRecipeManager()
                .getRecipeFor(MachineRecipes.Types.ALLOY_SMELTING, getRecipeWrapper(), level)
                .map(this::createTask)
                .orElse(null);

            if (task != null)
                return task;
        }

        // Search for a smelting recipe.
//        if (getMode().canSmelt()) {
//            NewPoweredCraftingTask task = level
//                .getRecipeManager()
//                .getRecipeFor(RecipeType.SMELTING, getRecipeWrapper(), level)
//                .map(recipe -> createTask(new WrappedSmeltingRecipe(recipe)))
//                .orElse(null);
//
//            if (task != null)
//                return task;
//        }

        return null;
    }

    @Override
    protected @Nullable PoweredCraftingTask<IAlloySmeltingRecipe, Container> loadTask(CompoundTag nbt) {
        PoweredCraftingTask<IAlloySmeltingRecipe, Container> task = createTask(null);
        task.deserializeNBT(nbt);
        return task;
    }

    private PoweredCraftingTask<IAlloySmeltingRecipe, Container> createTask(@Nullable IAlloySmeltingRecipe recipe) {
//        return new NewPoweredCraftingTask<>(energyStorage, recipe, level, getRecipeWrapper()) {
//            @Override
//            protected boolean takeOutputs(List<ItemStack> outputs, boolean simulate) {
//                // Alloy smelting recipes only have a single output
//                ItemStack out = outputs.get(0);
//
//                MachineInventory inv = getInventory();
//
//                if (inv.insertItem(3, out, true).isEmpty()) {
//                    if (!simulate)
//                        inv.insertItem(3, out, false);
//                    return true;
//                }
//
//                return false;
//            }
//
//            @Override
//            protected CompoundTag serializeRecipe(CompoundTag tag, IAlloySmeltingRecipe recipe) {
//                if (recipe instanceof WrappedSmeltingRecipe wrappedSmeltingRecipe) {
//                    tag.putInt("multiplier", wrappedSmeltingRecipe.multiplier);
//                }
//                return super.serializeRecipe(tag, recipe);
//            }
//
//            @Override
//            protected @Nullable IAlloySmeltingRecipe loadRecipe(CompoundTag nbt) {
//                ResourceLocation id = new ResourceLocation(nbt.getString("id"));
//                return level.getRecipeManager().byKey(id).map(recipe -> {
//                    if (recipe.getType() == MachineRecipes.Types.ALLOY_SMELTING) {
//                        return (AlloySmeltingRecipe) recipe;
//                    } else if (recipe.getType() == RecipeType.SMELTING) {
//                        int multiplier = nbt.getInt("multiplier");
//                        return new WrappedSmeltingRecipe((SmeltingRecipe) recipe, multiplier);
//                    }
//                    return null;
//                }).orElse(null);
//            }
//        };
        return null;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new AlloySmelterMenu(this, inventory, containerId);
    }

}

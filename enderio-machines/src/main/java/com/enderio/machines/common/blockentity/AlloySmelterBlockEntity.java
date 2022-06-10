package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.machines.recipes.IAlloySmeltingRecipe;
import com.enderio.api.machines.recipes.OutputStack;
import com.enderio.api.recipe.CountedIngredient;
import com.enderio.base.common.blockentity.sync.EnumDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PoweredTaskMachineEntity;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.compat.VanillaAlloySmeltingRecipe;
import com.enderio.machines.common.init.MachineCapacitorKeys;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.AlloySmelterMenu;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

// TODO: Award XP

public abstract class AlloySmelterBlockEntity extends PoweredTaskMachineEntity<PoweredCraftingTask<IAlloySmeltingRecipe, IAlloySmeltingRecipe.Container>> {
    public static class Simple extends AlloySmelterBlockEntity {

        public Simple(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(AlloySmelterMode.ALLOYS,
                MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_CONSUME.get(),
                pType, pWorldPosition, pBlockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.SIMPLE;
        }

        @Override
        public int getEnergyLeakRate() {
            return 5; // TODO: Config
        }
    }

    public static class Furnace extends AlloySmelterBlockEntity {

        public Furnace(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(AlloySmelterMode.FURNACE,
                MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_CONSUME.get(),
                pType, pWorldPosition, pBlockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.SIMPLE;
        }

        @Override
        public int getEnergyLeakRate() {
            return 5; // TODO: Config
        }
    }

    public static class Standard extends AlloySmelterBlockEntity {

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

    public static class Enhanced extends AlloySmelterBlockEntity {

        public Enhanced(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
            super(AlloySmelterMode.ALL,
                MachineCapacitorKeys.ENHANCED_ALLOY_SMELTER_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.ENHANCED_ALLOY_SMELTER_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.ENHANCED_ALLOY_SMELTER_ENERGY_CONSUME.get(),
                pType, pWorldPosition, pBlockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.ENHANCED;
        }
    }

    private final AlloySmelterMode defaultMode;
    private AlloySmelterMode mode;
    private boolean inventoryChanged = true;

    private final IAlloySmeltingRecipe.Container container;

    public AlloySmelterBlockEntity(AlloySmelterMode mode, CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey energyUseKey,
        BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(capacityKey, transferKey, energyUseKey, pType, pWorldPosition, pBlockState);

        this.defaultMode = mode;
        this.mode = mode;

        // Create the crafting inventory. Used for context in the vanilla recipe wrapper.
        this.container = new IAlloySmeltingRecipe.Container(getInventory());

        // This can be changed by the gui for the normal and enhanced machines.
        if (getTier() != MachineTier.SIMPLE) {
            add2WayDataSlot(new EnumDataSlot<>(this::getMode, this::setMode, SyncMode.GUI));
        }
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
        PoweredCraftingTask<IAlloySmeltingRecipe, IAlloySmeltingRecipe.Container> currentTask = getCurrentTask();
        if (currentTask != null) {
            MachineInventory inventory = getInventory();
            ItemStack currentContents = inventory.getStackInSlot(slot);
            inventory.setStackInSlot(slot, stack);

            boolean accept = currentTask.getRecipe().matches(container, level);

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
    protected @Nullable PoweredCraftingTask<IAlloySmeltingRecipe, IAlloySmeltingRecipe.Container> getNextTask() {
        if (level == null)
            return null;

        // Mark inventory changed false again
        inventoryChanged = false;

        // Search for an alloy recipe.
        if (getMode().canAlloy()) {
            var task = level
                .getRecipeManager()
                .getRecipeFor(MachineRecipes.Types.ALLOY_SMELTING, container, level)
                .map(this::createTask)
                .orElse(null);

            if (task != null)
                return task;
        }

        // Search for a smelting recipe.
        if (getMode().canSmelt()) {
            var task = level
                .getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, getRecipeWrapper(), level)
                .map(recipe -> createTask(new VanillaAlloySmeltingRecipe(recipe)))
                .orElse(null);

            if (task != null)
                return task;
        }

        return null;
    }

    @Override
    protected @Nullable PoweredCraftingTask<IAlloySmeltingRecipe, IAlloySmeltingRecipe.Container> loadTask(CompoundTag nbt) {
        PoweredCraftingTask<IAlloySmeltingRecipe, IAlloySmeltingRecipe.Container> task = createTask(null);
        task.deserializeNBT(nbt);
        return task;
    }

    private PoweredCraftingTask<IAlloySmeltingRecipe, IAlloySmeltingRecipe.Container> createTask(@Nullable IAlloySmeltingRecipe recipe) {
        return new PoweredCraftingTask<>(energyStorage, recipe, level, container) {

            @Override
            protected void takeInputs(IAlloySmeltingRecipe recipe) {
                // TODO: Maybe delegate this to the recipe by passing in the MachineRecipe itself? Or just the container?
                //       I'm really not a fan of how much code this leaves inside machines.
                //       But then again the machine should really decide how to deal with inputs and outputs, rather than the recipe it processes I suppose?
                if (recipe instanceof AlloySmeltingRecipe) {
                    // Track which ingredients have been consumed
                    MachineInventory inv = getInventory();
                    List<CountedIngredient> inputs = recipe.getInputs();
                    boolean[] consumed = new boolean[3];

                    // Iterate over the slots
                    for (int i = 0; i < 3; i++) {
                        ItemStack stack = inv.getStackInSlot(i);

                        // Iterate over the inputs
                        for (int j = 0; j < 3; j++) {

                            // If this ingredient has been matched already, continue
                            if (consumed[j])
                                continue;

                            if (j < inputs.size()) {
                                // If we expect an input, test we have a match for it.
                                CountedIngredient input = inputs.get(j);

                                if (input.test(stack)) {
                                    consumed[j] = true;
                                    stack.shrink(input.count());
                                }
                            } else if (stack.isEmpty()) {
                                // If we don't expect an input, make sure we have a blank for it.
                                consumed[j] = true;
                            }
                        }
                    }

                    // Only accepted *1* times inputs.
                    container.setInputsTaken(1);
                } else if (recipe instanceof VanillaAlloySmeltingRecipe) {
                    MachineInventory inv = getInventory();
                    CountedIngredient input = recipe.getInputs().get(0);

                    // Iterate over the slots
                    int consumeCount = 0;
                    for (int i = 0; i < 3; i++) {
                        ItemStack stack = inv.getStackInSlot(i);

                        if (input.test(stack)) {
                            stack.shrink(input.count());
                            consumeCount++;
                        }
                    }

                    container.setInputsTaken(consumeCount);
                }
            }

            @Override
            protected boolean takeOutputs(List<OutputStack> outputs, boolean simulate) {
                // Log some errors if a recipe is doing something wrong.
                if (outputs.size() > 1) {
                    EIOMachines.LOGGER.error("Alloy smelting recipe {} tried to have more than one result stack!", recipe.getId());
                }

                // Get the output
                OutputStack stack = outputs.get(0);

                // Ensure the recipe is returning a valid item.
                if (!stack.isItem()) {
                    EIOMachines.LOGGER.error("Alloy smelting recipe {} didn't return an item!", recipe.getId());
                    return false;
                }

                // Add the item to our inventory.
                MachineInventory inv = getInventory();
                if (inv.insertItem(3, stack.getItem(), true).isEmpty()) {
                    if (!simulate)
                        inv.insertItem(3, stack.getItem(), false);
                    return true;
                }
                return false;
            }

            @Override
            protected @Nullable IAlloySmeltingRecipe loadRecipe(CompoundTag nbt) {
                ResourceLocation id = new ResourceLocation(nbt.getString("id"));
                return level.getRecipeManager().byKey(id).map(recipe -> {
                    if (recipe.getType() == MachineRecipes.Types.ALLOY_SMELTING) {
                        return (AlloySmeltingRecipe) recipe;
                    } else if (recipe.getType() == RecipeType.SMELTING) {
                        return new VanillaAlloySmeltingRecipe((SmeltingRecipe) recipe);
                    }
                    return null;
                }).orElse(null);
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new AlloySmelterMenu(this, inventory, containerId);
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        if (getTier() != MachineTier.SIMPLE) {
            pTag.putInt("mode", this.mode.ordinal());
        }
        pTag.putInt("inputs_taken", container.getInputsTaken());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        if (getTier() != MachineTier.SIMPLE) {
            try {
                mode = AlloySmelterMode.values()[pTag.getInt("mode")];
            } catch (IndexOutOfBoundsException ex) { // In case something happens in the future.
                mode = defaultMode;
            }
        }
        container.setInputsTaken(pTag.getInt("inputs_taken"));
        super.load(pTag);
    }
}

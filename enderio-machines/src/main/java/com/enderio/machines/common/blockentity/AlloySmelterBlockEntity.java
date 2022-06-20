package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorKey;
import com.enderio.api.recipe.CountedIngredient;
import com.enderio.base.common.blockentity.sync.EnumDataSlot;
import com.enderio.base.common.blockentity.sync.SyncMode;
import com.enderio.machines.EIOMachines;
import com.enderio.machines.common.MachineTier;
import com.enderio.machines.common.blockentity.base.PoweredCraftingMachine;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.compat.VanillaAlloySmeltingRecipe;
import com.enderio.machines.common.init.MachineCapacitorKeys;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.AlloySmelterMenu;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import com.enderio.machines.common.recipe.IAlloySmeltingRecipe;
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
import java.util.Optional;

// TODO: Award XP

public abstract class AlloySmelterBlockEntity extends PoweredCraftingMachine<IAlloySmeltingRecipe, IAlloySmeltingRecipe.Container> {

    // region Tiers

    public static class Simple extends AlloySmelterBlockEntity {

        public Simple(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
            super(AlloySmelterMode.ALLOYS,
                MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_CONSUME.get(),
                type, worldPosition, blockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.SIMPLE;
        }

        @Override
        public int getEnergyLeakPerSecond() {
            return 5; // TODO: Config
        }
    }

    public static class Furnace extends AlloySmelterBlockEntity {

        public Furnace(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
            super(AlloySmelterMode.FURNACE,
                MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.SIMPLE_ALLOY_SMELTER_ENERGY_CONSUME.get(),
                type, worldPosition, blockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.SIMPLE;
        }

        @Override
        public int getEnergyLeakPerSecond() {
            return 1;
        }
    }

    public static class Standard extends AlloySmelterBlockEntity {

        public Standard(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
            super(AlloySmelterMode.ALL,
                MachineCapacitorKeys.ALLOY_SMELTER_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.ALLOY_SMELTER_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.ALLOY_SMELTER_ENERGY_CONSUME.get(),
                type, worldPosition, blockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.STANDARD;
        }
    }

    public static class Enhanced extends AlloySmelterBlockEntity {

        public Enhanced(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
            super(AlloySmelterMode.ALL,
                MachineCapacitorKeys.ENHANCED_ALLOY_SMELTER_ENERGY_CAPACITY.get(),
                MachineCapacitorKeys.ENHANCED_ALLOY_SMELTER_ENERGY_TRANSFER.get(),
                MachineCapacitorKeys.ENHANCED_ALLOY_SMELTER_ENERGY_CONSUME.get(),
                type, worldPosition, blockState);
        }

        @Override
        public MachineTier getTier() {
            return MachineTier.ENHANCED;
        }
    }

    // endregion

    /**
     * The alloying mode for the machine.
     * Determines which recipes it can craft.
     */
    private AlloySmelterMode mode;

    /**
     * The container used for crafting context.
     */
    private final IAlloySmeltingRecipe.Container container;

    public AlloySmelterBlockEntity(AlloySmelterMode mode, CapacitorKey capacityKey, CapacitorKey transferKey, CapacitorKey energyUseKey,
        BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(MachineRecipes.Types.ALLOY_SMELTING, capacityKey, transferKey, energyUseKey, pType, pWorldPosition, pBlockState);
        this.mode = mode;

        // Create the crafting inventory. Used for context in the vanilla recipe wrapper.
        this.container = new IAlloySmeltingRecipe.Container(getInventory());

        // This can be changed by the gui for the normal and enhanced machines.
        if (getTier() != MachineTier.SIMPLE) {
            add2WayDataSlot(new EnumDataSlot<>(this::getMode, this::setMode, SyncMode.GUI));
        }
    }

    /**
     * Get the alloy smelting mode.
     */
    public AlloySmelterMode getMode() {
        // Lock to default mode if this is a simple machine.
        return mode;
    }

    /**
     * Set the alloy smelting mode.
     * Calling on a simple tier machine does nothing.
     */
    public void setMode(AlloySmelterMode mode) {
        // Disallow changing the simple mode.
        if (getTier() == MachineTier.SIMPLE)
            return;
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
        var currentTask = getCurrentTask();
        if (currentTask != null) {
            var currentRecipe = currentTask.getRecipe();
            if (currentRecipe != null) {
                MachineInventory inventory = getInventory();
                ItemStack currentContents = inventory.getStackInSlot(slot);
                inventory.setStackInSlot(slot, stack);
                boolean accept = currentRecipe.matches(container, level);
                inventory.setStackInSlot(slot, currentContents);
                return accept;
            }
        }
        return true;
    }

    @Override
    protected Optional<IAlloySmeltingRecipe> findRecipe() {
        // Get alloy smelting recipe (Default)
        if (getMode().canAlloy()) {
            var recipe = super.findRecipe();
            if (recipe.isPresent())
                return recipe;
        }

        // Get vanilla smelting recipe.
        if (getMode().canSmelt()) {
            var recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, getContainer(), level);
            if (recipe.isPresent())
                return Optional.of(new VanillaAlloySmeltingRecipe(recipe.get()));
        }
        return Optional.empty();
    }

    @Override
    protected PoweredCraftingTask<IAlloySmeltingRecipe, IAlloySmeltingRecipe.Container> createTask(@Nullable IAlloySmeltingRecipe recipe) {
        return new PoweredCraftingTask<>(this, container, 3, recipe) {
            @Override
            protected void takeInputs(IAlloySmeltingRecipe recipe) {
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
            protected @Nullable IAlloySmeltingRecipe loadRecipe(ResourceLocation id) {
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

    @Override
    protected IAlloySmeltingRecipe.Container getContainer() {
        return container;
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        if (getTier() != MachineTier.SIMPLE) {
            pTag.putInt("Mode", this.mode.ordinal());
        }
        pTag.putInt("InputsTaken", container.getInputsTaken());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        if (getTier() != MachineTier.SIMPLE) {
            try {
                mode = AlloySmelterMode.values()[pTag.getInt("Mode")];
            } catch (IndexOutOfBoundsException ex) { // In case something happens in the future.
                EIOMachines.LOGGER.error("Invalid alloy smelter mode loaded from NBT. Ignoring.");
            }
        }
        container.setInputsTaken(pTag.getInt("InputsTaken"));
        super.load(pTag);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new AlloySmelterMenu(this, inventory, containerId);
    }
}

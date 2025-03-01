package com.enderio.machines.common.blocks.alloy;

import com.enderio.base.api.capacitor.CapacitorModifier;
import com.enderio.base.api.capacitor.QuadraticScalable;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.blocks.base.blockentity.PoweredMachineBlockEntity;
import com.enderio.machines.common.blocks.base.blockentity.flags.CapacitorSupport;
import com.enderio.machines.common.blocks.base.inventory.MachineInventory;
import com.enderio.machines.common.blocks.base.inventory.MachineInventoryLayout;
import com.enderio.machines.common.blocks.base.inventory.MultiSlotAccess;
import com.enderio.machines.common.blocks.base.inventory.SingleSlotAccess;
import com.enderio.machines.common.blocks.base.task.PoweredCraftingMachineTask;
import com.enderio.machines.common.blocks.base.task.host.CraftingMachineTaskHost;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineDataComponents;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import com.enderio.machines.common.recipe.RecipeCaches;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

// TODO: Award XP

public class AlloySmelterBlockEntity extends PoweredMachineBlockEntity {

    public static final MultiSlotAccess INPUTS = new MultiSlotAccess();
    public static final SingleSlotAccess OUTPUT = new SingleSlotAccess();

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.ALLOY_SMELTER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.ALLOY_SMELTER_USAGE);

    /**
     * The alloying mode for the machine.
     * Determines which recipes it can craft.
     */
    private AlloySmelterMode mode = AlloySmelterMode.ALL;

    protected final AlloySmeltingMachineTaskHost craftingTaskHost;

    private static final Logger LOGGER = LogUtils.getLogger();

    public static AlloySmelterBlockEntity factory(BlockPos pWorldPosition, BlockState pBlockState) {
        return new AlloySmelterBlockEntity(MachineBlockEntities.ALLOY_SMELTER.get(), pWorldPosition, pBlockState,
                CapacitorSupport.REQUIRED);
    }

    protected AlloySmelterBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState,
            CapacitorSupport capacitorSupport) {
        super(pType, pWorldPosition, pBlockState, true, capacitorSupport, EnergyIOMode.Input, CAPACITY, USAGE);

        // Crafting task host
        craftingTaskHost = new AlloySmeltingMachineTaskHost(this, this::canAcceptTask,
                MachineRecipes.ALLOY_SMELTING.type().get(), this::createTask, this::createRecipeInput);
    }

    protected boolean canAcceptTask() {
        return hasEnergy() && !isRedstoneBlocked();
    }

    /**
     * Get the alloy smelting mode.
     */
    public AlloySmelterMode getMode() {
        return mode;
    }

    public void setMode(AlloySmelterMode mode) {
        this.mode = mode;

        if (level != null && !level.isClientSide) {
            craftingTaskHost.newTaskAvailable();
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new AlloySmelterMenu(containerId, inventory, this);
    }

    @Override
    public void serverTick() {
        super.serverTick();

        if (canAct()) {
            craftingTaskHost.tick();
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        craftingTaskHost.onLevelReady();
    }

    // region Primitive Smelter Shims

    /**
     * Whether the mode is restricted.
     * Used to disable serialization of the mode and sync of the slot when this is the primitive variant.
     */
    public boolean isPrimitiveSmelter() {
        return false;
    }

    protected MultiSlotAccess getInputsSlotAccess() {
        return INPUTS;
    }

    protected SingleSlotAccess getOutputSlotAccess() {
        return OUTPUT;
    }

    // endregion

    // region Inventory

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
                .inputSlot(3, this::acceptSlotInput)
                .slotAccess(INPUTS)
                .outputSlot()
                .slotAccess(OUTPUT)
                .capacitor()
                .build();
    }

    protected boolean acceptSlotInput(int slot, ItemStack stack) {
        if (getMode().canAlloy()) {
            if (RecipeCaches.ALLOY_SMELTING_ONLY_ALLOY.hasValidRecipeIf(getInventory(), getInputsSlotAccess(), slot,
                    stack)) {
                return true;
            }
        }

        if (getMode().canSmelt()) {
            // Check all items are the same, or will be
            var currentStacks = getInputsSlotAccess().getAccesses()
                    .stream()
                    .map(i -> i.isSlot(slot) ? stack : i.getItemStack(getInventory()))
                    .filter(i -> !i.isEmpty())
                    .toList();

            if (currentStacks.stream().allMatch(i -> i.is(stack.getItem())) || currentStacks.size() == 1) {
                return RecipeCaches.ALLOY_SMELTING_ONLY_SMELTING.hasRecipe(List.of(stack));
            }
        }

        return false;
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        craftingTaskHost.newTaskAvailable();
    }

    private AlloySmeltingRecipe.Input createRecipeInput() {
        return new AlloySmeltingRecipe.Input(getInputsSlotAccess().getItemStacks(getInventory()), 1);
    }

    // endregion

    // region Crafting Task

    public float getCraftingProgress() {
        return craftingTaskHost.getProgress();
    }

    @Override
    public boolean isActive() {
        return canAct() && hasEnergy() && craftingTaskHost.hasTask();
    }

    protected AlloySmeltingMachineTask createTask(Level level, AlloySmeltingRecipe.Input recipeInput,
            @Nullable RecipeHolder<AlloySmeltingRecipe> recipe) {
        return new AlloySmeltingMachineTask(level, getInventory(), getEnergyStorage(), recipeInput,
                getInputsSlotAccess(), getOutputSlotAccess(), recipe);
    }

    protected static class AlloySmeltingMachineTask
            extends PoweredCraftingMachineTask<AlloySmeltingRecipe, AlloySmeltingRecipe.Input> {
        private final MultiSlotAccess inputs;
        private int inputsConsumed;

        public AlloySmeltingMachineTask(@NotNull Level level, MachineInventory inventory,
                IMachineEnergyStorage energyStorage, AlloySmeltingRecipe.Input recipeInput, MultiSlotAccess inputs,
                SingleSlotAccess outputSlot, @Nullable RecipeHolder<AlloySmeltingRecipe> recipe) {
            super(level, inventory, energyStorage, recipeInput, outputSlot, recipe);
            this.inputs = inputs;
        }

        @Override
        protected AlloySmeltingRecipe.Input prepareToDetermineOutputs(AlloySmeltingRecipe recipe,
                AlloySmeltingRecipe.Input recipeInput) {
            // This handles the output multiplication for vanilla smelting recipes.
            if (recipe.isSmelting()) {
                SizedIngredient input = recipe.inputs().getFirst();

                int inputCount = 0;
                for (int i = inputs.size() - 1; i >= 0; i--) {
                    ItemStack itemStack = inputs.get(i).getItemStack(getInventory());
                    if (input.test(itemStack)) {
                        inputCount += Math.min(3 - inputCount, itemStack.getCount());
                    }
                }

                inputsConsumed = inputCount;
                return recipeInput.withInputsConsumed(inputsConsumed);
            } else {
                inputsConsumed = 1;
                return recipeInput;
            }
        }

        @Override
        protected void consumeInputs(AlloySmeltingRecipe recipe) {
            MachineInventory inv = getInventory();

            if (recipe.isSmelting()) {
                SizedIngredient input = recipe.inputs().get(0);

                int consumed = 0;
                for (int i = inputs.size() - 1; i >= 0; i--) {
                    ItemStack itemStack = inputs.get(i).getItemStack(getInventory());
                    if (input.test(itemStack)) {
                        int consumedNow = Math.min(inputsConsumed - consumed, itemStack.getCount());
                        itemStack.shrink(consumedNow);
                        consumed += consumedNow;
                    }
                }
            } else {
                // Track which ingredients have been consumed
                List<SizedIngredient> inputs = recipe.inputs();
                boolean[] consumed = new boolean[3];

                // Iterate over the slots
                for (SingleSlotAccess slot : this.inputs.getAccesses()) {
                    ItemStack stack = slot.getItemStack(inv);

                    // Iterate over the inputs
                    for (int i = 0; i < 3; i++) {

                        // If this ingredient has been matched already, continue
                        if (consumed[i]) {
                            continue;
                        }

                        if (i < inputs.size()) {
                            // If we expect an input, test we have a match for it.
                            SizedIngredient input = inputs.get(i);

                            if (input.test(stack)) {
                                consumed[i] = true;
                                stack.shrink(input.count());
                            }
                        } else if (stack.isEmpty()) {
                            // If we don't expect an input, make sure we have a blank for it.
                            consumed[i] = true;
                        }
                    }
                }
            }
        }

        @Override
        public void deserializeNBT(HolderLookup.Provider lookupProvider, CompoundTag nbt) {
            super.deserializeNBT(lookupProvider, nbt);
            inputsConsumed = nbt.getInt(MachineNBTKeys.PROCESSED_INPUTS);
        }

        @Override
        public CompoundTag serializeNBT(HolderLookup.Provider lookupProvider) {
            var tag = super.serializeNBT(lookupProvider);
            tag.putInt(MachineNBTKeys.PROCESSED_INPUTS, inputsConsumed);
            return tag;
        }
    }

    protected class AlloySmeltingMachineTaskHost
            extends CraftingMachineTaskHost<AlloySmeltingRecipe, AlloySmeltingRecipe.Input> {
        public AlloySmeltingMachineTaskHost(EnderBlockEntity blockEntity, Supplier<Boolean> canAcceptNewTask,
                RecipeType<AlloySmeltingRecipe> recipeType,
                CraftingMachineTaskFactory<AlloySmeltingMachineTask, AlloySmeltingRecipe, AlloySmeltingRecipe.Input> taskFactory,
                Supplier<AlloySmeltingRecipe.Input> recipeInputSupplier) {
            super(blockEntity, canAcceptNewTask, recipeType, taskFactory, recipeInputSupplier);
        }

        @Override
        protected Optional<RecipeHolder<AlloySmeltingRecipe>> findRecipe() {
            var level = getLevel();
            if (level == null) {
                return Optional.empty();
            }

            var optionalRecipe = super.findRecipe();
            if (optionalRecipe.isEmpty()) {
                return Optional.empty();
            }

            if (optionalRecipe.get().value().isSmelting() ? !getMode().canSmelt() : !getMode().canAlloy()) {
                return Optional.empty();
            }

            return optionalRecipe;
        }
    }

    // endregion

    // region Serialization

    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        craftingTaskHost.save(lookupProvider, pTag);

        if (!isPrimitiveSmelter()) {
            pTag.putInt(MachineNBTKeys.MACHINE_MODE, this.mode.ordinal());
        }

        super.saveAdditional(pTag, lookupProvider);
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        craftingTaskHost.load(lookupProvider, pTag);

        if (!isPrimitiveSmelter()) {
            try {
                mode = AlloySmelterMode.values()[pTag.getInt(MachineNBTKeys.MACHINE_MODE)];
            } catch (IndexOutOfBoundsException ex) { // In case something happens in the future.
                LOGGER.error("Invalid alloy smelter mode loaded from NBT. Ignoring.");
            }
        }

        super.loadAdditional(pTag, lookupProvider);
    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components) {
        super.applyImplicitComponents(components);

        // TODO: 1.21: Write crafting host into the item components.

        if (!isPrimitiveSmelter()) {
            mode = components.getOrDefault(MachineDataComponents.ALLOY_SMELTER_MODE, AlloySmelterMode.ALL);
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        if (!isPrimitiveSmelter()) {
            components.set(MachineDataComponents.ALLOY_SMELTER_MODE, mode);
        }
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove(MachineNBTKeys.MACHINE_MODE);
    }

    // endregion
}

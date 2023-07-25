package com.enderio.machines.common.blockentity;

import com.enderio.EnderIO;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.core.common.network.slot.EnumNetworkDataSlot;
import com.enderio.core.common.recipes.CountedIngredient;
import com.enderio.machines.common.MachineNBTKeys;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.blockentity.task.PoweredCraftingMachineTask;
import com.enderio.machines.common.blockentity.task.host.CraftingMachineTaskHost;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.integrations.vanilla.VanillaAlloySmeltingRecipe;
import com.enderio.machines.common.io.energy.IMachineEnergyStorage;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.AlloySmelterMenu;
import com.enderio.machines.common.recipe.AlloySmeltingRecipe;
import com.enderio.machines.common.recipe.RecipeCaches;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

// TODO: Award XP

public class AlloySmelterBlockEntity extends PoweredMachineBlockEntity {

    public static final MultiSlotAccess INPUTS = new MultiSlotAccess();
    public static final SingleSlotAccess OUTPUT = new SingleSlotAccess();

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.ALLOY_SMELTER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.ALLOY_SMELTER_USAGE);

    /**
     * The alloying mode for the machine.
     * Determines which recipes it can craft.
     */
    private AlloySmelterMode mode = AlloySmelterMode.ALL;

    protected final AlloySmeltingMachineTaskHost craftingTaskHost;

    @Nullable
    private final EnumNetworkDataSlot<AlloySmelterMode> modeDataSlot;

    public AlloySmelterBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(EnergyIOMode.Input, CAPACITY, USAGE, pType, pWorldPosition, pBlockState);

        // Crafting task host
        craftingTaskHost = new AlloySmeltingMachineTaskHost(this, this::canAcceptTask,
            MachineRecipes.ALLOY_SMELTING.type().get(), new AlloySmeltingRecipe.ContainerWrapper(isPrimitiveSmelter(), getInventoryNN()), this::createTask);

        // This can be changed by the gui for the normal and enhanced machines.
        if (!isPrimitiveSmelter()) {
            modeDataSlot = new EnumNetworkDataSlot<>(AlloySmelterMode.class, this::getMode, m -> {
                mode = m;
                craftingTaskHost.newTaskAvailable();
            });
            addDataSlot(modeDataSlot);
        } else {
            modeDataSlot = null;
        }
    }

    protected boolean canAcceptTask() {
        return hasEnergy();
    }

    /**
     * Get the alloy smelting mode.
     */
    public AlloySmelterMode getMode() {
        return mode;
    }

    /**
     * Set the alloy smelting mode.
     * Calling on a simple tier machine does nothing.
     */
    public void setMode(AlloySmelterMode mode) {
        if (level != null && level.isClientSide()) {
            clientUpdateSlot(modeDataSlot, mode);
        } else {
            this.mode = mode;
            craftingTaskHost.newTaskAvailable();
        }
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new AlloySmelterMenu(this, inventory, containerId);
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
    protected boolean isPrimitiveSmelter() {
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
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .inputSlot(3, this::acceptSlotInput)
            .slotAccess(INPUTS)
            .outputSlot()
            .slotAccess(OUTPUT)
            .capacitor().build();
    }

    protected boolean acceptSlotInput(int slot, ItemStack stack) {
        if (getMode().canAlloy()) {
            if (RecipeCaches.ALLOY_SMELTING.hasValidRecipeIf(getInventoryNN(), getInputsSlotAccess(), slot, stack)) {
                return true;
            }
        }

        if (getMode().canSmelt()) {
            // Check all items are the same, or will be
            var currentStacks = getInputsSlotAccess().getAccesses().stream()
                .map(i -> i.isSlot(slot) ? stack : i.getItemStack(getInventoryNN()))
                .filter(i -> !i.isEmpty())
                .toList();

            if (currentStacks.stream().allMatch(i -> i.is(stack.getItem())) || currentStacks.size() == 1) {
                if (RecipeCaches.SMELTING.hasRecipe(List.of(stack))) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        craftingTaskHost.newTaskAvailable();
    }

    // endregion

    // region Crafting Task

    public float getCraftingProgress() {
        return craftingTaskHost.getProgress();
    }

    @Override
    protected boolean isActive() {
        return canAct() && hasEnergy() && craftingTaskHost.hasTask();
    }

    protected AlloySmeltingMachineTask createTask(Level level, AlloySmeltingRecipe.ContainerWrapper container, @Nullable AlloySmeltingRecipe recipe) {
        return new AlloySmeltingMachineTask(level, getInventoryNN(), getEnergyStorage(), container, getInputsSlotAccess(), getOutputSlotAccess(), recipe);
    }

    protected static class AlloySmeltingMachineTask extends PoweredCraftingMachineTask<AlloySmeltingRecipe, AlloySmeltingRecipe.ContainerWrapper> {
        private final MultiSlotAccess inputs;

        public AlloySmeltingMachineTask(@NotNull Level level, MachineInventory inventory, IMachineEnergyStorage energyStorage,
            AlloySmeltingRecipe.ContainerWrapper container, MultiSlotAccess inputs, SingleSlotAccess outputSlot, @Nullable AlloySmeltingRecipe recipe) {
            super(level, inventory, energyStorage, container, outputSlot, recipe);
            this.inputs = inputs;
        }

        @Override
        protected void onDetermineOutputs(AlloySmeltingRecipe recipe) {
            // This handles the output multiplication for vanilla smelting recipes.
            if (recipe instanceof VanillaAlloySmeltingRecipe) {
                CountedIngredient input = recipe.getInputs().get(0);

                int inputCount = 0;
                for (int i = inputs.size() - 1; i >= 0; i--) {
                    ItemStack itemStack = inputs.get(i).getItemStack(getInventory());
                    if (input.test(itemStack)) {
                        inputCount += Math.min(3 - inputCount, itemStack.getCount());
                    }
                }
                container.setInputsTaken(inputCount);
            } else {
                container.setInputsTaken(1);
            }
        }

        @Override
        protected void consumeInputs(AlloySmeltingRecipe recipe) {
            MachineInventory inv = getInventory();

            if (recipe instanceof VanillaAlloySmeltingRecipe) {
                CountedIngredient input = recipe.getInputs().get(0);

                int consumed = 0;
                for (int i = inputs.size() - 1; i >= 0; i--) {
                    ItemStack itemStack = inputs.get(i).getItemStack(getInventory());
                    if (input.test(itemStack)) {
                        int consumedNow = Math.min(container.getInputsTaken() - consumed, itemStack.getCount());
                        itemStack.shrink(consumedNow);
                        consumed += consumedNow;
                    }
                }
            } else {
                // Track which ingredients have been consumed
                List<CountedIngredient> inputs = recipe.getInputs();
                boolean[] consumed = new boolean[3];

                // Iterate over the slots
                for (SingleSlotAccess slot : this.inputs.getAccesses()) {
                    ItemStack stack = slot.getItemStack(inv);

                    // Iterate over the inputs
                    for (int i = 0; i < 3; i++) {

                        // If this ingredient has been matched already, continue
                        if (consumed[i])
                            continue;

                        if (i < inputs.size()) {
                            // If we expect an input, test we have a match for it.
                            CountedIngredient input = inputs.get(i);

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

        @Nullable
        @Override
        protected AlloySmeltingRecipe loadRecipe(ResourceLocation id) {
            return level.getRecipeManager().byKey(id).map(recipe -> {
                if (recipe.getType() == MachineRecipes.ALLOY_SMELTING.type().get()) {
                    return (AlloySmeltingRecipe) recipe;
                } else if (recipe.getType() == RecipeType.SMELTING) {
                    return new VanillaAlloySmeltingRecipe((SmeltingRecipe) recipe);
                }
                return null;
            }).orElse(null);
        }
    }

    protected class AlloySmeltingMachineTaskHost extends CraftingMachineTaskHost<AlloySmeltingRecipe, AlloySmeltingRecipe.ContainerWrapper> {
        public AlloySmeltingMachineTaskHost(EnderBlockEntity blockEntity, Supplier<Boolean> canAcceptNewTask, RecipeType<AlloySmeltingRecipe> recipeType,
            AlloySmeltingRecipe.ContainerWrapper container,
            ICraftingMachineTaskFactory<AlloySmeltingMachineTask, AlloySmeltingRecipe, AlloySmeltingRecipe.ContainerWrapper> taskFactory) {
            super(blockEntity, canAcceptNewTask, recipeType, container, taskFactory);
        }

        @Override
        protected Optional<AlloySmeltingRecipe> findRecipe() {
            var level = getLevel();
            if (level == null) {
                return Optional.empty();
            }

            // Get alloy smelting recipe (Default)
            if (getMode().canAlloy()) {
                var recipe = super.findRecipe();
                if (recipe.isPresent())
                    return recipe;
            }

            // Get vanilla smelting recipe.
            if (getMode().canSmelt()) {
                for (int i = 0; i < AlloySmelterBlockEntity.INPUTS.size(); i++) {
                    var recipe = level.getRecipeManager()
                        .getRecipeFor(RecipeType.SMELTING, new ContainerSubWrapper(getContainer(), i), level);
                    if (recipe.isPresent())
                        return Optional.of(new VanillaAlloySmeltingRecipe(recipe.get()));
                }
            }
            return Optional.empty();
        }
    }

    // endregion

    // region Serialization

    @Override
    public void saveAdditional(CompoundTag pTag) {
        craftingTaskHost.save(pTag);

        if (isPrimitiveSmelter()) {
            pTag.putInt(MachineNBTKeys.MACHINE_MODE, this.mode.ordinal());
        }
        pTag.putInt(MachineNBTKeys.PROCESSED_INPUTS, craftingTaskHost.getContainer().getInputsTaken());
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        craftingTaskHost.load(pTag);

        if (isPrimitiveSmelter()) {
            try {
                mode = AlloySmelterMode.values()[pTag.getInt(MachineNBTKeys.MACHINE_MODE)];
            } catch (IndexOutOfBoundsException ex) { // In case something happens in the future.
                EnderIO.LOGGER.error("Invalid alloy smelter mode loaded from NBT. Ignoring.");
            }
        }
        craftingTaskHost.getContainer().setInputsTaken(pTag.getInt(MachineNBTKeys.PROCESSED_INPUTS));
        super.load(pTag);
    }
    public record ContainerSubWrapper(AlloySmeltingRecipe.ContainerWrapper wrapper, int index) implements Container {

        @Override
        public int getContainerSize() {
            return 1;
        }

        @Override
        public boolean isEmpty() {
            return AlloySmelterBlockEntity.INPUTS.get(index).getItemStack(wrapper).isEmpty();
        }

        @Override
        public ItemStack getItem(int slot) {
            if (slot != 0)
                return ItemStack.EMPTY;
            return AlloySmelterBlockEntity.INPUTS.get(index).getItemStack(wrapper);
        }

        @Override
        public ItemStack removeItem(int slot, int amount) {
            return ItemStack.EMPTY;
        }

        @Override
        public ItemStack removeItemNoUpdate(int slot) {
            return ItemStack.EMPTY;
        }

        @Override
        public void setItem(int slot, ItemStack stack) {
        }

        @Override
        public void setChanged() {
        }

        @Override
        public boolean stillValid(Player player) {
            return false;
        }

        @Override
        public void clearContent() {
        }
    }
    // endregion
}

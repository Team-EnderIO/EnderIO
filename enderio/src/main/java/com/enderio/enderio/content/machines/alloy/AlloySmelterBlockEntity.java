package com.enderio.enderio.content.machines.alloy;

import com.enderio.core.common.blockentity.EnderBlockEntity;
import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import com.enderio.core.common.storage.slot.ResourceSlotKey;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.energy.MachineEnergyHandler;
import com.enderio.enderio.foundation.inventory.MachineSlotTemplates;
import com.enderio.enderio.foundation.recipe.MachineRecipeCaches;
import com.enderio.enderio.foundation.state.MachineStateUpdater;
import com.enderio.enderio.foundation.task.PoweredCraftingMachineTask;
import com.enderio.enderio.foundation.task.host.CraftingMachineTaskHost;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIORecipeTypes;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

// TODO: Award XP

public class AlloySmelterBlockEntity extends PoweredMachineBlockEntity {

    public static MultiResourceSlotKey<ItemResource> INPUTS = new MultiResourceSlotKey<>(3);
    public static SingleResourceSlotKey<ItemResource> OUTPUT = new SingleResourceSlotKey<>();
    public static SingleResourceSlotKey<ItemResource> CAPACITOR = new SingleResourceSlotKey<>();

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

    public AlloySmelterBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.ALLOY_SMELTER.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED, CAPACITOR, EnergyIOMode.Input,
            CAPACITY, USAGE);

        // Crafting task host
        craftingTaskHost = new AlloySmeltingMachineTaskHost(this, this::canAcceptTask,
            EIORecipeTypes.ALLOY_SMELTING.get(), this::createTask, this::createRecipeInput);
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

        if (level != null && !level.isClientSide()) {
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

    // region Inventory

    @Override
    protected @Nullable ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .add(INPUTS, SlotTemplates.input(), b -> b.filter(this::acceptSlotInput))
            .add(OUTPUT, SlotTemplates.output())
            .add(CAPACITOR, MachineSlotTemplates.capacitor())
            .build();
    }

    protected boolean acceptSlotInput(int slot, ItemResource resource) {
        if (getMode().canAlloy()) {
            if (MachineRecipeCaches.ALLOY_SMELTING_ONLY_ALLOY.hasValidRecipeIf(getInventory(), INPUTS, slot, resource.toStack())) {
                return true;
            }
        }

        if (getMode().canSmelt()) {
            // Check all items are the same, or will be
            var currentItems = INPUTS.slots()
                    .stream()
                    .map(i -> i.index(getInventory()) == slot ? resource : getInventory().getResource(i))
                    .filter(i -> !i.isEmpty())
                    .toList();

            if (currentItems.stream().allMatch(i -> i.is(resource.getItem())) || currentItems.size() == 1) {
                return MachineRecipeCaches.ALLOY_SMELTING_ONLY_SMELTING.hasRecipe(List.of(resource.toStack()));
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
        return new AlloySmeltingRecipe.Input(getInventory().getStacks(INPUTS), 1);
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
        return new AlloySmeltingMachineTask(level, this, getInventory(), getEnergyStorage(), recipeInput, INPUTS, OUTPUT,
                recipe);
    }

    protected static class AlloySmeltingMachineTask
            extends PoweredCraftingMachineTask<AlloySmeltingRecipe, AlloySmeltingRecipe.Input> {
        private final MultiResourceSlotKey<ItemResource> inputs;
        private int inputsConsumed;

        public AlloySmeltingMachineTask(
            Level level,
            MachineStateUpdater machineStateUpdater,
            ItemStorage inventory,
            MachineEnergyHandler energyStorage,
            AlloySmeltingRecipe.Input recipeInput,
            MultiResourceSlotKey<ItemResource> inputs,
            ResourceSlotKey<ItemResource> outputSlot,
            @Nullable RecipeHolder<AlloySmeltingRecipe> recipe) {
            super(level, machineStateUpdater, inventory, null, energyStorage, recipeInput, outputSlot, recipe);
            this.inputs = inputs;
        }

        @Override
        protected AlloySmeltingRecipe.Input prepareToDetermineOutputs(AlloySmeltingRecipe recipe,
                AlloySmeltingRecipe.Input recipeInput) {
            // This handles the output multiplication for vanilla smelting recipes.
            if (recipe.isSmelting()) {
                SizedIngredient input = recipe.inputs().getFirst();

                int inputCount = 0;
                for (int i = inputs.count() - 1; i >= 0; i--) {
                    ItemStack itemStack = getInventory().getStack(inputs.slot(i));
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
            ItemStorage inv = getInventory();

            if (recipe.isSmelting()) {
                SizedIngredient input = recipe.inputs().get(0);

                int consumed = 0;
                for (int i = inputs.count() - 1; i >= 0; i--) {
                    ItemStack itemStack = inv.getStack(inputs.slot(i));
                    if (input.test(itemStack)) {
                        int consumedNow = Math.min(inputsConsumed - consumed, itemStack.getCount());
                        itemStack.shrink(consumedNow);
                        inv.setStack(inputs.slot(i), itemStack);
                        consumed += consumedNow;
                    }
                }
            } else {
                // Track which ingredients have been consumed
                List<SizedIngredient> inputs = recipe.inputs();
                boolean[] consumed = new boolean[3];

                // Iterate over the slots
                for (ResourceSlotId<ItemResource> slot : this.inputs) {
                    ItemStack stack = inv.getStack(slot);

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
                                inv.setStack(slot, stack);
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
        public void serialize(ValueOutput output) {
            super.serialize(output);
            output.putInt(MachineNBTKeys.PROCESSED_INPUTS, inputsConsumed);
        }

        @Override
        public void deserialize(ValueInput input) {
            super.deserialize(input);
            // TODO: 1.21.8: is 1 a better default.
            inputsConsumed = input.getIntOr(MachineNBTKeys.PROCESSED_INPUTS, 0);
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
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild("CraftingTaskHost", craftingTaskHost);
        output.store(MachineNBTKeys.MACHINE_MODE, AlloySmelterMode.CODEC, mode);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);

        input.child("CraftingTaskHost").ifPresent(craftingTaskHost::deserialize);

        mode = input.read(MachineNBTKeys.MACHINE_MODE, AlloySmelterMode.CODEC)
            .orElse(AlloySmelterMode.ALL);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);

        // TODO: 1.21: Write crafting host into the item components.
        mode = components.getOrDefault(EIODataComponents.ALLOY_SMELTER_MODE, AlloySmelterMode.ALL);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(EIODataComponents.ALLOY_SMELTER_MODE, mode);
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard(MachineNBTKeys.MACHINE_MODE);
    }

    // endregion
}

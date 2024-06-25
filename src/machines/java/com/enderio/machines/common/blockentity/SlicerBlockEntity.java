package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.blockentity.task.CraftingMachineTask;
import com.enderio.machines.common.blockentity.task.PoweredCraftingMachineTask;
import com.enderio.machines.common.blockentity.task.host.CraftingMachineTaskHost;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.SlicerMenu;
import com.enderio.machines.common.recipe.SlicingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.Nullable;

public class SlicerBlockEntity extends PoweredMachineBlockEntity {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.SLICER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.SLICER_USAGE);

    public static final SingleSlotAccess OUTPUT = new SingleSlotAccess();
    public static final MultiSlotAccess INPUTS = new MultiSlotAccess();
    public static final SingleSlotAccess AXE = new SingleSlotAccess();
    public static final SingleSlotAccess SHEARS = new SingleSlotAccess();

    private final CraftingMachineTaskHost<SlicingRecipe, SlicingRecipe.Input> craftingTaskHost;

    public SlicerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, CAPACITY, USAGE, MachineBlockEntities.SLICE_AND_SPLICE.get(), worldPosition, blockState);

        craftingTaskHost = new CraftingMachineTaskHost<>(this, this::hasEnergy, MachineRecipes.SLICING.type().get(),
            this::createTask, this::createRecipeInput) {
            @Override
            protected @Nullable CraftingMachineTask<SlicingRecipe, SlicingRecipe.Input> getNewTask() {
                MachineInventory inv = getInventoryNN();
                if (AXE.getItemStack(inv).isEmpty() || SHEARS.getItemStack(inv).isEmpty()) {
                    return null;
                }

                return super.getNewTask();
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new SlicerMenu(this, inventory, containerId);
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
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .setStackLimit(1) // Force all input slots to have 1 output
            .inputSlot(6, this::isValidInput)
            .slotAccess(INPUTS)
            .inputSlot(this::validAxe)
            .slotAccess(AXE)
            .inputSlot((slot, stack) -> stack.getItem() instanceof ShearsItem)
            .slotAccess(SHEARS)
            .setStackLimit(64) // Reset stack limit
            .outputSlot()
            .slotAccess(OUTPUT)
            .capacitor()
            .build();
    }

    private boolean isValidInput(int index, ItemStack stack) {
        return SlicerRecipeManager.isSlicerValid(stack, index);
    }

    private boolean validAxe(int slot, ItemStack stack) {
        if (stack.getItem() instanceof AxeItem axeItem) {
            // TODO: 20.6: Need a better alternative.
            //return TierSortingRegistry.getSortedTiers().indexOf(axeItem.getTier()) > TierSortingRegistry.getSortedTiers().indexOf(Tiers.WOOD);
            return axeItem.getTier() != Tiers.WOOD;
        }
        return false;
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        craftingTaskHost.newTaskAvailable();
    }

    private SlicingRecipe.Input createRecipeInput() {
        return new SlicingRecipe.Input(INPUTS.getItemStacks(getInventoryNN()));
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

    protected PoweredCraftingMachineTask<SlicingRecipe, SlicingRecipe.Input> createTask(Level level, SlicingRecipe.Input recipeInput, @Nullable RecipeHolder<SlicingRecipe> recipe) {
        return new PoweredCraftingMachineTask<>(level, getInventoryNN(), getEnergyStorage(), recipeInput, OUTPUT, recipe) {
            @Override
            protected void consumeInputs(SlicingRecipe recipe) {
                // Deduct ingredients
                MachineInventory inv = getInventory();
                for (SingleSlotAccess access : INPUTS.getAccesses()) {
                    access.getItemStack(inv).shrink(1);
                }

                if (level instanceof ServerLevel serverLevel) {
                    AXE.getItemStack(inv).hurtAndBreak(1, serverLevel, null, item -> {});
                    SHEARS.getItemStack(inv).hurtAndBreak(1, serverLevel, null, item -> {});
                }
            }
        };
    }

    // endregion

    // region Serialization

    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(pTag, lookupProvider);
        craftingTaskHost.save(lookupProvider, pTag);
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(pTag, lookupProvider);
        craftingTaskHost.load(lookupProvider, pTag);
    }

    // endregion
}

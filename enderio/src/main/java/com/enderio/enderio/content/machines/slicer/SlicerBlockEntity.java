package com.enderio.enderio.content.machines.slicer;

import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineInventory;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.inventory.MultiSlotAccess;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.task.CraftingMachineTask;
import com.enderio.enderio.foundation.task.PoweredCraftingMachineTask;
import com.enderio.enderio.foundation.task.host.CraftingMachineTaskHost;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIORecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

public class SlicerBlockEntity extends PoweredMachineBlockEntity {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.SLICER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.SLICER_USAGE);

    public static final SingleSlotAccess OUTPUT = new SingleSlotAccess();
    public static final MultiSlotAccess INPUTS = new MultiSlotAccess();
    public static final SingleSlotAccess AXE = new SingleSlotAccess();
    public static final SingleSlotAccess SHEARS = new SingleSlotAccess();

    private final CraftingMachineTaskHost<SlicingRecipe, SlicingRecipe.Input> craftingTaskHost;

    public SlicerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.SLICE_AND_SPLICE.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED,
                EnergyIOMode.Input, CAPACITY, USAGE);

        craftingTaskHost = new CraftingMachineTaskHost<>(this, this::hasEnergy, EIORecipes.SLICING.type().get(),
                this::createTask, this::createRecipeInput) {
            @Override
            protected @Nullable CraftingMachineTask<SlicingRecipe, SlicingRecipe.Input> getNewTask() {
                if (AXE.getItemStack(SlicerBlockEntity.this).isEmpty()
                        || SHEARS.getItemStack(SlicerBlockEntity.this).isEmpty()) {
                    return null;
                }

                return super.getNewTask();
            }
        };
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new SlicerMenu(containerId, inventory, this);
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
    public MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
                .setStackLimit(1) // Force all input slots to have 1 output
                .inputSlot(6, this::isValidInput)
                .slotAccess(INPUTS)
                .inputSlot(this::validAxe)
                .slotAccess(AXE)
                .inputSlot((slot, stack) -> stack.getItem() instanceof ShearsItem)
                .slotAccess(SHEARS)
                .setStackLimit(64) // Reset originalStack limit
                .outputSlot()
                .slotAccess(OUTPUT)
                .capacitor()
                .build();
    }

    private boolean isValidInput(int index, ItemStack stack) {
        return SlicerRecipeManager.isSlicerValid(stack, index);
    }

    private boolean validAxe(int slot, ItemStack stack) {
        // TODO: 1.21.4: Need an EIO tag for axes that can't be used to slice.
        return stack.is(ItemTags.AXES) && !stack.is(Items.WOODEN_AXE);
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        craftingTaskHost.newTaskAvailable();
    }

    private SlicingRecipe.Input createRecipeInput() {
        return new SlicingRecipe.Input(INPUTS.getItemStacks(getInventory()));
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

    protected PoweredCraftingMachineTask<SlicingRecipe, SlicingRecipe.Input> createTask(Level level,
            SlicingRecipe.Input recipeInput, @Nullable RecipeHolder<SlicingRecipe> recipe) {
        return new PoweredCraftingMachineTask<>(level, getInventory(), getEnergyStorage(), recipeInput, OUTPUT,
                recipe) {
            @Override
            protected void consumeInputs(SlicingRecipe recipe) {
                // Deduct ingredients
                MachineInventory inv = getInventory();
                for (SingleSlotAccess access : INPUTS.getAccesses()) {
                    access.getItemStack(inv).shrink(1);
                }

                if (level instanceof ServerLevel serverLevel) {
                    AXE.getItemStack(inv).hurtAndBreak(1, serverLevel, null, item -> {
                    });
                    SHEARS.getItemStack(inv).hurtAndBreak(1, serverLevel, null, item -> {
                    });
                }
            }
        };
    }

    // endregion

    // region Serialization

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild(MachineNBTKeys.CRAFTING_TASK, craftingTaskHost);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        var task = input.child(MachineNBTKeys.CRAFTING_TASK);
        task.ifPresent(craftingTaskHost::deserialize);
    }

    // endregion
}

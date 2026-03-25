package com.enderio.enderio.content.machines.slicer;

import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.foundation.inventory.MachineSlotTemplates;
import com.enderio.enderio.foundation.task.CraftingMachineTask;
import com.enderio.enderio.foundation.task.PoweredCraftingMachineTask;
import com.enderio.enderio.foundation.task.host.CraftingMachineTaskHost;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIORecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

public class SlicerBlockEntity extends PoweredMachineBlockEntity {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.SLICER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.SLICER_USAGE);

    public static final SingleResourceSlotKey<ItemResource> OUTPUT = new SingleResourceSlotKey<>();
    public static final MultiResourceSlotKey<ItemResource> INPUTS = new MultiResourceSlotKey<>(6);
    public static final SingleResourceSlotKey<ItemResource> AXE = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> SHEARS = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> CAPACITOR = new SingleResourceSlotKey<>();

    private final CraftingMachineTaskHost<SlicingRecipe, SlicingRecipe.Input> craftingTaskHost;

    public SlicerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.SLICE_AND_SPLICE.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED, CAPACITOR,
                EnergyIOMode.Input, CAPACITY, USAGE);

        craftingTaskHost = new CraftingMachineTaskHost<>(this, this::hasEnergy, EIORecipeTypes.SLICING.get(),
                this::createTask, this::createRecipeInput) {
            @Override
            protected @Nullable CraftingMachineTask<SlicingRecipe, SlicingRecipe.Input> getNewTask() {
                if (getInventory().getStack(AXE).isEmpty()
                        || getInventory().getStack(SHEARS).isEmpty()) {
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
    public ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .slots(INPUTS, SlotTemplates.input(), b -> b
                .capacity(1)
                .filter(this::isValidInput))
            .slot(AXE, SlotTemplates.input(), b -> b
                .capacity(1)
                .filter(this::validAxe))
            .slot(SHEARS, SlotTemplates.input(), b -> b
                .capacity(1)
                .filter((_, itemResource) -> itemResource.getItem() instanceof ShearsItem))
            .slot(OUTPUT, SlotTemplates.output())
            .slot(CAPACITOR, MachineSlotTemplates.capacitor())
            .build();
    }

    private boolean isValidInput(int index, ItemResource stack) {
        return SlicerRecipeManager.isSlicerValid(stack.toStack(), index);
    }

    private boolean validAxe(int slot, ItemResource stack) {
        return stack.is(ItemTags.AXES) && !stack.is(EIOTags.Items.SLICER_INCOMPATIBLE_AXE);
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        craftingTaskHost.newTaskAvailable();
    }

    private SlicingRecipe.Input createRecipeInput() {
        return new SlicingRecipe.Input(getInventory().getStacks(INPUTS));
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
        return new PoweredCraftingMachineTask<>(level, this, getInventory(), getEnergyStorage(), recipeInput, OUTPUT,
                recipe) {
            @Override
            protected void consumeInputs(SlicingRecipe recipe) {
                // Deduct ingredients
                ItemStorage inv = getInventory();
                for (var inputSlot : INPUTS) {
                    inv.getStack(inputSlot).shrink(1);
                }

                if (level instanceof ServerLevel serverLevel) {
                    inv.getStack(AXE).hurtAndBreak(1, serverLevel, null, item -> {
                    });
                    inv.getStack(SHEARS).hurtAndBreak(1, serverLevel, null, item -> {
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

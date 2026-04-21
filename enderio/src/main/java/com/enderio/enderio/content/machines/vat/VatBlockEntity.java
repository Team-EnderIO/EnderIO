package com.enderio.enderio.content.machines.vat;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.core.common.storage.FluidStorage;
import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.layout.FluidStorageLayout;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.core.common.util.EnderResourceUtil;
import com.enderio.core.common.util.NamedFluidContents;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.recipe.MachineRecipeCaches;
import com.enderio.enderio.foundation.io.fluid.FluidItemInteractive;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.foundation.state.MachineStateUpdater;
import com.enderio.enderio.foundation.storage.SidedResourceHandler;
import com.enderio.enderio.foundation.task.CraftingMachineTask;
import com.enderio.enderio.foundation.task.host.CraftingMachineTaskHost;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIORecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class VatBlockEntity extends MachineBlockEntity implements FluidItemInteractive {

    public static final ICapabilityProvider<VatBlockEntity, Direction, ResourceHandler<FluidResource>> FLUID_HANDLER_PROVIDER = (be,
        side) -> be.fluidStorage != null ? SidedResourceHandler.of(be.fluidStorage, side, be) : null;

    public static final int TANK_CAPACITY = 8 * FluidType.BUCKET_VOLUME;
    public static final SingleResourceSlotKey<FluidResource> INPUT_TANK = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<FluidResource> OUTPUT_TANK = new SingleResourceSlotKey<>();

    public static final MultiResourceSlotKey<ItemResource> REAGENTS = new MultiResourceSlotKey<>(2);

    public static final FluidStorageLayout FLUID_STORAGE_LAYOUT =
        FluidStorageLayout.builder()
            .add(INPUT_TANK, SlotTemplates.storage(), slot -> slot.capacity(TANK_CAPACITY))
            .add(OUTPUT_TANK, SlotTemplates.storage(), slot -> slot.capacity(TANK_CAPACITY))
            .build();

    private final FluidStorage fluidStorage;
    private final CraftingMachineTaskHost<FermentingRecipe, FermentingRecipe.Input> craftingTaskHost;

    public VatBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.VAT.get(), worldPosition, blockState, true);

        fluidStorage = new FluidStorage(FLUID_STORAGE_LAYOUT) {
            @Override
            protected void onContentsChanged(int index, FluidStack previousContents) {
                super.onContentsChanged(index, previousContents);
                craftingTaskHost.newTaskAvailable();
                setChanged();
                if (level != null) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                }
            }
        };

        craftingTaskHost = new CraftingMachineTaskHost<>(this, () -> true, EIORecipeTypes.VAT_FERMENTING.get(),
                this::createTask, this::createRecipeInput);
    }

    public FluidStorage getFluidStorage() {
        return fluidStorage;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new VatMenu(containerId, playerInventory, this);
    }

    @Override
    public void serverTick() {
        super.serverTick();

        if (canAct()) {
            craftingTaskHost.tick();
        }
        updateMachineState(MachineState.ACTIVE, isActive());
    }

    public boolean isActive() {
        return canAct() && craftingTaskHost.hasTask();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        craftingTaskHost.onLevelReady();
    }

    protected VatCraftingMachineTask createTask(Level level, FermentingRecipe.Input input,
            @Nullable RecipeHolder<FermentingRecipe> recipe) {
        return new VatCraftingMachineTask(level, this, getInventory(), fluidStorage, input, recipe);
    }

    @Override
    public @Nullable ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .add(REAGENTS, SlotTemplates.input(), b -> b
                .filter(this::acceptSlotInput))
            .build();
    }

    protected boolean acceptSlotInput(int slot, ItemResource resource) {
        return MachineRecipeCaches.FERMENTING.hasValidRecipeIf(getInventory(), REAGENTS, slot, resource.toStack());
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        craftingTaskHost.newTaskAvailable();
    }

    private FermentingRecipe.Input createRecipeInput() {
        List<ItemStack> reagents = getInventory().getStacks(REAGENTS);
        return new FermentingRecipe.Input(reagents.get(0), reagents.get(1), fluidStorage.getStack(INPUT_TANK));
    }

    public FluidStack getInputFluid() {
        return fluidStorage.getStack(INPUT_TANK);
    }

    public FluidStack getOutputFluid() {
        return fluidStorage.getStack(OUTPUT_TANK);
    }

    public float getCraftingProgress() {
        return craftingTaskHost.getProgress();
    }

    @Nullable
    public RecipeHolder<FermentingRecipe> getRecipe() {
        if (craftingTaskHost.hasTask()) {
            return craftingTaskHost.getCurrentTask().getRecipeHolder();
        }

        return null;
    }

    @UseOnly(LogicalSide.SERVER)
    public void moveFluidToOutputTank() {
        FluidStack inputFluid = fluidStorage.getStack(INPUT_TANK);
        FluidStack outputFluid = fluidStorage.getStack(OUTPUT_TANK);

        if (outputFluid.isEmpty() && !inputFluid.isEmpty()) {
            fluidStorage.setStack(OUTPUT_TANK, inputFluid.copy());
            fluidStorage.setStack(INPUT_TANK, FluidStack.EMPTY);
        }
    }

    @UseOnly(LogicalSide.SERVER)
    public void dumpOutputTank() {
        fluidStorage.setStack(OUTPUT_TANK, FluidStack.EMPTY);
    }

    protected static class VatCraftingMachineTask
            extends CraftingMachineTask<FermentingRecipe, FermentingRecipe.Input> {

        public VatCraftingMachineTask(@NonNull Level level, MachineStateUpdater machineStateUpdater, ItemStorage inventory,
                FluidStorage fluidStorage, FermentingRecipe.Input input,
                @Nullable RecipeHolder<FermentingRecipe> recipe) {
            super(level, machineStateUpdater, inventory, fluidStorage, input, recipe);
        }

        @Override
        protected void consumeInputs(FermentingRecipe recipe) {
            inventory.mutateStack(REAGENTS.slot(0), stack -> stack.shrink(1));
            inventory.mutateStack(REAGENTS.slot(1), stack -> stack.shrink(1));

            FluidStack inputFluid = EnderResourceUtil.getFluidStack(fluidStorage, INPUT_TANK);
            try (Transaction transaction = Transaction.openRoot()) {
                int inputIndex = fluidStorage.layout().indexOf(INPUT_TANK);
                fluidStorage.extract(inputIndex, FluidResource.of(inputFluid), recipe.input().amount(), transaction);
                transaction.commit();
            }
        }

        @Override
        protected boolean placeOutputs(List<OutputStack> outputs, boolean simulate) {
            FluidStack output = outputs.getFirst().getFluid();

            try (Transaction transaction = Transaction.openRoot()) {
                int outputIndex = fluidStorage.layout().indexOf(OUTPUT_TANK);
                int inserted = fluidStorage.insert(outputIndex, FluidResource.of(output), output.getAmount(), transaction);
                if (inserted == output.getAmount()) {
                    if (!simulate) {
                        transaction.commit();
                    }
                    return true;
                }
                return false;
            }
        }

        @Override
        protected int makeProgress(int remainingProgress) {
            return 1; // VAT doesn't consume power
        }

        @Override
        protected int getProgressRequired(FermentingRecipe recipe) {
            return recipe.ticks();
        }

    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);

        NamedFluidContents fluidContents = components.get(EIODataComponents.NAMED_FLUID_CONTENTS);
        if (fluidContents != null) {
            FluidStack inputFluid = fluidContents.copy("input_tank");
            FluidStack outputFluid = fluidContents.copy("output_tank");

            if (!inputFluid.isEmpty()) {
                fluidStorage.setStack(INPUT_TANK, inputFluid);
            }
            if (!outputFluid.isEmpty()) {
                fluidStorage.setStack(OUTPUT_TANK, outputFluid);
            }
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        FluidStack inputFluid = fluidStorage.getStack(INPUT_TANK);
        FluidStack outputFluid = fluidStorage.getStack(OUTPUT_TANK);
        if (!inputFluid.isEmpty() || !outputFluid.isEmpty()) {
            components.set(EIODataComponents.NAMED_FLUID_CONTENTS, NamedFluidContents
                    .copyOf(Map.of("input_tank", inputFluid, "output_tank", outputFluid)));
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild("fluids", fluidStorage);
        output.putChild(MachineNBTKeys.CRAFTING_TASK, craftingTaskHost);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        var fluids = input.child("fluids");
        fluids.ifPresent(fluidStorage::deserialize);
        var task = input.child(MachineNBTKeys.CRAFTING_TASK);
        task.ifPresent(craftingTaskHost::deserialize);
    }
}

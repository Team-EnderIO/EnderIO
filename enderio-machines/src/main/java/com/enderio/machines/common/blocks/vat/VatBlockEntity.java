package com.enderio.machines.common.blocks.vat;

import com.enderio.EnderIOBase;
import com.enderio.base.api.UseOnly;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.core.common.util.NamedFluidContents;
import com.enderio.machines.common.attachment.FluidTankUser;
import com.enderio.machines.common.blocks.base.blockentity.MachineBlockEntity;
import com.enderio.machines.common.blocks.base.inventory.MachineInventory;
import com.enderio.machines.common.blocks.base.inventory.MachineInventoryLayout;
import com.enderio.machines.common.blocks.base.inventory.MultiSlotAccess;
import com.enderio.machines.common.blocks.base.state.MachineState;
import com.enderio.machines.common.blocks.base.task.CraftingMachineTask;
import com.enderio.machines.common.blocks.base.task.host.CraftingMachineTaskHost;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.fluid.FluidItemInteractive;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import com.enderio.machines.common.io.fluid.TankAccess;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VatBlockEntity extends MachineBlockEntity implements FluidTankUser, FluidItemInteractive {

    public static final int TANK_CAPACITY = 8 * FluidType.BUCKET_VOLUME;
    public static final TankAccess INPUT_TANK = new TankAccess();
    public static final TankAccess OUTPUT_TANK = new TankAccess();
    public static final MultiSlotAccess REAGENTS = new MultiSlotAccess();

    private static final ResourceLocation EMPTY = EnderIOBase.loc("");

    private final MachineFluidHandler fluidHandler;
    private final CraftingMachineTaskHost<FermentingRecipe, FermentingRecipe.Input> craftingTaskHost;

    public VatBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.VAT.get(), worldPosition, blockState, true);
        fluidHandler = createFluidHandler();

        craftingTaskHost = new CraftingMachineTaskHost<>(this, () -> true, MachineRecipes.VAT_FERMENTING.type().get(),
                this::createTask, this::createRecipeInput);
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
        return new VatCraftingMachineTask(level, getInventory(), getFluidHandler(), input, recipe);
    }

    @Override
    public @Nullable MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder().inputSlot(2).slotAccess(REAGENTS).build();
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        craftingTaskHost.newTaskAvailable();
    }

    private FermentingRecipe.Input createRecipeInput() {
        List<ItemStack> reagents = REAGENTS.getItemStacks(getInventory());
        return new FermentingRecipe.Input(reagents.get(0), reagents.get(1), getInputTank());
    }

    @Override
    public MachineTankLayout getTankLayout() {
        return MachineTankLayout.builder()
                .tank(INPUT_TANK, TANK_CAPACITY, true, false, (stack) -> true)
                .tank(OUTPUT_TANK, TANK_CAPACITY, false, true, (stack) -> true)
                .build();
    }

    @Override
    public MachineFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    @Override
    public MachineFluidHandler createFluidHandler() {
        return new MachineFluidHandler(this, getTankLayout()) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                craftingTaskHost.newTaskAvailable();
                setChanged();
            }
        };
    }

    public MachineFluidTank getInputTank() {
        return INPUT_TANK.getTank(this);
    }

    public MachineFluidTank getOutputTank() {
        return OUTPUT_TANK.getTank(this);
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
        if (OUTPUT_TANK.isEmpty(this) && !INPUT_TANK.isEmpty(this)) {
            OUTPUT_TANK.setFluid(this, INPUT_TANK.getFluid(this));
            INPUT_TANK.setFluid(this, FluidStack.EMPTY);
        }
    }

    @UseOnly(LogicalSide.SERVER)
    public void dumpOutputTank() {
        OUTPUT_TANK.setFluid(this, FluidStack.EMPTY);
    }

    protected static class VatCraftingMachineTask
            extends CraftingMachineTask<FermentingRecipe, FermentingRecipe.Input> {

        public VatCraftingMachineTask(@NotNull Level level, MachineInventory inventory,
                MachineFluidHandler fluidHandler, FermentingRecipe.Input input,
                @Nullable RecipeHolder<FermentingRecipe> recipe) {
            super(level, inventory, fluidHandler, input, recipe);
        }

        @Override
        protected void consumeInputs(FermentingRecipe recipe) {
            REAGENTS.get(0).getItemStack(inventory).shrink(1);
            REAGENTS.get(1).getItemStack(inventory).shrink(1);

            INPUT_TANK.getTank(fluidHandler).drain(recipe.input().amount(), IFluidHandler.FluidAction.EXECUTE);
        }

        @Override
        protected boolean placeOutputs(List<OutputStack> outputs, boolean simulate) {
            var action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
            FluidStack output = outputs.getFirst().getFluid();
            int filled = OUTPUT_TANK.getTank(fluidHandler).fill(output, action);
            return filled == output.getAmount();
        }

        @Override
        protected int makeProgress(int remainingProgress) {
            return 1; // do nothing. VAT doesn't consume power
        }

        @Override
        protected int getProgressRequired(FermentingRecipe recipe) {
            return recipe.ticks();
        }

    }

    @Override
    protected void applyImplicitComponents(DataComponentInput components) {
        super.applyImplicitComponents(components);

        NamedFluidContents fluidContents = components.get(EIODataComponents.NAMED_FLUID_CONTENTS);
        if (fluidContents != null) {
            INPUT_TANK.setFluid(this, fluidContents.copy("input_tank"));
            OUTPUT_TANK.setFluid(this, fluidContents.copy("output_tank"));
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        var inputTank = INPUT_TANK.getTank(this);
        var outputTank = OUTPUT_TANK.getTank(this);
        if (!inputTank.isEmpty() || !outputTank.isEmpty()) {
            components.set(EIODataComponents.NAMED_FLUID_CONTENTS, NamedFluidContents
                    .copyOf(Map.of("input_tank", inputTank.getFluid(), "output_tank", outputTank.getFluid())));
        }
    }

    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(pTag, lookupProvider);
        saveTank(lookupProvider, pTag);
        craftingTaskHost.save(lookupProvider, pTag);
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(pTag, lookupProvider);
        loadTank(lookupProvider, pTag);
        craftingTaskHost.load(lookupProvider, pTag);
    }
}

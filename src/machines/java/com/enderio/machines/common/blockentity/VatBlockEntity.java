package com.enderio.machines.common.blockentity;

import com.enderio.core.common.network.slot.FluidStackNetworkDataSlot;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.attachment.IFluidTankUser;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.blockentity.task.CraftingMachineTask;
import com.enderio.machines.common.blockentity.task.host.CraftingMachineTaskHost;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import com.enderio.machines.common.io.fluid.TankAccess;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.menu.VatMenu;
import com.enderio.machines.common.recipe.FermentingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VatBlockEntity extends MachineBlockEntity implements IFluidTankUser {

    public static final int TANK_CAPACITY_COMMON = 8 * FluidType.BUCKET_VOLUME;
    private static final TankAccess INPUT_TANK = new TankAccess();
    private static final TankAccess OUTPUT_TANK = new TankAccess();
    public static final MultiSlotAccess REAGENTS = new MultiSlotAccess();
    private final MachineFluidHandler fluidHandler;
    private final CraftingMachineTaskHost<FermentingRecipe, FermentingRecipe.Container> craftingTaskHost;

    public VatBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.VAT.get(), worldPosition, blockState);
        fluidHandler = createFluidHandler();

        addDataSlot(new FluidStackNetworkDataSlot(() -> INPUT_TANK.getFluid(this), f -> INPUT_TANK.setFluid(this, f)));
        addDataSlot(new FluidStackNetworkDataSlot(() -> OUTPUT_TANK.getFluid(this), f -> OUTPUT_TANK.setFluid(this, f)));
        craftingTaskHost = new CraftingMachineTaskHost<>(this, () -> true, MachineRecipes.VAT_FERMENTING.type().get(),
            new FermentingRecipe.Container(getInventoryNN(), getInputTank()), this::createTask);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new VatMenu(this, playerInventory, containerId);
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

    protected VatBlockEntity.VatCraftingMachineTask createTask(Level level, FermentingRecipe.Container container,
        @Nullable RecipeHolder<FermentingRecipe> recipe) {
        return new VatBlockEntity.VatCraftingMachineTask(level, getInventoryNN(), getFluidHandler(), container, recipe);
    }

    @Override
    public @Nullable MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder().inputSlot(2).slotAccess(REAGENTS).build();
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        craftingTaskHost.newTaskAvailable();
    }

    @Override
    public MachineTankLayout getTankLayout() {
        return MachineTankLayout
            .builder()
            .tank(INPUT_TANK, TANK_CAPACITY_COMMON, true, false, (stack) -> true)
            .tank(OUTPUT_TANK, TANK_CAPACITY_COMMON, false, true, (stack) -> true)
            .build();
    }

    @Override
    public MachineFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    @Override
    public MachineFluidHandler createFluidHandler() {
        return new MachineFluidHandler(getIOConfig(), getTankLayout()) {
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

    protected static class VatCraftingMachineTask extends CraftingMachineTask<FermentingRecipe, FermentingRecipe.Container> {

        public VatCraftingMachineTask(@NotNull Level level, MachineInventory inventory, MachineFluidHandler fluidHandler, FermentingRecipe.Container container,
            @Nullable RecipeHolder<FermentingRecipe> recipe) {
            super(level, inventory, fluidHandler, container, recipe);
        }

        @Override
        protected void consumeInputs(FermentingRecipe recipe) {
            REAGENTS.get(0).getItemStack(inventory).shrink(1);
            REAGENTS.get(1).getItemStack(inventory).shrink(1);

            INPUT_TANK.getTank(fluidHandler).drain(recipe.getInputFluidAmount(), IFluidHandler.FluidAction.EXECUTE);
        }

        @Override
        protected boolean placeOutputs(List<OutputStack> outputs, boolean simulate) {
            var action = simulate ? IFluidHandler.FluidAction.SIMULATE : IFluidHandler.FluidAction.EXECUTE;
            FluidStack output = outputs.get(0).getFluid();
            int filled = OUTPUT_TANK.getTank(fluidHandler).fill(output, action);
            return filled == output.getAmount();
        }

        @Override
        protected int makeProgress(int remainingProgress) {
            return 1;
        }

        @Override
        protected int getProgressRequired(FermentingRecipe recipe) {
            return recipe.getTicks();
        }

    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        saveTank(pTag);
        craftingTaskHost.save(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        loadTank(pTag);
        craftingTaskHost.load(pTag);
    }
}

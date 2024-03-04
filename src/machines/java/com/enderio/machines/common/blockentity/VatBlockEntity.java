package com.enderio.machines.common.blockentity;

import com.enderio.machines.common.attachment.IFluidTankUser;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import com.enderio.machines.common.io.fluid.TankAccess;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.menu.VatMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.FluidType;
import org.jetbrains.annotations.Nullable;

public class VatBlockEntity extends MachineBlockEntity implements IFluidTankUser {

    public static final int TANK_CAPACITY_COMMON = 8 * FluidType.BUCKET_VOLUME;
    private static final TankAccess INPUT_TANK = new TankAccess();
    private static final TankAccess OUTPUT_TANK = new TankAccess();
    public static final MultiSlotAccess REAGENTS = new MultiSlotAccess();
    private final MachineFluidHandler fluidHandler;
    //    private final CraftingMachineTaskHost<VatFermentingRecipe, VatFermentingRecipe.Container> craftingTaskHost;

    public VatBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.VAT.get(), worldPosition, blockState);
        fluidHandler = createFluidHandler();

        //        craftingTaskHost = new CraftingMachineTaskHost<>(this, () -> true, MachineRecipes.VAT_FERMENTING.type().get(),
        //            new VatFermentingRecipe.Container(getInventoryNN(), getFluidTankNN()), this::createTask);
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
            //            craftingTaskHost.tick();
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        //        craftingTaskHost.onLevelReady();
    }

    //    protected VatBlockEntity.VatCraftingMachineTask createTask(Level level, VatFermentingRecipe.Container container, @Nullable VatFermentingRecipe recipe) {
    //        return new VatBlockEntity.VatCraftingMachineTask(level, getInventoryNN(), container, null, recipe);
    //    }

    @Override
    public @Nullable MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder().inputSlot(2).slotAccess(REAGENTS).build();
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
                //                craftingTaskHost.newTaskAvailable();
                setChanged();
            }
        };
    }

    //    protected static class VatCraftingMachineTask extends CraftingMachineTask<VatFermentingRecipe, VatFermentingRecipe.Container> {
    //
    //        public VatCraftingMachineTask(@NotNull Level level, MachineInventory inventory, VatFermentingRecipe.Container container, MultiSlotAccess outputSlots,
    //            @Nullable VatFermentingRecipe recipe) {
    //            super(level, inventory, container, outputSlots, recipe);
    //        }
    //
    //        @Override
    //        protected void consumeInputs(VatFermentingRecipe recipe) {
    //            container.getItem(0).shrink(1);
    //            container.getItem(1).shrink(1);
    //            // TODO: Extract fluid from first tank
    //            //            container.getTank().getFluidInTank(0).shrink(recipe.getInputFluid().getAmount());
    //        }
    //
    //        @Override
    //        protected boolean placeOutputs(List<OutputStack> outputs, boolean simulate) {
    //            // TODO: Place the output fluid in the second tank
    //            return super.placeOutputs(outputs, simulate);
    //        }
    //
    //        @Override
    //        protected int makeProgress(int remainingProgress) {
    //            return 1;
    //        }
    //
    //        @Override
    //        protected int getProgressRequired(VatFermentingRecipe recipe) {
    //            return recipe.getTicks();
    //        }
    //
    //    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        //        craftingTaskHost.save(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        //        craftingTaskHost.load(pTag);
    }
}

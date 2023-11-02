package com.enderio.machines.common.blockentity;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blockentity.base.MachineBlockEntity;
import com.enderio.machines.common.blockentity.task.CraftingMachineTask;
import com.enderio.machines.common.blockentity.task.host.CraftingMachineTaskHost;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.menu.VatMenu;
import com.enderio.machines.common.recipe.VatFermentingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class VatBlockEntity extends MachineBlockEntity {

    public static final int TANK_CAPACITY_COMMON = 8 * FluidType.BUCKET_VOLUME;
    private final CraftingMachineTaskHost<VatFermentingRecipe, VatFermentingRecipe.Container> craftingTaskHost;

    public VatBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState);

        craftingTaskHost = new CraftingMachineTaskHost<>(this, () -> true, MachineRecipes.VAT_FERMENTING.type().get(),
            new VatFermentingRecipe.Container(getInventoryNN(), getFluidTankNN()), this::createTask);
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

    @Override
    protected @Nullable FluidTank createFluidTank() {
        return new MachineFluidTank(TANK_CAPACITY_COMMON, this);
    }

    protected VatBlockEntity.VatCraftingMachineTask createTask(Level level, VatFermentingRecipe.Container container, @Nullable VatFermentingRecipe recipe) {
        return new VatBlockEntity.VatCraftingMachineTask(level, getInventoryNN(), container, null, recipe);
    }

    protected static class VatCraftingMachineTask extends CraftingMachineTask<VatFermentingRecipe, VatFermentingRecipe.Container> {

        public VatCraftingMachineTask(@NotNull Level level, MachineInventory inventory, VatFermentingRecipe.Container container, MultiSlotAccess outputSlots,
            @Nullable VatFermentingRecipe recipe) {
            super(level, inventory, container, outputSlots, recipe);
        }

        @Override
        protected void consumeInputs(VatFermentingRecipe recipe) {
            container.getItem(0).shrink(1);
            container.getItem(1).shrink(1);
            // TODO: Extract fluid from first tank
            //            container.getTank().getFluidInTank(0).shrink(recipe.getInputFluid().getAmount());
        }

        @Override
        protected boolean placeOutputs(List<OutputStack> outputs, boolean simulate) {
            // TODO: Place the output fluid in the second tank
            return super.placeOutputs(outputs, simulate);
        }

        @Override
        protected int makeProgress(int remainingProgress) {
            return 1;
        }

        @Override
        protected int getProgressRequired(VatFermentingRecipe recipe) {
            return recipe.getTicks();
        }

    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        craftingTaskHost.save(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        craftingTaskHost.load(pTag);
    }
}

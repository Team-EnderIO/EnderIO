package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.core.common.sync.IntegerDataSlot;
import com.enderio.core.common.sync.SyncMode;
import com.enderio.machines.common.blockentity.base.PoweredCraftingMachine;
import com.enderio.machines.common.blockentity.task.PoweredCraftingTask;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.SoulBinderMenu;
import com.enderio.machines.common.recipe.SoulBindingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

public class SoulBinderBlockEntity extends PoweredCraftingMachine<SoulBindingRecipe, SoulBindingRecipe.Container> {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, () -> 100000f);
    public static final QuadraticScalable TRANSFER = new QuadraticScalable(CapacitorModifier.ENERGY_TRANSFER, () -> 120f);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, () -> 30f);
    private final SoulBindingRecipe.Container container;
    private final FluidTank fluidTank;
    private int neededXP;

    public SoulBinderBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(MachineRecipes.SOUL_BINDING.type().get(), CAPACITY, TRANSFER, USAGE, type, worldPosition, blockState);

        fluidTank = new FluidTank(1000, f -> f.getFluid().is(EIOTags.Fluids.EXPERIENCE));
        container = new SoulBindingRecipe.Container(getInventory(), fluidTank);

        addDataSlot(new IntegerDataSlot(() -> fluidTank.getFluidInTank(0).getAmount(), (i) -> fluidTank.setFluid(new FluidStack(EIOFluids.XP_JUICE.get(), i)),
            SyncMode.WORLD));
        addDataSlot(new IntegerDataSlot(() -> neededXP, (i) -> neededXP = i, SyncMode.GUI));
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new SoulBinderMenu(this, pPlayerInventory, pContainerId);
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .setStackLimit(1)
            .inputSlot((slot, stack) -> stack.getCapability(EIOCapabilities.ENTITY_STORAGE).isPresent())
            .inputSlot()
            .setStackLimit(64)
            .outputSlot(2)
            .capacitor()
            .build();
    }

    @Override
    protected PoweredCraftingTask<SoulBindingRecipe, SoulBindingRecipe.Container> createTask(@Nullable SoulBindingRecipe recipe) {
        if (recipe != null) {
            SoulBinderBlockEntity.this.neededXP = 10;
        }
        return new PoweredCraftingTask<>(this, getContainer(), 2, recipe) {

            @Override
            protected void takeInputs(SoulBindingRecipe recipe) {
                getInventory().getStackInSlot(0).shrink(1);
                getInventory().getStackInSlot(1).shrink(1);

                fluidTank.drain(recipe.getExpCost(), IFluidHandler.FluidAction.EXECUTE);

            }
        };
    }

    @Override
    protected SoulBindingRecipe.Container getContainer() {
        return container;
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public int getNeededXP() {
        return neededXP;
    }
}

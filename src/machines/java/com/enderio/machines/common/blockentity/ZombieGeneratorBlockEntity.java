package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.core.common.sync.FluidStackDataSlot;
import com.enderio.core.common.sync.SyncMode;
import com.enderio.machines.common.blockentity.base.PowerGeneratingMachineEntity;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.menu.ZombieGeneratorMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import static com.enderio.base.common.init.EIOFluids.DEW_OF_THE_VOID;
import static com.enderio.base.common.init.EIOFluids.NUTRIENT_DISTILLATION;

public class ZombieGeneratorBlockEntity extends PowerGeneratingMachineEntity {

    private final FluidTank fluidTank;

    private final MachineFluidHandler fluidHandler;

    private final int MINIMUM_FLUID_AMOUNT = 1400;

    public ZombieGeneratorBlockEntity(QuadraticScalable capacity, QuadraticScalable transfer, QuadraticScalable use, BlockEntityType<?> type,
        BlockPos worldPosition, BlockState blockState) {
        super(capacity, transfer, use, type, worldPosition, blockState);
        fluidTank = new FluidTank(10000) {
            @Override
            protected void onContentsChanged() {
                setChanged();
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                return stack.getFluid().isSame(getFluidType());
            }
        };

        // Register fluid capability and sync
        fluidHandler = new MachineFluidHandler(getIOConfig(), fluidTank);

        addCapabilityProvider(fluidHandler);
        addDataSlot(new FluidStackDataSlot(fluidTank::getFluid, fluidTank::setFluid, SyncMode.WORLD));
    }

    public ZombieGeneratorBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        this(new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, () -> 100000f), new QuadraticScalable(CapacitorModifier.ENERGY_TRANSFER, () -> 80f),
            new QuadraticScalable(CapacitorModifier.ENERGY_USE, () -> 0f), type, worldPosition, blockState);
    }

    public static class FrankenZombieGeneratorBlockEntity extends ZombieGeneratorBlockEntity {
        public FrankenZombieGeneratorBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
            super(new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, () -> 200000f), new QuadraticScalable(CapacitorModifier.ENERGY_TRANSFER, () -> 120f),
                new QuadraticScalable(CapacitorModifier.ENERGY_USE, () -> 0f), type, worldPosition, blockState);
        }

        @Override
        public int getGenerationRate() {
            return 120;
        }

        @Override
        public Fluid getFluidType() {
            return NUTRIENT_DISTILLATION.get();
        }
    }

    public static class EnderGeneratorBlockEntity extends ZombieGeneratorBlockEntity {
        public EnderGeneratorBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {

            super(new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, () -> 500000f), new QuadraticScalable(CapacitorModifier.ENERGY_TRANSFER, () -> 380f),
                new QuadraticScalable(CapacitorModifier.ENERGY_USE, () -> 0f), type, worldPosition, blockState);
        }

        @Override
        public int getGenerationRate() {
            return 380;
        }

        @Override
        public Fluid getFluidType() {
            return DEW_OF_THE_VOID.get();
        }
    }

    @Override
    public boolean isGenerating() {
        return fluidTank.getFluidAmount() >= MINIMUM_FLUID_AMOUNT && canAct();
    }

    @Override
    public int getGenerationRate() {
        return 80;
    }

    @Override
    public void serverTick() {
        if (canAct() && isGenerating()) {
            FluidStack fluid = fluidTank.getFluid();
            fluidTank.drain(new FluidStack(fluid, 100), IFluidHandler.FluidAction.EXECUTE);
        }
        super.serverTick();
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new ZombieGeneratorMenu(this, pPlayerInventory, pContainerId);
    }

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("fluid_amount", fluidTank.writeToNBT(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        fluidTank.readFromNBT(pTag.getCompound("fluid_amount"));
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder().capacitor().build();
    }

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public Fluid getFluidType() {
        return NUTRIENT_DISTILLATION.get();
    }
}

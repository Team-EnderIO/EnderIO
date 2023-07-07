package com.enderio.machines.common.blockentity;

import com.enderio.api.UseOnly;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.core.common.network.slot.FloatNetworkDataSlot;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.io.energy.MachineEnergyStorage;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.PrimitiveAlloySmelterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.LogicalSide;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * The primitive variant of the alloy smelter burns coal instead of using an energy buffer.
 * In order to keep implementation logic together, we do some kinda hacky stuff to emulate an internal buffer.
 * This buffer cannot be accessed via external means, however.
 */
public class PrimitiveAlloySmelterBlockEntity extends AlloySmelterBlockEntity {
    // TODO: Currently smelts really slowly. Needs addressed when we deal with burn -> FE rates.
    private int burnTime;
    private int burnDuration;
    public static final SingleSlotAccess FUEL = new SingleSlotAccess();
    @UseOnly(LogicalSide.CLIENT)
    private float clientBurnProgress;

    public PrimitiveAlloySmelterBlockEntity(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) {
        super(pType, pWorldPosition, pBlockState);
        addDataSlot(new FloatNetworkDataSlot(this::getBurnProgress, p -> clientBurnProgress = p));
    }

    @Override
    protected boolean restrictedMode() {
        return true;
    }

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .inputSlot(3, this::acceptSlotInput)
            .slotAccess(INPUTS)
            .outputSlot()
            .slotAccess(OUTPUT)
            .inputSlot(this::acceptSlotInput)
            .slotAccess(FUEL)
            .build();
    }

    @Override
    public AlloySmelterMode getMode() {
        // Force alloys only
        return AlloySmelterMode.ALLOYS;
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new PrimitiveAlloySmelterMenu(this, inventory, containerId);
    }

    @Override
    public void serverTick() {
        super.serverTick();

        // Tick burn time even if redstone activation has stopped.
        if (isBurning()) {
            burnTime--;
        }

        // Only continue burning if redstone is enabled and the internal buffer has space.
        if (canAct() && !isBurning() && craftingTaskHost.hasTask() && !craftingTaskHost.getCurrentTask().isCompleted()) {
            // Get the fuel
            ItemStack fuel = FUEL.getItemStack(this);
            if (!fuel.isEmpty()) {
                // Get the burn time.
                int burningTime = ForgeHooks.getBurnTime(fuel, RecipeType.SMELTING);

                // If this item can burn, burn it.
                if (burningTime > 0) {
                    burnTime = burningTime;
                    burnDuration = burnTime;

                    // Remove the fuel
                    fuel.shrink(1);
                }
            }
        }
    }

    @Override
    protected MachineEnergyStorage createEnergyStorage(EnergyIOMode energyIOMode, Supplier<Integer> capacity, Supplier<Integer> usageRate) {
        return new MachineEnergyStorage(getIOConfig(), energyIOMode, this::getBurnToFE, () -> 0) {
            @Override
            public int getEnergyStored() {
                return getBurnToFE();
            }

            @Override
            public int consumeEnergy(int energy, boolean simulate) {
                // We burn fuel, this energy storage is merely a wrapper now.
                if (isBurning()) {
                    return getBurnToFE();
                }
                return 0;
            }

            // Stop things from connecting to the block.
            @Override
            public LazyOptional<IEnergyStorage> getCapability(@Nullable Direction side) {
                return LazyOptional.empty();
            }
        };
    }

    @Override
    public @Nullable MachineEnergyStorage createExposedEnergyStorage() {
        // Hide the power cap from other machines and TOP and such.
        return null;
    }

    public boolean isBurning() {
        return burnTime > 0;
    }

    public float getBurnProgress() {
        if (level.isClientSide)
            return clientBurnProgress;
        if (burnDuration == 0)
            return 0;
        return burnTime / (float) burnDuration;
    }

    public int getBurnToFE() {
        return MachinesConfig.COMMON.ENERGY.STIRLING_GENERATOR_PRODUCTION.get();
    }

    @Override
    protected boolean isActive() {
        // Ignores power.
        return canAct() && craftingTaskHost.hasTask();
    }
}

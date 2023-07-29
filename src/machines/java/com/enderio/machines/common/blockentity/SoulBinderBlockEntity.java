package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.common.network.slot.IntegerNetworkDataSlot;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.blockentity.task.PoweredCraftingMachineTask;
import com.enderio.machines.common.blockentity.task.host.CraftingMachineTaskHost;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.SoulBinderMenu;
import com.enderio.machines.common.recipe.RecipeCaches;
import com.enderio.machines.common.recipe.SoulBindingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.enderio.base.common.util.ExperienceUtil.EXP_TO_FLUID;

public class SoulBinderBlockEntity extends PoweredMachineBlockEntity {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.SOUL_BINDER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.SOUL_BINDER_USAGE);

    public static final SingleSlotAccess INPUT_SOUL = new SingleSlotAccess();
    public static final SingleSlotAccess INPUT_OTHER = new SingleSlotAccess();
    public static final MultiSlotAccess OUTPUT = new MultiSlotAccess();

    private final CraftingMachineTaskHost<SoulBindingRecipe, SoulBindingRecipe.Container> craftingTaskHost;

    public SoulBinderBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, CAPACITY, USAGE, type, worldPosition, blockState);

        // Sync fluid amount to client.
        addDataSlot(new IntegerNetworkDataSlot(
            () -> getFluidTankNN().getFluidInTank(0).getAmount(),
            i -> getFluidTankNN().setFluid(new FluidStack(EIOFluids.XP_JUICE.get(), i))
        ));

        // Create the crafting task host
        craftingTaskHost = new CraftingMachineTaskHost<>(this, this::hasEnergy,
            MachineRecipes.SOUL_BINDING.type().get(), new SoulBindingRecipe.Container(getInventoryNN(), getFluidTankNN()), this::createTask);

        // Sync crafting container needed xp
        addDataSlot(new IntegerNetworkDataSlot(
            () -> craftingTaskHost.getContainer().getNeededXP(),
            i -> craftingTaskHost.getContainer().setNeededXP(i)
        ));
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new SoulBinderMenu(this, pPlayerInventory, pContainerId);
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
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .setStackLimit(1)
            .inputSlot((slot, stack) -> stack.is(EIOItems.FILLED_SOUL_VIAL.get()))
            .slotAccess(INPUT_SOUL)
            .inputSlot(this::isValidInput)
            .slotAccess(INPUT_OTHER)
            .setStackLimit(64)
            .outputSlot(2)
            .slotAccess(OUTPUT)
            .capacitor()
            .build();
    }

    private boolean isValidInput(int index, ItemStack stack) {
        return RecipeCaches.SOUL_BINDING.hasRecipe(List.of(stack));
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        craftingTaskHost.newTaskAvailable();
    }

    // endregion

    // region Fluid Storage

    @Override
    protected @Nullable FluidTank createFluidTank() {
        return new FluidTank(10000, f -> f.getFluid().is(EIOTags.Fluids.EXPERIENCE)) {
            @Override
            protected void onContentsChanged() {
                craftingTaskHost.newTaskAvailable();
                setChanged();
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                // Convert into XP Juice
                if (this.isFluidValid(resource)) {
                    var currentFluid = this.getFluid().getFluid();
                    if (currentFluid == Fluids.EMPTY || resource.getFluid().isSame(currentFluid)) {
                        return super.fill(resource, action);
                    } else {
                        return super.fill(new FluidStack(currentFluid, resource.getAmount()), action);
                    }
                }

                // Non-XP is not allowed.
                return 0;
            }
        };
    }

    // endregion

    // region Crafting Task

    public float getCraftingProgress() {
        return craftingTaskHost.getProgress();
    }

    @Override
    protected boolean isActive() {
        return canAct() && hasEnergy() && craftingTaskHost.hasTask();
    }

    protected PoweredCraftingMachineTask<SoulBindingRecipe, SoulBindingRecipe.Container> createTask(Level level, SoulBindingRecipe.Container container, @Nullable SoulBindingRecipe recipe) {
        return new PoweredCraftingMachineTask<>(level, getInventoryNN(), getEnergyStorage(), container, OUTPUT, recipe) {

            @Override
            protected void consumeInputs(SoulBindingRecipe recipe) {
                INPUT_SOUL.getItemStack(getInventory()).shrink(1);
                INPUT_OTHER.getItemStack(getInventory()).shrink(1);

                var fluidTank = getFluidTankNN();
                int leftover = ExperienceUtil.getLevelFromFluidWithLeftover(fluidTank.getFluidAmount(), 0, craftingTaskHost.getContainer().getNeededXP()).y();
                fluidTank.drain(fluidTank.getFluidAmount() - leftover * EXP_TO_FLUID, IFluidHandler.FluidAction.EXECUTE);
            }

            @Override
            protected boolean placeOutputs(List<OutputStack> outputs, boolean simulate) {
                craftingTaskHost.getContainer().setNeededXP(0);
                return super.placeOutputs(outputs, simulate);
            }
        };
    }

    // endregion

    // region Fluid Tank

    public int getNeededXP() {
        return craftingTaskHost.getContainer().getNeededXP();
    }

    // endregion

    // region Serialization

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

    // endregion

}

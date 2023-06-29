package com.enderio.machines.common.blockentity;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.core.common.sync.IntegerDataSlot;
import com.enderio.core.common.sync.SyncMode;
import com.enderio.machines.common.blockentity.base.PoweredMachineEntity;
import com.enderio.machines.common.blockentity.task.host.CraftingMachineTaskHost;
import com.enderio.machines.common.blockentity.task.PoweredCraftingMachineTask;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.SoulBinderMenu;
import com.enderio.machines.common.recipe.SoulBindingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static com.enderio.base.common.util.ExperienceUtil.EXPTOFLUID;

public class SoulBinderBlockEntity extends PoweredMachineEntity {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, () -> 100000f);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, () -> 30f);
    public static final SingleSlotAccess INPUT_SOUL = new SingleSlotAccess();
    public static final SingleSlotAccess INPUT_OTHER = new SingleSlotAccess();
    public static final MultiSlotAccess OUTPUT = new MultiSlotAccess();

    private final FluidTank fluidTank;
    private final MachineFluidHandler fluidHandler;

    private final CraftingMachineTaskHost<SoulBindingRecipe, SoulBindingRecipe.Container> craftingTaskHost;

    public SoulBinderBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, CAPACITY, USAGE, type, worldPosition, blockState);

        fluidTank = createFluidTank(10000);

        // Create fluid tank storage.
        this.fluidHandler = new MachineFluidHandler(getIOConfig(), fluidTank);
        addCapabilityProvider(fluidHandler);

        addDataSlot(new IntegerDataSlot(() -> fluidTank.getFluidInTank(0).getAmount(), (i) -> fluidTank.setFluid(new FluidStack(EIOFluids.XP_JUICE.get(), i)),
            SyncMode.WORLD));

        // Create the crafting task host
        craftingTaskHost = new CraftingMachineTaskHost<>(this, () -> energyStorage.getEnergyStored() > 0,
            MachineRecipes.SOUL_BINDING.type().get(), new SoulBindingRecipe.Container(Objects.requireNonNull(getInventory()), fluidTank), this::createTask);

        // Sync crafting container needed xp
        addDataSlot(new IntegerDataSlot(
            () -> craftingTaskHost.getContainer().getNeededXP(),
            (i) -> craftingTaskHost.getContainer().setNeededXP(i),
            SyncMode.GUI));
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

    // region Inventory

    @Override
    public MachineInventoryLayout getInventoryLayout() {
        return MachineInventoryLayout.builder()
            .setStackLimit(1)
            .inputSlot((slot, stack) -> stack.is(EIOItems.FILLED_SOUL_VIAL.get()))
            .slotAccess(INPUT_SOUL)
            .inputSlot()
            .slotAccess(INPUT_OTHER)
            .setStackLimit(64)
            .outputSlot(2)
            .slotAccess(OUTPUT)
            .capacitor()
            .build();
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        craftingTaskHost.newTaskAvailable();
    }

    // endregion

    // region Crafting Task

    public float getCraftingProgress() {
        return craftingTaskHost.getProgress();
    }

    protected PoweredCraftingMachineTask<SoulBindingRecipe, SoulBindingRecipe.Container> createTask(Level level, SoulBindingRecipe.Container container, @Nullable SoulBindingRecipe recipe) {
        return new PoweredCraftingMachineTask<>(level, Objects.requireNonNull(getInventory()), getEnergyStorage(), container, OUTPUT, recipe) {

            @Override
            protected void consumeInputs(SoulBindingRecipe recipe) {
                INPUT_SOUL.getItemStack(getInventory()).shrink(1);
                INPUT_OTHER.getItemStack(getInventory()).shrink(1);

                int leftover = ExperienceUtil.getLevelFromFluidWithLeftover(fluidTank.getFluidAmount(), 0, craftingTaskHost.getContainer().getNeededXP()).y();
                fluidTank.drain(fluidTank.getFluidAmount() - leftover * EXPTOFLUID, IFluidHandler.FluidAction.EXECUTE);
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

    // TODO: I want to have some kind of general fluid tank stuff like items.

    public FluidTank getFluidTank() {
        return fluidTank;
    }

    public int getNeededXP() {
        return craftingTaskHost.getContainer().getNeededXP();
    }

    private FluidTank createFluidTank(int capacity) {
        return new FluidTank(capacity, f -> f.getFluid().is(EIOTags.Fluids.EXPERIENCE)) {
            @Override
            protected void onContentsChanged() {
                craftingTaskHost.newTaskAvailable();
                setChanged();
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                if (!this.getFluid().getFluid().isSame(EIOFluids.XP_JUICE.getSource()) && this.isFluidValid(resource)) { // Auto convert to own fluid (source) type
                    resource = new FluidStack(EIOFluids.XP_JUICE.getSource(), resource.getAmount());
                }
                return super.fill(resource, action);
            }
        };
    }

    // endregion

    // region Serialization

    @Override
    public void saveAdditional(CompoundTag pTag) {
        super.saveAdditional(pTag);
        pTag.put("fluid", fluidTank.writeToNBT(new CompoundTag()));
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        fluidTank.readFromNBT(pTag.getCompound("fluid"));
    }

    // endregion

}

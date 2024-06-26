package com.enderio.machines.common.blockentity;

import com.enderio.api.UseOnly;
import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.capacitor.QuadraticScalable;
import com.enderio.api.io.energy.EnergyIOMode;
import com.enderio.base.common.init.EIOFluids;
import com.enderio.base.common.init.EIOItems;
import com.enderio.base.common.tag.EIOTags;
import com.enderio.base.common.util.ExperienceUtil;
import com.enderio.core.common.network.NetworkDataSlot;
import com.enderio.machines.common.attachment.FluidTankUser;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.blockentity.task.PoweredCraftingMachineTask;
import com.enderio.machines.common.blockentity.task.host.CraftingMachineTaskHost;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import com.enderio.machines.common.io.fluid.TankAccess;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.SoulBinderMenu;
import com.enderio.machines.common.recipe.RecipeCaches;
import com.enderio.machines.common.recipe.SoulBindingRecipe;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.enderio.base.common.util.ExperienceUtil.EXP_TO_FLUID;

public class SoulBinderBlockEntity extends PoweredMachineBlockEntity implements FluidTankUser {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.SOUL_BINDER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.SOUL_BINDER_USAGE);

    public static final SingleSlotAccess INPUT_SOUL = new SingleSlotAccess();
    public static final SingleSlotAccess INPUT_OTHER = new SingleSlotAccess();
    public static final MultiSlotAccess OUTPUT = new MultiSlotAccess();
    private final MachineFluidHandler fluidHandler;
    private static final TankAccess TANK = new TankAccess();

    @UseOnly(LogicalSide.CLIENT)
    @Nullable
    private RecipeHolder<SoulBindingRecipe> clientRecipe;

    private final CraftingMachineTaskHost<SoulBindingRecipe, SoulBindingRecipe.Input> craftingTaskHost;

    public SoulBinderBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, CAPACITY, USAGE, MachineBlockEntities.SOUL_BINDER.get(), worldPosition, blockState);
        fluidHandler = createFluidHandler();

        // Sync fluid amount to client.
        addDataSlot(NetworkDataSlot.INT.create(
            () -> TANK.getFluidAmount(this),
            i -> TANK.setFluid(this, new FluidStack(EIOFluids.XP_JUICE.getSource(), i))));

        // Create the crafting task host
        craftingTaskHost = new CraftingMachineTaskHost<>(this, this::hasEnergy, MachineRecipes.SOUL_BINDING.type().get(),
            this::createTask, this::createRecipeInput);
    }

    @Override
    public AbstractContainerMenu createMenu(int pContainerId, Inventory pPlayerInventory, Player pPlayer) {
        return new SoulBinderMenu(pContainerId, this, pPlayerInventory);
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

        if (level != null && level.isClientSide) {
            clientRecipe = level.getRecipeManager().getRecipeFor(MachineRecipes.SOUL_BINDING.type().get(), createFakeRecipeInput(), level).orElse(null);
        }
    }

    private SoulBindingRecipe.Input createRecipeInput() {
        return new SoulBindingRecipe.Input(
            INPUT_SOUL.getItemStack(getInventoryNN()),
            INPUT_OTHER.getItemStack(getInventoryNN()),
            TANK.getFluid(getFluidHandler())
        );
    }

    @EnsureSide(EnsureSide.Side.CLIENT)
    private SoulBindingRecipe.Input createFakeRecipeInput() {
        return new SoulBindingRecipe.Input(
            INPUT_SOUL.getItemStack(getInventoryNN()),
            INPUT_OTHER.getItemStack(getInventoryNN()),
            new FluidStack(EIOFluids.XP_JUICE.getSource(), Integer.MAX_VALUE)
        );
    }

    // endregion

    @EnsureSide(EnsureSide.Side.CLIENT)
    public int getClientExp() {
        // This should always set a valid recipe.
        if (level != null && clientRecipe == null && hasValidRecipe()) {
            clientRecipe = level.getRecipeManager().getRecipeFor(MachineRecipes.SOUL_BINDING.type().get(), createFakeRecipeInput(), level).orElse(null);
        }

        return clientRecipe != null ? clientRecipe.value().experience() : 0;
    }

    private boolean hasValidRecipe() {
        return RecipeCaches.SOUL_BINDING.hasRecipe(List.of(
            INPUT_SOUL.getItemStack(getInventoryNN()),
            INPUT_OTHER.getItemStack(getInventoryNN())));
    }

    // region Fluid Storage
    @Override
    public @Nullable MachineTankLayout getTankLayout() {
        return MachineTankLayout.builder().tank(TANK, 10000, f -> f.is(EIOTags.Fluids.EXPERIENCE)).build();
    }

    @Override
    public MachineFluidHandler createFluidHandler() {
        return new MachineFluidHandler(this, getTankLayout()) {
            @Override
            protected void onContentsChanged(int slot) {
                craftingTaskHost.newTaskAvailable();
                updateMachineState(MachineState.EMPTY_TANK, TANK.getFluidAmount(this) <= 0);
                setChanged();
            }

            @Override
            public int fill(FluidStack resource, FluidAction action) {
                // Convert into XP Juice
                if (TANK.isFluidValid(this, resource)) {
                    var currentFluid = TANK.getFluid(this).getFluid();
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

    @Override
    public MachineFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    public MachineFluidTank getFluidTank() {
        return TANK.getTank(this);
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

    protected PoweredCraftingMachineTask<SoulBindingRecipe, SoulBindingRecipe.Input> createTask(Level level, SoulBindingRecipe.Input container,
        @Nullable RecipeHolder<SoulBindingRecipe> recipe) {
        return new PoweredCraftingMachineTask<>(level, getInventoryNN(), getEnergyStorage(), container, OUTPUT, recipe) {

            @Override
            protected void consumeInputs(SoulBindingRecipe recipe) {
                INPUT_SOUL.getItemStack(getInventory()).shrink(1);
                INPUT_OTHER.getItemStack(getInventory()).shrink(1);

                MachineFluidHandler handler = getFluidHandler();
                int leftover = ExperienceUtil.getLevelFromFluidWithLeftover(TANK.getFluidAmount(handler), 0, recipe.experience()).experience();
                TANK.drain(handler, TANK.getFluidAmount(handler) - leftover * EXP_TO_FLUID, IFluidHandler.FluidAction.EXECUTE);
            }

        };
    }

    // endregion

    // region Serialization

    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(pTag, lookupProvider);
        craftingTaskHost.save(lookupProvider, pTag);
        saveTank(lookupProvider, pTag);
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(pTag, lookupProvider);
        craftingTaskHost.load(lookupProvider, pTag);
        loadTank(lookupProvider, pTag);
    }

    // endregion
}

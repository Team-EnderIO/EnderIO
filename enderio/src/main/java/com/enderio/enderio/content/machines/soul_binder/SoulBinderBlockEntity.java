package com.enderio.enderio.content.machines.soul_binder;

import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.api.soul.SoulBoundUtils;
import com.enderio.enderio.client.SoundHandler;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.attachment.FluidTankUser;
import com.enderio.enderio.foundation.block.ProgressMachineBlock;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.inventory.MultiSlotAccess;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.io.fluid.MachineFluidHandler;
import com.enderio.enderio.foundation.io.fluid.MachineFluidTank;
import com.enderio.enderio.foundation.io.fluid.MachineTankLayout;
import com.enderio.enderio.foundation.io.fluid.TankAccess;
import com.enderio.enderio.foundation.recipe.MachineRecipeCaches;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.foundation.task.PoweredCraftingMachineTask;
import com.enderio.enderio.foundation.task.host.CraftingMachineTaskHost;
import com.enderio.enderio.foundation.util.ExperienceUtil;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOFluids;
import com.enderio.enderio.init.EIOItems;
import com.enderio.enderio.init.EIORecipes;
import com.enderio.enderio.init.EIOSounds;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.enderio.enderio.foundation.util.ExperienceUtil.EXP_TO_FLUID;

public class SoulBinderBlockEntity extends PoweredMachineBlockEntity implements FluidTankUser {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.SOUL_BINDER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.SOUL_BINDER_USAGE);

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
        super(EIOBlockEntities.SOUL_BINDER.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED,
                EnergyIOMode.Input, CAPACITY, USAGE);
        fluidHandler = createFluidHandler();

        // Create the crafting task host
        craftingTaskHost = new CraftingMachineTaskHost<>(this, this::hasEnergy,
                EIORecipes.SOUL_BINDING.type().get(), this::createTask, this::createRecipeInput);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SoulBinderMenu(containerId, playerInventory, this);
    }

    @Override
    public void serverTick() {
        super.serverTick();

        if (canAct()) {
            craftingTaskHost.tick();
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(ProgressMachineBlock.POWERED)) {
            double x = pos.getX() + 0.5;
            double y = pos.getY();
            double z = pos.getZ() + 0.5;

            SoundHandler.playSound(pos, EIOSounds.SOUL_BINDER.get(), SoundSource.BLOCKS, 1.0f, 1.0f, random, x, y, z);

            Direction direction = state.getValue(ProgressMachineBlock.FACING);
            Direction.Axis axis = direction.getAxis();
            double r = 0.52;
            double ss = random.nextDouble() * 0.6 - 0.2;
            double dx = axis == Direction.Axis.X ? direction.getStepX() * r : ss;
            double dy = random.nextDouble() * 6.0 / 16.0 + 7.0 / 16.0;
            double dz = axis == Direction.Axis.Z ? direction.getStepZ() * r : ss;
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x + dx, y + dy, z + dz, 0.0, 0.0, 0.0); //TODO green flame
        } else {
            SoundHandler.stopSound(pos);
        }
    }

    @Override
    public void onLoad() {
        super.onLoad();
        craftingTaskHost.onLevelReady();
    }

    // region Inventory

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        // TODO: Support for non-soul vial storages.
        return MachineInventoryLayout.builder()
                .setStackLimit(1)
                .inputSlot((slot, stack) -> stack.is(EIOItems.SOUL_VIAL.get()) && SoulBoundUtils.isBound(stack))
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
        return MachineRecipeCaches.SOUL_BINDING.hasRecipe(List.of(stack));
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        craftingTaskHost.newTaskAvailable();

        if (level != null && level.isClientSide) {
            clientRecipe = level.getRecipeManager()
                    .getRecipeFor(EIORecipes.SOUL_BINDING.type().get(), createFakeRecipeInput(), level)
                    .orElse(null);
        }
    }

    private SoulBindingRecipe.Input createRecipeInput() {
        return new SoulBindingRecipe.Input(INPUT_SOUL.getItemStack(getInventory()),
                INPUT_OTHER.getItemStack(getInventory()), TANK.getFluid(getFluidHandler()));
    }

    @UseOnly(LogicalSide.CLIENT)
    private SoulBindingRecipe.Input createFakeRecipeInput() {
        return new SoulBindingRecipe.Input(INPUT_SOUL.getItemStack(getInventory()),
                INPUT_OTHER.getItemStack(getInventory()),
                new FluidStack(EIOFluids.XP_JUICE.source(), Integer.MAX_VALUE));
    }

    // endregion

    @UseOnly(LogicalSide.CLIENT)
    public int getClientExp() {
        // This should always set a valid recipe.
        if (level != null && clientRecipe == null && hasValidRecipe()) {
            clientRecipe = level.getRecipeManager()
                    .getRecipeFor(EIORecipes.SOUL_BINDING.type().get(), createFakeRecipeInput(), level)
                    .orElse(null);
        }

        return clientRecipe != null ? clientRecipe.value().experience() : 0;
    }

    private boolean hasValidRecipe() {
        return MachineRecipeCaches.SOUL_BINDING
                .hasRecipe(List.of(INPUT_SOUL.getItemStack(getInventory()), INPUT_OTHER.getItemStack(getInventory())));
    }

    // region Fluid Storage
    @Override
    public @Nullable MachineTankLayout getTankLayout() {
        return MachineTankLayout.builder().tank(TANK, 10000, true, false, f -> f.is(Tags.Fluids.EXPERIENCE)).build();
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
    public boolean isActive() {
        return canAct() && hasEnergy() && craftingTaskHost.hasTask();
    }

    protected PoweredCraftingMachineTask<SoulBindingRecipe, SoulBindingRecipe.Input> createTask(Level level,
            SoulBindingRecipe.Input container, @Nullable RecipeHolder<SoulBindingRecipe> recipe) {
        return new PoweredCraftingMachineTask<>(level, getInventory(), getEnergyStorage(), container, OUTPUT, recipe) {

            @Override
            protected void consumeInputs(SoulBindingRecipe recipe) {
                INPUT_SOUL.getItemStack(getInventory()).shrink(1);
                INPUT_OTHER.getItemStack(getInventory()).shrink(1);

                MachineFluidHandler handler = getFluidHandler();
                int leftover = ExperienceUtil
                        .getLevelFromFluidWithLeftover(TANK.getFluidAmount(handler), 0, recipe.experience())
                        .experience();
                TANK.drain(handler, TANK.getFluidAmount(handler) - leftover * EXP_TO_FLUID,
                        IFluidHandler.FluidAction.EXECUTE);
            }

        };
    }

    // endregion

    // region Serialization

    @Override
    protected void applyImplicitComponents(DataComponentInput components) {
        super.applyImplicitComponents(components);

        SimpleFluidContent storedFluid = components.get(EIODataComponents.ITEM_FLUID_CONTENT);
        if (storedFluid != null) {
            var tank = TANK.getTank(this);
            tank.setFluid(storedFluid.copy());
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        var tank = TANK.getTank(this);
        if (!tank.isEmpty()) {
            components.set(EIODataComponents.ITEM_FLUID_CONTENT, SimpleFluidContent.copyOf(tank.getFluid()));
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(tag, lookupProvider);
        craftingTaskHost.save(lookupProvider, tag);
        saveTank(lookupProvider, tag);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(tag, lookupProvider);
        craftingTaskHost.load(lookupProvider, tag);
        loadTank(lookupProvider, tag);
    }

    // endregion
}

package com.enderio.machines.common.blocks.obelisks.weather;

import com.enderio.base.api.io.IOMode;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.core.common.recipes.OutputStack;
import com.enderio.machines.common.attachment.FluidTankUser;
import com.enderio.machines.common.blocks.base.blockentity.MachineBlockEntity;
import com.enderio.machines.common.blocks.base.inventory.MachineInventoryLayout;
import com.enderio.machines.common.blocks.base.inventory.SingleSlotAccess;
import com.enderio.machines.common.blocks.base.state.MachineState;
import com.enderio.machines.common.blocks.base.task.CraftingMachineTask;
import com.enderio.machines.common.blocks.base.task.host.CraftingMachineTaskHost;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.io.IOConfig;
import com.enderio.machines.common.io.fluid.MachineFluidHandler;
import com.enderio.machines.common.io.fluid.MachineFluidTank;
import com.enderio.machines.common.io.fluid.MachineTankLayout;
import com.enderio.machines.common.io.fluid.TankAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.Calendar;
import java.util.List;

public class WeatherObeliskBlockEntity extends MachineBlockEntity implements FluidTankUser {

    public static final ItemStack FIREWORK = new ItemStack(Items.FIREWORK_ROCKET, 1);
    private final MachineFluidHandler fluidHandler;
    private static final TankAccess TANK = new TankAccess();
    public static final SingleSlotAccess ROCKET = new SingleSlotAccess();
    public static final int TANK_CAPACITY = 3000;
    private final CraftingMachineTaskHost<WeatherChangeRecipe, WeatherChangeRecipe.Input> craftingTaskHost;

    public WeatherObeliskBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.WEATHER_OBELISK.get(), worldPosition, blockState, false);
        fluidHandler = createFluidHandler();

        craftingTaskHost = new CraftingMachineTaskHost<>(this, this::canAcceptTask,
                MachineRecipes.WEATHER_CHANGE.type().get(), this::createTask, this::createRecipeInput) {

            @Override
            protected boolean shouldStartNewTask() {
                if (ROCKET.getItemStack(getInventory()).isEmpty()) {
                    return true;
                }
                return super.shouldStartNewTask();
            }
        };
    }

    private WeatherChangeRecipe.Input createRecipeInput() {
        return new WeatherChangeRecipe.Input(TANK.getFluid(getFluidHandler()));
    }

    private CraftingMachineTask<WeatherChangeRecipe, WeatherChangeRecipe.Input> createTask(Level level,
            WeatherChangeRecipe.Input input,
            @Nullable RecipeHolder<WeatherChangeRecipe> weatherChangeRecipeRecipeHolder) {
        return new CraftingMachineTask<>(level, getInventory(), input, null, weatherChangeRecipeRecipeHolder) {

            @Override
            protected void consumeInputs(WeatherChangeRecipe recipe) {
                MachineFluidHandler handler = getFluidHandler();
                TANK.drain(handler, recipe.fluid(), IFluidHandler.FluidAction.EXECUTE);
                ROCKET.getItemStack(getInventory()).shrink(1);
            }

            @Override
            protected int makeProgress(int remainingProgress) {
                boolean hasRocket = !ROCKET.getItemStack(getInventory()).isEmpty();
                boolean weatherDifferent = switch (getRecipe().mode()) {
                case RAIN -> !level.isRaining();
                case CLEAR -> level.isRaining() || level.isThundering();
                case LIGHTNING -> !level.isThundering();
                };
                boolean sky = level.canSeeSky(getBlockPos().above());
                return hasRocket && weatherDifferent && sky ? 1 : 0;
            }

            @Override
            protected int getProgressRequired(WeatherChangeRecipe recipe) {
                return 600;
            }

            @Override
            protected boolean placeOutputs(List<OutputStack> outputs, boolean simulate) {
                if (!simulate && level instanceof ServerLevel server) {
                    switch (getRecipe().mode()) {
                    case RAIN -> server.setWeatherParameters(0, ServerLevel.RAIN_DURATION.sample(server.getRandom()),
                            true, false);
                    case CLEAR ->
                        server.setWeatherParameters(ServerLevel.RAIN_DELAY.sample(server.getRandom()), 0, false, false);
                    case LIGHTNING -> server.setWeatherParameters(0,
                            ServerLevel.THUNDER_DURATION.sample(server.getRandom()), true, true);
                    }
                    Calendar calendar = Calendar.getInstance();
                    int month = calendar.get(Calendar.MONTH);
                    if (month == Calendar.JUNE) {
                        FIREWORK.set(DataComponents.FIREWORKS, WeatherChangeRecipe.WeatherMode.SURPRISE);
                    } else if (month == Calendar.MARCH && calendar.get(Calendar.DAY_OF_MONTH) == 31) {
                        FIREWORK.set(DataComponents.FIREWORKS, WeatherChangeRecipe.WeatherMode.SURPRISE_2);
                    } else {
                        FIREWORK.set(DataComponents.FIREWORKS, getRecipe().mode().getFireworks());
                    }
                    server.addFreshEntity(new FireworkRocketEntity(server, null, getBlockPos().getX() + 0.5,
                            getBlockPos().getY() + 1.1, getBlockPos().getZ() + 0.5, FIREWORK));
                }
                return true;
            }
        };
    }

    private Boolean canAcceptTask() {
        return !isRedstoneBlocked();
    }

    @Override
    public void serverTick() {
        super.serverTick();

        if (canAct()) {
            craftingTaskHost.tick();
        }
        updateMachineState(MachineState.ACTIVE, isActive());
    }

    @Override
    public boolean isActive() {
        return canAct() && craftingTaskHost.hasTask();
    }

    @Override
    public MachineTankLayout getTankLayout() {
        return MachineTankLayout.builder().tank(TANK, TANK_CAPACITY).build();
    }

    @Override
    public MachineFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    public MachineFluidTank getTank() {
        return TANK.getTank(getFluidHandler());
    }

    @Override
    public MachineFluidHandler createFluidHandler() {
        return new MachineFluidHandler(this, getTankLayout()) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                super.onContentsChanged(slot);
                updateMachineState(MachineState.EMPTY_TANK, TANK.getFluidAmount(this) <= 0);
                craftingTaskHost.newTaskAvailable();
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        };
    }

    @Override
    protected @Nullable MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
                .inputSlot((i, s) -> s.is(Items.FIREWORK_ROCKET))
                .slotAccess(ROCKET)
                .build();
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        craftingTaskHost.newTaskAvailable();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        craftingTaskHost.onLevelReady();
    }

    @Override
    public IOConfig getDefaultIOConfig() {
        return IOConfig.of(IOMode.PULL);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new WeatherObeliskMenu(containerId, playerInventory, this);
    }

    public float getCraftingProgress() {
        return craftingTaskHost.getProgress();
    }

    @Override
    public void saveAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(pTag, lookupProvider);
        saveTank(lookupProvider, pTag);
        craftingTaskHost.save(lookupProvider, pTag);
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(pTag, lookupProvider);
        loadTank(lookupProvider, pTag);
        craftingTaskHost.load(lookupProvider, pTag);
    }

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
}

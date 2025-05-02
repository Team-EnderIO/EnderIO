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
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.Nullable;

public class WeatherObeliskBlockEntity extends MachineBlockEntity implements FluidTankUser {

    private final MachineFluidHandler fluidHandler;
    private static final TankAccess TANK = new TankAccess();
    public static final SingleSlotAccess ROCKET = new SingleSlotAccess();
    private final CraftingMachineTaskHost<WeatherChangeRecipe, WeatherChangeRecipe.Input> craftingTaskHost;

    public WeatherObeliskBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.WEATHER_OBELISK.get(), worldPosition, blockState, false);
        fluidHandler = createFluidHandler();

        craftingTaskHost = new CraftingMachineTaskHost<>(this, this::canAcceptTask,
                MachineRecipes.WEATHER_CHANGE.type().get(), this::createTask, this::createRecipeInput);
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
                TANK.drain(handler, getRecipe().fluid(), IFluidHandler.FluidAction.EXECUTE);
                ROCKET.getItemStack(getInventory()).shrink(1);
            }

            @Override
            protected int makeProgress(int remainingProgress) {
                return ROCKET.getItemStack(getInventory()).isEmpty() ? 0 : 1;
            }

            @Override
            protected int getProgressRequired(WeatherChangeRecipe recipe) {
                return 600;
            }

            @Override
            protected boolean placeOutputs(List<OutputStack> outputs, boolean simulate) {
                if (!simulate && level instanceof ServerLevel server) {
                    switch (getRecipe().mode()) {
                    case DAY -> server.setDayTime(1000);
                    case NIGHT -> server.setDayTime(13000);
                    case RAIN -> server.setWeatherParameters(0, ServerLevel.RAIN_DURATION.sample(server.getRandom()),
                            true, false);
                    case CLEAR ->
                        server.setWeatherParameters(ServerLevel.RAIN_DELAY.sample(server.getRandom()), 0, false, false);
                    case LIGHTNING -> server.setWeatherParameters(0,
                            ServerLevel.THUNDER_DURATION.sample(server.getRandom()), true, true);
                    }
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
        return MachineTankLayout.builder().tank(TANK, 3000).build();
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

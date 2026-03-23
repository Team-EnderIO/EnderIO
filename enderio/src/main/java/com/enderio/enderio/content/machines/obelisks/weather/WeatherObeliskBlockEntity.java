package com.enderio.enderio.content.machines.obelisks.weather;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.core.common.storage.FluidStorage;
import com.enderio.core.common.storage.layout.FluidStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.api.io.IOMode;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.io.IOConfig;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.foundation.storage.SidedResourceHandler;
import com.enderio.enderio.foundation.task.CraftingMachineTask;
import com.enderio.enderio.foundation.task.host.CraftingMachineTaskHost;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIORecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.Calendar;
import java.util.List;

public class WeatherObeliskBlockEntity extends MachineBlockEntity {

    public static final ItemStackTemplate FIREWORK = new ItemStackTemplate(Items.FIREWORK_ROCKET, 1);

    public static final ICapabilityProvider<WeatherObeliskBlockEntity, Direction, ResourceHandler<FluidResource>> FLUID_HANDLER_PROVIDER = (be,
        side) -> be.fluidStorage != null ? SidedResourceHandler.of(be.fluidStorage, side, be) : null;

    public static final SingleSlotAccess ROCKET = new SingleSlotAccess();
    public static final int TANK_CAPACITY = 3000;

    public static final SingleResourceSlotKey<FluidResource> TANK_SLOT = new SingleResourceSlotKey<>();

    public static final FluidStorageLayout<WeatherObeliskBlockEntity> FLUID_STORAGE_LAYOUT =
        FluidStorageLayout.<WeatherObeliskBlockEntity>builder()
            .slot(TANK_SLOT, SlotTemplates.storage(), slot -> slot.capacity(TANK_CAPACITY))
            .build();

    private final FluidStorage<WeatherObeliskBlockEntity> fluidStorage;
    private final CraftingMachineTaskHost<WeatherChangeRecipe, WeatherChangeRecipe.Input> craftingTaskHost;

    public WeatherObeliskBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.WEATHER_OBELISK.get(), worldPosition, blockState, false);

        fluidStorage = new FluidStorage<>(FLUID_STORAGE_LAYOUT, this) {
            @Override
            protected void onContentsChanged(int index, FluidStack previousContents) {
                super.onContentsChanged(index, previousContents);
                setChanged();
                updateMachineState(MachineState.EMPTY_TANK, fluidStorage.getAmountAsInt(TANK_SLOT) <= 0);
                craftingTaskHost.newTaskAvailable();
                if (level != null) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                }
            }
        };

        craftingTaskHost = new CraftingMachineTaskHost<>(this, this::canAcceptTask,
                EIORecipeTypes.WEATHER_CHANGE.get(), this::createTask, this::createRecipeInput) {

            @Override
            protected boolean shouldStartNewTask() {
                if (ROCKET.getStack(getInventory()).isEmpty()) {
                    return true;
                }
                return super.shouldStartNewTask();
            }
        };
    }

    private WeatherChangeRecipe.Input createRecipeInput() {
        return new WeatherChangeRecipe.Input(fluidStorage.getStack(TANK_SLOT));
    }

    private CraftingMachineTask<WeatherChangeRecipe, WeatherChangeRecipe.Input> createTask(Level level,
            WeatherChangeRecipe.Input input,
            @Nullable RecipeHolder<WeatherChangeRecipe> weatherChangeRecipeRecipeHolder) {
        return new CraftingMachineTask<>(level, getInventory(), input, null, weatherChangeRecipeRecipeHolder) {

            @Override
            protected void consumeInputs(WeatherChangeRecipe recipe) {
                try (Transaction transaction = Transaction.openRoot()) {
                    fluidStorage.internalExtract(TANK_SLOT, FluidResource.of(recipe.fluid()), recipe.fluid().amount(), transaction);
                    transaction.commit();
                }
                ROCKET.getStack(getInventory()).shrink(1);
            }

            @Override
            protected int makeProgress(int remainingProgress) {
                boolean hasRocket = !ROCKET.getStack(getInventory()).isEmpty();
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
                if (!simulate && level instanceof ServerLevel serverLevel) {
                    MinecraftServer server = serverLevel.getServer();
                    switch (getRecipe().mode()) {
                    case RAIN -> server.setWeatherParameters(0, ServerLevel.RAIN_DURATION.sample(serverLevel.getRandom()),
                            true, false);
                    case CLEAR ->
                        server.setWeatherParameters(ServerLevel.RAIN_DELAY.sample(serverLevel.getRandom()), 0, false, false);
                    case LIGHTNING -> server.setWeatherParameters(0,
                            ServerLevel.THUNDER_DURATION.sample(serverLevel.getRandom()), true, true);
                    }
                    Calendar calendar = Calendar.getInstance();
                    int month = calendar.get(Calendar.MONTH);

                    ItemStack firework = FIREWORK.create();
                    if (month == Calendar.JUNE) {
                        firework.set(DataComponents.FIREWORKS, WeatherChangeRecipe.WeatherMode.SURPRISE);
                    } else if (month == Calendar.MARCH && calendar.get(Calendar.DAY_OF_MONTH) == 31) {
                        firework.set(DataComponents.FIREWORKS, WeatherChangeRecipe.WeatherMode.SURPRISE_2);
                    } else {
                        firework.set(DataComponents.FIREWORKS, getRecipe().mode().getFireworks());
                    }
                    serverLevel.addFreshEntity(new FireworkRocketEntity(serverLevel, null, getBlockPos().getX() + 0.5,
                            getBlockPos().getY() + 1.1, getBlockPos().getZ() + 0.5, firework));
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

    public FluidStack getStoredFluid() {
        return fluidStorage.getStack(TANK_SLOT);
    }

    @Override
    protected @Nullable MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
                .storageSlot((i, s) -> s.is(Items.FIREWORK_ROCKET))
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
        return IOConfig.of(IOMode.BOTH);
    }

    @Override
    public @Nullable AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new WeatherObeliskMenu(containerId, playerInventory, this);
    }

    public float getCraftingProgress() {
        return craftingTaskHost.getProgress();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild("Fluid", fluidStorage);
        output.putChild(MachineNBTKeys.CRAFTING_TASK, craftingTaskHost);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.child("Fluid")
            .ifPresent(fluidStorage::deserialize);
        var task = input.child(MachineNBTKeys.CRAFTING_TASK);
        task.ifPresent(craftingTaskHost::deserialize);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);

        SimpleFluidContent storedFluid = components.get(EIODataComponents.ITEM_FLUID_CONTENT);
        if (storedFluid != null) {
            fluidStorage.setStack(TANK_SLOT, storedFluid.copy());
        }
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        var fluidStored = getStoredFluid();
        if (!fluidStored.isEmpty()) {
            components.set(EIODataComponents.ITEM_FLUID_CONTENT, SimpleFluidContent.copyOf(fluidStored));
        }
    }
}

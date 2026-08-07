package com.enderio.enderio.content.machines.obelisks.weather;

import com.enderio.core.common.storage.FluidStorage;
import com.enderio.core.common.storage.layout.FluidStorageLayout;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.api.io.IOMode;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.crafting.MachineCraftingContext;
import com.enderio.enderio.foundation.crafting.MachineCraftingManager;
import com.enderio.enderio.foundation.crafting.MachineCraftingStatus;
import com.enderio.enderio.foundation.io.IOConfig;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.foundation.storage.SidedResourceHandler;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIORecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.Calendar;
import java.util.function.Consumer;

public class WeatherObeliskBlockEntity extends MachineBlockEntity {

    public static final ItemStackTemplate FIREWORK = new ItemStackTemplate(Items.FIREWORK_ROCKET, 1);

    public static final ICapabilityProvider<WeatherObeliskBlockEntity, Direction, ResourceHandler<FluidResource>> FLUID_HANDLER_PROVIDER = (be,
        side) -> be.fluidStorage != null ? SidedResourceHandler.of(be.fluidStorage, side, be) : null;

    public static final SingleResourceSlotKey<ItemResource> ROCKET = new SingleResourceSlotKey<>();
    public static final int TANK_CAPACITY = 3000;

    public static final SingleResourceSlotKey<FluidResource> TANK_SLOT = new SingleResourceSlotKey<>();

    public static final FluidStorageLayout FLUID_STORAGE_LAYOUT =
        FluidStorageLayout.builder()
            .add(TANK_SLOT, SlotTemplates.storage(TANK_CAPACITY))
            .build();

    private final FluidStorage fluidStorage;
    private final MachineCraftingManager<WeatherChangeRecipe, WeatherChangeRecipe.Input> craftingManager;
    private WeatherChangeRecipe.@Nullable Input recipeInput;

    public WeatherObeliskBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.WEATHER_OBELISK.get(), worldPosition, blockState, false);

        fluidStorage = new FluidStorage(FLUID_STORAGE_LAYOUT) {
            @Override
            protected void onContentsChanged(int index, FluidStack previousContents) {
                super.onContentsChanged(index, previousContents);
                setChanged();
                updateMachineState(MachineState.EMPTY_TANK, fluidStorage.getAmountAsInt(TANK_SLOT) <= 0);
                recipeInput = null;

                if (level != null) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                }
            }
        };

        craftingManager = new MachineCraftingManager<>(EIORecipeTypes.WEATHER_CHANGE.get(), new CraftingContext());
    }

    private WeatherChangeRecipe.Input getRecipeInput() {
        if (recipeInput == null) {
            recipeInput = new WeatherChangeRecipe.Input(fluidStorage.getStack(TANK_SLOT), getInventory().getStack(ROCKET));
        }

        return recipeInput;
    }

    @Override
    public void serverTick() {
        super.serverTick();

        if (canAct()) {
            craftingManager.tick();
        }

        updateMachineState(MachineState.ACTIVE, isActive());
    }

    @Override
    public boolean isActive() {
        return canAct() && craftingManager.status() == MachineCraftingStatus.ACTIVE;
    }

    public FluidStack getStoredFluid() {
        return fluidStorage.getStack(TANK_SLOT);
    }

    @Override
    protected @Nullable ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .add(ROCKET, SlotTemplates.storage(64), b -> b
                .filter((_, itemResource) -> itemResource.is(Items.FIREWORK_ROCKET)))
            .build();
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        recipeInput = null;
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
        return craftingManager.craftingProgress();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild("Fluid", fluidStorage);
        output.putChild(MachineNBTKeys.CRAFTING_TASK, craftingManager);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.readChild("Fluid", fluidStorage);
        input.readChild(MachineNBTKeys.CRAFTING_TASK, craftingManager);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);

        SimpleFluidContent storedFluid = components.get(EIODataComponents.ITEM_FLUID_CONTENT);
        if (storedFluid != null) {
            fluidStorage.setStack(TANK_SLOT, storedFluid.copy());
        }

        craftingManager.applyCraftingState(components.get(EIODataComponents.MACHINE_CRAFTING_STATE));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        var fluidStored = getStoredFluid();
        if (!fluidStored.isEmpty()) {
            components.set(EIODataComponents.ITEM_FLUID_CONTENT, SimpleFluidContent.copyOf(fluidStored));
        }

        components.set(EIODataComponents.MACHINE_CRAFTING_STATE, craftingManager.getCraftingState());
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard("Fluid");
        output.discard(MachineNBTKeys.CRAFTING_TASK);
    }

    private class CraftingContext extends MachineCraftingContext<WeatherChangeRecipe, WeatherChangeRecipe.Input> {

        private ItemStack firework;
        private Consumer<ServerLevel> weatherFunction;
        private int cooldownTicks;

        @Override
        public WeatherChangeRecipe.Input recipeInput() {
            return getRecipeInput();
        }

        @Override
        public @Nullable ServerLevel level() {
            if (getLevel() instanceof ServerLevel serverLevel) {
                return serverLevel;
            }

            return null;
        }

        @Override
        public int getCraftingTicks(RecipeHolder<WeatherChangeRecipe> recipe) {
            // TODO: Config and lower it.
            return 600;
        }

        @Override
        public boolean tryProgressCraft(WeatherChangeRecipe recipe) {
            if (cooldownTicks > 0) {
                cooldownTicks--;
                return false;
            }

            boolean hasRocket = !getInventory().getStack(ROCKET).isEmpty();
            boolean weatherDifferent = switch (recipe.mode()) {
                case RAIN -> !level.isRaining();
                case CLEAR -> level.isRaining() || level.isThundering();
                case LIGHTNING -> !level.isThundering();
            };
            boolean sky = level.canSeeSky(getBlockPos().above());
            return hasRocket && weatherDifferent && sky ? true : false;
        }

        @Override
        public boolean tryCompleteCraft(WeatherChangeRecipe recipe, RandomSource random) {
            boolean didComplete = super.tryCompleteCraft(recipe, random);

            // Do this outside the transaction in the base implementationt to avoid any accidental changes (insert outputs occurs before consume inputs).
            if (didComplete) {
                // Apply weather change
                weatherFunction.accept(level());

                // Fire the firework
                level().addFreshEntity(new FireworkRocketEntity(level(), null, getBlockPos().getX() + 0.5,
                    getBlockPos().getY() + 1.1, getBlockPos().getZ() + 0.5, firework));

                // 2 seconds cooldown to wait for weather to change.
                cooldownTicks = 40;
            }

            return didComplete;
        }

        @Override
        protected boolean consumeRecipeInputs(WeatherChangeRecipe recipe, WeatherChangeRecipe.Input recipeInput, TransactionContext transaction) {
            int fluidConsumed = fluidStorage.extract(TANK_SLOT, FluidResource.of(recipeInput.fluid()), recipe.fluid().amount(), transaction);
            if (fluidConsumed != recipe.fluid().amount()) {
                return false;
            }

            int rocketsConsumed = getInventory().extract(ROCKET, ItemResource.of(recipeInput.fireworks()), 1, transaction);
            if (rocketsConsumed != 1) {
                return false;
            }

            return true;
        }

        @Override
        protected boolean insertRecipeOutputs(WeatherChangeRecipe recipe, WeatherChangeRecipe.Input recipeInput, RandomSource random,
            TransactionContext transaction) {

            weatherFunction = switch (recipe.mode()) {
                case CLEAR -> level -> level.getServer().setWeatherParameters(ServerLevel.RAIN_DELAY.sample(level.getRandom()), 0, false, false);
                case RAIN -> level -> level.getServer().setWeatherParameters(0, ServerLevel.RAIN_DURATION.sample(level.getRandom()), true, false);
                case LIGHTNING -> level -> level.getServer().setWeatherParameters(0, ServerLevel.THUNDER_DURATION.sample(level.getRandom()), true, true);
            };

            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH);

            firework = FIREWORK.create();
            if (month == Calendar.JUNE) {
                firework.set(DataComponents.FIREWORKS, WeatherChangeRecipe.WeatherMode.SURPRISE);
            } else if (month == Calendar.MARCH && calendar.get(Calendar.DAY_OF_MONTH) == 31) {
                firework.set(DataComponents.FIREWORKS, WeatherChangeRecipe.WeatherMode.SURPRISE_2);
            } else {
                firework.set(DataComponents.FIREWORKS, recipe.mode().getFireworks());
            }

            return true;
        }
    }
}

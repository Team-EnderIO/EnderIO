package com.enderio.enderio.content.storage.fluid_tank;

import com.enderio.enderio.foundation.attachment.FluidTankUser;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.io.fluid.FluidItemInteractive;
import com.enderio.enderio.foundation.io.fluid.MachineFluidHandler;
import com.enderio.enderio.foundation.io.fluid.MachineFluidTank;
import com.enderio.enderio.foundation.io.fluid.MachineTankLayout;
import com.enderio.enderio.foundation.io.fluid.TankAccess;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIORecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

// TODO: Rewrite this with tasks?
//       Could implement a task for each thing it currently has in the If's
public abstract class FluidTankBlockEntity extends MachineBlockEntity implements FluidItemInteractive, FluidTankUser {

    public static class Standard extends FluidTankBlockEntity {
        public static final int CAPACITY = 16 * FluidType.BUCKET_VOLUME;

        public Standard(BlockPos worldPosition, BlockState blockState) {
            super(EIOBlockEntities.FLUID_TANK.get(), worldPosition, blockState);
        }

        @Override
        public MachineTankLayout getTankLayout() {
            return new MachineTankLayout.Builder().tank(TANK, CAPACITY).build();
        }
    }

    public static class Enhanced extends FluidTankBlockEntity {
        public static final int CAPACITY = 32 * FluidType.BUCKET_VOLUME;

        public Enhanced(BlockPos worldPosition, BlockState blockState) {
            super(EIOBlockEntities.PRESSURIZED_FLUID_TANK.get(), worldPosition, blockState);
        }

        @Override
        public MachineTankLayout getTankLayout() {
            return new MachineTankLayout.Builder().tank(TANK, CAPACITY).build();
        }

    }

    private final MachineFluidHandler fluidHandler;
    public static final TankAccess TANK = new TankAccess();

    // TODO: Swap from optional to nullable?
    private Optional<RecipeHolder<TankRecipe>> currentRecipe = Optional.empty();

    public static final SingleSlotAccess FLUID_FILL_INPUT = new SingleSlotAccess();
    public static final SingleSlotAccess FLUID_FILL_OUTPUT = new SingleSlotAccess();
    public static final SingleSlotAccess FLUID_DRAIN_INPUT = new SingleSlotAccess();
    public static final SingleSlotAccess FLUID_DRAIN_OUTPUT = new SingleSlotAccess();

    public FluidTankBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState, true);
        fluidHandler = createFluidHandler();
    }

    @Override
    public boolean isActive() {
        // No active state on tanks
        return false;
    }

    private TankRecipe.Input createRecipeInput() {
        return new TankRecipe.Input(FLUID_DRAIN_INPUT.getItemStack(getInventory()),
                FLUID_FILL_INPUT.getItemStack(getInventory()), TANK.getTank(this));
    }

    @Override
    public void serverTick() {
        if (canAct(5)) {
            fillInternal();
            drainInternal();
            tryTankRecipe();
            tryMendTool();
        }

        super.serverTick();
    }

    // region Inventory

    public boolean acceptItemFill(ItemStack item) {
        // bucket types
        IFluidHandlerItem fluidHandlerCap = item.getCapability(Capabilities.Fluid.ITEM);
        if (fluidHandlerCap != null) {
            return true;
        }

        // fill recipes
        if (level instanceof ServerLevel serverLevel) {
            //TODO use the new methods to check instead of doing this ourselves
            List<RecipeHolder<TankRecipe>> allRecipes = serverLevel.recipeAccess()
                    .recipeMap().byType(EIORecipes.TANK.type().get()).stream().toList();
            return allRecipes.stream()
                    .anyMatch((recipe) -> recipe.value().mode() == TankRecipe.Mode.EMPTY
                            && recipe.value().input().test(item));
        }

        return false;
    }

    public boolean acceptItemDrain(ItemStack item) {
        // bucket types
        IFluidHandlerItem fluidHandlerCap = item.getCapability(Capabilities.Fluid.ITEM);
        if (fluidHandlerCap != null) {
            return true;
        }

        // Mending
        FluidStack fluid = TANK.getFluid(this);

        if (item.isDamageableItem() && !fluid.isEmpty() && fluid.is(Tags.Fluids.EXPERIENCE)) {
            var enchantmentsRecipe = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            var mendingEnchantment = enchantmentsRecipe.getOrThrow(Enchantments.MENDING);

            if (item.getEnchantmentLevel(mendingEnchantment) > 0) {
                return true;
            }
        }

        // drain recipes
        if (level instanceof ServerLevel serverLevel) {
            List<RecipeHolder<TankRecipe>> allRecipes = serverLevel.recipeAccess()
                    .recipeMap().byType(EIORecipes.TANK.type().get()).stream().toList();
            return allRecipes.stream()
                    .anyMatch((recipe) -> recipe.value().mode() == TankRecipe.Mode.FILL
                            && recipe.value().input().test(item));
        }

        return false;
    }

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
                .inputSlot((slot, stack) -> acceptItemFill(stack))
                .slotAccess(FLUID_FILL_INPUT)
                .outputSlot()
                .slotAccess(FLUID_FILL_OUTPUT)
                .inputSlot((slot, stack) -> acceptItemDrain(stack))
                .slotAccess(FLUID_DRAIN_INPUT)
                .outputSlot()
                .slotAccess(FLUID_DRAIN_OUTPUT)
                .build();
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);

        if (level != null) {
            if (level instanceof ServerLevel serverLevel) {
                currentRecipe = serverLevel.recipeAccess()
                        .getRecipeFor(EIORecipes.TANK.type().get(), createRecipeInput(), level);
            }
        }
    }

    // endregion

    @Override
    public MachineFluidHandler getFluidHandler() {
        return this.fluidHandler;
    }

    @Override
    public MachineFluidHandler createFluidHandler() {
        return new MachineFluidHandler(this, getTankLayout()) {
            @Override
            protected void onContentsChanged(int slot) {
                onTankContentsChanged();
                setChanged();
                super.onContentsChanged(slot);
                updateMachineState(MachineState.EMPTY_TANK, TANK.getFluidAmount(this) <= 0);
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        };
    }

    public MachineFluidTank getFluidTank() {
        return TANK.getTank(this);
    }


    private void fillInternal() {
        InternalTankTasks.fillInternal(this, TANK, FLUID_FILL_INPUT, FLUID_FILL_OUTPUT);
    }

    private void drainInternal() {
        InternalTankTasks.drainInternal(this, TANK, FLUID_DRAIN_INPUT, FLUID_DRAIN_OUTPUT);
    }

    private void tryMendTool() {
        InternalTankTasks.tryMendTool(this, TANK, FLUID_DRAIN_INPUT, FLUID_DRAIN_OUTPUT);
    }

    // endregion

    private void tryTankRecipe() {
        currentRecipe.ifPresent(recipe -> {
            ItemStack recipeResultStack = recipe.value().output().copy();

            switch (recipe.value().mode()) {
            case EMPTY -> {
                ItemStack outputStack = FLUID_FILL_OUTPUT.getItemStack(this);

                if (outputStack.isEmpty() || (outputStack.is(recipeResultStack.getItem())
                        && outputStack.getCount() < outputStack.getMaxStackSize())) {
                    FLUID_FILL_INPUT.getItemStack(this).shrink(1);

                    TANK.fill(this, recipe.value().fluid(), IFluidHandler.FluidAction.EXECUTE);

                    if (outputStack.isEmpty()) {
                        FLUID_FILL_OUTPUT.setStackInSlot(this, recipeResultStack.copy());
                    } else {
                        FLUID_FILL_OUTPUT.getItemStack(this).grow(1);
                    }
                }
            }
            case FILL -> {
                ItemStack outputStack = FLUID_DRAIN_OUTPUT.getItemStack(this);

                if (outputStack.isEmpty() || (outputStack.is(recipeResultStack.getItem())
                        && outputStack.getCount() < outputStack.getMaxStackSize())) {
                    FLUID_DRAIN_INPUT.getItemStack(this).shrink(1);

                    TANK.drain(this, recipe.value().fluid(), IFluidHandler.FluidAction.EXECUTE);

                    if (outputStack.isEmpty()) {
                        FLUID_DRAIN_OUTPUT.setStackInSlot(this, recipeResultStack.copy());
                    } else {
                        FLUID_DRAIN_OUTPUT.getItemStack(this).grow(1);
                    }
                }
            }
            default -> throw new NotImplementedException();
            }
        });
    }

    private void onTankContentsChanged() {
        if (level != null) {
            if (level instanceof ServerLevel serverLevel) {
                currentRecipe = serverLevel.recipeAccess()
                        .getRecipeFor(EIORecipes.TANK.type().get(), createRecipeInput(), level);
            }

            level.getLightEngine().checkBlock(worldPosition);
        }
    }

    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player pPlayer) {
        return new FluidTankMenu(containerId, playerInventory, this);
    }

    // region Serialization

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
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
    protected void saveAdditionalSynced(ValueOutput output) {
        super.saveAdditionalSynced(output);
        saveTank(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        loadTank(input);
    }

    // endregion
}

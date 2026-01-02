package com.enderio.enderio.content.storage.fluid_tank;

import com.enderio.core.common.storage.FluidStorageLayout;
import com.enderio.core.common.storage.SingleResourceSlotKey;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.io.fluid.FluidItemInteractive;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.foundation.storage.FluidStorage;
import com.enderio.enderio.foundation.storage.SidedResourceHandler;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIORecipes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.apache.commons.lang3.NotImplementedException;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

// TODO: Rewrite this with tasks?
//       Could implement a task for each thing it currently has in the If's
public abstract class FluidTankBlockEntity extends MachineBlockEntity implements FluidItemInteractive {

    public static final ICapabilityProvider<FluidTankBlockEntity, Direction, ResourceHandler<FluidResource>> FLUID_HANDLER_PROVIDER = (be,
        side) -> be.fluidStorage != null ? SidedResourceHandler.of(be.fluidStorage, side, be) : null;

    public static class Standard extends FluidTankBlockEntity {
        public static final int CAPACITY = 16 * FluidType.BUCKET_VOLUME;

        public Standard(BlockPos worldPosition, BlockState blockState) {
            super(EIOBlockEntities.FLUID_TANK.get(), worldPosition, blockState);
        }

        @Override
        public int getCapacity() {
            return CAPACITY;
        }
    }

    public static class Enhanced extends FluidTankBlockEntity {
        public static final int CAPACITY = 32 * FluidType.BUCKET_VOLUME;

        public Enhanced(BlockPos worldPosition, BlockState blockState) {
            super(EIOBlockEntities.PRESSURIZED_FLUID_TANK.get(), worldPosition, blockState);
        }

        @Override
        public int getCapacity() {
            return CAPACITY;
        }
    }

    public static final SingleResourceSlotKey<FluidResource> TANK_SLOT = new SingleResourceSlotKey<>();

    public static final FluidStorageLayout<FluidTankBlockEntity> FLUID_STORAGE_LAYOUT =
        FluidStorageLayout.<FluidTankBlockEntity>builder()
            .storageSlot(TANK_SLOT, slot -> slot.capacity((fr, tank) -> tank.getCapacity()))
            .build();

    private final FluidStorage<FluidTankBlockEntity> fluidStorage;

    // TODO: Swap from optional to nullable?
    private Optional<RecipeHolder<TankRecipe>> currentRecipe = Optional.empty();

    public static final SingleSlotAccess FLUID_FILL_INPUT = new SingleSlotAccess();
    public static final SingleSlotAccess FLUID_FILL_OUTPUT = new SingleSlotAccess();
    public static final SingleSlotAccess FLUID_DRAIN_INPUT = new SingleSlotAccess();
    public static final SingleSlotAccess FLUID_DRAIN_OUTPUT = new SingleSlotAccess();

    public FluidTankBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState, true);

        fluidStorage = new FluidStorage<>(FLUID_STORAGE_LAYOUT, this) {
            @Override
            protected void onContentsChanged(int index, FluidStack previousContents) {
                onTankContentsChanged();
                setChanged();
                super.onContentsChanged(index, previousContents);
                updateMachineState(MachineState.EMPTY_TANK, fluidStorage.getAmountAsInt(TANK_SLOT) <= 0);
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
            }
        };
    }

    public abstract int getCapacity();

    @Override
    public boolean isActive() {
        // No active state on tanks
        return false;
    }

    private TankRecipe.Input createRecipeInput() {
        return new TankRecipe.Input(FLUID_DRAIN_INPUT.getStack(getInventory()),
                FLUID_FILL_INPUT.getStack(getInventory()), fluidStorage.getStack(TANK_SLOT), getCapacity());
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

    public boolean acceptItemFill(ItemResource item) {
        var stack = item.toStack();

        // bucket types
        var fluidHandlerCap = ItemAccess.forStack(stack).getCapability(Capabilities.Fluid.ITEM);
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
                            && recipe.value().input().test(stack));
        }

        return false;
    }

    public boolean acceptItemDrain(ItemResource item) {
        var stack = item.toStack();

        // bucket types
        var fluidHandlerCap = ItemAccess.forStack(stack).getCapability(Capabilities.Fluid.ITEM);
        if (fluidHandlerCap != null) {
            return true;
        }

        // Mending
        FluidStack fluid = fluidStorage.getStack(TANK_SLOT);

        if (stack.isDamageableItem() && !fluid.isEmpty() && fluid.is(Tags.Fluids.EXPERIENCE)) {
            var enchantmentsRecipe = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
            var mendingEnchantment = enchantmentsRecipe.getOrThrow(Enchantments.MENDING);

            if (stack.getEnchantmentLevel(mendingEnchantment) > 0) {
                return true;
            }
        }

        // drain recipes
        if (level instanceof ServerLevel serverLevel) {
            List<RecipeHolder<TankRecipe>> allRecipes = serverLevel.recipeAccess()
                    .recipeMap().byType(EIORecipes.TANK.type().get()).stream().toList();
            return allRecipes.stream()
                    .anyMatch((recipe) -> recipe.value().mode() == TankRecipe.Mode.FILL
                            && recipe.value().input().test(stack));
        }

        return false;
    }

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
                .inputSlot((slot, itemResource) -> acceptItemFill(itemResource))
                .slotAccess(FLUID_FILL_INPUT)
                .outputSlot()
                .slotAccess(FLUID_FILL_OUTPUT)
                .inputSlot((slot, itemResource) -> acceptItemDrain(itemResource))
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

    public FluidStack getStoredFluid() {
        return fluidStorage.getStack(TANK_SLOT);
    }


    private void fillInternal() {
        // TODO: re-enable once it uses the new transfer API
//        InternalTankTasks.fillInternal(this, TANK, FLUID_FILL_INPUT, FLUID_FILL_OUTPUT);
    }

    private void drainInternal() {
        // TODO: re-enable once it uses the new transfer API
//        InternalTankTasks.drainInternal(this, TANK, FLUID_DRAIN_INPUT, FLUID_DRAIN_OUTPUT);
    }

    private void tryMendTool() {
        // TODO: re-enable once it uses the new transfer API
//        InternalTankTasks.tryMendTool(this, TANK, FLUID_DRAIN_INPUT, FLUID_DRAIN_OUTPUT);
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

                    try (Transaction transaction = Transaction.openRoot()) {
                        int filled = fluidStorage.insert(TANK_SLOT, FluidResource.of(recipe.value().fluid()),
                            recipe.value().fluid().getAmount(), transaction);

                        if (filled != recipe.value().fluid().getAmount()) {
                            return;
                        }

                        transaction.commit();
                        FLUID_FILL_INPUT.getItemStack(this).shrink(1);

                        if (outputStack.isEmpty()) {
                            FLUID_FILL_OUTPUT.setStackInSlot(this, recipeResultStack.copy());
                        } else {
                            FLUID_FILL_OUTPUT.getItemStack(this).grow(1);
                        }
                    }
                }
            }
            case FILL -> {
                ItemStack outputStack = FLUID_DRAIN_OUTPUT.getItemStack(this);

                if (outputStack.isEmpty() || (outputStack.is(recipeResultStack.getItem())
                        && outputStack.getCount() < outputStack.getMaxStackSize())) {

                    try (Transaction transaction = Transaction.openRoot()) {
                        int extracted = fluidStorage.extract(TANK_SLOT, FluidResource.of(recipe.value().fluid()),
                            recipe.value().fluid().getAmount(), transaction);

                        if (extracted != recipe.value().fluid().getAmount()) {
                            return;
                        }

                        transaction.commit();
                        FLUID_DRAIN_INPUT.getItemStack(this).shrink(1);

                        if (outputStack.isEmpty()) {
                            FLUID_DRAIN_OUTPUT.setStackInSlot(this, recipeResultStack.copy());
                        } else {
                            FLUID_DRAIN_OUTPUT.getItemStack(this).grow(1);
                        }
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

    @Override
    protected void saveAdditionalSynced(ValueOutput output) {
        super.saveAdditionalSynced(output);
        output.putChild("Fluid", fluidStorage);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.child("Fluid")
            .ifPresent(fluidStorage::deserialize);
    }

    // endregion
}

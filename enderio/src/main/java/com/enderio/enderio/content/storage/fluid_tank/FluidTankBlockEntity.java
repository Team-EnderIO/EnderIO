package com.enderio.enderio.content.storage.fluid_tank;

import com.enderio.core.common.storage.FluidStorage;
import com.enderio.core.common.storage.layout.FluidStorageLayout;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.io.fluid.FluidItemInteractive;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.foundation.storage.SidedResourceHandler;
import com.enderio.enderio.foundation.util.SizedFluidIngredientHelper;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIORecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
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
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.apache.commons.lang3.NotImplementedException;
import org.jspecify.annotations.Nullable;

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

    private final FluidStorage fluidStorage;

    // TODO: Swap from optional to nullable?
    private Optional<RecipeHolder<TankRecipe>> currentRecipe = Optional.empty();

    public static final SingleResourceSlotKey<ItemResource> FLUID_FILL_INPUT = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> FLUID_FILL_OUTPUT = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> FLUID_DRAIN_INPUT = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> FLUID_DRAIN_OUTPUT = new SingleResourceSlotKey<>();

    public FluidTankBlockEntity(BlockEntityType<?> type, BlockPos worldPosition, BlockState blockState) {
        super(type, worldPosition, blockState, true);

        var fluidStorageLayout = FluidStorageLayout.builder()
            .add(TANK_SLOT, SlotTemplates.storage(), slot -> slot.capacity((_) -> this.getCapacity()))
            .build();

        fluidStorage = new FluidStorage(fluidStorageLayout) {
            @Override
            protected void onContentsChanged(int index, FluidStack previousContents) {
                onTankContentsChanged();
                setChanged();
                super.onContentsChanged(index, previousContents);
                updateMachineState(MachineState.EMPTY_TANK, fluidStorage.getAmountAsInt(TANK_SLOT) <= 0);

                if (level != null) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                }
            }
        };
    }

    public abstract int getCapacity();

    public FluidStorage getFluidStorage() {
        return fluidStorage;
    }

    @Override
    public boolean isActive() {
        // No active state on tanks
        return false;
    }

    private TankRecipe.Input createRecipeInput() {
        return new TankRecipe.Input(getInventory().getStack(FLUID_DRAIN_INPUT),
                getInventory().getStack(FLUID_FILL_INPUT), fluidStorage.getStack(TANK_SLOT), getCapacity());
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
            for (int i = 0; i < fluidHandlerCap.size(); i++) {
                if (!fluidHandlerCap.getResource(i).isEmpty()) {
                    return true;
                }
            }
        }

        // fill recipes
        if (level instanceof ServerLevel serverLevel) {
            //TODO use the new methods to check instead of doing this ourselves
            List<RecipeHolder<TankRecipe>> allRecipes = serverLevel.recipeAccess()
                    .recipeMap().byType(EIORecipeTypes.TANK.get()).stream().toList();
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
                    .recipeMap().byType(EIORecipeTypes.TANK.get()).stream().toList();
            return allRecipes.stream()
                    .anyMatch((recipe) -> recipe.value().mode() == TankRecipe.Mode.FILL
                            && recipe.value().input().test(stack));
        }

        return false;
    }

    @Override
    public ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .add(FLUID_FILL_INPUT, SlotTemplates.input(), b -> b
                .filter((_, itemResource) -> acceptItemFill(itemResource)))
            .add(FLUID_FILL_OUTPUT, SlotTemplates.output())
            .add(FLUID_DRAIN_INPUT, SlotTemplates.input(), b -> b
                .filter((_, itemResource) -> acceptItemDrain(itemResource)))
            .add(FLUID_DRAIN_OUTPUT, SlotTemplates.output())
            .build();
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);

        if (level != null) {
            if (level instanceof ServerLevel serverLevel) {
                currentRecipe = serverLevel.recipeAccess()
                        .getRecipeFor(EIORecipeTypes.TANK.get(), createRecipeInput(), level);
            }
        }
    }

    // endregion

    public FluidStack getStoredFluid() {
        return fluidStorage.getStack(TANK_SLOT);
    }


    private void fillInternal() {
        InternalTankTasks.fillInternal(fluidStorage, TANK_SLOT, getInventory(), FLUID_FILL_INPUT, FLUID_FILL_OUTPUT);
    }

    private void drainInternal() {
        InternalTankTasks.drainInternal(fluidStorage, TANK_SLOT, getInventory(), FLUID_DRAIN_INPUT, FLUID_DRAIN_OUTPUT);
    }

    private void tryMendTool() {
        InternalTankTasks.tryMendTool(level.registryAccess(), fluidStorage, TANK_SLOT, getInventory(), FLUID_DRAIN_INPUT, FLUID_DRAIN_OUTPUT);
    }

    // endregion

    private void tryTankRecipe() {
        currentRecipe.ifPresent(recipe -> {
            // Ensure the recipe is still valid.
            // Fixes GH-1302.
            if (!recipe.value().matches(createRecipeInput(), level)) {
                return;
            }

            ItemStack recipeResultStack = recipe.value().output().create();

            switch (recipe.value().mode()) {
            case EMPTY -> {
                ItemStack outputStack = getInventory().getStack(FLUID_FILL_OUTPUT);

                if (outputStack.isEmpty() || (outputStack.is(recipeResultStack.getItem())
                    && outputStack.getCount() < outputStack.getMaxStackSize())) {

                    try (Transaction transaction = Transaction.openRoot()) {
                        // Get the first matching fluid from the ingredient (for EMPTY mode, we're adding fluid to tank)
                        List<Holder<Fluid>> possibleFluids = SizedFluidIngredientHelper.getFluidStacksInPreferredOrder(recipe.value().fluid());
                        FluidResource fluidToFill = FluidResource.EMPTY;
                        for (Holder<Fluid> fluidToTry : possibleFluids) {
                            var fluidResource = FluidResource.of(fluidToTry);
                            try (Transaction simulateTransaction = Transaction.open(transaction)) {
                                int filled = fluidStorage.internalInsert(TANK_SLOT, fluidResource, recipe.value().fluid().amount(), simulateTransaction);
                                if (filled == recipe.value().fluid().amount()) {
                                    fluidToFill = fluidResource;
                                    break;
                                }
                            }
                        }

                        if (fluidToFill.isEmpty()) {
                            return;
                        }

                        int inserted = fluidStorage.internalInsert(TANK_SLOT, fluidToFill,
                            recipe.value().fluid().amount(), transaction);
                        if (inserted != recipe.value().fluid().amount()) {
                            return;
                        }

                        getInventory().getStack(FLUID_FILL_INPUT).shrink(1);

                        if (outputStack.isEmpty()) {
                            getInventory().setStack(FLUID_FILL_OUTPUT, recipeResultStack.copy());
                        } else {
                            getInventory().getStack(FLUID_FILL_OUTPUT).grow(1);
                        }

                        transaction.commit();
                    }
                }
            }
            case FILL -> {
                ItemStack outputStack = getInventory().getStack(FLUID_DRAIN_OUTPUT);

                if (outputStack.isEmpty() || (outputStack.is(recipeResultStack.getItem())
                    && outputStack.getCount() < outputStack.getMaxStackSize())) {

                    // Ensure the fluid matches the recipe
                    FluidStack storedFluid = fluidStorage.getStack(TANK_SLOT);
                    if (!recipe.value().fluid().test(storedFluid)) {
                        return;
                    }

                    try (Transaction transaction = Transaction.openRoot()) {
                        int drained = fluidStorage.extract(TANK_SLOT, fluidStorage.getResource(TANK_SLOT), recipe.value().fluid().amount(), transaction);
                        if (drained != recipe.value().fluid().amount()) {
                            return;
                        }

                        getInventory().getStack(FLUID_DRAIN_INPUT).shrink(1);

                        if (outputStack.isEmpty()) {
                            getInventory().setStack(FLUID_DRAIN_OUTPUT, recipeResultStack.copy());
                        } else {
                            getInventory().getStack(FLUID_DRAIN_OUTPUT).grow(1);
                        }

                        transaction.commit();
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
                        .getRecipeFor(EIORecipeTypes.TANK.get(), createRecipeInput(), level);
            }

            level.getLightEngine().checkBlock(worldPosition);
        }
    }

    @Nullable
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
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

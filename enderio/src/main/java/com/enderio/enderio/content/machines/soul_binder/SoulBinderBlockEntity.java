package com.enderio.enderio.content.machines.soul_binder;

import com.enderio.core.common.storage.FluidStorage;
import com.enderio.core.common.storage.layout.FluidStorageLayout;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.api.UseOnly;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.api.soul.SoulBoundUtils;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineInventoryLayout;
import com.enderio.enderio.foundation.inventory.MultiSlotAccess;
import com.enderio.enderio.foundation.inventory.SingleSlotAccess;
import com.enderio.enderio.foundation.recipe.MachineRecipeCaches;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.foundation.storage.IOConfigurableExternalDelegatingResourceStorage;
import com.enderio.enderio.foundation.task.PoweredCraftingMachineTask;
import com.enderio.enderio.foundation.task.host.CraftingMachineTaskHost;
import com.enderio.enderio.foundation.util.ExperienceUtil;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOFluids;
import com.enderio.enderio.init.EIOItems;
import com.enderio.enderio.init.EIORecipes;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.SimpleFluidContent;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.enderio.enderio.foundation.util.ExperienceUtil.EXP_TO_FLUID;

public class SoulBinderBlockEntity extends PoweredMachineBlockEntity {

    public static final QuadraticScalable CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.SOUL_BINDER_CAPACITY);
    public static final QuadraticScalable USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.SOUL_BINDER_USAGE);

    public static final ICapabilityProvider<SoulBinderBlockEntity, Direction, ResourceHandler<FluidResource>> FLUID_HANDLER_PROVIDER = (be,
        side) -> be.fluidStorage != null ? IOConfigurableExternalDelegatingResourceStorage.of(be.fluidStorage, side, be) : null;

    public static final int TANK_CAPACITY = 10000;

    public static final SingleResourceSlotKey<FluidResource> TANK_SLOT = new SingleResourceSlotKey<>();

    public static final FluidStorageLayout<SoulBinderBlockEntity> FLUID_STORAGE_LAYOUT =
        FluidStorageLayout.<SoulBinderBlockEntity>builder()
            .storageSlot(TANK_SLOT, slot -> slot
                .capacity(TANK_CAPACITY)
                .filter((index, resource, binder) -> resource.is(Tags.Fluids.EXPERIENCE)))
            .build();

    private final FluidStorage<SoulBinderBlockEntity> fluidStorage;

    public static final SingleSlotAccess INPUT_SOUL = new SingleSlotAccess();
    public static final SingleSlotAccess INPUT_OTHER = new SingleSlotAccess();
    public static final MultiSlotAccess OUTPUT = new MultiSlotAccess();

    @UseOnly(LogicalSide.CLIENT)
    @Nullable
    private RecipeHolder<SoulBindingRecipe> clientRecipe;

    private final CraftingMachineTaskHost<SoulBindingRecipe, SoulBindingRecipe.Input> craftingTaskHost;

    public SoulBinderBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.SOUL_BINDER.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED,
                EnergyIOMode.Input, CAPACITY, USAGE);

        fluidStorage = new FluidStorage<>(FLUID_STORAGE_LAYOUT, this) {
            @Override
            protected void onContentsChanged(int index, FluidStack previousContents) {
                super.onContentsChanged(index, previousContents);
                craftingTaskHost.newTaskAvailable();
                updateMachineState(MachineState.EMPTY_TANK, fluidStorage.getAmountAsInt(TANK_SLOT) <= 0);
                setChanged();
            }

            @Override
            public int insert(int index, FluidResource resource, int amount, net.neoforged.neoforge.transfer.transaction.TransactionContext transaction) {
                // Convert into XP Juice - allow any XP fluid type to be inserted but normalize to the current fluid
                if (isValid(index, resource)) {
                    var currentFluid = getResource(index);
                    if (currentFluid.getFluid() == Fluids.EMPTY || resource.getFluid().isSame(currentFluid.getFluid())) {
                        return super.insert(index, resource, amount, transaction);
                    } else {
                        // Insert the same amount but as the current fluid type
                        return super.insert(index, currentFluid, amount, transaction);
                    }
                }

                // Non-XP is not allowed.
                return 0;
            }
        };

        // Create the crafting task host
        craftingTaskHost = new CraftingMachineTaskHost<>(this, this::hasEnergy,
                EIORecipes.SOUL_BINDING.type().get(), this::createTask, this::createRecipeInput);
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory pPlayerInventory, Player pPlayer) {
        return new SoulBinderMenu(containerId, pPlayerInventory, this);
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
    public MachineInventoryLayout createInventoryLayout() {
        // TODO: Support for non-soul vial storages.
        return MachineInventoryLayout.builder()
                .setStackLimit(1)
                .inputSlot((slot, resource) -> resource.is(EIOItems.SOUL_VIAL.get()) && SoulBoundUtils.isBound(resource.toStack()))
                .slotAccess(INPUT_SOUL)
                .inputSlot(this::isValidInput)
                .slotAccess(INPUT_OTHER)
                .setStackLimit(64)
                .outputSlot(2)
                .slotAccess(OUTPUT)
                .capacitor()
                .build();
    }

    private boolean isValidInput(int index, ItemResource stack) {
        return MachineRecipeCaches.SOUL_BINDING.hasRecipe(List.of(stack.toStack()));
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        craftingTaskHost.newTaskAvailable();

        //TODO this doesn't work, client has no recipes unless we sync
//        if (level != null && level.isClientSide) {
//            clientRecipe = level.getRecipeManager()
//                    .getRecipeFor(EIORecipes.SOUL_BINDING.type().get(), createFakeRecipeInput(), level)
//                    .orElse(null);
//        }
    }

    private SoulBindingRecipe.Input createRecipeInput() {
        return new SoulBindingRecipe.Input(INPUT_SOUL.getStack(getInventory()),
                INPUT_OTHER.getStack(getInventory()), fluidStorage.getStack(TANK_SLOT));
    }

    @EnsureSide(EnsureSide.Side.CLIENT)
    private SoulBindingRecipe.Input createFakeRecipeInput() {
        return new SoulBindingRecipe.Input(INPUT_SOUL.getStack(getInventory()),
                INPUT_OTHER.getStack(getInventory()),
                new FluidStack(EIOFluids.XP_JUICE.source(), Integer.MAX_VALUE));
    }

    // endregion

    @EnsureSide(EnsureSide.Side.CLIENT)
    public int getClientExp() {
        // This should always set a valid recipe.
        if (level != null && clientRecipe == null && hasValidRecipe()) {
            //TODO this doesn't work, client has no recipes unless we sync
//            clientRecipe = level.getRecipeManager()
//                    .getRecipeFor(EIORecipes.SOUL_BINDING.type().get(), createFakeRecipeInput(), level)
//                    .orElse(null);
        }

        return clientRecipe != null ? clientRecipe.value().experience() : 0;
    }

    private boolean hasValidRecipe() {
        return MachineRecipeCaches.SOUL_BINDING
                .hasRecipe(List.of(INPUT_SOUL.getStack(getInventory()), INPUT_OTHER.getStack(getInventory())));
    }

    // region Fluid Storage

    public FluidStack getStoredFluid() {
        return fluidStorage.getStack(TANK_SLOT);
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
        return new PoweredCraftingMachineTask<>(level, getInventory(), fluidStorage, getEnergyStorage(), container, OUTPUT, recipe) {

            @Override
            protected void consumeInputs(SoulBindingRecipe recipe) {
                INPUT_SOUL.getStack(getInventory()).shrink(1);
                INPUT_OTHER.getStack(getInventory()).shrink(1);

                int currentFluidAmount = fluidStorage.getAmountAsInt(TANK_SLOT);
                int leftover = ExperienceUtil
                        .getLevelFromFluidWithLeftover(currentFluidAmount, 0, recipe.experience())
                        .experience();
                int toExtract = currentFluidAmount - leftover * EXP_TO_FLUID;

                FluidStack currentFluid = getStoredFluid();
                if (!currentFluid.isEmpty() && toExtract > 0) {
                    try (Transaction transaction = Transaction.openRoot()) {
                        fluidStorage.internalExtract(TANK_SLOT, FluidResource.of(currentFluid.getFluid()), toExtract, transaction);
                        transaction.commit();
                    }
                }
            }

        };
    }

    // endregion

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

    // endregion
}

package com.enderio.enderio.content.machines.soul_binder;

import com.enderio.core.common.storage.FluidStorage;
import com.enderio.core.common.storage.layout.FluidStorageLayout;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.scaling.QuadraticIntScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.api.soul.SoulBoundUtils;
import com.enderio.enderio.client.SoundHandler;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.block.ProgressMachineBlock;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.capacitor.TempMachineSpeedScalable;
import com.enderio.enderio.foundation.crafting.MachineCraftingContext;
import com.enderio.enderio.foundation.crafting.MachineCraftingManager;
import com.enderio.enderio.foundation.crafting.MachineCraftingStatus;
import com.enderio.enderio.foundation.inventory.MachineSlotTemplates;
import com.enderio.enderio.foundation.recipe.MachineRecipeCaches;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.foundation.storage.SidedResourceHandler;
import com.enderio.enderio.foundation.task.host.CraftingMachineTaskHost;
import com.enderio.enderio.foundation.util.ExperienceUtil;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOItems;
import com.enderio.enderio.init.EIORecipeTypes;
import com.enderio.core.annotations.UseOnly;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import com.enderio.enderio.init.EIOSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
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
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class SoulBinderBlockEntity extends PoweredMachineBlockEntity {

    public static final QuadraticIntScalable CAPACITY = new QuadraticIntScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.SOUL_BINDER_CAPACITY);
    public static final QuadraticIntScalable USAGE = new QuadraticIntScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.SOUL_BINDER_USAGE);

    public static final ICapabilityProvider<SoulBinderBlockEntity, Direction, ResourceHandler<FluidResource>> FLUID_HANDLER_PROVIDER = (be,
        side) -> be.fluidStorage != null ? SidedResourceHandler.of(be.fluidStorage, side, be) : null;

    public static final TempMachineSpeedScalable SPEED = new TempMachineSpeedScalable(USAGE);

    public static final int TANK_CAPACITY = 10000;

    public static final SingleResourceSlotKey<FluidResource> TANK_SLOT = new SingleResourceSlotKey<>();

    public static final FluidStorageLayout FLUID_STORAGE_LAYOUT =
         FluidStorageLayout.builder()
            .add(TANK_SLOT, SlotTemplates.input(), slot -> slot
                .capacity(TANK_CAPACITY)
                .filter((_, resource) -> resource.is(Tags.Fluids.EXPERIENCE)))
            .build();

    private final FluidStorage fluidStorage;

    public static final SingleResourceSlotKey<ItemResource> INPUT_SOUL = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> INPUT_OTHER = new SingleResourceSlotKey<>();
    public static final MultiResourceSlotKey<ItemResource> OUTPUTS = new MultiResourceSlotKey<>(2);
    public static final SingleResourceSlotKey<ItemResource> CAPACITOR = new SingleResourceSlotKey<>();

    @UseOnly(LogicalSide.CLIENT)
    @Nullable
    private RecipeHolder<SoulBindingRecipe> clientRecipe;

    private final ResourceHandler<ItemResource> outputItemHandler;

    private final MachineCraftingManager<SoulBindingRecipe, SoulBindingRecipe.Input> craftingManager;
    private SoulBindingRecipe.@Nullable Input recipeInput;

    public SoulBinderBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.SOUL_BINDER.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED, CAPACITOR,
                EnergyIOMode.Input, CAPACITY, USAGE);

        fluidStorage = new FluidStorage(FLUID_STORAGE_LAYOUT) {
            @Override
            protected void onContentsChanged(int index, FluidStack previousContents) {
                super.onContentsChanged(index, previousContents);
                recipeInput = null;
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

        outputItemHandler = OUTPUTS.rangedHandler(getInventory());

        // Create the crafting task host
        craftingManager = new MachineCraftingManager<>(EIORecipeTypes.SOUL_BINDING.get(), new CraftingContext());
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new SoulBinderMenu(containerId, playerInventory, this);
    }

    @Override
    public void serverTick() {
        super.serverTick();

        if (canAct()) {
            craftingManager.tick();
        }
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(ProgressMachineBlock.POWERED)) {
            double x = pos.getX() + 0.5;
            double y = pos.getY();
            double z = pos.getZ() + 0.5;

            SoundHandler.playSound(pos, EIOSounds.SOUL_BINDER.get(), SoundSource.BLOCKS, MachinesConfig.CLIENT.MACHINE_VOLUME.get(), 1.0f, random, x, y, z);

            Direction direction = state.getValue(ProgressMachineBlock.FACING);
            Direction.Axis axis = direction.getAxis();
            double r = 0.52;
            double ss = random.nextDouble() * 0.8 - 0.4;
            double dx = axis == Direction.Axis.X ? direction.getStepX() * r : ss;
            double dy = random.nextDouble() * 6.0 / 16.0 + 7.0 / 16.0;
            double dz = axis == Direction.Axis.Z ? direction.getStepZ() * r : ss;
            level.addParticle(ParticleTypes.COPPER_FIRE_FLAME, x + dx, y + dy, z + dz, 0.0, 0.0, 0.0); //TODO green flame
        } else {
            SoundHandler.stopSound(pos);
        }
    }

    // region Inventory

    @Override
    public ItemStorageLayout createInventoryLayout() {
        // TODO: Support for non-soul vial storages.
        return ItemStorageLayout
            .builder()
            .add(INPUT_SOUL, SlotTemplates.input(),
                b -> b.capacity(1).filter((_, itemResource) -> itemResource.is(EIOItems.SOUL_VIAL.get()) && SoulBoundUtils.isBound(itemResource.toStack())))
            .add(INPUT_OTHER, SlotTemplates.input(), b -> b
                .capacity(1)
                .filter(this::isValidInput))
            .add(OUTPUTS, SlotTemplates.output())
            .add(CAPACITOR, MachineSlotTemplates.capacitor())
            .build();
    }

    private boolean isValidInput(int index, ItemResource stack) {
        return MachineRecipeCaches.SOUL_BINDING.hasRecipe(List.of(stack.toStack()));
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        recipeInput = null;
    }

    private SoulBindingRecipe.Input getRecipeInput() {
        if (recipeInput == null) {
            recipeInput = new SoulBindingRecipe.Input(getInventory().getStack(INPUT_SOUL),
                getInventory().getStack(INPUT_OTHER), fluidStorage.getStack(TANK_SLOT));
        }

        return recipeInput;
    }

    // endregion

    // TODO: Won't be necessary soon, so just stubbing it out
    @UseOnly(LogicalSide.CLIENT)
    public int getClientExp() {
        return 0;
    }

    // region Fluid Storage

    public FluidStack getStoredFluid() {
        return fluidStorage.getStack(TANK_SLOT);
    }

    // endregion

    // region Crafting Task

    public float getCraftingProgress() {
        return craftingManager.craftingProgress();
    }

    @Override
    public boolean isActive() {
        return canAct() && hasEnergy() && craftingManager.status() == MachineCraftingStatus.ACTIVE;
    }

    // endregion

    // region Serialization

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild("Fluid", fluidStorage);
        output.putChild(MachineNBTKeys.CRAFTING_TASK, craftingManager);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.child("Fluid")
            .ifPresent(fluidStorage::deserialize);
        var task = input.child(MachineNBTKeys.CRAFTING_TASK);
        task.ifPresent(craftingManager::deserialize);
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

    // endregion

    private class CraftingContext extends MachineCraftingContext<SoulBindingRecipe, SoulBindingRecipe.Input> {

        @Override
        public SoulBindingRecipe.Input recipeInput() {
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
        public int getCraftingTicks(RecipeHolder<SoulBindingRecipe> recipe) {
            return Math.round(recipe.value().getOperationTime(recipeInput()) * SPEED.scale(getCapacitorData()));
        }

        @Override
        public boolean tryProgressCraft(SoulBindingRecipe recipe) {
            try (var transaction = Transaction.openRoot()) {
                int consumed = getEnergyStorage().consume(getMaxEnergyUse(), transaction);
                if (consumed == getMaxEnergyUse()) {
                    transaction.commit();
                    return true;
                }
            }

            return false;
        }

        @Override
        protected boolean consumeRecipeInputs(SoulBindingRecipe recipe, SoulBindingRecipe.Input recipeInput, TransactionContext transaction) {
            int soulConsumed = getInventory().extract(INPUT_SOUL, ItemResource.of(recipeInput.boundSoulItem()), 1, transaction);
            if (soulConsumed != 1) {
                return false;
            }

            int otherConsumed = getInventory().extract(INPUT_OTHER, ItemResource.of(recipeInput.itemToBind()), 1, transaction);
            if (otherConsumed != 1) {
                return false;
            }

            var currentFluid = fluidStorage.getResource(TANK_SLOT);
            if (currentFluid.isEmpty()) {
                return false;
            }

            int fluidToExtract = recipe.experience() * ExperienceUtil.EXP_TO_FLUID;
            int fluidConsumed = fluidStorage.extract(TANK_SLOT, currentFluid, fluidToExtract, transaction);
            if (fluidConsumed != fluidToExtract) {
                return false;
            }

            return true;
        }

        @Override
        protected boolean insertRecipeOutputs(SoulBindingRecipe recipe, SoulBindingRecipe.Input recipeInput, RandomSource random,
            TransactionContext transaction) {
            // TODO: Once we're fully migrated, just use assemble for single output recipes...
            var results = recipe.craft(recipeInput, random, level.registryAccess());

            for (var result : results) {
                if (result.isItem()) {
                    int inserted = outputItemHandler.insert(ItemResource.of(result.getItem()), result.getItem().count(), transaction);
                    if (inserted < result.getItem().count()) {
                        return false;
                    }
                }
            }

            return true;
        }
    }
}

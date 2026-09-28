package com.enderio.enderio.content.machines.vat;

import com.enderio.core.common.storage.FluidStorage;
import com.enderio.core.common.storage.layout.FluidStorageLayout;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.core.common.util.NamedFluidContents;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.client.SoundHandler;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.block.ProgressMachineBlock;
import com.enderio.enderio.foundation.block.entity.MachineBlockEntity;
import com.enderio.enderio.foundation.crafting.MachineCraftingContext;
import com.enderio.enderio.foundation.crafting.MachineCraftingManager;
import com.enderio.enderio.foundation.crafting.MachineCraftingStatus;
import com.enderio.enderio.foundation.recipe.MachineRecipeCaches;
import com.enderio.enderio.foundation.io.fluid.FluidItemInteractive;
import com.enderio.enderio.foundation.state.MachineState;
import com.enderio.enderio.foundation.storage.SidedResourceHandler;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIORecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import com.enderio.enderio.init.EIOSounds;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Map;

public class VatBlockEntity extends MachineBlockEntity implements FluidItemInteractive {

    public static final ICapabilityProvider<VatBlockEntity, Direction, ResourceHandler<FluidResource>> FLUID_HANDLER_PROVIDER = (be,
        side) -> be.fluidStorage != null ? SidedResourceHandler.of(be.fluidStorage, side, be) : null;

    // TODO: Configurable tank sizes...
    public static final int TANK_CAPACITY = 16 * FluidType.BUCKET_VOLUME;
    public static final SingleResourceSlotKey<FluidResource> INPUT_TANK = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<FluidResource> OUTPUT_TANK = new SingleResourceSlotKey<>();

    public static final MultiResourceSlotKey<ItemResource> REAGENTS = new MultiResourceSlotKey<>(2);

    public static final FluidStorageLayout FLUID_STORAGE_LAYOUT =
        FluidStorageLayout.builder()
            .add(INPUT_TANK, SlotTemplates.input(TANK_CAPACITY))
            .add(OUTPUT_TANK, SlotTemplates.output(TANK_CAPACITY))
            .build();

    private final FluidStorage fluidStorage;

    private final ResourceHandler<ItemResource> reagentsHandler;

    private final MachineCraftingManager<FermentingRecipe, FermentingRecipe.Input> craftingManager;
    private FermentingRecipe.@Nullable Input recipeInput;

    public VatBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.VAT.get(), worldPosition, blockState, true);

        fluidStorage = new FluidStorage(FLUID_STORAGE_LAYOUT) {
            @Override
            protected void onContentsChanged(int index, FluidStack previousContents) {
                super.onContentsChanged(index, previousContents);
                recipeInput = null;
                setChanged();
                if (level != null) {
                    level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), Block.UPDATE_ALL);
                }
            }
        };

        reagentsHandler = REAGENTS.rangedHandler(getInventory());

        craftingManager = new MachineCraftingManager<>(EIORecipeTypes.VAT_FERMENTING.get(), new CraftingContext());
    }

    public FluidStorage getFluidStorage() {
        return fluidStorage;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new VatMenu(containerId, playerInventory, this);
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
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(ProgressMachineBlock.POWERED)) {
            double x = pos.getX() + 0.5;
            double y = pos.getY();
            double z = pos.getZ() + 0.5;

            SoundHandler.playSound(pos, EIOSounds.VAT.get(), SoundSource.BLOCKS, MachinesConfig.CLIENT.MACHINE_VOLUME.get(), 1.0f, random, x, y, z);
        } else {
            SoundHandler.stopSound(pos);
        }
    }

    public boolean isActive() {
        return canAct() && craftingManager.status() == MachineCraftingStatus.ACTIVE;
    }

    @Override
    public @Nullable ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .add(REAGENTS, SlotTemplates.input(64), b -> b
                .filter(this::acceptSlotInput))
            .build();
    }

    protected boolean acceptSlotInput(int slot, ItemResource resource) {
        return MachineRecipeCaches.FERMENTING.hasValidRecipeIf(getInventory(), REAGENTS, slot, resource.toStack());
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        recipeInput = null;
    }

    private FermentingRecipe.Input getRecipeInput() {
        if (recipeInput == null) {
            List<ItemStack> reagents = getInventory().getStacks(REAGENTS);
            recipeInput = new FermentingRecipe.Input(reagents.get(0), reagents.get(1), fluidStorage.getStack(INPUT_TANK));
        }

        return recipeInput;
    }

    public FluidStack getInputFluid() {
        return fluidStorage.getStack(INPUT_TANK);
    }

    public FluidStack getOutputFluid() {
        return fluidStorage.getStack(OUTPUT_TANK);
    }

    public float getCraftingProgress() {
        return craftingManager.craftingProgress();
    }

    @Nullable
    public RecipeHolder<FermentingRecipe> getRecipe() {
        return craftingManager.currentRecipe();
    }

    @UseOnly(LogicalSide.SERVER)
    public void moveFluidToOutputTank() {
        FluidStack inputFluid = fluidStorage.getStack(INPUT_TANK);
        FluidStack outputFluid = fluidStorage.getStack(OUTPUT_TANK);

        if (outputFluid.isEmpty() && !inputFluid.isEmpty()) {
            fluidStorage.setStack(OUTPUT_TANK, inputFluid.copy());
            fluidStorage.setStack(INPUT_TANK, FluidStack.EMPTY);
        }
    }

    @UseOnly(LogicalSide.SERVER)
    public void dumpOutputTank() {
        fluidStorage.setStack(OUTPUT_TANK, FluidStack.EMPTY);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild("fluids", fluidStorage);
        output.putChild(MachineNBTKeys.CRAFTING_TASK, craftingManager);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.readChild("fluids", fluidStorage);
        input.readChild(MachineNBTKeys.CRAFTING_TASK, craftingManager);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);

        NamedFluidContents fluidContents = components.get(EIODataComponents.NAMED_FLUID_CONTENTS);
        if (fluidContents != null) {
            FluidStack inputFluid = fluidContents.copy("input_tank");
            FluidStack outputFluid = fluidContents.copy("output_tank");

            if (!inputFluid.isEmpty()) {
                fluidStorage.setStack(INPUT_TANK, inputFluid);
            }
            if (!outputFluid.isEmpty()) {
                fluidStorage.setStack(OUTPUT_TANK, outputFluid);
            }
        }

        craftingManager.applyCraftingState(components.get(EIODataComponents.MACHINE_CRAFTING_STATE));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);

        FluidStack inputFluid = fluidStorage.getStack(INPUT_TANK);
        FluidStack outputFluid = fluidStorage.getStack(OUTPUT_TANK);
        if (!inputFluid.isEmpty() || !outputFluid.isEmpty()) {
            components.set(EIODataComponents.NAMED_FLUID_CONTENTS, NamedFluidContents
                .copyOf(Map.of("input_tank", inputFluid, "output_tank", outputFluid)));
        }

        components.set(EIODataComponents.MACHINE_CRAFTING_STATE, craftingManager.getCraftingState());
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard("fluids");
        output.discard(MachineNBTKeys.CRAFTING_TASK);
    }

    private class CraftingContext extends MachineCraftingContext<FermentingRecipe, FermentingRecipe.Input> {

        @Override
        public FermentingRecipe.Input recipeInput() {
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
        public int getCraftingTicks(RecipeHolder<FermentingRecipe> recipe) {
            return recipe.value().ticks();
        }

        @Override
        public boolean tryProgressCraft(FermentingRecipe recipe) {
            // Vat recipes consume nothing to progress
            return true;
        }

        @Override
        protected boolean consumeRecipeInputs(FermentingRecipe recipe, FermentingRecipe.Input recipeInput, TransactionContext transaction) {
            int firstConsumed = reagentsHandler.extract(ItemResource.of(recipeInput.firstReagent()), 1, transaction);
            if (firstConsumed != 1) {
                return false;
            }

            int secondConsumed = reagentsHandler.extract(ItemResource.of(recipeInput.secondStack()), 1, transaction);
            if (secondConsumed != 1) {
                return false;
            }

            int fluidConsumed = fluidStorage.extract(INPUT_TANK, FluidResource.of(recipeInput.inputFluid()), recipe.input().amount(), transaction);
            if (fluidConsumed != recipe.input().amount()) {
                return false;
            }

            return true;
        }

        @Override
        protected boolean insertRecipeOutputs(FermentingRecipe recipe, FermentingRecipe.Input recipeInput, RandomSource random,
            TransactionContext transaction) {
            // TODO: Once we're fully migrated, just use assemble for single output recipes...
            var results = recipe.craft(recipeInput, random, level.registryAccess());

            FluidStack output = results.getFirst().getFluid();

            int inserted = fluidStorage.insert(OUTPUT_TANK, FluidResource.of(output), output.getAmount(), transaction);
            if (inserted == output.getAmount()) {
                return true;
            }

            return false;
        }
    }
}

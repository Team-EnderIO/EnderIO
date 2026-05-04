package com.enderio.enderio.content.machines.slicer;

import com.enderio.core.common.storage.ResourceStorage;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.ResourceSlotId;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.scaling.QuadraticIntScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
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
import com.enderio.enderio.foundation.tag.EIOTags;
import com.enderio.enderio.foundation.inventory.MachineSlotTemplates;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIORecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import com.enderio.enderio.init.EIOSounds;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ShearsItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.Optional;

public class SlicerBlockEntity extends PoweredMachineBlockEntity {

    public static final QuadraticIntScalable CAPACITY = new QuadraticIntScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.SLICER_CAPACITY);
    public static final QuadraticIntScalable USAGE = new QuadraticIntScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.SLICER_USAGE);

    public static final TempMachineSpeedScalable SPEED = new TempMachineSpeedScalable(USAGE);

    public static final SingleResourceSlotKey<ItemResource> OUTPUT = new SingleResourceSlotKey<>();
    public static final MultiResourceSlotKey<ItemResource> INPUTS = new MultiResourceSlotKey<>(6);
    public static final SingleResourceSlotKey<ItemResource> AXE = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> SHEARS = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> CAPACITOR = new SingleResourceSlotKey<>();

    private final ResourceHandler<ItemResource> inputHandler;

    private final MachineCraftingManager<SlicingRecipe, SlicingRecipe.Input> craftingManager;
    private SlicingRecipe.@Nullable Input recipeInput;

    public SlicerBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.SLICE_AND_SPLICE.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED, CAPACITOR,
                EnergyIOMode.Input, CAPACITY, USAGE);

        inputHandler = INPUTS.rangedHandler(getInventory());
        craftingManager = new MachineCraftingManager<>(EIORecipeTypes.SLICING.get(), new CraftingContext());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new SlicerMenu(containerId, inventory, this);
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

            SoundHandler.playSound(pos, EIOSounds.SLICER.get(), SoundSource.BLOCKS, MachinesConfig.CLIENT.MACHINE_VOLUME.get(), 1.0f, random, x, y, z);


            Direction direction = state.getValue(ProgressMachineBlock.FACING);
            Direction.Axis axis = direction.getAxis();
            double r = 0.52;
            double ss = random.nextDouble() * 0.8 - 0.4;
            double dx = axis == Direction.Axis.X ? direction.getStepX() * r : ss;
            double dy = random.nextDouble() * 6.0 / 16.0 + 7.0 / 16.0;
            double dz = axis == Direction.Axis.Z ? direction.getStepZ() * r : ss;
            level.addParticle(ParticleTypes.SOUL_FIRE_FLAME, x + dx, y + dy, z + dz, 0.0, 0.0, 0.0);
        } else {
            SoundHandler.stopSound(pos);
        }
    }

    // region Inventory

    @Override
    public ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .add(INPUTS, SlotTemplates.input(), b -> b
                .capacity(1)
                .filter(this::isValidInput))
            .add(AXE, SlotTemplates.input(), b -> b
                .capacity(1)
                .filter(this::validAxe))
            .add(SHEARS, SlotTemplates.input(), b -> b
                .capacity(1)
                .filter((_, itemResource) -> itemResource.getItem() instanceof ShearsItem))
            .add(OUTPUT, SlotTemplates.output())
            .add(CAPACITOR, MachineSlotTemplates.capacitor())
            .build();
    }

    private boolean isValidInput(int index, ItemResource stack) {
        return SlicerRecipeManager.isSlicerValid(stack.toStack(), index);
    }

    private boolean validAxe(int slot, ItemResource stack) {
        return stack.is(ItemTags.AXES) && !stack.is(EIOTags.Items.SLICER_INCOMPATIBLE_AXE);
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        recipeInput = null;
    }

    public SlicingRecipe.Input getRecipeInput() {
        if (recipeInput == null) {
            recipeInput = new SlicingRecipe.Input(getInventory().getStacks(INPUTS));
        }

        return recipeInput;
    }

    // endregion

    // region Crafting Task

    public float getCraftingProgress() {
        return craftingManager.craftingProgress();
    }

    @Override
    public boolean isActive() {
        return canAct() && hasEnergy() && craftingManager.status() != MachineCraftingStatus.IDLE;
    }

    // endregion

    // region Serialization

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild(MachineNBTKeys.CRAFTING_TASK, craftingManager);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.readChild(MachineNBTKeys.CRAFTING_TASK, craftingManager);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        craftingManager.applyCraftingState(components.get(EIODataComponents.MACHINE_CRAFTING_STATE));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(EIODataComponents.MACHINE_CRAFTING_STATE, craftingManager.getCraftingState());
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard(MachineNBTKeys.CRAFTING_TASK);
    }

    // endregion

    private class CraftingContext extends MachineCraftingContext<SlicingRecipe, SlicingRecipe.Input> {

        private ItemResource damagedAxe = ItemResource.EMPTY;
        private ItemResource damagedShears = ItemResource.EMPTY;

        @Override
        public SlicingRecipe.Input recipeInput() {
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
        public int getCraftingTicks(RecipeHolder<SlicingRecipe> recipe) {
            return Math.round(recipe.value().getOperationTime(recipeInput()) * SPEED.scale(getCapacitorData()));
        }

        @Override
        public boolean tryProgressCraft(SlicingRecipe recipe) {
            var inv = getInventory();
            if (inv.getAmountAsInt(AXE) < 1 || inv.getAmountAsInt(SHEARS) < 1) {
                return false;
            }

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
        public boolean tryCompleteCraft(SlicingRecipe recipe, RandomSource random) {
            // Prepare the damaged axe and shears ahead of time.
            // It's dangerous to do this from within a transaction as a modded tool may open a new transaction to consume energy or similar.
            var damagedAxeOpt = prepareDamagedItem(AXE);
            var damagedShearsOpt = prepareDamagedItem(SHEARS);

            if (damagedAxeOpt.isEmpty() || damagedShearsOpt.isEmpty()) {
                return false;
            }

            damagedAxe = damagedAxeOpt.get();
            damagedShears = damagedShearsOpt.get();

            return super.tryCompleteCraft(recipe, random);
        }

        private Optional<ItemResource> prepareDamagedItem(ResourceSlotId<ItemResource> slot) {
            var currentResource = getInventory().getResource(slot);
            if (currentResource.isEmpty()) {
                return Optional.empty();
            }

            var stack = currentResource.toStack();
            stack.hurtAndBreak(1, level(), null, _ -> {});
            return Optional.of(ItemResource.of(stack));
        }

        @Override
        public boolean consumeRecipeInputs(SlicingRecipe recipe, SlicingRecipe.Input recipeInput, TransactionContext transaction) {
            for (var input : recipeInput.inputs()) {
                int extracted = inputHandler.extract(ItemResource.of(input), input.getCount(), transaction);
                if (extracted != input.getCount()) {
                    return false;
                }
            }

            // Try to swap out for the damaged tools
            if (!swapForDamagedTool(AXE, damagedAxe, transaction) || !swapForDamagedTool(SHEARS, damagedShears, transaction)) {
                return false;
            }

            return true;
        }

        private boolean swapForDamagedTool(ResourceSlotId<ItemResource> slot, ItemResource damagedTool, TransactionContext transaction) {
            int extracted = getInventory().extract(slot, getInventory().getResource(slot), 1, transaction);
            if (extracted != 1) {
                return false;
            }

            if (!damagedTool.isEmpty()) {
                int inserted = getInventory().insert(slot, damagedTool, 1, transaction);
                if (inserted != 1) {
                    return false;
                }
            }

            return true;
        }

        @Override
        public boolean insertRecipeOutputs(SlicingRecipe recipe, SlicingRecipe.Input recipeInput, RandomSource random, TransactionContext transaction) {
            // TODO: Once we're fully migrated, just use assemble for single output recipes...
            var results = recipe.craft(recipeInput, random, level.registryAccess());

            for (var result : results) {
                if (result.isItem()) {
                    int inserted = getInventory().insert(OUTPUT, ItemResource.of(result.getItem()), result.getItem().count(), transaction);
                    if (inserted < result.getItem().count()) {
                        return false;
                    }
                }
            }

            return true;
        }
    }
}

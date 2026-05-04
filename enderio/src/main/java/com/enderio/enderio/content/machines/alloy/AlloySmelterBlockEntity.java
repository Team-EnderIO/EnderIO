package com.enderio.enderio.content.machines.alloy;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.scaling.QuadraticIntScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
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
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIORecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.neoforged.neoforge.common.crafting.SizedIngredient;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.List;

// TODO: Award XP

public class AlloySmelterBlockEntity extends PoweredMachineBlockEntity {

    public static MultiResourceSlotKey<ItemResource> INPUTS = new MultiResourceSlotKey<>(3);
    public static SingleResourceSlotKey<ItemResource> OUTPUT = new SingleResourceSlotKey<>();
    public static SingleResourceSlotKey<ItemResource> CAPACITOR = new SingleResourceSlotKey<>();

    public static final QuadraticIntScalable CAPACITY = new QuadraticIntScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.ALLOY_SMELTER_CAPACITY);
    public static final QuadraticIntScalable USAGE = new QuadraticIntScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.ALLOY_SMELTER_USAGE);

    public static final TempMachineSpeedScalable SPEED = new TempMachineSpeedScalable(USAGE);

    private final ResourceHandler<ItemResource> inputHandler;

    /**
     * The alloying mode for the machine.
     * Determines which recipes it can craft.
     */
    private AlloySmelterMode mode = AlloySmelterMode.ALL;

    private final MachineCraftingManager<AlloySmeltingRecipe, AlloySmeltingRecipe.Input> craftingManager;
    private AlloySmeltingRecipe.@Nullable Input recipeInput;

    public AlloySmelterBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.ALLOY_SMELTER.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED, CAPACITOR, EnergyIOMode.Input,
            CAPACITY, USAGE);

        inputHandler = INPUTS.rangedHandler(getInventory());

        craftingManager = new MachineCraftingManager<>(EIORecipeTypes.ALLOY_SMELTING.get(), new CraftingContext());
    }
    
    /**
     * Get the alloy smelting mode.
     */
    public AlloySmelterMode getMode() {
        return mode;
    }

    public void setMode(AlloySmelterMode mode) {
        this.mode = mode;

        // This changes the recipe input.
        recipeInput = null;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new AlloySmelterMenu(containerId, inventory, this);
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

            if (random.nextDouble() < 0.1) {
                level.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundSource.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = state.getValue(ProgressMachineBlock.FACING);
            Direction.Axis axis = direction.getAxis();
            double r = 0.52;
            double ss = random.nextDouble() * 0.6 - 0.3;
            double dx = axis == Direction.Axis.X ? direction.getStepX() * r : ss;
            double dy = random.nextDouble() * 3.0 / 16.0 + 10.0 / 16.0;
            double dz = axis == Direction.Axis.Z ? direction.getStepZ() * r : ss;
            level.addParticle(ParticleTypes.SMOKE, x + dx, y + dy, z + dz, 0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.FLAME, x + dx, y + dy, z + dz, 0.0, 0.0, 0.0);
        }
    }

    // region Inventory

    @Override
    protected @Nullable ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .add(INPUTS, SlotTemplates.input(), b -> b.filter(this::acceptSlotInput))
            .add(OUTPUT, SlotTemplates.output())
            .add(CAPACITOR, MachineSlotTemplates.capacitor())
            .build();
    }

    protected boolean acceptSlotInput(int slot, ItemResource resource) {
        if (getMode().canAlloy()) {
            if (MachineRecipeCaches.ALLOY_SMELTING_ONLY_ALLOY.hasValidRecipeIf(getInventory(), INPUTS, slot, resource.toStack())) {
                return true;
            }
        }

        if (getMode().canSmelt()) {
            // Check all items are the same, or will be
            var currentItems = INPUTS.slots()
                    .stream()
                    .map(i -> i.index(getInventory()) == slot ? resource : getInventory().getResource(i))
                    .filter(i -> !i.isEmpty())
                    .toList();

            if (currentItems.stream().allMatch(i -> i.is(resource.getItem())) || currentItems.size() == 1) {
                return MachineRecipeCaches.ALLOY_SMELTING_ONLY_SMELTING.hasRecipe(List.of(resource.toStack()));
            }
        }

        return false;
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);

        // This changes the recipe input.
        recipeInput = null;
    }

    public AlloySmeltingRecipe.Input getRecipeInput() {
        if (recipeInput == null) {
            recipeInput = new AlloySmeltingRecipe.Input(mode, getInventory().getStacks(INPUTS));
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
    public void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putChild(MachineNBTKeys.CRAFTING_TASK, craftingManager);
        output.store(MachineNBTKeys.MACHINE_MODE, AlloySmelterMode.CODEC, mode);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.readChild(MachineNBTKeys.CRAFTING_TASK, craftingManager);

        mode = input.read(MachineNBTKeys.MACHINE_MODE, AlloySmelterMode.CODEC)
            .orElse(AlloySmelterMode.ALL);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter components) {
        super.applyImplicitComponents(components);
        mode = components.getOrDefault(EIODataComponents.ALLOY_SMELTER_MODE, AlloySmelterMode.ALL);
        craftingManager.applyCraftingState(components.get(EIODataComponents.MACHINE_CRAFTING_STATE));
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(EIODataComponents.ALLOY_SMELTER_MODE, mode);
        components.set(EIODataComponents.MACHINE_CRAFTING_STATE, craftingManager.getCraftingState());
    }

    @Override
    public void removeComponentsFromTag(ValueOutput output) {
        super.removeComponentsFromTag(output);
        output.discard(MachineNBTKeys.MACHINE_MODE);
        output.discard(MachineNBTKeys.CRAFTING_TASK);
    }

    // endregion

    private class CraftingContext implements MachineCraftingContext<AlloySmeltingRecipe, AlloySmeltingRecipe.Input> {
        @Override
        public AlloySmeltingRecipe.Input recipeInput() {
            return getRecipeInput();
        }

        @Override
        @Nullable
        public ServerLevel level() {
            if (getLevel() instanceof ServerLevel serverLevel) {
                return serverLevel;
            }

            return null;
        }

        @Override
        public int getCraftingTicks(RecipeHolder<AlloySmeltingRecipe> recipe) {
            return Math.round(recipe.value().getOperationTime(recipeInput()) * SPEED.scale(getCapacitorData()));
        }

        @Override
        public boolean tryProgressCraft(AlloySmeltingRecipe recipe) {
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
        public boolean consumeRecipeInputs(AlloySmeltingRecipe recipe, TransactionContext transaction) {
            if (recipe.isSmelting()) {
                // Smelting recipes only have one ingredient and can consume 1-3 of said ingredient.
                SizedIngredient input = recipe.inputs().getFirst();

                var consumedResource = ResourceHandlerUtil.extractFirst(inputHandler, ir -> input.ingredient().test(ir.toStack()), 3, transaction);
                if (consumedResource == null) {
                    return false;
                }

                return consumedResource.amount() > 0;
            } else {
                // Track which ingredients have been consumed
                List<SizedIngredient> inputs = recipe.inputs();

                for (var input : inputs) {
                    var consumedResource = ResourceHandlerUtil.extractFirst(inputHandler, ir -> input.ingredient().test(ir.toStack()),
                        input.count(), transaction);

                    if (consumedResource == null || consumedResource.amount() != input.count()) {
                        return false;
                    }
                }

                return true;
            }
        }

        @Override
        public boolean insertRecipeOutputs(AlloySmeltingRecipe recipe, RandomSource random, TransactionContext transaction) {
            // TODO: Once we're fully migrated, just use assemble for single output recipes...
            var results = recipe.craft(recipeInput(), random, level.registryAccess());

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

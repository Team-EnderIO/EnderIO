package com.enderio.enderio.content.machines.painting;

import com.enderio.core.common.recipes.OutputStack;
import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.scaling.QuadraticIntScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.content.paint.BlockPaintData;
import com.enderio.enderio.content.paint.block.PaintedBlock;
import com.enderio.enderio.foundation.MachineNBTKeys;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.capacitor.TempMachineSpeedScalable;
import com.enderio.enderio.foundation.crafting.MachineCraftingContext;
import com.enderio.enderio.foundation.crafting.MachineCraftingManager;
import com.enderio.enderio.foundation.crafting.MachineCraftingStatus;
import com.enderio.enderio.foundation.inventory.MachineSlotTemplates;
import com.enderio.enderio.foundation.recipe.MachineRecipeCaches;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOCriterions;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIORecipeTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentGetter;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.ResourceHandlerUtil;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
import org.jspecify.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class PaintingMachineBlockEntity extends PoweredMachineBlockEntity {

    public static final SingleResourceSlotKey<ItemResource> INPUT = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> PAINT = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> OUTPUT = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> CAPACITOR = new SingleResourceSlotKey<>();

    public static final QuadraticIntScalable CAPACITY = new QuadraticIntScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.PAINTING_MACHINE_CAPACITY);
    public static final QuadraticIntScalable USAGE = new QuadraticIntScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.PAINTING_MACHINE_USAGE);

    public static final TempMachineSpeedScalable SPEED = new TempMachineSpeedScalable(USAGE);

    private final ResourceHandler<ItemResource> inputHandler;

    private final AABB area;

    private final MachineCraftingManager<PaintingRecipe, PaintingRecipe.Input> craftingManager;
    private PaintingRecipe.@Nullable Input recipeInput;

    public PaintingMachineBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.PAINTING_MACHINE.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED, CAPACITOR,
                EnergyIOMode.Input, CAPACITY, USAGE);

        area = AABB.ofSize(worldPosition.getCenter(), 10, 10, 10);

        inputHandler = INPUT.rangedHandler(getInventory());
        craftingManager = new MachineCraftingManager<>(EIORecipeTypes.PAINTING.get(), new CraftingContext());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new PaintingMachineMenu(inventory, containerId, this);
    }

    @Override
    public void serverTick() {
        super.serverTick();

        if (canAct()) {
            craftingManager.tick();
        }
    }

    // region Inventory

    @Override
    public ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .add(CAPACITOR, MachineSlotTemplates.capacitor())
            .add(INPUT, SlotTemplates.input(), b -> b
                .filter(this::isValidInput))
            .add(PAINT, SlotTemplates.input(), b -> b
                .filter(this::isValidPaint))
            .add(OUTPUT, SlotTemplates.output())
            .build();
    }

    private boolean isValidInput(int index, ItemResource stack) {
        return MachineRecipeCaches.PAINTING.hasRecipe(List.of(stack.toStack()));
    }

    private boolean isValidPaint(int index, ItemResource stack) {
        if (stack.getItem() instanceof BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block instanceof PaintedBlock) {
                return false;
            }

            return block.defaultBlockState().isCollisionShapeFullBlock(EmptyBlockGetter.INSTANCE, BlockPos.ZERO);
        }

        return false;
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        recipeInput = null;
    }

    public PaintingRecipe.Input getRecipeInput() {
        if (recipeInput == null) {
            recipeInput = new PaintingRecipe.Input(getInventory().getStack(INPUT), getInventory().getStack(PAINT));
        }

        return recipeInput;
    }

    private PaintingRecipe.Input createRecipeInput() {
        return new PaintingRecipe.Input(getInventory().getStack(INPUT), getInventory().getStack(PAINT));
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
        output.putChild(MachineNBTKeys.CRAFTING_TASK, craftingManager);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        input.readChild(MachineNBTKeys.CRAFTING_TASK, craftingManager);
    }

    @Override
    protected void applyImplicitComponents(DataComponentGetter componentInput) {
        super.applyImplicitComponents(componentInput);
        craftingManager.applyCraftingState(componentInput.get(EIODataComponents.MACHINE_CRAFTING_STATE));
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

    private class CraftingContext extends MachineCraftingContext<PaintingRecipe, PaintingRecipe.Input> {
        @Override
        public PaintingRecipe.Input recipeInput() {
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
        public int getCraftingTicks(RecipeHolder<PaintingRecipe> recipe) {
            return Math.round(recipe.value().getOperationTime(recipeInput()) * SPEED.scale(getCapacitorData()));
        }

        @Override
        public boolean tryProgressCraft(PaintingRecipe recipe) {
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
        public boolean consumeRecipeInputs(PaintingRecipe recipe, PaintingRecipe.Input input, TransactionContext transaction) {
            int consumed = inputHandler.extract(ItemResource.of(input.template()), 1, transaction);
            return consumed == 1;
        }

        @Override
        public boolean insertRecipeOutputs(PaintingRecipe recipe, PaintingRecipe.Input recipeInput, RandomSource random, TransactionContext transaction) {
            var results = recipe.craft(recipeInput, random, getLevel().registryAccess());

            for (var result : results) {
                if (result.isItem()) {
                    int inserted = getInventory().insert(OUTPUT, ItemResource.of(result.getItem()), result.getItem().count(), transaction);
                    if (inserted < result.getItem().count()) {
                        return false;
                    }
                }
            }

            // Advancement trigger
            Optional<BlockPaintData> s = results.stream()
                .findFirst()
                .map(OutputStack::getItem)
                .flatMap(item -> Optional.ofNullable(item.get(EIODataComponents.BLOCK_PAINT)));

            s.ifPresent(paintData -> {
                for (Player player : getLevel().players()) {
                    if (player instanceof ServerPlayer serverPlayer
                        && area.contains(player.getX(), player.getY(), player.getZ())) {
                        EIOCriterions.PAINTING_TRIGGER.get().trigger(serverPlayer, paintData.paint());
                    }
                }
            });

            return true;
        }
    }
}

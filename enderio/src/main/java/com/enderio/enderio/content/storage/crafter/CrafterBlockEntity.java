package com.enderio.enderio.content.storage.crafter;

import com.enderio.core.common.storage.layout.ItemStorageLayout;
import com.enderio.core.common.storage.layout.SlotTemplates;
import com.enderio.core.common.storage.slot.MultiResourceSlotKey;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.capacitor.QuadraticScalable;
import com.enderio.enderio.api.io.energy.EnergyIOMode;
import com.enderio.enderio.config.machines.MachinesConfig;
import com.enderio.enderio.foundation.block.entity.PoweredMachineBlockEntity;
import com.enderio.enderio.foundation.block.entity.flags.CapacitorSupport;
import com.enderio.enderio.foundation.inventory.MachineSlotTemplates;
import com.enderio.enderio.init.EIOBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.transaction.Transaction;
import org.jspecify.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

// TODO: Might want to see if we can adapt this into a crafting task.
public class CrafterBlockEntity extends PoweredMachineBlockEntity {

    public static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.CRAFTER_CAPACITY);
    public static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.CRAFTER_USAGE);

    public static final MultiResourceSlotKey<ItemResource> INPUT = new MultiResourceSlotKey<>(9);
    public static final SingleResourceSlotKey<ItemResource> OUTPUT = new SingleResourceSlotKey<>();
    public static final MultiResourceSlotKey<ItemResource> GHOST = new MultiResourceSlotKey<>(9);
    public static final SingleResourceSlotKey<ItemResource> PREVIEW = new SingleResourceSlotKey<>();
    public static final SingleResourceSlotKey<ItemResource> CAPACITOR = new SingleResourceSlotKey<>();

    @Nullable
    private RecipeHolder<CraftingRecipe> recipe;
    private final Queue<ItemStack> outputBuffer = new ArrayDeque<>();

    public CrafterBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EIOBlockEntities.CRAFTER.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED, CAPACITOR,
                EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE);
    }

    private CraftingInput getCraftingInput(MultiResourceSlotKey<ItemResource> sourceSlots) {
        return CraftingInput.of(3, 3, getInventory().getStacks(sourceSlots));
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);

        if (GHOST.contains(getInventory(), slot)) {
            updateRecipe();
        }
    }

    private void updateRecipe() {
        var input = getCraftingInput(GHOST);

        if (getLevel() instanceof ServerLevel level) {
            recipe = level.recipeAccess().getRecipeFor(RecipeType.CRAFTING, input, level).orElse(null);
        }
        getInventory().setStack(PREVIEW, ItemStack.EMPTY);

        if (recipe != null) {
            getInventory().setStack(PREVIEW, recipe.value().assemble(input));
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new CrafterMenu(containerId, inventory, this);
    }

    @Override
    public ItemStorageLayout createInventoryLayout() {
        return ItemStorageLayout.builder()
            .add(CAPACITOR, MachineSlotTemplates.capacitor())
            .add(INPUT, SlotTemplates.input(1), b -> b.filter(this::acceptSlotInput))
            .add(OUTPUT, SlotTemplates.output(64))
            .add(GHOST, SlotTemplates.ghost(1))
            .add(PREVIEW, SlotTemplates.inaccessible(1))
            .build();
    }

    private boolean acceptSlotInput(int slot, ItemResource resource) {
        return this.getInventory().getResource(slot + 10).is(resource.getItem());
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateRecipe();
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        if (level != null && !level.isClientSide()) {
            updateRecipe();
        }
    }

    @Override
    public void serverTick() {
        if (canAct()) {
            tryCraft();
        }

        super.serverTick();
        processOutputBuffer();
    }

    @Override
    public boolean isActive() {
        return canAct() && hasEnergy();
    }

    private void tryCraft() {
        getRecipeResult().ifPresent(result -> {
            if (shouldActTick() && hasPowerToCraft() && canMergeOutput(result) && outputBuffer.isEmpty()) {
                craftItem();
            }
        });
    }

    private boolean shouldActTick() {
        return canAct() && level.getGameTime() % ticksForAction() == 0;
    }

    private int ticksForAction() {
        return 20;
    }

    private boolean hasPowerToCraft() {
        return getEnergyStorage().canConsumeAtLeast(MachinesConfig.COMMON.ENERGY.CRAFTING_RECIPE_COST.get());
    }

    private void processOutputBuffer() {
        if (outputBuffer.isEmpty()) {
            return;
        }

        // output
        try (Transaction transaction = Transaction.openRoot()) {
            var toAdd = outputBuffer.peek();
            int inserted = getInventory().insert(OUTPUT, ItemResource.of(toAdd), toAdd.count(), transaction);
            if (inserted == 0) {
                return;
            }

            toAdd.shrink(inserted);
            if (toAdd.isEmpty()) {
                outputBuffer.remove();
            }

            transaction.commit();
        }
    }

    private Optional<ItemStack> getRecipeResult() {
        var input = getCraftingInput(INPUT);
        if (recipe != null && recipe.value().matches(input, getLevel())) {
            return Optional.of(recipe.value().assemble(input));
        }
        return Optional.empty();
    }

    private boolean canMergeOutput(ItemStack item) {
        ItemStack output = getInventory().getStack(OUTPUT);
        return output.isEmpty()
                || (ItemStack.isSameItemSameComponents(output, item) && (output.getCount() + item.getCount() <= 64));
    }

    private void craftItem() {
        var inventory = getInventory();

        for (int i = 0; i < 9; i++) {
            if (!ItemStack.isSameItem(inventory.getStack(INPUT.slot(i)), inventory.getStack(GHOST.slot(i)))) {
                return;
            }
        }
        // get input
        var input = getCraftingInput(INPUT);
        // craft
        clearInput();
        outputBuffer.add(recipe.value().assemble(input));
        outputBuffer.addAll(recipe.value().getRemainingItems(input));
        // clean buffer
        outputBuffer.removeIf(ItemStack::isEmpty);
        // consume power
        getEnergyStorage().consume(MachinesConfig.COMMON.ENERGY.CRAFTING_RECIPE_COST.get(), null);
        // check resource reload
        if (level instanceof ServerLevel serverLevel && serverLevel.recipeAccess().byKey(recipe.id()).orElse(null) != recipe) {
            recipe = null;
        }
    }

    private void clearInput() {
        for (int i = 0; i < 9; i++) {
            getInventory().setStack(INPUT.slot(i), ItemStack.EMPTY);
        }
    }
}

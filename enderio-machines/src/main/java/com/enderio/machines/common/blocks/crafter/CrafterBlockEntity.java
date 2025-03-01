package com.enderio.machines.common.blocks.crafter;

import com.enderio.base.api.capacitor.CapacitorModifier;
import com.enderio.base.api.capacitor.QuadraticScalable;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.blocks.base.blockentity.PoweredMachineBlockEntity;
import com.enderio.machines.common.blocks.base.blockentity.flags.CapacitorSupport;
import com.enderio.machines.common.blocks.base.inventory.MachineInventoryLayout;
import com.enderio.machines.common.blocks.base.inventory.MultiSlotAccess;
import com.enderio.machines.common.blocks.base.inventory.SingleSlotAccess;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

// TODO: Might want to see if we can adapt this into a crafting task.
public class CrafterBlockEntity extends PoweredMachineBlockEntity {

    public static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY,
            MachinesConfig.COMMON.ENERGY.CRAFTER_CAPACITY);
    public static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE,
            MachinesConfig.COMMON.ENERGY.CRAFTER_USAGE);
    public static final MultiSlotAccess INPUT = new MultiSlotAccess();
    public static final SingleSlotAccess OUTPUT = new SingleSlotAccess();
    public static final MultiSlotAccess GHOST = new MultiSlotAccess();
    public static final SingleSlotAccess PREVIEW = new SingleSlotAccess();

    @Nullable
    private RecipeHolder<CraftingRecipe> recipe;
    private final Queue<ItemStack> outputBuffer = new ArrayDeque<>();

    public CrafterBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(MachineBlockEntities.CRAFTER.get(), worldPosition, blockState, true, CapacitorSupport.REQUIRED,
                EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE);
    }

    private CraftingInput getCraftingInput(MultiSlotAccess sourceSlots) {
        return CraftingInput.of(3, 3, sourceSlots.getItemStacks(getInventory()));
    }

    @Override
    protected void onInventoryContentsChanged(int slot) {
        super.onInventoryContentsChanged(slot);
        if (GHOST.contains(slot)) {
            updateRecipe();
        }
    }

    private void updateRecipe() {
        var input = getCraftingInput(GHOST);

        recipe = getLevel().getRecipeManager().getRecipeFor(RecipeType.CRAFTING, input, getLevel()).orElse(null);
        PREVIEW.setStackInSlot(this, ItemStack.EMPTY);

        if (recipe != null) {
            PREVIEW.setStackInSlot(this, recipe.value().getResultItem(getLevel().registryAccess()));
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new CrafterMenu(containerId, inventory, this);
    }

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout.builder()
                .capacitor()
                .setStackLimit(1)
                .inputSlot(9, this::acceptSlotInput)
                .slotAccess(INPUT)
                .setStackLimit(64)
                .outputSlot(1)
                .slotAccess(OUTPUT)
                .setStackLimit(1)
                .ghostSlot(9)
                .slotAccess(GHOST)
                .previewSlot()
                .slotAccess(PREVIEW)
                .build();
    }

    private boolean acceptSlotInput(int slot, ItemStack stack) {
        return ItemStack.isSameItem(this.getInventory().getStackInSlot(slot + 10), stack);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        updateRecipe();
    }

    @Override
    public void loadAdditional(CompoundTag pTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(pTag, lookupProvider);
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
        return getEnergyStorage().consumeEnergy(MachinesConfig.COMMON.ENERGY.CRAFTING_RECIPE_COST.get(),
                true) >= MachinesConfig.COMMON.ENERGY.CRAFTING_RECIPE_COST.get();
    }

    private void processOutputBuffer() {
        if (outputBuffer.isEmpty()) {
            return;
        }

        // output
        if (canMergeOutput(outputBuffer.peek())) {
            var stack = OUTPUT.getItemStack(this);
            if (stack.isEmpty()) {
                OUTPUT.setStackInSlot(this, outputBuffer.peek().copy());
            } else {
                stack.grow(outputBuffer.peek().getCount());
            }
            outputBuffer.remove();
        }
    }

    private Optional<ItemStack> getRecipeResult() {
        var input = getCraftingInput(INPUT);
        if (recipe != null && recipe.value().matches(input, getLevel())) {
            return Optional.of(recipe.value().assemble(input, getLevel().registryAccess()));
        }
        return Optional.empty();
    }

    private boolean canMergeOutput(ItemStack item) {
        ItemStack output = OUTPUT.getItemStack(this);
        return output.isEmpty()
                || (ItemStack.isSameItemSameComponents(output, item) && (output.getCount() + item.getCount() <= 64));
    }

    private void craftItem() {
        for (int i = 0; i < 9; i++) {
            if (!ItemStack.isSameItem(INPUT.get(i).getItemStack(this), GHOST.get(i).getItemStack(this))) {
                return;
            }
        }
        // get input
        var input = getCraftingInput(INPUT);
        // craft
        clearInput();
        outputBuffer.add(recipe.value().assemble(input, getLevel().registryAccess()));
        outputBuffer.addAll(recipe.value().getRemainingItems(input));
        // clean buffer
        outputBuffer.removeIf(ItemStack::isEmpty);
        // consume power
        getEnergyStorage().consumeEnergy(MachinesConfig.COMMON.ENERGY.CRAFTING_RECIPE_COST.get(), false);
        // check resource reload
        if (level.getRecipeManager().byKey(recipe.id()).orElse(null) != recipe) {
            recipe = null;
        }
    }

    private void clearInput() {
        for (int i = 0; i < 9; i++) {
            INPUT.get(i).setStackInSlot(this, ItemStack.EMPTY);
        }
    }
}

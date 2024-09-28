package com.enderio.machines.common.blockentity;

import com.enderio.base.api.capacitor.CapacitorModifier;
import com.enderio.base.api.capacitor.QuadraticScalable;
import com.enderio.base.api.io.energy.EnergyIOMode;
import com.enderio.machines.common.blockentity.base.PoweredMachineBlockEntity;
import com.enderio.machines.common.config.MachinesConfig;
import com.enderio.machines.common.init.MachineBlockEntities;
import com.enderio.machines.common.io.item.MachineInventoryLayout;
import com.enderio.machines.common.io.item.MultiSlotAccess;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.CrafterMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayDeque;
import java.util.Optional;
import java.util.Queue;

// TODO: Might want to see if we can adapt this into a crafting task.
public class CrafterBlockEntity extends PoweredMachineBlockEntity {

    public static final QuadraticScalable ENERGY_CAPACITY = new QuadraticScalable(CapacitorModifier.ENERGY_CAPACITY, MachinesConfig.COMMON.ENERGY.CRAFTER_CAPACITY);
    public static final QuadraticScalable ENERGY_USAGE = new QuadraticScalable(CapacitorModifier.ENERGY_USE, MachinesConfig.COMMON.ENERGY.CRAFTER_USAGE);
    public static final MultiSlotAccess INPUT = new MultiSlotAccess();
    public static final SingleSlotAccess OUTPUT = new SingleSlotAccess();
    public static final MultiSlotAccess GHOST = new MultiSlotAccess();
    public static final SingleSlotAccess PREVIEW = new SingleSlotAccess();

    @Nullable
    private RecipeHolder<CraftingRecipe> recipe;
    private final Queue<ItemStack> outputBuffer = new ArrayDeque<>();

    private static final CraftingContainer DUMMY_CRAFTING_CONTAINER = new TransientCraftingContainer(new AbstractContainerMenu(null, -1) {
        @Override
        public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean stillValid(Player pPlayer) {
            return false;
        }
    }, 3, 3);


    public CrafterBlockEntity(BlockPos worldPosition, BlockState blockState) {
        super(EnergyIOMode.Input, ENERGY_CAPACITY, ENERGY_USAGE, MachineBlockEntities.CRAFTER.get(), worldPosition, blockState);
        getInventoryNN().addSlotChangedCallback(this::onSlotChanged);
    }

    private void onSlotChanged(int slot) {
        if (GHOST.contains(slot)) {
            updateRecipe();
        }
    }

    private void updateRecipe() {
        for (int i = 0; i < 9; i++) {
            DUMMY_CRAFTING_CONTAINER.setItem(i, GHOST.get(i).getItemStack(this).copy());
        }
        recipe = getLevel()
            .getRecipeManager()
            .getRecipeFor(RecipeType.CRAFTING, DUMMY_CRAFTING_CONTAINER.asCraftInput(), getLevel()).orElse(null);
        PREVIEW.setStackInSlot(this, ItemStack.EMPTY);

        if (recipe != null) {
            PREVIEW.setStackInSlot(this, recipe.value().getResultItem(getLevel().registryAccess()));
        }
    }

    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
        return new CrafterMenu(this, inventory, containerId);
    }

    @Override
    public MachineInventoryLayout createInventoryLayout() {
        return MachineInventoryLayout
            .builder()
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
        return ItemStack.isSameItem(this.getInventoryNN().getStackInSlot(slot + 10), stack);
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
    protected boolean isActive() {
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
        return this.energyStorage.consumeEnergy(MachinesConfig.COMMON.ENERGY.CRAFTING_RECIPE_COST.get(), true) >=
            MachinesConfig.COMMON.ENERGY.CRAFTING_RECIPE_COST.get();
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
        if (recipe != null) {
            return Optional.of(recipe.value().assemble(DUMMY_CRAFTING_CONTAINER.asCraftInput(), getLevel().registryAccess()));
        }
        return Optional.empty();
    }

    private boolean canMergeOutput(ItemStack item) {
        ItemStack output = OUTPUT.getItemStack(this);
        return output.isEmpty() || (ItemStack.isSameItemSameComponents(output, item) && (output.getCount() + item.getCount() <= 64));
    }

    private void craftItem() {
        for (int i = 0; i < 9; i++) {
            if (!ItemStack.isSameItem(INPUT.get(i).getItemStack(this), GHOST.get(i).getItemStack(this))) {
                return;
            }
        }
        //copy input items
        for (int i = 0; i < 9; i++) {
            DUMMY_CRAFTING_CONTAINER.setItem(i, INPUT.get(i).getItemStack(this).copy());
        }
        //craft
        clearInput();
        outputBuffer.add(recipe.value().assemble(DUMMY_CRAFTING_CONTAINER.asCraftInput(), getLevel().registryAccess()));
        outputBuffer.addAll(recipe.value().getRemainingItems(DUMMY_CRAFTING_CONTAINER.asCraftInput()));
        // clean buffer
        outputBuffer.removeIf(ItemStack::isEmpty);
        // consume power
        this.energyStorage.consumeEnergy(MachinesConfig.COMMON.ENERGY.CRAFTING_RECIPE_COST.get(), false);
        //check resource reload
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

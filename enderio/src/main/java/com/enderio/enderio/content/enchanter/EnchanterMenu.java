package com.enderio.enderio.content.enchanter;

import com.enderio.core.common.menu.BaseBlockEntityMenu;
import com.enderio.core.common.storage.ItemStorage;
import com.enderio.core.common.storage.slot.SingleResourceSlotKey;
import com.enderio.enderio.foundation.menu.MachineSlot;
import com.enderio.enderio.init.EIOBlockEntities;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedItemContents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Prediction;
import net.minecraft.world.inventory.RecipeBookMenu.PostPlaceAction;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.RecipeBookType;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.transfer.item.ItemResource;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class EnchanterMenu extends BaseBlockEntityMenu<EnchanterBlockEntity> {
    public static final int INPUTS_INDEX = 0;
    public static final int INPUT_COUNT = 3;
    public static final int LAST_INDEX = 3;

    public EnchanterMenu(int containerId, Inventory inventory, EnchanterBlockEntity blockEntity) {
        super(EIOMenus.ENCHANTER.get(), containerId, inventory, blockEntity);
        addSlots();
    }

    public EnchanterMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
        super(EIOMenus.ENCHANTER.get(), containerId, playerInventory, buf, EIOBlockEntities.ENCHANTER.get());
        addSlots();
    }

    private void addSlots() {
        addSlot(new MachineSlot(getMachineInventory(), EnchanterBlockEntity.BOOK, 16, 35));
        addSlot(new MachineSlot(getMachineInventory(), EnchanterBlockEntity.CATALYST, 65, 35));
        addSlot(new MachineSlot(getMachineInventory(), EnchanterBlockEntity.LAPIS, 85, 35));
        addSlot(new EnchanterOutputMachineSlot(getBlockEntity(), EnchanterBlockEntity.OUTPUT, 144, 35));

        addPlayerInventorySlots(8, 84);
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        // The recipe book settings are per type. A dedicated extensible type can
        // replace this once we have a shared Ender IO recipe book type registry.
        return RecipeBookType.CRAFTING;
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedItemContents contents) {
        contents.accountSimpleStack(getSlot(0).getItem());
        contents.accountSimpleStack(getSlot(1).getItem());
        contents.accountSimpleStack(getSlot(2).getItem());
    }

    @Override
    public PostPlaceAction handlePlacement(boolean useMaxItems, boolean allowDroppingItemsToClear, RecipeHolder<?> holder,
            ServerLevel level, Inventory inventory) {
        if (!(holder.value() instanceof EnchanterRecipe recipe)) {
            return PostPlaceAction.NOTHING;
        }

        int catalystCount = recipe.input().count();
        int lapisCount = recipe.getLapisForLevel(1);
        List<TransferPlan> plans = new java.util.ArrayList<>();
        if (!planInputs(recipe, catalystCount, lapisCount, plans)) {
            returnInputsToInventory(inventory);
            plans.clear();
            if (!planInputs(recipe, catalystCount, lapisCount, plans)) {
                return PostPlaceAction.PLACE_GHOST_RECIPE;
            }
        }

        for (TransferPlan plan : plans) {
            Slot target = getSlot(plan.targetSlot());
            for (var source : plan.sources()) {
                ItemStack moved = getSlot(source.slot()).remove(source.count());
                if (target.hasItem()) {
                    target.getItem().grow(moved.getCount());
                    target.setChanged();
                } else {
                    target.set(moved);
                }
            }
        }
        return PostPlaceAction.NOTHING;
    }

    private boolean planInputs(EnchanterRecipe recipe, int catalystCount, int lapisCount, List<TransferPlan> plans) {
        return planInput(0, 1, stack -> stack.is(Items.WRITABLE_BOOK), plans)
            && planInput(1, catalystCount, recipe.input().ingredient()::test, plans)
            && planInput(2, lapisCount, stack -> stack.is(Tags.Items.GEMS_LAPIS), plans);
    }

    private void returnInputsToInventory(Inventory inventory) {
        for (int i = 0; i < INPUT_COUNT; i++) {
            Slot slot = getSlot(i);
            ItemStack remainder = slot.getItem().copy();
            if (!remainder.isEmpty()) {
                inventory.placeItemBackInInventory(remainder, false, Prediction.SERVER_ONLY);
                slot.set(remainder);
            }
        }
    }

    private boolean planInput(int targetSlotIndex, int requiredCount, java.util.function.Predicate<ItemStack> valid,
            List<TransferPlan> plans) {
        Slot target = getSlot(targetSlotIndex);
        ItemStack existing = target.getItem();
        if (!existing.isEmpty() && !valid.test(existing)) {
            return false;
        }

        int alreadyPresent = existing.isEmpty() ? 0 : existing.getCount();
        int remaining = requiredCount - alreadyPresent;
        if (remaining <= 0) {
            return true;
        }

        ItemStack chosen = existing;
        List<SourceAmount> sources = new java.util.ArrayList<>();
        for (int i = 4; i < slots.size() && remaining > 0; i++) {
            if (i == targetSlotIndex) {
                continue;
            }
            ItemStack candidate = getSlot(i).getItem();
            if (candidate.isEmpty() || !valid.test(candidate)) {
                continue;
            }
            if (!chosen.isEmpty() && !ItemStack.isSameItemSameComponents(chosen, candidate)) {
                continue;
            }
            if (chosen.isEmpty()) {
                chosen = candidate;
            }
            int sourceSlot = i;
            int alreadyReserved = plans.stream().flatMap(plan -> plan.sources().stream())
                    .filter(source -> source.slot() == sourceSlot).mapToInt(SourceAmount::count).sum();
            int moved = Math.min(remaining, Math.max(0, candidate.getCount() - alreadyReserved));
            if (moved == 0) {
                continue;
            }
            sources.add(new SourceAmount(i, moved));
            remaining -= moved;
        }

        if (remaining > 0 || !target.mayPlace(chosen)) {
            return false;
        }
        plans.add(new TransferPlan(targetSlotIndex, sources));
        return true;
    }

    private record TransferPlan(int targetSlot, List<SourceAmount> sources) {}

    private record SourceAmount(int slot, int count) {}

    public ItemStorage getMachineInventory() {
        return getBlockEntity().getInventory();
    }

    public int getCurrentCost() {
        EnchanterRecipe recipe = this.getBlockEntity().getCurrentRecipe();
        if (recipe != null) {
            return recipe.getXPCost(this.getBlockEntity().createRecipeInput());
        }
        return -1;
    }

    // TODO: need to find a way to factor this out somehow...
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index < this.slots.size() - PLAYER_INVENTORY_SIZE) {
                if (!this.moveItemStackTo(itemstack1, this.slots.size() - PLAYER_INVENTORY_SIZE, this.slots.size(),
                        true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, 0, this.slots.size() - PLAYER_INVENTORY_SIZE, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            slot.onTake(player, itemstack1);
        }

        return itemstack;
    }

    public static class EnchanterOutputMachineSlot extends MachineSlot {
        @Nullable
        private final EnchanterBlockEntity blockEntity;

        public EnchanterOutputMachineSlot(@Nullable EnchanterBlockEntity blockEntity, SingleResourceSlotKey<ItemResource> access,
                int xPosition, int yPosition) {
            super(blockEntity.getInventory(), access, xPosition, yPosition);
            this.blockEntity = blockEntity;
        }

        @Override
        public void onTake(Player player, ItemStack stack) {
            var inventory = blockEntity.getInventory();
            EnchanterRecipe recipe = blockEntity.getCurrentRecipe();
            EnchanterRecipe.Input recipeInput = blockEntity.createRecipeInput();

            if (recipe != null && (player.experienceLevel >= recipe.getXPCost(recipeInput) || player.isCreative())) {
                int amount = recipe.getInputAmountConsumed(recipeInput);
                int lapizForLevel = recipe.getLapisForLevel(
                        recipe.getEnchantmentLevel(inventory.getStack(EnchanterBlockEntity.CATALYST).getCount()));
                player.giveExperienceLevels(-recipe.getXPCost(recipeInput));

                inventory.mutateStack(EnchanterBlockEntity.BOOK, s -> s.shrink(1));
                inventory.mutateStack(EnchanterBlockEntity.CATALYST, s -> s.shrink(amount));
                inventory.mutateStack(EnchanterBlockEntity.LAPIS, s -> s.shrink(lapizForLevel));
                super.onTake(player, stack);
            }
        }

        @Override
        public boolean mayPickup(Player playerIn) {
            EnchanterRecipe recipe = blockEntity.getCurrentRecipe();
            EnchanterRecipe.Input recipeInput = blockEntity.createRecipeInput();

            if (recipe != null
                    && (playerIn.experienceLevel >= recipe.getXPCost(recipeInput) || playerIn.isCreative())) {
                return super.mayPickup(playerIn);
            }

            return false;
        }
    }
}

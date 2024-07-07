package com.enderio.machines.common.menu;

import com.enderio.core.common.menu.BaseBlockEntityMenu;
import com.enderio.machines.common.blockentity.EnchanterBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.io.item.MachineInventory;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.menu.base.MachineMenu;
import com.enderio.machines.common.recipe.EnchanterRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class EnchanterMenu extends BaseBlockEntityMenu<EnchanterBlockEntity> {
    public static int INPUTS_INDEX = 0;
    public static int INPUT_COUNT = 3;
    public static int LAST_INDEX = 3;

    public EnchanterMenu(Inventory inventory, int pContainerId, @Nullable EnchanterBlockEntity blockEntity) {
        super(MachineMenus.ENCHANTER.get(), pContainerId, blockEntity, inventory);

        if (blockEntity != null) {
            addSlot(new MachineSlot(getMachineInventory(), EnchanterBlockEntity.BOOK, 16, 35));
            addSlot(new MachineSlot(getMachineInventory(), EnchanterBlockEntity.CATALYST, 65, 35));
            addSlot(new MachineSlot(getMachineInventory(), EnchanterBlockEntity.LAPIS, 85, 35));
            addSlot(new EnchanterOutputMachineSlot(blockEntity, EnchanterBlockEntity.OUTPUT, 144, 35));
        }

        addPlayerInventorySlots(8,84);
    }

    public MachineInventory getMachineInventory() {
        if (getBlockEntity() == null) {
            throw new IllegalStateException("BlockEntity is null");
        }

        return getBlockEntity().getInventory();
    }

    public static EnchanterMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof EnchanterBlockEntity castBlockEntity) {
            return new EnchanterMenu(inventory, pContainerId, castBlockEntity);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new EnchanterMenu(inventory, pContainerId, null);
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
    public ItemStack quickMoveStack(Player pPlayer, int pIndex) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(pIndex);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (pIndex < this.slots.size() - PLAYER_INVENTORY_SIZE) {
                if (!this.moveItemStackTo(itemstack1, this.slots.size() - PLAYER_INVENTORY_SIZE, this.slots.size(), true)) {
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

            slot.onTake(pPlayer, itemstack1);
        }

        return itemstack;
    }

    public static class EnchanterOutputMachineSlot extends MachineSlot {
        @Nullable
        private final EnchanterBlockEntity blockEntity;

        public EnchanterOutputMachineSlot(@Nullable EnchanterBlockEntity blockEntity, SingleSlotAccess access, int xPosition, int yPosition) {
            super(blockEntity.getInventory(), access, xPosition, yPosition);
            this.blockEntity = blockEntity;
        }

        @Override
        public void onTake(Player pPlayer, ItemStack pStack) {
            var inventory = blockEntity.getInventory();
            EnchanterRecipe recipe = blockEntity.getCurrentRecipe();
            EnchanterRecipe.Input recipeInput = blockEntity.createRecipeInput();

            if (recipe != null && (pPlayer.experienceLevel >= recipe.getXPCost(recipeInput) || pPlayer.isCreative())) {
                int amount = recipe.getInputAmountConsumed(recipeInput);
                int lapizForLevel = recipe.getLapisForLevel(recipe.getEnchantmentLevel(EnchanterBlockEntity.CATALYST.getItemStack(inventory).getCount()));
                pPlayer.giveExperienceLevels(-recipe.getXPCost(recipeInput));
                EnchanterBlockEntity.BOOK.getItemStack(inventory).shrink(1);
                EnchanterBlockEntity.CATALYST.getItemStack(inventory).shrink(amount);
                EnchanterBlockEntity.LAPIS.getItemStack(inventory).shrink(lapizForLevel);
                super.onTake(pPlayer, pStack);
            }
        }

        @Override
        public boolean mayPickup(Player playerIn) {
            EnchanterRecipe recipe = blockEntity.getCurrentRecipe();
            EnchanterRecipe.Input recipeInput = blockEntity.createRecipeInput();

            if (recipe != null && (playerIn.experienceLevel >= recipe.getXPCost(recipeInput) || playerIn.isCreative())) {
                return super.mayPickup(playerIn);
            }

            return false;
        }
    }
}

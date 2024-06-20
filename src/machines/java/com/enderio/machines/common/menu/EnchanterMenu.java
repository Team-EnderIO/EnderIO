package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.EnchanterBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.io.item.SingleSlotAccess;
import com.enderio.machines.common.recipe.EnchanterRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.wrapper.RecipeWrapper;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class EnchanterMenu extends MachineMenu<EnchanterBlockEntity> {
    public static int INPUTS_INDEX = 0;
    public static int INPUT_COUNT = 3;
    public static int LAST_INDEX = 3;

    public EnchanterMenu(@Nullable EnchanterBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.ENCHANTER.get(), pContainerId);
        if (blockEntity != null) {
            addSlot(new MachineSlot(blockEntity.getInventory(), EnchanterBlockEntity.BOOK, 16, 35));
            addSlot(new MachineSlot(blockEntity.getInventory(), EnchanterBlockEntity.CATALYST, 65, 35));
            addSlot(new MachineSlot(blockEntity.getInventory(), EnchanterBlockEntity.LAPIS, 85, 35));
            addSlot(new EnchanterOutputMachineSlot(blockEntity, EnchanterBlockEntity.OUTPUT, 144, 35));
        }
        addInventorySlots(8,84);
    }

    public static EnchanterMenu factory(int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof EnchanterBlockEntity castBlockEntity) {
            return new EnchanterMenu(castBlockEntity, inventory, pContainerId);
        }

        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new EnchanterMenu(null, inventory, pContainerId);
    }

    public int getCurrentCost() {
        EnchanterRecipe recipe = this.getBlockEntity().getCurrentRecipe();
        if (recipe != null) {
            return recipe.getXPCost(this.getBlockEntity().createRecipeInput());
        }
        return -1;
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

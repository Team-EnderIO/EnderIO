package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.EnchanterBlockEntity;
import com.enderio.machines.common.init.MachineMenus;
import com.enderio.machines.common.init.MachineRecipes;
import com.enderio.machines.common.recipe.EnchanterRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.apache.logging.log4j.LogManager;

import javax.annotation.Nullable;
import java.util.Optional;

public class EnchanterMenu extends MachineMenu<EnchanterBlockEntity> {
    private Level level;

    public EnchanterMenu(@Nullable EnchanterBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.ENCHANTER.get(), pContainerId);
        if (blockEntity != null) {
            this.level = blockEntity.getLevel();
            addSlot(new MachineSlot(blockEntity.getInventory(), 0, 16, 35));
            addSlot(new MachineSlot(blockEntity.getInventory(), 1, 65, 35));
            addSlot(new MachineSlot(blockEntity.getInventory(), 2, 85, 35));
            addSlot(new MachineSlot(blockEntity.getInventory(), 3, 144, 35) {
                @Override
                public void onTake(Player pPlayer, ItemStack pStack) {
                    Optional<EnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.Types.ENCHANTING, blockEntity.getContainer(), level);
                    if (recipe.isPresent() && (pPlayer.experienceLevel > recipe.get().getXPCost(blockEntity.getContainer()) || pPlayer.isCreative())) {
                        int amount = recipe.get().getInputAmountConsumed(blockEntity.getContainer());
                        int lapizForLevel = recipe.get().getLapisForLevel(recipe.get().getEnchantmentLevel(blockEntity.getInventory().getStackInSlot(1).getCount()));
                        pPlayer.giveExperienceLevels(-recipe.get().getXPCost(blockEntity.getContainer()));
                        blockEntity.getInventory().getStackInSlot(0).shrink(1);
                        blockEntity.getInventory().getStackInSlot(1).shrink(amount);
                        blockEntity.getInventory().getStackInSlot(2).shrink(lapizForLevel);
                    }
                    super.onTake(pPlayer, pStack);
                }

                @Override
                public boolean mayPickup(Player playerIn) {
                    Optional<EnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.Types.ENCHANTING, blockEntity.getContainer(), level);
                    if (recipe.isPresent() && (playerIn.experienceLevel > recipe.get().getXPCost(blockEntity.getContainer()) || playerIn.isCreative()) && blockEntity.canAct()) {
                        return super.mayPickup(playerIn);
                    }
                    return false;
                }
            });
        }
        addInventorySlots(8,84);
    }

    public static EnchanterMenu factory(@Nullable MenuType<EnchanterMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level.getBlockEntity(buf.readBlockPos());
        if (entity instanceof EnchanterBlockEntity castBlockEntity)
            return new EnchanterMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new EnchanterMenu(null, inventory, pContainerId);
    }

    public int getCurrentCost() {
        if (level != null) {
            Optional<EnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.Types.ENCHANTING, getBlockEntity().getContainer(), level);
            if (recipe.isPresent()) {
                return recipe.get().getXPCost(new RecipeWrapper(this.getBlockEntity().getInventory()));
            }
        }
        return -1;
    }
}

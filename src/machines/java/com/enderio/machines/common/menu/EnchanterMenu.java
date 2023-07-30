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
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class EnchanterMenu extends MachineMenu<EnchanterBlockEntity> {
    public static int INPUTS_INDEX = 0;
    public static int INPUT_COUNT = 3;
    public static int LAST_INDEX = 3;

    private Level level;

    public EnchanterMenu(@Nullable EnchanterBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.ENCHANTER.get(), pContainerId);
        if (blockEntity != null) {
            this.level = blockEntity.getLevel();
            addSlot(new MachineSlot(blockEntity.getInventory(), EnchanterBlockEntity.BOOK, 16, 35));
            addSlot(new MachineSlot(blockEntity.getInventory(), EnchanterBlockEntity.CATALYST, 65, 35));
            addSlot(new MachineSlot(blockEntity.getInventory(), EnchanterBlockEntity.LAPIS, 85, 35));
            addSlot(new MachineSlot(blockEntity.getInventory(), EnchanterBlockEntity.OUTPUT, 144, 35) {
                @Override
                public void onTake(Player pPlayer, ItemStack pStack) {
                    Optional<EnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.ENCHANTING.type().get(), blockEntity.getContainer(), level);
                    if (recipe.isPresent() && (pPlayer.experienceLevel > recipe.get().getXPCost(blockEntity.getContainer()) || pPlayer.isCreative())) {
                        int amount = recipe.get().getInputAmountConsumed(blockEntity.getContainer());
                        int lapizForLevel = recipe.get().getLapisForLevel(recipe.get().getEnchantmentLevel(EnchanterBlockEntity.CATALYST.getItemStack(blockEntity).getCount()));
                        pPlayer.giveExperienceLevels(-recipe.get().getXPCost(blockEntity.getContainer()));
                        EnchanterBlockEntity.BOOK.getItemStack(blockEntity).shrink(1);
                        EnchanterBlockEntity.CATALYST.getItemStack(blockEntity).shrink(amount);
                        EnchanterBlockEntity.LAPIS.getItemStack(blockEntity).shrink(lapizForLevel);
                    }
                    super.onTake(pPlayer, pStack);
                }

                @Override
                public boolean mayPickup(Player playerIn) {
                    Optional<EnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.ENCHANTING.type().get(), blockEntity.getContainer(), level);
                    if (recipe.isPresent() && (playerIn.experienceLevel >= recipe.get().getXPCost(blockEntity.getContainer()) || playerIn.isCreative()) && blockEntity.canAct()) {
                        return super.mayPickup(playerIn);
                    }
                    return false;
                }
            });
        }
        addInventorySlots(8,84);
    }

    public static EnchanterMenu factory(@Nullable MenuType<EnchanterMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level().getBlockEntity(buf.readBlockPos());
        if (entity instanceof EnchanterBlockEntity castBlockEntity)
            return new EnchanterMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new EnchanterMenu(null, inventory, pContainerId);
    }

    public int getCurrentCost() {
        if (level != null) {
            Optional<EnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.ENCHANTING.type().get(), getBlockEntity().getContainer(), level);
            if (recipe.isPresent()) {
                return recipe.get().getXPCost(new RecipeWrapper(this.getBlockEntity().getInventory()));
            }
        }
        return -1;
    }
}

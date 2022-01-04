package com.enderio.machines.common.menu;

import com.enderio.machines.common.blockentity.AlloySmelterBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

public class AlloySmelterMenu extends MachineMenu<AlloySmelterBlockEntity> {
    public AlloySmelterMenu(@Nullable AlloySmelterBlockEntity blockEntity, Inventory inventory, int pContainerId) {
        super(blockEntity, inventory, MachineMenus.ALLOY_SMELTER.get(), pContainerId);
        if (blockEntity != null) {
            addSlot(new MachineSlot(blockEntity.getItemHandlerMaster(), 0, 54, 17));
            addSlot(new MachineSlot(blockEntity.getItemHandlerMaster(), 1, 79, 7));
            addSlot(new MachineSlot(blockEntity.getItemHandlerMaster(), 2, 103, 17));
            addSlot(new MachineSlot(blockEntity.getItemHandlerMaster(), 3, 79, 58) {
//                @Override
//                public void onTake(Player pPlayer, ItemStack pStack) {
//                    Optional<EnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.Types.ENCHANTING, new RecipeWrapper(blockEntity.getItemHandlerMaster()), level);
//                    if (recipe.isPresent() && (pPlayer.experienceLevel > recipe.get().getLevelCost(new RecipeWrapper(blockEntity.getItemHandlerMaster())) || pPlayer.isCreative())) {
//                        int amount = recipe.get().getAmount(new RecipeWrapper(blockEntity.getItemHandlerMaster()));
//                        int lapizForLevel = recipe.get().getLapisForLevel(recipe.get().getEnchantmentLevel(blockEntity.getItemHandlerMaster().getStackInSlot(1).getCount()));
//                        pPlayer.giveExperienceLevels(-recipe.get().getLevelCost(new RecipeWrapper(blockEntity.getItemHandlerMaster())));
//                        blockEntity.getItemHandlerMaster().getStackInSlot(0).shrink(1);
//                        blockEntity.getItemHandlerMaster().getStackInSlot(1).shrink(amount);
//                        blockEntity.getItemHandlerMaster().getStackInSlot(2).shrink(lapizForLevel);
//                    }
//                    super.onTake(pPlayer, pStack);
//                }
//
//                @Override
//                public boolean mayPickup(Player playerIn) {
//                    Optional<EnchanterRecipe> recipe = level.getRecipeManager().getRecipeFor(MachineRecipes.Types.ENCHANTING, new RecipeWrapper(blockEntity.getItemHandlerMaster()), level);
//                    if (recipe.isPresent() && (playerIn.experienceLevel > recipe.get().getLevelCost(new RecipeWrapper(blockEntity.getItemHandlerMaster())) || playerIn.isCreative()) && blockEntity.shouldAct()) {
//                        return super.mayPickup(playerIn);
//                    }
//                    return false;
//                }
            });
        }
        addInventorySlots(8,84);
    }

    public static AlloySmelterMenu factory(@javax.annotation.Nullable MenuType<AlloySmelterMenu> pMenuType, int pContainerId, Inventory inventory, FriendlyByteBuf buf) {
        BlockEntity entity = inventory.player.level.getBlockEntity(buf.readBlockPos());
        if (entity instanceof AlloySmelterBlockEntity castBlockEntity)
            return new AlloySmelterMenu(castBlockEntity, inventory, pContainerId);
        LogManager.getLogger().warn("couldn't find BlockEntity");
        return new AlloySmelterMenu(null, inventory, pContainerId);
    }
}

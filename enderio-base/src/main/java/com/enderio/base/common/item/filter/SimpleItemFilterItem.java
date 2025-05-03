package com.enderio.base.common.item.filter;

import com.enderio.base.api.new_filter.ItemStackFilter;
import com.enderio.base.common.filter.EnderItemStackFilter;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.menu.AbstractFilterMenu;
import com.enderio.base.common.menu.EnderItemFilterMenu;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

public class SimpleItemFilterItem extends Item {

    public static ICapabilityProvider<ItemStack, Void, ItemStackFilter> ITEM_STACK_FILTER_PROVIDER =
        (stack, v) -> stack.getOrDefault(EIODataComponents.SIMPLE_ITEM_STACK_FILTER, EnderItemStackFilter.EMPTY);

    public SimpleItemFilterItem(Properties properties) {
        super(properties);
    }

    // TODO: Open the menu

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return Component.literal("");
                }

                @Override
                public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
                    return EnderItemFilterMenu.basic(pContainerId, pInventory, new AbstractFilterMenu.HandFilterAccess(player.getItemInHand(usedHand)));
                }
            });
        }

        return super.use(level, player, usedHand);
    }
}

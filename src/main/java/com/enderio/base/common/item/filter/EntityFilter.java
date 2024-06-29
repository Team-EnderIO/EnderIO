package com.enderio.base.common.item.filter;

import com.enderio.api.filter.ResourceFilter;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.menu.EntityFilterMenu;
import com.enderio.base.common.menu.FluidFilterMenu;
import com.enderio.core.common.capability.EntityFilterCapability;
import com.enderio.core.common.capability.FluidFilterCapability;
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

public class EntityFilter extends Item {

    // TODO: DataComponents
    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> ENTITY_FILTER =
        (stack, v) -> new EntityFilterCapability(EIODataComponents.ENTITY_FILTER, stack);

    public EntityFilter(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pPlayer instanceof ServerPlayer serverPlayer) {
            openMenu(serverPlayer);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    private static void openMenu(ServerPlayer player) {
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal("");
            }

            @Override
            public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
                return new EntityFilterMenu(pContainerId, pInventory, player.getMainHandItem());
            }
        });
    }
}

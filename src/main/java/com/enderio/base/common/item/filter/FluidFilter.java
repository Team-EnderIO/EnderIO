package com.enderio.base.common.item.filter;

import com.enderio.base.common.init.EIOAttachments;
import com.enderio.base.common.menu.FilterMenu;
import com.enderio.core.common.capability.IFilterCapability;
import com.enderio.core.common.item.IEnderFilter;
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

public class FluidFilter extends Item implements IEnderFilter {

    public static ICapabilityProvider<ItemStack, Void, IFilterCapability> FILTER_PROVIDER =
        (stack, v) -> stack.getData(EIOAttachments.FLUID_FILTER);

    private final int size;

    public FluidFilter(Properties pProperties, int size) {
        super(pProperties);
        this.size = size;
    }

    @Override
    public int size() {
        return size;
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
                return new FilterMenu(pContainerId, pInventory, player.getMainHandItem());
            }
        });
    }
}

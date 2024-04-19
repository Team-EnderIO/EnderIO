package com.enderio.base.common.item.filter;

import com.enderio.base.common.init.EIOAttachments;
import com.enderio.base.common.menu.FilterMenu;
import com.enderio.core.common.capability.ItemFilterCapability;
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

public class ItemFilter extends Item implements IEnderFilter {

    public static ICapabilityProvider<ItemStack, Void, ItemFilterCapability> FILTER_PROVIDER =
        (stack, v) -> stack.getData(EIOAttachments.ITEM_FILTER);

    private final int size;
    private final boolean advanced;
    private final boolean invert;

    public ItemFilter(Properties pProperties, int size, boolean advanced, boolean inverted) {
        super(pProperties);
        this.size = size;
        this.advanced = advanced;
        this.invert = inverted;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isAdvanced() {
        return advanced;
    }

    @Override
    public boolean isInverted() {
        return invert;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        if (pPlayer instanceof ServerPlayer serverPlayer) {
            openMenu(serverPlayer, stack);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    private static void openMenu(ServerPlayer player, ItemStack stack) {
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal("");
            }

            @Override
            public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
                return new FilterMenu(pContainerId, stack);
            }
        });
    }
}

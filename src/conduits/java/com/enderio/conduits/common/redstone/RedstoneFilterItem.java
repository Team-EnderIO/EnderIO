package com.enderio.conduits.common.redstone;

import com.enderio.api.filter.ResourceFilter;
import com.enderio.conduits.common.init.ConduitComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

import java.util.function.Supplier;

public class RedstoneFilterItem extends Item {

    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> AND_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_AND_FILTER);
    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> COUNT_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_COUNT_FILTER);
    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> NAND_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_NAND_FILTER);
    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> NOR_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_NOR_FILTER);
    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> NOT_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_NOT_FILTER);
    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> OR_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_OR_FILTER);
    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> SENSOR_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_SENSOR_FILTER);
    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> TLATCH_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_TLATCH_FILTER);
    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> XNOR_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_XNOR_FILTER);
    public static ICapabilityProvider<ItemStack, Void, ResourceFilter> XOR_FILTER_PROVIDER =
        (stack, v) -> stack.get(ConduitComponents.REDSTONE_XOR_FILTER);
    private final Supplier<MenuType<?>> menu;

    public RedstoneFilterItem(Properties pProperties, Supplier<MenuType<?>> menu) {
        super(pProperties);
        this.menu = menu;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pPlayer instanceof ServerPlayer serverPlayer) {
            openMenu(serverPlayer);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }

    private void openMenu(ServerPlayer player) {
        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return Component.literal("");
            }

            @Override
            public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
                return menu.get().create(pContainerId, pInventory);
            }
        });
    }
}

package com.enderio.conduits.common.redstone;

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
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class RedstoneFilterItem extends Item {

    // Insert filters
    public static final ICapabilityProvider<ItemStack, Void, RedstoneInsertFilter> AND_FILTER_PROVIDER =
        (stack, v) -> new RedstoneANDFilter(stack);
    public static final ICapabilityProvider<ItemStack, Void, RedstoneInsertFilter> COUNT_FILTER_PROVIDER =
        (stack, v) -> new RedstoneCountFilter(stack);
    public static final ICapabilityProvider<ItemStack, Void, RedstoneInsertFilter> NAND_FILTER_PROVIDER =
        (stack, v) -> new RedstoneNANDFilter(stack);
    public static final ICapabilityProvider<ItemStack, Void, RedstoneInsertFilter> NOR_FILTER_PROVIDER =
        (stack, v) -> new RedstoneNORFilter(stack);
    public static final ICapabilityProvider<ItemStack, Void, RedstoneInsertFilter> OR_FILTER_PROVIDER =
        (stack, v) -> new RedstoneORFilter(stack);
    public static final ICapabilityProvider<ItemStack, Void, RedstoneInsertFilter> TLATCH_FILTER_PROVIDER =
        (stack, v) -> new RedstoneTLatchFilter(stack);
    public static final ICapabilityProvider<ItemStack, Void, RedstoneInsertFilter> XNOR_FILTER_PROVIDER =
        (stack, v) -> new RedstoneXNORFilter(stack);
    public static final ICapabilityProvider<ItemStack, Void, RedstoneInsertFilter> XOR_FILTER_PROVIDER =
        (stack, v) -> new RedstoneXORFilter(stack);

    // Extract filters
    public static final ICapabilityProvider<ItemStack, Void, RedstoneExtractFilter> SENSOR_FILTER_PROVIDER =
        (stack, v) -> RedstoneSensorFilter.INSTANCE;
    public static final ICapabilityProvider<ItemStack, Void, RedstoneExtractFilter> TIMER_FILTER_PROVIDER =
        (stack, v) -> new RedstoneTimerFilter(stack);

    // Insert & Extract filters
    public static final ICapabilityProvider<ItemStack, Void, RedstoneInsertFilter> NOT_FILTER_PROVIDER_INSERT =
        (stack, v) -> RedstoneNOTFilter.INSTANCE;
    public static final ICapabilityProvider<ItemStack, Void, RedstoneExtractFilter> NOT_FILTER_PROVIDER_EXTRACT =
        (stack, v) -> RedstoneNOTFilter.INSTANCE;

    @Nullable
    private final Supplier<MenuType<?>> menu;

    public RedstoneFilterItem(Properties pProperties, @Nullable Supplier<MenuType<?>> menu) {
        super(pProperties);
        this.menu = menu;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        if (pPlayer instanceof ServerPlayer serverPlayer && menu != null) {
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

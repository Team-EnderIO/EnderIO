package com.enderio.enderio.content.filters.redstone;

import com.enderio.enderio.api.filter.RedstoneInputFilter;
import com.enderio.enderio.api.filter.RedstoneOutputFilter;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOMenus;
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
import java.util.function.UnaryOperator;

public class RedstoneFilterItem extends Item {

    // Insert filters
    public static final ICapabilityProvider<ItemStack, Void, RedstoneOutputFilter> AND_FILTER_PROVIDER =
        (stack, v) -> new RedstoneANDFilter(stack);
    public static final ICapabilityProvider<ItemStack, Void, RedstoneOutputFilter> COUNT_FILTER_PROVIDER =
        (stack, v) -> new RedstoneCountFilter(stack);
    public static final ICapabilityProvider<ItemStack, Void, RedstoneOutputFilter> NAND_FILTER_PROVIDER =
        (stack, v) -> new RedstoneNANDFilter(stack);
    public static final ICapabilityProvider<ItemStack, Void, RedstoneOutputFilter> NOR_FILTER_PROVIDER =
        (stack, v) -> new RedstoneNORFilter(stack);
    public static final ICapabilityProvider<ItemStack, Void, RedstoneOutputFilter> OR_FILTER_PROVIDER =
        (stack, v) -> new RedstoneORFilter(stack);
    public static final ICapabilityProvider<ItemStack, Void, RedstoneOutputFilter> TLATCH_FILTER_PROVIDER =
        (stack, v) -> new RedstoneTLatchFilter(stack);
    public static final ICapabilityProvider<ItemStack, Void, RedstoneOutputFilter> XNOR_FILTER_PROVIDER =
        (stack, v) -> new RedstoneXNORFilter(stack);
    public static final ICapabilityProvider<ItemStack, Void, RedstoneOutputFilter> XOR_FILTER_PROVIDER =
        (stack, v) -> new RedstoneXORFilter(stack);

    // Extract filters
    public static final ICapabilityProvider<ItemStack, Void, RedstoneInputFilter> SENSOR_FILTER_PROVIDER =
        (stack, v) -> RedstoneSensorFilter.INSTANCE;
    public static final ICapabilityProvider<ItemStack, Void, RedstoneInputFilter> TIMER_FILTER_PROVIDER =
        (stack, v) -> new RedstoneTimerFilter(stack);

    // Insert & Extract filters
    public static final ICapabilityProvider<ItemStack, Void, RedstoneOutputFilter> NOT_FILTER_PROVIDER_INSERT =
        (stack, v) -> RedstoneNOTFilter.INSTANCE;
    public static final ICapabilityProvider<ItemStack, Void, RedstoneInputFilter> NOT_FILTER_PROVIDER_EXTRACT =
        (stack, v) -> RedstoneNOTFilter.INSTANCE;

    private final Type type;

    public RedstoneFilterItem(Properties pProperties, Type type) {
        super(type.componentApplicator().apply(pProperties));
        this.type = type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        var menu = type.menu();
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
                return type.menu().create(pContainerId, pInventory);
            }
        });
    }

    public enum Type {
        NOT(p -> p, null),
        OR(p -> p.component(EIODataComponents.REDSTONE_FILTER_DOUBLE_CHANNEL, DoubleRedstoneChannel.INSTANCE), EIOMenus.REDSTONE_DOUBLE_CHANNEL_FILTER::get),
        AND(p -> p.component(EIODataComponents.REDSTONE_FILTER_DOUBLE_CHANNEL, DoubleRedstoneChannel.INSTANCE), EIOMenus.REDSTONE_DOUBLE_CHANNEL_FILTER::get),
        NOR(p -> p.component(EIODataComponents.REDSTONE_FILTER_DOUBLE_CHANNEL, DoubleRedstoneChannel.INSTANCE), EIOMenus.REDSTONE_DOUBLE_CHANNEL_FILTER::get),
        NAND(p -> p.component(EIODataComponents.REDSTONE_FILTER_DOUBLE_CHANNEL, DoubleRedstoneChannel.INSTANCE), EIOMenus.REDSTONE_DOUBLE_CHANNEL_FILTER::get),
        XOR(p -> p.component(EIODataComponents.REDSTONE_FILTER_DOUBLE_CHANNEL, DoubleRedstoneChannel.INSTANCE), EIOMenus.REDSTONE_DOUBLE_CHANNEL_FILTER::get),
        XNOR(p -> p.component(EIODataComponents.REDSTONE_FILTER_DOUBLE_CHANNEL, DoubleRedstoneChannel.INSTANCE), EIOMenus.REDSTONE_DOUBLE_CHANNEL_FILTER::get),
        TLATCH(p -> p.component(EIODataComponents.REDSTONE_TLATCH_FILTER, RedstoneTLatchFilter.INSTANCE), null),
        COUNT(p -> p.component(EIODataComponents.REDSTONE_COUNT_FILTER, RedstoneCountFilter.INSTANCE), EIOMenus.REDSTONE_COUNT_FILTER::get),
        SENSOR(p -> p, null),
        TIMER(p -> p.component(EIODataComponents.REDSTONE_TIMER_FILTER, RedstoneTimerFilter.INSTANCE), EIOMenus.REDSTONE_TIMER_FILTER::get);

        private UnaryOperator<Item.Properties> componentApplicator;

        @Nullable
        private Supplier<MenuType<?>> menu;

        Type(UnaryOperator<Item.Properties> componentApplicator, @Nullable Supplier<MenuType<?>> menu) {
            this.componentApplicator = componentApplicator;
            this.menu = menu;
        }

        public UnaryOperator<Item.Properties> componentApplicator() {
            return componentApplicator;
        }

        public @Nullable MenuType<?> menu() {
            if (menu == null) {
                return null;
            }

            return menu.get();
        }
    }
}

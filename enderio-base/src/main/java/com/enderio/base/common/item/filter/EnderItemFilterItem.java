package com.enderio.base.common.item.filter;

import com.enderio.base.api.new_filter.ItemStackFilter;
import com.enderio.base.common.filter.EnderItemStackFilter;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOMenus;
import com.enderio.base.common.menu.AbstractFilterMenu;
import com.enderio.base.common.menu.EnderItemFilterMenu;
import com.enderio.regilite.holder.RegiliteMenu;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.network.RegistryFriendlyByteBuf;
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

public class EnderItemFilterItem extends Item {

    public static ICapabilityProvider<ItemStack, Void, ItemStackFilter> ITEM_STACK_FILTER_PROVIDER =
        (stack, v) -> stack.getOrDefault(EIODataComponents.ITEM_STACK_FILTER, EnderItemStackFilter.EMPTY);

    private final Type type;

    public EnderItemFilterItem(Properties properties, Type type) {
        super(properties);
        this.type = type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
        if (player.isSteppingCarefully()) {
            if (player instanceof ServerPlayer serverPlayer) {
                var itemInHand = player.getItemInHand(usedHand);

                serverPlayer.openMenu(new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return getName(itemInHand);
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int pContainerId, Inventory pInventory, Player pPlayer) {
                        return type.openMenu(pContainerId, pInventory, new AbstractFilterMenu.HandFilterAccess(itemInHand));
                    }
                });
            }
        }

        return super.use(level, player, usedHand);
    }

    public enum Type {
        BASIC(() -> EIOMenus.BASIC_ITEM_FILTER, 5, false, false),
        ADVANCED(() -> EIOMenus.ADVANCED_ITEM_FILTER, 10, true, true),

        // TODO: Big ones need more work.
        BIG(() -> EIOMenus.BIG_ITEM_FILTER,3*9, false, false),
        BIG_ADVANCED(() -> EIOMenus.BIG_ADVANCED_ITEM_FILTER,3*9, true, true)
        ;

        private final Supplier<RegiliteMenu<EnderItemFilterMenu>> menuType;
        private final int slotCount;
        private final boolean canMatchComponents;
        private final boolean canFilterByDamage;

        Type(Supplier<RegiliteMenu<EnderItemFilterMenu>> menuType, int slotCount, boolean canMatchComponents, boolean canFilterByDamage) {
            this.menuType = menuType;
            this.slotCount = slotCount;
            this.canMatchComponents = canMatchComponents;
            this.canFilterByDamage = canFilterByDamage;
        }

        public int slotCount() {
            return slotCount;
        }

        public boolean canMatchComponents() {
            return canMatchComponents;
        }

        public boolean canFilterByDamage() {
            return canFilterByDamage;
        }

        @EnsureSide(EnsureSide.Side.SERVER)
        public EnderItemFilterMenu openMenu(int containerId, Inventory playerInventory, AbstractFilterMenu.FilterAccess filterAccess) {
            return new EnderItemFilterMenu(menuType.get().get(), this, containerId, playerInventory, filterAccess);
        }

        @EnsureSide(EnsureSide.Side.CLIENT)
        public EnderItemFilterMenu openMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
            return new EnderItemFilterMenu(menuType.get().get(), this, containerId, playerInventory);
        }
    }

}

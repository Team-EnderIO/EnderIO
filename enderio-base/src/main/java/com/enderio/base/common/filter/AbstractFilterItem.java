package com.enderio.base.common.filter;

import com.enderio.base.api.filter.FilterMenuProvider;
import com.enderio.base.common.lang.EIOLang;
import net.minecraft.core.component.DataComponentType;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractFilterItem<T> extends Item implements FilterMenuProvider {

    public static final ICapabilityProvider<ItemStack, Void, FilterMenuProvider> FILTER_MENU_PROVIDER = (stack, v) -> (AbstractFilterItem<?>) stack.getItem();

    public AbstractFilterItem(Properties properties) {
        super(properties);
    }

    protected abstract Supplier<DataComponentType<T>> dataComponentType();
    protected abstract T defaultFilter();
    protected abstract AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, AbstractFilterMenu.FilterAccess filterAccess);

    protected T getFilter(ItemStack stack) {
        return stack.getOrDefault(dataComponentType(), defaultFilter());
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
                    public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                        return AbstractFilterItem.this.createMenu(containerId, inventory, new AbstractFilterMenu.HandFilterAccess(player, itemInHand));
                    }
                });
            }
        }

        return super.use(level, player, usedHand);
    }

    @Override
    public void openMenu(ServerPlayer player, IItemHandlerModifiable itemHandler, int slot, @Nullable Runnable goBackRunnable) {
        var filterStack = itemHandler.getStackInSlot(slot);

        player.openMenu(new MenuProvider() {
            @Override
            public Component getDisplayName() {
                return getName(filterStack);
            }

            @Override
            public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                return AbstractFilterItem.this.createMenu(containerId, inventory,
                    new AbstractFilterMenu.InventoryFilterAccess(filterStack, itemHandler, slot, goBackRunnable));
            }

            public boolean shouldTriggerClientSideContainerClosingOnOpen() {
                // Prevents the mouse from jumping when moving between menus
                return false;
            }
        });
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        var filter = stack.getOrDefault(dataComponentType(), defaultFilter());
        if (!filter.equals(defaultFilter())) {
            tooltipComponents.add(EIOLang.CONFIGURED);
        }
    }
}

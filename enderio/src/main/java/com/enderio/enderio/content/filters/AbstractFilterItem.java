package com.enderio.enderio.content.filters;

import com.enderio.enderio.api.filter.FilterMenuProvider;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import org.jspecify.annotations.Nullable;

import java.util.Optional;
import java.util.function.Consumer;

public abstract class AbstractFilterItem<T> extends Item {

    public static final ICapabilityProvider<ItemStack, ItemAccess, FilterMenuProvider> FILTER_MENU_PROVIDER = (_, itemAccess) -> new FilterItemMenuProvider(itemAccess);

    public AbstractFilterItem(Properties properties) {
        super(properties);
    }

    protected abstract DataComponentType<T> dataComponentType();
    protected abstract T defaultFilter();
    protected abstract AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, AbstractFilterMenu.FilterAccess filterAccess);

    protected T getFilter(ItemStack stack) {
        return stack.getOrDefault(dataComponentType(), defaultFilter());
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand usedHand) {
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
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        var filter = stack.getOrDefault(dataComponentType(), defaultFilter());
        if (filter.equals(defaultFilter())) {
            // Add helpful hint for unconfigured filters
            tooltipAdder.accept(FiltersLang.UNCONFIGURED_HINT);
        }
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        TooltipDisplay display = stack.getOrDefault(DataComponents.TOOLTIP_DISPLAY, TooltipDisplay.DEFAULT);
        if (!display.shows(dataComponentType())) {
            return Optional.empty();
        }

        var filter = stack.getOrDefault(dataComponentType(), defaultFilter());
        if (filter.equals(defaultFilter()) || !(filter instanceof TooltipComponent filterTooltipComponent)) {
            return Optional.empty();
        }

        return Optional.of(filterTooltipComponent);
    }

    private static class FilterItemMenuProvider implements FilterMenuProvider {
        private final ItemAccess itemAccess;

        private FilterItemMenuProvider(ItemAccess itemAccess) {
            this.itemAccess = itemAccess;
        }

        @Override
        public void openMenu(ServerPlayer player, @Nullable Runnable goBackRunnable) {
            var filterStack = itemAccess.getResource().toStack(itemAccess.getAmount());

            player.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return filterStack.getHoverName();
                }

                @Override
                public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                    if (!(filterStack.getItem() instanceof AbstractFilterItem<?> abstractFilterItem)) {
                        throw new IllegalStateException("Filter item is not an instance of AbstractFilterItem");
                    }

                    return abstractFilterItem.createMenu(containerId, inventory,
                        new AbstractFilterMenu.InventoryFilterAccess(filterStack, itemAccess, goBackRunnable));

                }

                public boolean shouldTriggerClientSideContainerClosingOnOpen() {
                    // Prevents the mouse from jumping when moving between menus
                    return false;
                }
            });
        }
    }
}

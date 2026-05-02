package com.enderio.enderio.content.filters.soul;

import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.api.filter.SoulFilter;
import com.enderio.enderio.content.filters.AbstractFilterItem;
import com.enderio.enderio.content.filters.AbstractFilterMenu;
import com.enderio.enderio.content.filters.FiltersLang;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.fml.LogicalSide;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnderSoulFilterItem extends AbstractFilterItem<EnderSoulFilter> {

    public static final ICapabilityProvider<ItemStack, Void, SoulFilter> ENTITY_FILTER_PROVIDER = (stack, v) -> stack
            .getOrDefault(EIODataComponents.SOUL_FILTER, EnderSoulFilter.EMPTY);

    private final Type type;

    public EnderSoulFilterItem(Properties properties, Type type) {
        super(properties);
        this.type = type;
    }

    @Override
    protected DataComponentType<EnderSoulFilter> dataComponentType() {
        return EIODataComponents.SOUL_FILTER;
    }

    @Override
    protected EnderSoulFilter defaultFilter() {
        return EnderSoulFilter.EMPTY;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, AbstractFilterMenu.FilterAccess filterAccess) {
        return type.openMenu(containerId, playerInventory, filterAccess);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        var filter = getFilter(stack);
        if (filter.isDenyList()) {
            tooltipAdder.accept(FiltersLang.FILTER_DENY_LIST);
        } else {
            tooltipAdder.accept(FiltersLang.FILTER_ALLOW_LIST);
        }

        if (type.canMatchTags) {
            if (filter.shouldCompareTags()) {
                tooltipAdder.accept(FiltersLang.FILTER_MATCH_TAGS);
            } else {
                tooltipAdder.accept(FiltersLang.FILTER_IGNORE_TAGS);
            }
        }
    }

    public enum Type {
        BASIC(() -> EIOMenus.BASIC_SOUL_FILTER, 1, true);

        private final Supplier<Supplier<MenuType<EnderSoulFilterMenu>>> menuType;
        private final int rowCount;
        private final boolean canMatchTags;

        Type(Supplier<Supplier<MenuType<EnderSoulFilterMenu>>> menuType, int rowCount, boolean canMatchTags) {
            this.menuType = menuType;
            this.rowCount = rowCount;
            this.canMatchTags = canMatchTags;
        }

        public int rowCount() {
            return rowCount;
        }

        public int slotCount() {
            return rowCount * 9;
        }

        public boolean canMatchComponents() {
            return canMatchTags;
        }

        @UseOnly(LogicalSide.SERVER)
        public EnderSoulFilterMenu openMenu(int containerId, Inventory playerInventory,
                AbstractFilterMenu.FilterAccess filterAccess) {
            return new EnderSoulFilterMenu(menuType.get().get(), this, containerId, playerInventory, filterAccess);
        }

        @UseOnly(LogicalSide.CLIENT)
        public EnderSoulFilterMenu openMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
            return new EnderSoulFilterMenu(menuType.get().get(), this, containerId, playerInventory);
        }
    }

}

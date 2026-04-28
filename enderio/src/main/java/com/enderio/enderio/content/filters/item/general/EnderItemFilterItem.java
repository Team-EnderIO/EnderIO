package com.enderio.enderio.content.filters.item.general;

import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.api.filter.ItemFilter;
import com.enderio.enderio.content.filters.AbstractFilterItem;
import com.enderio.enderio.content.filters.AbstractFilterMenu;
import com.enderio.enderio.content.filters.FiltersLang;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOMenus;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.fml.LogicalSide;
import net.minecraft.world.item.component.BundleContents;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import oshi.util.tuples.Pair;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class EnderItemFilterItem extends AbstractFilterItem<EnderItemFilter> {

    public static final ICapabilityProvider<ItemStack, Void, ItemFilter> ITEM_FILTER_PROVIDER = (stack, v) -> stack
            .getOrDefault(EIODataComponents.ITEM_FILTER, EnderItemFilter.EMPTY);

    private final Type type;

    public EnderItemFilterItem(Properties properties, Type type) {
        super(properties);
        this.type = type;
    }

    @Override
    protected DataComponentType<EnderItemFilter> dataComponentType() {
        return EIODataComponents.ITEM_FILTER;
    }

    @Override
    protected EnderItemFilter defaultFilter() {
        return EnderItemFilter.EMPTY;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, AbstractFilterMenu.FilterAccess filterAccess) {
        return type.openMenu(containerId, playerInventory, filterAccess);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);
        // Display warning on basic item filters which have been set to match on
        // NBT/Components.
        // This avoids us invalidating existing filters, but lets the user know that the
        // filter has invalid settings that they can't see.
        var filter = getFilter(stack);
        if (filter.shouldCompareComponents() && !type.canMatchComponents()) {
            tooltipComponents.add(FiltersLang.FILTER_CONFIG_NOT_ALLOWED_COMPONENT_MATCH);
        }

        if (filter.isDenyList()) {
            tooltipComponents.add(FiltersLang.FILTER_DENY_LIST);
        } else {
            tooltipComponents.add(FiltersLang.FILTER_ALLOW_LIST);
        }

        if (type.canMatchComponents) {
            if (filter.shouldCompareComponents()) {
                tooltipComponents.add(FiltersLang.FILTER_MATCH_COMPONENTS);
            } else {
                tooltipComponents.add(FiltersLang.FILTER_IGNORE_COMPONENTS);
            }
        }

        if (type.canFilterByDamage()) {
            tooltipComponents.add(FiltersLang.DAMAGE_FILTER_MODE.copy().append(" - ").append(filter.damageFilterMode().getComponent()));
        }
    }

    public enum Type {
        BASIC(() -> EIOMenus.BASIC_ITEM_FILTER, 1, false, false),
        ADVANCED(() -> EIOMenus.ADVANCED_ITEM_FILTER, 2, true, true),
        BIG(() -> EIOMenus.BIG_ITEM_FILTER, 4, false, false),
        BIG_ADVANCED(() -> EIOMenus.BIG_ADVANCED_ITEM_FILTER, 4, true, true);

        private final Supplier<Supplier<MenuType<EnderItemFilterMenu>>> menuType;
        private final int rowCount;
        private final boolean canMatchComponents;
        private final boolean canFilterByDamage;

        Type(Supplier<Supplier<MenuType<EnderItemFilterMenu>>> menuType, int rowCount, boolean canMatchComponents,
                boolean canFilterByDamage) {
            this.menuType = menuType;
            this.rowCount = rowCount;
            this.canMatchComponents = canMatchComponents;
            this.canFilterByDamage = canFilterByDamage;
        }

        public int rowCount() {
            return rowCount;
        }

        public int slotCount() {
            return rowCount * 9;
        }

        public boolean canMatchComponents() {
            return canMatchComponents;
        }

        public boolean canFilterByDamage() {
            return canFilterByDamage;
        }

        @UseOnly(LogicalSide.SERVER)
        public EnderItemFilterMenu openMenu(int containerId, Inventory playerInventory,
                AbstractFilterMenu.FilterAccess filterAccess) {
            return new EnderItemFilterMenu(menuType.get().get(), this, containerId, playerInventory, filterAccess);
        }

        @UseOnly(LogicalSide.CLIENT)
        public EnderItemFilterMenu openMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
            return new EnderItemFilterMenu(menuType.get().get(), this, containerId, playerInventory);
        }
    }

}

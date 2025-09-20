package com.enderio.base.common.filter.item.general;

import com.enderio.base.api.filter.ItemFilter;
import com.enderio.base.common.filter.AbstractFilterItem;
import com.enderio.base.common.filter.AbstractFilterMenu;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOMenus;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.regilite.holder.RegiliteMenu;
import java.util.List;
import java.util.function.Supplier;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

public class EnderItemFilterItem extends AbstractFilterItem<EnderItemFilter> {

    public static final ICapabilityProvider<ItemStack, Void, ItemFilter> ITEM_FILTER_PROVIDER = (stack, v) -> stack
            .getOrDefault(EIODataComponents.ITEM_FILTER, EnderItemFilter.EMPTY);

    private final Type type;

    public EnderItemFilterItem(Properties properties, Type type) {
        super(properties);
        this.type = type;
    }

    @Override
    protected Supplier<DataComponentType<EnderItemFilter>> dataComponentType() {
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
            tooltipComponents.add(EIOLang.FILTER_CONFIG_NOT_ALLOWED_COMPONENT_MATCH);
        }
    }

    public enum Type {
        BASIC(() -> EIOMenus.BASIC_ITEM_FILTER, 1, false, false),
        ADVANCED(() -> EIOMenus.ADVANCED_ITEM_FILTER, 2, true, true),
        BIG(() -> EIOMenus.BIG_ITEM_FILTER, 4, false, false),
        BIG_ADVANCED(() -> EIOMenus.BIG_ADVANCED_ITEM_FILTER, 4, true, true);

        private final Supplier<RegiliteMenu<EnderItemFilterMenu>> menuType;
        private final int rowCount;
        private final boolean canMatchComponents;
        private final boolean canFilterByDamage;

        Type(Supplier<RegiliteMenu<EnderItemFilterMenu>> menuType, int rowCount, boolean canMatchComponents,
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

        @EnsureSide(EnsureSide.Side.SERVER)
        public EnderItemFilterMenu openMenu(int containerId, Inventory playerInventory,
                AbstractFilterMenu.FilterAccess filterAccess) {
            return new EnderItemFilterMenu(menuType.get().get(), this, containerId, playerInventory, filterAccess);
        }

        @EnsureSide(EnsureSide.Side.CLIENT)
        public EnderItemFilterMenu openMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
            return new EnderItemFilterMenu(menuType.get().get(), this, containerId, playerInventory);
        }
    }

}

package com.enderio.base.common.filter.entity;

import com.enderio.base.api.filter.EntityFilter;
import com.enderio.base.api.filter.FilterMenuProvider;
import com.enderio.base.common.filter.AbstractFilterItem;
import com.enderio.base.common.filter.AbstractFilterMenu;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOMenus;
import com.enderio.regilite.holder.RegiliteMenu;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

import java.util.function.Supplier;

public class EnderEntityFilterItem extends AbstractFilterItem<EnderEntityFilter> {

    public static ICapabilityProvider<ItemStack, Void, EntityFilter> ENTITY_FILTER_PROVIDER = (stack, v) -> stack
            .getOrDefault(EIODataComponents.ENTITY_FILTER, EnderEntityFilter.EMPTY);

    private final Type type;

    public EnderEntityFilterItem(Properties properties, Type type) {
        super(properties);
        this.type = type;
    }

    @Override
    protected Supplier<DataComponentType<EnderEntityFilter>> dataComponentType() {
        return EIODataComponents.ENTITY_FILTER;
    }

    @Override
    protected EnderEntityFilter defaultFilter() {
        return EnderEntityFilter.EMPTY;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, AbstractFilterMenu.FilterAccess filterAccess) {
        return type.openMenu(containerId, playerInventory, filterAccess);
    }

    public enum Type {
        BASIC(() -> EIOMenus.ENTITY_FILTER, 1, true);

        private final Supplier<RegiliteMenu<EnderEntityFilterMenu>> menuType;
        private final int rowCount;
        private final boolean canMatchComponents;

        Type(Supplier<RegiliteMenu<EnderEntityFilterMenu>> menuType, int rowCount, boolean canMatchComponents) {
            this.menuType = menuType;
            this.rowCount = rowCount;
            this.canMatchComponents = canMatchComponents;
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

        @EnsureSide(EnsureSide.Side.SERVER)
        public EnderEntityFilterMenu openMenu(int containerId, Inventory playerInventory,
                AbstractFilterMenu.FilterAccess filterAccess) {
            return new EnderEntityFilterMenu(menuType.get().get(), this, containerId, playerInventory, filterAccess);
        }

        @EnsureSide(EnsureSide.Side.CLIENT)
        public EnderEntityFilterMenu openMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
            return new EnderEntityFilterMenu(menuType.get().get(), this, containerId, playerInventory);
        }
    }

}

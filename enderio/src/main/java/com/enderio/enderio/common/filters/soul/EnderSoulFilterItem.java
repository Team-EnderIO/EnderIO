package com.enderio.enderio.common.filters.soul;

import com.enderio.enderio.api.filter.SoulFilter;
import com.enderio.enderio.common.filters.AbstractFilterItem;
import com.enderio.enderio.common.filters.AbstractFilterMenu;
import com.enderio.enderio.common.init.EIODataComponents;
import com.enderio.enderio.common.init.EIOMenus;
import com.enderio.regilite.holder.RegiliteMenu;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

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
    protected Supplier<DataComponentType<EnderSoulFilter>> dataComponentType() {
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

    public enum Type {
        BASIC(() -> EIOMenus.BASIC_SOUL_FILTER, 1, true);

        private final Supplier<RegiliteMenu<EnderSoulFilterMenu>> menuType;
        private final int rowCount;
        private final boolean canMatchComponents;

        Type(Supplier<RegiliteMenu<EnderSoulFilterMenu>> menuType, int rowCount, boolean canMatchComponents) {
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
        public EnderSoulFilterMenu openMenu(int containerId, Inventory playerInventory,
                AbstractFilterMenu.FilterAccess filterAccess) {
            return new EnderSoulFilterMenu(menuType.get().get(), this, containerId, playerInventory, filterAccess);
        }

        @EnsureSide(EnsureSide.Side.CLIENT)
        public EnderSoulFilterMenu openMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
            return new EnderSoulFilterMenu(menuType.get().get(), this, containerId, playerInventory);
        }
    }

}

package com.enderio.base.common.filter.fluid;

import com.enderio.base.api.filter.FluidFilter;
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

public class EnderFluidFilterItem extends AbstractFilterItem<EnderFluidFilter> {

    public static final ICapabilityProvider<ItemStack, Void, FluidFilter> FLUID_FILTER_PROVIDER = (stack, v) -> stack
            .getOrDefault(EIODataComponents.FLUID_FILTER, EnderFluidFilter.EMPTY);

    private final Type type;

    public EnderFluidFilterItem(Properties properties, Type type) {
        super(properties);
        this.type = type;
    }

    @Override
    protected Supplier<DataComponentType<EnderFluidFilter>> dataComponentType() {
        return EIODataComponents.FLUID_FILTER;
    }

    @Override
    protected EnderFluidFilter defaultFilter() {
        return EnderFluidFilter.EMPTY;
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
        BASIC(() -> EIOMenus.BASIC_FLUID_FILTER, 1, true);

        private final Supplier<RegiliteMenu<EnderFluidFilterMenu>> menuType;
        private final int rowCount;
        private final boolean canMatchComponents;

        Type(Supplier<RegiliteMenu<EnderFluidFilterMenu>> menuType, int rowCount, boolean canMatchComponents) {
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
        public EnderFluidFilterMenu openMenu(int containerId, Inventory playerInventory,
                AbstractFilterMenu.FilterAccess filterAccess) {
            return new EnderFluidFilterMenu(menuType.get().get(), this, containerId, playerInventory, filterAccess);
        }

        @EnsureSide(EnsureSide.Side.CLIENT)
        public EnderFluidFilterMenu openMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
            return new EnderFluidFilterMenu(menuType.get().get(), this, containerId, playerInventory);
        }
    }

}

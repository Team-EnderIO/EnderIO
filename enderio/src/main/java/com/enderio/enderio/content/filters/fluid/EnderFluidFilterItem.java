package com.enderio.enderio.content.filters.fluid;

import com.enderio.core.annotations.UseOnly;
import com.enderio.enderio.api.filter.FluidFilter;
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
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class EnderFluidFilterItem extends AbstractFilterItem<EnderFluidFilter> {

    public static final ICapabilityProvider<ItemStack, Void, FluidFilter> FLUID_FILTER_PROVIDER = (stack, v) -> stack
            .getOrDefault(EIODataComponents.FLUID_FILTER, EnderFluidFilter.EMPTY);

    private final Type type;

    public EnderFluidFilterItem(Properties properties, Type type) {
        super(properties);
        this.type = type;
    }

    @Override
    protected DataComponentType<EnderFluidFilter> dataComponentType() {
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
    public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltipAdder, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltipDisplay, tooltipAdder, flag);

        // Display warning on basic item filters which have been set to match on
        // NBT/Components.
        // This avoids us invalidating existing filters, but lets the user know that the
        // filter has invalid settings that they can't see.
        var filter = getFilter(stack);
        if (filter.shouldCompareComponents() && !type.canMatchComponents()) {
            tooltipAdder.accept(FiltersLang.FILTER_CONFIG_NOT_ALLOWED_COMPONENT_MATCH);
        }
    }

    public enum Type {
        BASIC(() -> EIOMenus.BASIC_FLUID_FILTER, 1, true);

        private final Supplier<Supplier<MenuType<EnderFluidFilterMenu>>> menuType;
        private final int rowCount;
        private final boolean canMatchComponents;

        Type(Supplier<Supplier<MenuType<EnderFluidFilterMenu>>> menuType, int rowCount, boolean canMatchComponents) {
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

        @UseOnly(LogicalSide.SERVER)
        public EnderFluidFilterMenu openMenu(int containerId, Inventory playerInventory,
                AbstractFilterMenu.FilterAccess filterAccess) {
            return new EnderFluidFilterMenu(menuType.get().get(), this, containerId, playerInventory, filterAccess);
        }

        @UseOnly(LogicalSide.CLIENT)
        public EnderFluidFilterMenu openMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
            return new EnderFluidFilterMenu(menuType.get().get(), this, containerId, playerInventory);
        }
    }

}

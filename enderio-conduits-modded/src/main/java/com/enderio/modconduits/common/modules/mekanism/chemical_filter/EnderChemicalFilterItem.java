package com.enderio.modconduits.common.modules.mekanism.chemical_filter;

import com.enderio.base.common.filter.AbstractFilterItem;
import com.enderio.base.common.filter.AbstractFilterMenu;
import com.enderio.base.common.filter.item.general.EnderItemFilterMenu;
import com.enderio.base.common.init.EIOMenus;
import com.enderio.modconduits.common.modules.mekanism.MekanismModule;
import com.enderio.regilite.holder.RegiliteMenu;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;

import java.util.function.Supplier;

public class EnderChemicalFilterItem extends AbstractFilterItem<EnderChemicalFilter> {

    public static final ICapabilityProvider<ItemStack, Void, ChemicalFilter> CHEMICAL_FILTER_PROVIDER = (stack, v) -> stack
        .getOrDefault(MekanismModule.CHEMICAL_FILTER, EnderChemicalFilter.EMPTY);

    private final Type type;

    public EnderChemicalFilterItem(Properties properties, Type type) {
        super(properties);
        this.type = type;
    }

    @Override
    protected Supplier<DataComponentType<EnderChemicalFilter>> dataComponentType() {
        return MekanismModule.CHEMICAL_FILTER;
    }

    @Override
    protected EnderChemicalFilter defaultFilter() {
        return EnderChemicalFilter.EMPTY;
    }

    @Override
    protected AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, AbstractFilterMenu.FilterAccess filterAccess) {
        return type.openMenu(containerId, playerInventory, filterAccess);
    }

    public enum Type {
        BASIC(() -> MekanismModule.CHEMICAL_FILTER_MENU, 1);

        private final Supplier<RegiliteMenu<EnderChemicalFilterMenu>> menuType;
        private final int rowCount;

        Type(Supplier<RegiliteMenu<EnderChemicalFilterMenu>> menuType, int rowCount) {
            this.menuType = menuType;
            this.rowCount = rowCount;
        }

        public int rowCount() {
            return rowCount;
        }

        public int slotCount() {
            return rowCount * 9;
        }

        @EnsureSide(EnsureSide.Side.SERVER)
        public EnderChemicalFilterMenu openMenu(int containerId, Inventory playerInventory,
            AbstractFilterMenu.FilterAccess filterAccess) {
            return new EnderChemicalFilterMenu(menuType.get().get(), this, containerId, playerInventory, filterAccess);
        }

        @EnsureSide(EnsureSide.Side.CLIENT)
        public EnderChemicalFilterMenu openMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
            return new EnderChemicalFilterMenu(menuType.get().get(), this, containerId, playerInventory);
        }
    }
}

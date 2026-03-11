package com.enderio.modded_conduits.common.modules.mekanism.chemical_filter;

import com.enderio.enderio.content.filters.AbstractFilterItem;
import com.enderio.enderio.content.filters.AbstractFilterMenu;
import com.enderio.modded_conduits.common.modules.mekanism.MekanismModule;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.registries.RegistryObject;

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
    protected DataComponentType<EnderChemicalFilter> dataComponentType() {
        return MekanismModule.CHEMICAL_FILTER.get();
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

        private final Supplier<RegistryObject<MenuType<EnderChemicalFilterMenu>>> menuType;
        private final int rowCount;

        Type(Supplier<RegistryObject<MenuType<EnderChemicalFilterMenu>>> menuType, int rowCount) {
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
        public EnderChemicalFilterMenu openMenu(int containerId, Inventory playerInventory, FriendlyByteBuf buf) {
            return new EnderChemicalFilterMenu(menuType.get().get(), this, containerId, playerInventory);
        }
    }
}

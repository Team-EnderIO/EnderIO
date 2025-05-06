package com.enderio.base.common.item.filter;

import com.enderio.base.api.new_filter.FilterMenuProvider;
import com.enderio.base.api.new_filter.FluidStackFilter;
import com.enderio.base.common.filter.fluid.EnderFluidStackFilter;
import com.enderio.base.common.filter.fluid.EnderFluidStackFilterMenu;
import com.enderio.base.common.filter.item.EnderItemStackFilter;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.init.EIOMenus;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.filter.AbstractFilterMenu;
import com.enderio.base.common.filter.item.EnderItemFilterMenu;
import com.enderio.regilite.holder.RegiliteMenu;
import me.liliandev.ensure.ensures.EnsureSide;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.items.IItemHandlerModifiable;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class EnderFluidStackFilterItem extends Item implements FilterMenuProvider {

    public static ICapabilityProvider<ItemStack, Void, FluidStackFilter> FLUID_STACK_FILTER_PROVIDER =
        (stack, v) -> stack.getOrDefault(EIODataComponents.FLUID_STACK_FILTER, EnderFluidStackFilter.EMPTY);

    public static ICapabilityProvider<ItemStack, Void, FilterMenuProvider> FILTER_MENU_PROVIDER = (stack,
            v) -> (EnderFluidStackFilterItem) stack.getItem();

    private final Type type;

    public EnderFluidStackFilterItem(Properties properties, Type type) {
        super(properties);
        this.type = type;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand) {
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
                        return type.openMenu(containerId, inventory,
                                new AbstractFilterMenu.HandFilterAccess(player, itemInHand));
                    }
                });
            }
        }

        return super.use(level, player, usedHand);
    }

    @Override
    public void openMenu(Player player, IItemHandlerModifiable itemHandler, int slot,
            @Nullable Runnable goBackRunnable) {
        if (player instanceof ServerPlayer serverPlayer) {
            var filterStack = itemHandler.getStackInSlot(slot);

            serverPlayer.openMenu(new MenuProvider() {
                @Override
                public Component getDisplayName() {
                    return getName(filterStack);
                }

                @Override
                public AbstractContainerMenu createMenu(int containerId, Inventory inventory, Player player) {
                    return type.openMenu(containerId, inventory, new AbstractFilterMenu.InventoryFilterAccess(
                            filterStack, itemHandler, slot, goBackRunnable));
                }

                public boolean shouldTriggerClientSideContainerClosingOnOpen() {
                    // Prevents the mouse from jumping when moving between menus
                    return false;
                }
            });
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        var filter = stack.getOrDefault(EIODataComponents.ITEM_STACK_FILTER, EnderItemStackFilter.EMPTY);
        if (!filter.equals(EnderItemStackFilter.EMPTY)) {
            tooltipComponents.add(EIOLang.CONFIGURED);
        }

        // Display warning on basic item filters which have been set to match on
        // NBT/Components.
        // This avoids us invalidating existing filters, but lets the user know that the
        // filter has invalid settings that they can't see.
        if (filter.shouldCompareComponents() && !type.canMatchComponents()) {
            tooltipComponents.add(EIOLang.FILTER_CONFIG_NOT_ALLOWED_COMPONENT_MATCH);
        }
    }

    public enum Type {
        BASIC(() -> EIOMenus.FLUID_FILTER, 1, true);

        private final Supplier<RegiliteMenu<EnderFluidStackFilterMenu>> menuType;
        private final int rowCount;
        private final boolean canMatchComponents;

        Type(Supplier<RegiliteMenu<EnderFluidStackFilterMenu>> menuType, int rowCount, boolean canMatchComponents) {
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
        public EnderFluidStackFilterMenu openMenu(int containerId, Inventory playerInventory,
                AbstractFilterMenu.FilterAccess filterAccess) {
            return new EnderFluidStackFilterMenu(menuType.get().get(), this, containerId, playerInventory, filterAccess);
        }

        @EnsureSide(EnsureSide.Side.CLIENT)
        public EnderFluidStackFilterMenu openMenu(int containerId, Inventory playerInventory, RegistryFriendlyByteBuf buf) {
            return new EnderFluidStackFilterMenu(menuType.get().get(), this, containerId, playerInventory);
        }
    }

}

package com.enderio.machines.common.blocks.fluid_tank;

import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.common.util.TooltipUtil;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.Nullable;

public class FluidTankBlockItem extends BlockItem implements AdvancedTooltipProvider {

    public static final ICapabilityProvider<ItemStack, Void, IFluidHandlerItem> FLUID_HANDLER_PROVIDER = (stack,
            v) -> new FluidHandlerItemStack(EIODataComponents.ITEM_FLUID_CONTENT, stack,
                    ((FluidTankBlockItem) stack.getItem()).capacity);

    protected final int capacity;

    public FluidTankBlockItem(FluidTankBlock block, Properties properties, int capacity) {
        super(block, properties);
        this.capacity = capacity;
    }

    public int getCapacity() {
        return capacity;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 1;// TODO: when fluid tank entity accepts item stacks of more than 1 in the
                 // internalDrain/fill. Remove this method to allow fluid tank items to
                 // originalStack to
                 // 64.
    }

    @Override
    public void addCommonTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        var fluidHandler = itemStack.getCapability(Capabilities.FluidHandler.ITEM);
        if (fluidHandler != null) {
            if (fluidHandler instanceof FluidHandlerItemStack itemFluidHandler) {
                if (itemFluidHandler.getFluid().isEmpty()) {
                    tooltips.add(TooltipUtil.style(EIOLang.TANK_EMPTY_STRING));
                } else {
                    tooltips.add(TooltipUtil.styledWithArgs(EIOLang.FLUID_TANK_TOOLTIP,
                            itemFluidHandler.getFluid().getAmount(), capacity,
                            itemFluidHandler.getFluid().getFluid().getFluidType().getDescription().getString()));
                }
            }
        }
    }
}

package com.enderio.enderio.content.storage.fluid_tank;

import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.ICapabilityProvider;
import net.neoforged.neoforge.transfer.ResourceHandler;
import net.neoforged.neoforge.transfer.access.ItemAccess;
import net.neoforged.neoforge.transfer.fluid.FluidResource;
import net.neoforged.neoforge.transfer.fluid.ItemAccessFluidHandler;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class FluidTankBlockItem extends BlockItem implements AdvancedTooltipProvider {

    public static final ICapabilityProvider<ItemStack, ItemAccess, ResourceHandler<FluidResource>> FLUID_HANDLER_PROVIDER = (stack,
            itemAccess) -> new ItemAccessFluidHandler(itemAccess, EIODataComponents.ITEM_FLUID_CONTENT,
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
        var fluidHandler = itemStack.getCapability(Capabilities.Fluid.ITEM, ItemAccess.forStack(itemStack));
        if (fluidHandler != null) {
            if (fluidHandler.getAmountAsInt(0) <= 0) {
                tooltips.add(TooltipUtil.style(EIOCommonLang.TANK_EMPTY_STRING));
            } else {
                tooltips.add(TooltipUtil.styledWithArgs(EIOCommonLang.FLUID_TANK_TOOLTIP,
                    fluidHandler.getAmountAsInt(0), capacity,
                    fluidHandler.getResource(0).getFluid().getFluidType().getDescription().getString()));
            }
        }
    }
}

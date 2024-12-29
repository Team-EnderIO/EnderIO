package com.enderio.conduits.common.conduit.facades;

import com.enderio.conduits.EnderIOConduits;
import com.enderio.conduits.common.init.ConduitCapabilities;
import com.enderio.conduits.common.init.ConduitLang;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = EnderIOConduits.MODULE_MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class FacadeTooltipHandler {
    @SubscribeEvent
    public static void addTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        var facade = stack.getCapability(ConduitCapabilities.ConduitFacade.ITEM);

        if (facade != null) {
            if (facade.type().doesHideConduits()) {
                event.getToolTip().add(TooltipUtil.style(ConduitLang.TRANSPARENT_FACADE_TOOLTIP));
            }

            if (facade.type().isBlastResistant()) {
                event.getToolTip().add(TooltipUtil.style(ConduitLang.BLAST_RESIST_FACADE_TOOLTIP));
            }
        }
    }
}

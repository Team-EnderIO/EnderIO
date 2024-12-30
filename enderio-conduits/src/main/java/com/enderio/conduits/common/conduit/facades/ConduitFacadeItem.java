package com.enderio.conduits.common.conduit.facades;

import com.enderio.base.common.lang.EIOLang;
import com.enderio.conduits.api.ConduitCapabilities;
import com.enderio.conduits.common.init.ConduitLang;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

public class ConduitFacadeItem extends Item {
    public ConduitFacadeItem(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents,
            TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, tooltipComponents, tooltipFlag);

        var facade = stack.getCapability(ConduitCapabilities.CONDUIT_FACADE_PROVIDER);
        boolean hasFacadeTooltip = facade != null
                && (facade.type().isBlastResistant() || facade.type().doesHideConduits());

        if (hasFacadeTooltip) {
            if (tooltipFlag.hasShiftDown()) {
                if (facade.type().doesHideConduits()) {
                    tooltipComponents.add(ConduitLang.TRANSPARENT_FACADE_TOOLTIP);
                }

                if (facade.type().isBlastResistant()) {
                    tooltipComponents.add(ConduitLang.BLAST_RESIST_FACADE_TOOLTIP);
                }
            } else {
                tooltipComponents.add(EIOLang.SHOW_DETAIL_TOOLTIP);
            }
        }
    }
}

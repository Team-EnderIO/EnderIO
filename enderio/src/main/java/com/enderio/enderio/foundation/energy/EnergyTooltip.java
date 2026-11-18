package com.enderio.enderio.foundation.energy;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.EnderIO;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;

import java.util.List;

/**
 * Adds the stored energy line to an item's tooltip.
 * <p>
 * The line is tagged so the client can swap it for a rendered energy bar when the tooltip is drawn
 * (see {@code TooltipHandler#onGatherEnergyBar}). Anything that only reads tooltip text (or a tooltip rendered without
 * the gather event) still gets the plain "stored/max µI" line.
 */
public final class EnergyTooltip {

    // Style insertions are only used for shift-clicking chat, so this has no visible effect on the line.
    private static final String MARKER = EnderIO.rl("energy_bar").toString();

    private EnergyTooltip() {
    }

    public static void add(ItemStack stack, List<Component> tooltips) {
        var storage = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if (storage == null || storage.getMaxEnergyStored() <= 0) {
            return;
        }

        String energy = String.format("%,d", storage.getEnergyStored()) + "/"
            + String.format("%,d", storage.getMaxEnergyStored());
        tooltips.add(TooltipUtil.style(TooltipUtil.withArgs(EIOCommonLang.ENERGY_AMOUNT, energy))
            .withStyle(style -> style.withInsertion(MARKER)));
    }

    public static boolean isEnergyLine(FormattedText text) {
        return text instanceof Component component && MARKER.equals(component.getStyle().getInsertion());
    }
}

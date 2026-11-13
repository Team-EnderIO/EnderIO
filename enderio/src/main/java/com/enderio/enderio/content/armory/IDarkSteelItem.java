package com.enderio.enderio.content.armory;

import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.common.energy.ItemStackEnergy;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.foundation.energy.EnergyTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Deprecated
public interface IDarkSteelItem extends AdvancedTooltipProvider {

    @Override
    default void addCommonTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        // Only shown once a capacitor gives the item an energy buffer.
        EnergyTooltip.add(itemStack, tooltips);
    }

}

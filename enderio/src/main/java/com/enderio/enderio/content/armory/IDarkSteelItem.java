package com.enderio.enderio.content.armory;

import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.common.energy.ItemStackEnergy;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface IDarkSteelItem extends AdvancedTooltipProvider {

    @Override
    default void addDetailedTooltips(ItemStack itemStack, @Nullable Player player, List<Component> tooltips) {
        addDurabilityTooltips(itemStack, tooltips);
    }

    default void addDurabilityTooltips(ItemStack itemStack, List<Component> tooltips) {
        if (itemStack.isDamageableItem()) {
            String durability = (itemStack.getMaxDamage() - itemStack.getDamageValue()) + "/"
                    + itemStack.getMaxDamage();
            tooltips.add(TooltipUtil.withArgs(ArmoryLang.DURABILITY_AMOUNT, durability).withStyle(ChatFormatting.GRAY));
        }
    }

    default boolean isDurabilityBarVisible(ItemStack stack) {
        return stack.getDamageValue() > 0 || ItemStackEnergy.getMaxEnergyStored(stack) > 0;
    }

}

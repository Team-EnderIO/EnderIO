package com.enderio.base.client.tooltip;

import com.enderio.base.common.lang.EIOLang;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TooltipHandler {

    private static final List<Component> tempTooltip = new ArrayList<>();

    @SubscribeEvent
    public static void addTooltips(ItemTooltipEvent evt) {
        ItemStack forItem = evt.getItemStack();
        IAdvancedTooltipProvider provider = null;
        if (forItem.getItem() instanceof IAdvancedTooltipProvider p) {
            provider = p;
        } else if(forItem.getItem() instanceof BlockItem bi && bi.getBlock() instanceof IAdvancedTooltipProvider p) {
            provider = p;
        }
        if (provider != null) {
            addTooltips(provider, forItem, evt.getPlayer(), evt.getToolTip(), shouldShowAdvancedTooltips());
        }
    }

    public static void addTooltips(IAdvancedTooltipProvider tt, ItemStack itemstack, @Nullable Player player, List<Component> list, boolean showAdvanced) {
        tt.addCommonTooltips(itemstack, player, list);
        if (showAdvanced) {
            tt.addDetailedTooltips(itemstack, player, list);
        } else {
            tt.addBasicTooltips(itemstack, player, list);
            if (hasDetailedTooltip(tt, itemstack, player)) {
                addShowDetailsTooltip(list);
            }
        }
    }

    public static boolean shouldShowAdvancedTooltips() {
        return Screen.hasShiftDown();
    }

    private static void addShowDetailsTooltip(List<Component> list) {
        list.add(EIOLang.SHOW_DETAIL_TOOLTIP.copy().withStyle(ChatFormatting.WHITE, ChatFormatting.ITALIC));
    }

    private static boolean hasDetailedTooltip(IAdvancedTooltipProvider tt, ItemStack stack, @Nullable Player player) {
        tempTooltip.clear();
        tt.addDetailedTooltips(stack, player, tempTooltip);
        return !tempTooltip.isEmpty();
    }
}

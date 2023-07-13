package com.enderio.base.client.tooltip;

import com.enderio.api.capacitor.CapacitorModifier;
import com.enderio.api.grindingball.IGrindingBallData;
import com.enderio.base.common.capacitor.CapacitorUtil;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.base.common.util.GrindingBallManager;
import com.enderio.core.client.item.IAdvancedTooltipProvider;
import com.enderio.core.common.util.TooltipUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.*;

// TODO: 1.19: Move to core. Need to work out what to do about the shift lang key. Will now need decoupled from the capacitor and grindingball logic.
@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TooltipHandler {

    private static final Component DETAIL_TOOLTIP = EIOLang.SHOW_DETAIL_TOOLTIP.copy().withStyle(ChatFormatting.WHITE, ChatFormatting.ITALIC);

    @SubscribeEvent
    public static void addAdvancedTooltips(ItemTooltipEvent evt) {
        ItemStack forItem = evt.getItemStack();
        boolean advanced = Screen.hasShiftDown();

        // Misc tooltips.
        addCapacitorTooltips(forItem, evt.getToolTip(), advanced);
        addGrindingBallTooltips(forItem, evt.getToolTip(), advanced);

        // Advanced tooltip system
        getAdvancedProvider(forItem.getItem()).ifPresent(provider ->
            addAdvancedTooltips(provider, forItem, evt.getEntity(), evt.getToolTip(), advanced)
        );
    }

    // region Configurable items (datapackable or otherwise)

    private static void addCapacitorTooltips(ItemStack itemStack, List<Component> components, boolean showAdvanced) {
        if (CapacitorUtil.isCapacitor(itemStack)) {
            if (showAdvanced) {
                CapacitorUtil.getCapacitorData(itemStack).ifPresent(data -> {
                    NumberFormat fmt = NumberFormat.getInstance(Locale.ENGLISH);
                    components.add(TooltipUtil.styledWithArgs(EIOLang.CAPACITOR_TOOLTIP_BASE, fmt.format(data.getBase())));
                    for (Map.Entry<CapacitorModifier, Float> modifier : data.getAllModifiers().entrySet()) {
                        components.add(TooltipUtil.styledWithArgs(new ResourceLocation("tooltip", modifier.getKey().id.toLanguageKey()), fmt.format(modifier.getValue())));
                    }
                });
            } else addShowDetailsTooltip(components);
        }
    }

    private static void addGrindingBallTooltips(ItemStack itemStack, List<Component> components, boolean showAdvanced) {
        if (GrindingBallManager.isGrindingBall(itemStack)) {
            if (showAdvanced) {
                IGrindingBallData data = GrindingBallManager.getData(itemStack);
                components.add(TooltipUtil.styledWithArgs(EIOLang.GRINDINGBALL_MAIN_OUTPUT, (int) (data.getOutputMultiplier() * 100)));
                components.add(TooltipUtil.styledWithArgs(EIOLang.GRINDINGBALL_BONUS_OUTPUT, (int) (data.getBonusMultiplier() * 100)));
                components.add(TooltipUtil.styledWithArgs(EIOLang.GRINDINGBALL_POWER_USE, (int) (data.getPowerUse() * 100)));
            } else addShowDetailsTooltip(components);
        }
    }

    // endregion

    // region Advanced Tooltips

    private static Optional<IAdvancedTooltipProvider> getAdvancedProvider(Item item) {
        if (item instanceof IAdvancedTooltipProvider provider)
            return Optional.of(provider);
        if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof IAdvancedTooltipProvider provider)
            return Optional.of(provider);
        return Optional.empty();
    }

    private static void addAdvancedTooltips(IAdvancedTooltipProvider tooltipProvider, ItemStack itemstack, @Nullable Player player, List<Component> components, boolean showAdvanced) {
        tooltipProvider.addCommonTooltips(itemstack, player, components);
        if (showAdvanced) {
            tooltipProvider.addDetailedTooltips(itemstack, player, components);
        } else {
            tooltipProvider.addBasicTooltips(itemstack, player, components);
            if (hasDetailedTooltip(tooltipProvider, itemstack, player)) {
                addShowDetailsTooltip(components);
            }
        }
    }

    // endregion

    private static void addShowDetailsTooltip(List<Component> components) {
        if (!components.contains(DETAIL_TOOLTIP))
            components.add(DETAIL_TOOLTIP);
    }

    private static boolean hasDetailedTooltip(IAdvancedTooltipProvider tooltipProvider, ItemStack stack, @Nullable Player player) {
        List<Component> tooltips = new ArrayList<>();
        tooltipProvider.addDetailedTooltips(stack, player, tooltips);
        return !tooltips.isEmpty();
    }
}

package com.enderio.enderio.client.foundation.tooltip;

import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.components.GrindingBallData;
import com.enderio.enderio.content.capacitors.CapacitorLang;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jspecify.annotations.Nullable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@EventBusSubscriber(value = Dist.CLIENT)
public class TooltipHandler {

    @SubscribeEvent
    public static void addAdvancedTooltips(ItemTooltipEvent evt) {
        ItemStack forItem = evt.getItemStack();
        boolean advanced = evt.getFlags().hasShiftDown();

        // Misc tooltips.
        addCapacitorTooltips(forItem, evt.getToolTip(), evt.getContext().registries());
        addGrindingBallTooltips(forItem, evt.getToolTip(), advanced);
        addSoulBindableTooltips(forItem, evt.getToolTip(), advanced);

        // Advanced tooltip system
        getAdvancedProvider(forItem.getItem()).ifPresent(
                provider -> addAdvancedTooltips(provider, forItem, evt.getEntity(), evt.getToolTip(), advanced));
    }

    // region Configurable items (datapackable or otherwise)

    private static void addCapacitorTooltips(ItemStack itemStack, List<Component> components, HolderLookup.@Nullable Provider registries) {
        CapacitorData capacitorData;
        var capacitorExtension = itemStack.getCapability(EnderIOCapabilities.CAPACITOR_EXTENSION);
        if (capacitorExtension != null) {
            capacitorData = capacitorExtension.getCapacitorData(itemStack, registries);
        } else {
            capacitorData = itemStack.get(EIODataComponents.CAPACITOR_DATA);
        }

        if (capacitorData != null) {
            NumberFormat fmt = NumberFormat.getInstance(Locale.ENGLISH);
            components
                    .add(TooltipUtil.styledWithArgs(CapacitorLang.CAPACITOR_TOOLTIP_BASE, fmt.format(capacitorData.base())));

            for (Map.Entry<CapacitorModifier, Float> modifier : capacitorData.modifiers().entrySet()) {
                components.add(TooltipUtil.styledWithArgs(
                        Component.translatable(modifier.getKey().modifierId.toLanguageKey() + ".tooltip"),
                        fmt.format(modifier.getValue())));
            }
        }
    }

    private static void addGrindingBallTooltips(ItemStack itemStack, List<Component> components, boolean showAdvanced) {
        GrindingBallData data = itemStack.typeHolder().getData(GrindingBallData.DATA_MAP_TYPE);
        if (data != null) {
            components.add(TooltipUtil.styledWithArgs(EIOCommonLang.GRINDINGBALL_MAIN_OUTPUT, (int) (data.outputMultiplier() * 100)));
            components.add(TooltipUtil.styledWithArgs(EIOCommonLang.GRINDINGBALL_BONUS_OUTPUT, (int) (data.bonusMultiplier() * 100)));
            components.add(TooltipUtil.styledWithArgs(EIOCommonLang.GRINDINGBALL_POWER_USE, (int) (data.powerUse() * 100)));
        }
    }

    // endregion

    // region Soul Storage

    private static void addSoulBindableTooltips(ItemStack itemStack, List<Component> components, boolean showAdvanced) {
        var soulBindable = itemStack.getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);
        if (soulBindable != null && soulBindable.canBind()) {
            var soul = soulBindable.getBoundSoul();

            if (soul.hasEntity()) {
                components.add(TooltipUtil.style(Component.translatable(soul.entityType().getDescriptionId())));
            } else {
                components.add(TooltipUtil.style(EIOCommonLang.TOOLTIP_NO_SOULBOUND));
            }
        }
    }

    // endregion

    // region Advanced Tooltips

    private static Optional<AdvancedTooltipProvider> getAdvancedProvider(Item item) {
        if (item instanceof AdvancedTooltipProvider provider) {
            return Optional.of(provider);
        }

        if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof AdvancedTooltipProvider provider) {
            return Optional.of(provider);
        }

        return Optional.empty();
    }

    private static void addAdvancedTooltips(AdvancedTooltipProvider tooltipProvider, ItemStack itemstack,
            @Nullable Player player, List<Component> components, boolean showAdvanced) {
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
        if (!components.contains(EIOCommonLang.SHOW_DETAIL_TOOLTIP)) {
            components.add(EIOCommonLang.SHOW_DETAIL_TOOLTIP);
        }
    }

    private static boolean hasDetailedTooltip(AdvancedTooltipProvider tooltipProvider, ItemStack stack,
            @Nullable Player player) {
        List<Component> tooltips = new ArrayList<>();
        tooltipProvider.addDetailedTooltips(stack, player, tooltips);
        return !tooltips.isEmpty();
    }
}

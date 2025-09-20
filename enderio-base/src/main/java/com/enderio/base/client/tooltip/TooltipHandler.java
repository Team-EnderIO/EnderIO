package com.enderio.base.client.tooltip;

import com.enderio.EnderIOBase;
import com.enderio.base.api.capacitor.CapacitorModifier;
import com.enderio.base.api.grindingball.GrindingBallData;
import com.enderio.base.common.init.EIOCapabilities;
import com.enderio.base.common.init.EIODataComponents;
import com.enderio.base.common.lang.EIOLang;
import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.common.util.EntityUtil;
import com.enderio.core.common.util.TooltipUtil;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber(modid = EnderIOBase.MODULE_MOD_ID, value = Dist.CLIENT)
public class TooltipHandler {

    @SubscribeEvent
    public static void addAdvancedTooltips(ItemTooltipEvent evt) {
        ItemStack forItem = evt.getItemStack();
        boolean advanced = Screen.hasShiftDown();

        // Misc tooltips.
        addCapacitorTooltips(forItem, evt.getToolTip(), advanced);
        addGrindingBallTooltips(forItem, evt.getToolTip(), advanced);
        addSoulBindableTooltips(forItem, evt.getToolTip(), advanced);

        // Advanced tooltip system
        getAdvancedProvider(forItem.getItem()).ifPresent(
                provider -> addAdvancedTooltips(provider, forItem, evt.getEntity(), evt.getToolTip(), advanced));
    }

    // region Configurable items (datapackable or otherwise)

    private static void addCapacitorTooltips(ItemStack itemStack, List<Component> components, boolean showAdvanced) {
        if (itemStack.has(EIODataComponents.CAPACITOR_DATA)) {
            var capacitorData = Objects.requireNonNull(itemStack.get(EIODataComponents.CAPACITOR_DATA));

            NumberFormat fmt = NumberFormat.getInstance(Locale.ENGLISH);
            components
                    .add(TooltipUtil.styledWithArgs(EIOLang.CAPACITOR_TOOLTIP_BASE, fmt.format(capacitorData.base())));

            for (Map.Entry<CapacitorModifier, Float> modifier : capacitorData.modifiers().entrySet()) {
                components.add(TooltipUtil.styledWithArgs(
                        ResourceLocation.fromNamespaceAndPath("tooltip", modifier.getKey().modifierId.toLanguageKey()),
                        fmt.format(modifier.getValue())));
            }
        }
    }

    private static void addGrindingBallTooltips(ItemStack itemStack, List<Component> components, boolean showAdvanced) {
        if (itemStack.has(EIODataComponents.GRINDING_BALL)) {
            if (showAdvanced) {
                GrindingBallData data = itemStack.get(EIODataComponents.GRINDING_BALL);
                components.add(TooltipUtil.styledWithArgs(EIOLang.GRINDINGBALL_MAIN_OUTPUT,
                        (int) (data.outputMultiplier() * 100)));
                components.add(TooltipUtil.styledWithArgs(EIOLang.GRINDINGBALL_BONUS_OUTPUT,
                        (int) (data.bonusMultiplier() * 100)));
                components
                        .add(TooltipUtil.styledWithArgs(EIOLang.GRINDINGBALL_POWER_USE, (int) (data.powerUse() * 100)));
            } else {
                addShowDetailsTooltip(components);
            }
        }
    }

    // endregion

    // region Soul Storage

    private static void addSoulBindableTooltips(ItemStack itemStack, List<Component> components, boolean showAdvanced) {
        var soulBindable = itemStack.getCapability(EIOCapabilities.SoulBindable.ITEM);
        if (soulBindable != null && soulBindable.canBind()) {
            var soul = soulBindable.getBoundSoul();

            if (soul.hasEntity()) {
                components.add(TooltipUtil.style(Component.translatable(soul.entityType().getDescriptionId())));
            } else {
                components.add(TooltipUtil.style(EIOLang.TOOLTIP_NO_SOULBOUND));
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
        if (!components.contains(EIOLang.SHOW_DETAIL_TOOLTIP)) {
            components.add(EIOLang.SHOW_DETAIL_TOOLTIP);
        }
    }

    private static boolean hasDetailedTooltip(AdvancedTooltipProvider tooltipProvider, ItemStack stack,
            @Nullable Player player) {
        List<Component> tooltips = new ArrayList<>();
        tooltipProvider.addDetailedTooltips(stack, player, tooltips);
        return !tooltips.isEmpty();
    }
}

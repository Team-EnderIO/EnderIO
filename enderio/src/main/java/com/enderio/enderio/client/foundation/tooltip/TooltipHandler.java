package com.enderio.enderio.client.foundation.tooltip;

import com.enderio.core.client.item.AdvancedTooltipProvider;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.api.EnderIOCapabilities;
import com.enderio.enderio.api.capacitor.CapacitorData;
import com.enderio.enderio.api.capacitor.CapacitorModifier;
import com.enderio.enderio.api.components.GrindingBallData;
import com.enderio.enderio.client.content.armory.SocketedCapacitorTooltip;
import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgrade;
import com.enderio.enderio.content.armory.ArmoryLang;
import com.enderio.enderio.content.armory.ItemDarkSteelUpgrades;
import com.enderio.enderio.content.capacitors.CapacitorLang;
import com.enderio.enderio.foundation.energy.EnergyTooltip;
import com.enderio.enderio.foundation.lang.EIOCommonLang;
import com.enderio.enderio.init.EIODataComponents;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.client.event.RenderTooltipEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import org.jetbrains.annotations.Nullable;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@EventBusSubscriber(value = Dist.CLIENT)
public class TooltipHandler {

    // Kept as a single instance so the slot can be placed directly after it in onGatherTooltipComponents.
    private static final Component SOCKETED_CAPACITOR_HEADING = ArmoryLang.SOCKETED_CAPACITOR.copy().withStyle(ChatFormatting.GRAY);

    @SubscribeEvent
    public static void addAdvancedTooltips(ItemTooltipEvent evt) {
        ItemStack forItem = evt.getItemStack();
        boolean advanced = Screen.hasShiftDown();

        // Misc tooltips.
        addCapacitorTooltips(forItem, evt.getToolTip(), evt.getContext().registries());
        addGrindingBallTooltips(forItem, evt.getToolTip(), advanced);
        addSoulBindableTooltips(forItem, evt.getToolTip(), advanced);
        boolean hasHiddenSocketDetails = addSocketedCapacitorTooltips(forItem, evt.getToolTip(), evt.getContext().registries(), advanced);
        boolean hasHiddenUpgradeDetails = addDarkSteelUpgradeTooltips(forItem, evt.getToolTip(), advanced);

        // Advanced tooltip system
        getAdvancedProvider(forItem.getItem()).ifPresent(
                provider -> addAdvancedTooltips(provider, forItem, evt.getEntity(), evt.getToolTip(), advanced));

        // The energy bar shows the exact amount instead of a percentage with shift held (see onGatherEnergyBar).
        boolean hasHiddenEnergyDetails = !advanced && evt.getToolTip().stream().anyMatch(EnergyTooltip::isEnergyLine);

        if (hasHiddenUpgradeDetails || hasHiddenSocketDetails || hasHiddenEnergyDetails) {
            addShowDetailsTooltip(evt.getToolTip());
        }
    }

    /**
     * Swaps the text line added by {@link EnergyTooltip} for a "Capacity: X" line and a rendered energy bar. The bar is
     * labelled with a percentage, or the stored amount while shift is held.
     */
    @SubscribeEvent
    public static void onGatherEnergyBar(RenderTooltipEvent.GatherComponents event) {
        var energyStorage = event.getItemStack().getCapability(Capabilities.EnergyStorage.ITEM);
        if (energyStorage == null) {
            return;
        }

        var elements = event.getTooltipElements();
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).left().filter(EnergyTooltip::isEnergyLine).isPresent()) {
                int max = energyStorage.getMaxEnergyStored();
                Component capacity = TooltipUtil.styledWithArgs(EIOCommonLang.ENERGY_CAPACITY,
                    TooltipUtil.withArgs(EIOCommonLang.ENERGY_AMOUNT, String.format("%,d", max)));

                elements.set(i, Either.right(new EnergyBarTooltip(energyStorage.getEnergyStored(), max, Screen.hasShiftDown())));
                elements.add(i, Either.left(capacity));
                return;
            }
        }
    }

    @SubscribeEvent
    public static void onGatherTooltipComponents(RenderTooltipEvent.GatherComponents event) {
        var capacitorSocket = event.getItemStack().getCapability(EnderIOCapabilities.CAPACITOR_SOCKET);
        if (capacitorSocket == null) {
            return;
        }

        var elements = event.getTooltipElements();
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).left().orElse(null) == SOCKETED_CAPACITOR_HEADING) {
                elements.add(i + 1, Either.right(new SocketedCapacitorTooltip(capacitorSocket.getCapacitor())));
                return;
            }
        }
    }

    // region Configurable items (datapackable or otherwise)

    private static void addCapacitorTooltips(ItemStack itemStack, List<Component> components, @Nullable HolderLookup.Provider registries) {
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
        GrindingBallData data = itemStack.getItemHolder().getData(GrindingBallData.DATA_MAP_TYPE);
        if (data != null) {
            components.add(TooltipUtil.styledWithArgs(EIOCommonLang.GRINDINGBALL_MAIN_OUTPUT, (int) (data.outputMultiplier() * 100)));
            components.add(TooltipUtil.styledWithArgs(EIOCommonLang.GRINDINGBALL_BONUS_OUTPUT, (int) (data.bonusMultiplier() * 100)));
            components.add(TooltipUtil.styledWithArgs(EIOCommonLang.GRINDINGBALL_POWER_USE, (int) (data.powerUse() * 100)));
        }
    }

    // endregion

    // region Dark Steel Upgrades

    /**
     * Lists the item's applied upgrades by name, adding an indented description under each when shift is held.
     * @return true if there are upgrade descriptions that are hidden until shift is held.
     */
    private static boolean addDarkSteelUpgradeTooltips(ItemStack itemStack, List<Component> components, boolean showAdvanced) {
        if (itemStack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)) {
            return false;
        }

        ItemDarkSteelUpgrades upgrades = itemStack.get(EIODataComponents.DARK_STEEL_UPGRADES);
        if (upgrades == null || upgrades.upgrades().isEmpty()) {
            return false;
        }

        for (Holder<DarkSteelUpgrade> upgrade : upgrades.upgrades()) {
            components.add(TooltipUtil.withArgs(ArmoryLang.UPGRADE_ENTRY, upgrade.value().name()).withStyle(ChatFormatting.GRAY));
            if (showAdvanced) {
                components.add(TooltipUtil.withArgs(ArmoryLang.UPGRADE_DESCRIPTION, upgrade.value().description()).withStyle(ChatFormatting.DARK_GRAY));
            }
        }

        return !showAdvanced;
    }

    // endregion

    // region Capacitor Sockets

    /**
     * Adds the socket heading (the slot itself is inserted after it in {@link #onGatherTooltipComponents}), followed by the socketed
     * capacitor's stats when shift is held.
     * @return true if there are capacitor stats that are hidden until shift is held.
     */
    private static boolean addSocketedCapacitorTooltips(ItemStack itemStack, List<Component> components, @Nullable HolderLookup.Provider registries,
        boolean showAdvanced) {
        if (itemStack.has(DataComponents.HIDE_ADDITIONAL_TOOLTIP)) {
            return false;
        }

        var capacitorSocket = itemStack.getCapability(EnderIOCapabilities.CAPACITOR_SOCKET);
        if (capacitorSocket == null) {
            return false;
        }

        components.add(SOCKETED_CAPACITOR_HEADING);

        ItemStack capacitor = capacitorSocket.getCapacitor();
        if (capacitor.isEmpty()) {
            return false;
        }

        if (showAdvanced) {
            addCapacitorTooltips(capacitor, components, registries);
            return false;
        }

        return true;
    }

    // endregion

    // region Soul Storage

    private static void addSoulBindableTooltips(ItemStack itemStack, List<Component> components, boolean showAdvanced) {
        var soulBindable = itemStack.getCapability(EnderIOCapabilities.SOUL_BINDABLE_ITEM);
        if (soulBindable != null && soulBindable.canBind()) {
            var soul = soulBindable.getBoundSoul();

            if (soul.hasEntity()) {
                components.add(TooltipUtil.styledWithArgs(EIOCommonLang.BOUND_SOUL, soul.entityType().getDescription()));
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

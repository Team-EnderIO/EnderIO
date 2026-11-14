package com.enderio.enderio.content.armory.items;

import com.enderio.core.common.item.ICustomCreativeTabEntries;
import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.api.EnderIORegistries;
import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgrade;
import com.enderio.enderio.content.armory.ArmoryLang;
import com.enderio.enderio.init.EIODataComponents;
import com.enderio.enderio.init.EIOItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Carries a single {@link DarkSteelUpgrade}, ready to be applied to a dark steel item.
 */
public class DarkSteelUpgradeItem extends Item implements ICustomCreativeTabEntries {

    public DarkSteelUpgradeItem(Properties properties) {
        super(properties);
    }

    public static ItemStack getStackFor(Holder<DarkSteelUpgrade> upgrade) {
        return getStackFor(upgrade, 1);
    }

    public static ItemStack getStackFor(Holder<DarkSteelUpgrade> upgrade, int count) {
        var stack = new ItemStack(EIOItems.DARK_STEEL_UPGRADE.get(), count);
        stack.set(EIODataComponents.DARK_STEEL_UPGRADE, upgrade);
        return stack;
    }

    /**
     * @return the upgrade carried by the stack, or null if it carries none or one that no longer exists.
     */
    @Nullable
    public static Holder<DarkSteelUpgrade> getUpgrade(ItemStack stack) {
        Holder<DarkSteelUpgrade> upgrade = stack.get(EIODataComponents.DARK_STEEL_UPGRADE);
        if (upgrade == null || !upgrade.isBound()) {
            return null;
        }

        return upgrade;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        Holder<DarkSteelUpgrade> holder = getUpgrade(stack);
        if (holder == null) {
            tooltipComponents.add(ArmoryLang.UPGRADE_UNKNOWN.copy().withStyle(ChatFormatting.RED));
            return;
        }

        DarkSteelUpgrade upgrade = holder.value();
        tooltipComponents.add(upgrade.name().copy().withStyle(ChatFormatting.AQUA));
        tooltipComponents.add(upgrade.description().copy().withStyle(ChatFormatting.GRAY));
        tooltipComponents.add(TooltipUtil.withArgs(ArmoryLang.UPGRADE_SUPPORTED_ITEMS, describeItems(upgrade.definition().supportedItems()))
            .withStyle(ChatFormatting.DARK_GRAY));

        if (upgrade.definition().requiresSocketedCapacitor()) {
            tooltipComponents.add(ArmoryLang.UPGRADE_REQUIRES_CAPACITOR.copy().withStyle(ChatFormatting.YELLOW));
        }

        if ((tooltipFlag.isAdvanced() || tooltipFlag.hasShiftDown()) && upgrade.exclusiveSet().size() > 0) {
            Component conflicts = ComponentUtils.formatList(upgrade.exclusiveSet().stream().map(other -> other.value().name()).toList(),
                Component.literal(", "));
            tooltipComponents.add(TooltipUtil.withArgs(ArmoryLang.UPGRADE_CONFLICTS, conflicts).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    private static Component describeItems(HolderSet<Item> items) {
        return items.unwrapKey()
            .<Component>map(tag -> Component.translatableWithFallback(Tags.getTagTranslationKey(tag), "#" + tag.location()))
            .orElseGet(() -> ComponentUtils.formatList(items.stream().map(item -> item.value().getDescription()).toList(),
                Component.literal(", ")));
    }

    @Override
    public boolean shouldAddDefaultItem() {
        return false;
    }

    @Override
    public void addAdditionalCreativeTabEntries(CreativeModeTab.ItemDisplayParameters parameters, CreativeModeTab.Output output) {
        parameters.holders()
            .lookup(EnderIORegistries.Keys.DARK_STEEL_UPGRADE)
            .ifPresent(upgrades -> upgrades.listElements().forEach(upgrade -> output.accept(getStackFor(upgrade))));
    }
}

package com.enderio.enderio.content.armory.toggles;

import com.enderio.core.common.util.TooltipUtil;
import com.enderio.enderio.api.armory.upgrades.DarkSteelToggle;
import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradableItem;
import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgrade;
import com.enderio.enderio.content.armory.ArmoryLang;
import com.enderio.enderio.content.armory.ItemDarkSteelUpgrades;
import com.enderio.enderio.init.EIODataComponents;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class DarkSteelToggleHelper {

    private DarkSteelToggleHelper() {
    }

    /**
     * @return whether the toggle is switched on for this item. Always false if none of the item's upgrades declare the toggle.
     */
    public static boolean isEnabled(ItemStack stack, ResourceLocation toggle) {
        DarkSteelToggle declaration = getDeclaration(stack, toggle);
        if (declaration == null) {
            return false;
        }

        return getState(stack, toggle, declaration);
    }

    /**
     * @return the first declaration of the toggle among the item's upgrades, in the order they were applied.
     */
    @Nullable
    public static DarkSteelToggle getDeclaration(ItemStack stack, ResourceLocation toggle) {
        ItemDarkSteelUpgrades upgrades = stack.get(EIODataComponents.DARK_STEEL_UPGRADES);
        if (upgrades == null) {
            return null;
        }

        for (Holder<DarkSteelUpgrade> upgrade : upgrades.upgrades()) {
            DarkSteelToggle declaration = upgrade.value().toggles().get(toggle);
            if (declaration != null) {
                return declaration;
            }
        }

        return null;
    }

    /**
     * @return every toggle declared by the item's upgrades. Where several upgrades declare the same toggle, the first one applied wins.
     */
    public static Map<ResourceLocation, DarkSteelToggle> getDeclarations(ItemStack stack) {
        ItemDarkSteelUpgrades upgrades = stack.get(EIODataComponents.DARK_STEEL_UPGRADES);
        if (upgrades == null) {
            return Map.of();
        }

        Map<ResourceLocation, DarkSteelToggle> declarations = new LinkedHashMap<>();
        for (Holder<DarkSteelUpgrade> upgrade : upgrades.upgrades()) {
            upgrade.value().toggles().forEach(declarations::putIfAbsent);
        }

        return declarations;
    }

    private static boolean getState(ItemStack stack, ResourceLocation toggle, DarkSteelToggle declaration) {
        return stack.getOrDefault(EIODataComponents.DARK_STEEL_TOGGLES, DarkSteelToggles.EMPTY)
            .states()
            .getOrDefault(toggle, declaration.defaultState());
    }

    private static void setState(ItemStack stack, ResourceLocation toggle, DarkSteelToggle declaration, boolean state) {
        var toggles = stack.getOrDefault(EIODataComponents.DARK_STEEL_TOGGLES, DarkSteelToggles.EMPTY)
            .with(toggle, state, declaration.defaultState());

        if (toggles.states().isEmpty()) {
            stack.remove(EIODataComponents.DARK_STEEL_TOGGLES);
        } else {
            stack.set(EIODataComponents.DARK_STEEL_TOGGLES, toggles);
        }
    }

    private record BoundToggle(ItemStack stack, ResourceLocation id, DarkSteelToggle declaration) {
    }

    /**
     * Flips every toggle bound to the key on the player's equipped dark steel items.
     * All of them are set to the same state: off if any are on, otherwise on. This keeps toggles sharing a key in sync.
     */
    public static void onKeyPressed(Player player, ResourceLocation key) {
        List<BoundToggle> bound = new ArrayList<>();
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack stack = player.getItemBySlot(slot);

            // Match the equipment tick hook: only items worn or held in the slot they're intended for.
            if (!(stack.getItem() instanceof DarkSteelUpgradableItem) || player.getEquipmentSlotForItem(stack) != slot) {
                continue;
            }

            getDeclarations(stack).forEach((id, declaration) -> {
                if (declaration.key().filter(key::equals).isPresent()) {
                    bound.add(new BoundToggle(stack, id, declaration));
                }
            });
        }

        if (bound.isEmpty()) {
            return;
        }

        boolean newState = bound.stream().noneMatch(toggle -> getState(toggle.stack(), toggle.id(), toggle.declaration()));

        Map<ResourceLocation, Component> names = new LinkedHashMap<>();
        for (BoundToggle toggle : bound) {
            setState(toggle.stack(), toggle.id(), toggle.declaration(), newState);
            names.putIfAbsent(toggle.id(), toggle.declaration().name());
        }

        Component nameList = ComponentUtils.formatList(names.values(), Component.literal(", "));
        player.displayClientMessage(TooltipUtil.withArgs(newState ? ArmoryLang.TOGGLE_ENABLED : ArmoryLang.TOGGLE_DISABLED, nameList), true);
    }
}

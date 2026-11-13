package com.enderio.enderio.content.armory;

import com.enderio.enderio.EnderIO;
import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgrade;
import com.enderio.enderio.api.armory.upgrades.DarkSteelUpgradeContainer;
import com.enderio.enderio.api.armory.upgrades.hooks.DarkSteelUpgradeHookType;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

public record ItemDarkSteelUpgrades(List<Holder<DarkSteelUpgrade>> upgrades) implements DarkSteelUpgradeContainer {

    public static final ItemDarkSteelUpgrades EMPTY = new ItemDarkSteelUpgrades(List.of());

    public static final Codec<ItemDarkSteelUpgrades> CODEC = RecordCodecBuilder.<ItemDarkSteelUpgrades>create(
        inst -> inst.group(
            DarkSteelUpgrade.CODEC.sizeLimitedListOf(255).fieldOf("upgrades").forGetter(ItemDarkSteelUpgrades::upgrades)
        ).apply(inst, ItemDarkSteelUpgrades::new)
    ).promotePartial(error -> EnderIO.logger.error("Failed to load stored upgrades: {}", error));

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemDarkSteelUpgrades> STREAM_CODEC = StreamCodec.composite(
        DarkSteelUpgrade.STREAM_CODEC.apply(ByteBufCodecs.list(255)), ItemDarkSteelUpgrades::upgrades,
        ItemDarkSteelUpgrades::new
    );

    @SuppressWarnings("unchecked")
    public <T> Collection<T> getHooks(DarkSteelUpgradeHookType<T> type) {
        return upgrades.stream()
            .flatMap(upgradeHolder -> upgradeHolder.value().effects().stream())
            .filter(effect -> effect.getActiveHooks().contains(type))
            .map(effect -> (T)effect)
            // Ordered (upgrade application order) so hooks that pick the first match behave predictably.
            .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    /**
     * @return all effects of the given type, in the order the upgrades were applied (and then in effect definition order).
     */
    public <T> List<T> getAllEffectsOfType(Class<T> clazz) {
        return upgrades.stream()
            .flatMap(upgradeHolder -> upgradeHolder.value().effects().stream())
            .filter(clazz::isInstance)
            .map(clazz::cast)
            .toList();
    }
}

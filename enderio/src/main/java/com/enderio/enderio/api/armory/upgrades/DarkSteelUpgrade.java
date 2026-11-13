package com.enderio.enderio.api.armory.upgrades;

import com.enderio.enderio.api.EnderIORegistries;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Map;

public record DarkSteelUpgrade(Component name, Component description, UpgradeDefinition definition, HolderSet<DarkSteelUpgrade> exclusiveSet, List<DarkSteelUpgradeEffect> effects,
    Map<ResourceLocation, DarkSteelToggle> toggles) {
    public static final Codec<DarkSteelUpgrade> DIRECT_CODEC = RecordCodecBuilder.create(
        inst -> inst.group(
            ComponentSerialization.CODEC.fieldOf("name").forGetter(DarkSteelUpgrade::name),
            ComponentSerialization.CODEC.fieldOf("description").forGetter(DarkSteelUpgrade::description),
            DarkSteelUpgrade.UpgradeDefinition.CODEC.forGetter(DarkSteelUpgrade::definition),
            RegistryCodecs.homogeneousList(EnderIORegistries.Keys.DARK_STEEL_UPGRADE)
                .optionalFieldOf("exclusive_set", HolderSet.direct())
                .forGetter(DarkSteelUpgrade::exclusiveSet),
            DarkSteelUpgradeEffect.CODEC.sizeLimitedListOf(255).fieldOf("effects").forGetter(DarkSteelUpgrade::effects),
            Codec.unboundedMap(ResourceLocation.CODEC, DarkSteelToggle.CODEC).optionalFieldOf("toggles", Map.of()).forGetter(DarkSteelUpgrade::toggles)
        ).apply(inst, DarkSteelUpgrade::new)
    );

    public static final Codec<Holder<DarkSteelUpgrade>> CODEC = RegistryFixedCodec.create(EnderIORegistries.Keys.DARK_STEEL_UPGRADE);

    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<DarkSteelUpgrade>> STREAM_CODEC = ByteBufCodecs
        .holderRegistry(EnderIORegistries.Keys.DARK_STEEL_UPGRADE);

    public DarkSteelUpgrade(Component name, Component description, UpgradeDefinition definition, HolderSet<DarkSteelUpgrade> exclusiveSet,
        List<DarkSteelUpgradeEffect> effects) {
        this(name, description, definition, exclusiveSet, effects, Map.of());
    }

    public record UpgradeDefinition(HolderSet<Item> supportedItems, boolean requiresSocketedCapacitor) {
        public static final MapCodec<DarkSteelUpgrade.UpgradeDefinition> CODEC = RecordCodecBuilder.mapCodec(
            inst -> inst.group(
                RegistryCodecs.homogeneousList(Registries.ITEM).fieldOf("supported_items").forGetter(DarkSteelUpgrade.UpgradeDefinition::supportedItems),
                Codec.BOOL.fieldOf("requires_socketed_capacitor").forGetter(DarkSteelUpgrade.UpgradeDefinition::requiresSocketedCapacitor)
            ).apply(inst, DarkSteelUpgrade.UpgradeDefinition::new)
        );
    }
}

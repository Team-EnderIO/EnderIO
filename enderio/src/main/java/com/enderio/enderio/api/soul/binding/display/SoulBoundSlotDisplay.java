package com.enderio.enderio.api.soul.binding.display;

import com.enderio.enderio.api.soul.Soul;
import com.enderio.enderio.api.soul.SoulBoundUtils;
import com.enderio.enderio.foundation.util.EntityCaptureUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.registries.codec.RegistryCodecs;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.DisplayContentsFactory;
import net.minecraft.world.item.crafting.display.SlotDisplay;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public record SoulBoundSlotDisplay(HolderSet<EntityType<?>> entityTypes, SlotDisplay slotDisplay) implements SlotDisplay {

    // @formatter:off

    public static final MapCodec<SoulBoundSlotDisplay> MAP_CODEC = RecordCodecBuilder.mapCodec((inst) -> inst.group(
        RegistryCodecs.holderSet(Registries.ENTITY_TYPE).fieldOf("entity_types").forGetter(SoulBoundSlotDisplay::entityTypes),
        SlotDisplay.CODEC.fieldOf("slot_display").forGetter(SoulBoundSlotDisplay::slotDisplay)
    ).apply(inst, SoulBoundSlotDisplay::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, SoulBoundSlotDisplay> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.holderSet(Registries.ENTITY_TYPE), SoulBoundSlotDisplay::entityTypes,
        SlotDisplay.STREAM_CODEC, SoulBoundSlotDisplay::slotDisplay,
        SoulBoundSlotDisplay::new
    );

    public static final Type<SoulBoundSlotDisplay> TYPE = new Type<>(MAP_CODEC, STREAM_CODEC);

    // @formatter:on

    @Override
    public <T> Stream<T> resolve(ContextMap contextMap, DisplayContentsFactory<T> displayContentsFactory) {
        if (displayContentsFactory instanceof DisplayContentsFactory.ForStacks<T> stacks) {
            List<ItemStack> displayStacks = this.slotDisplay.resolveForStacks(contextMap);
            return displayStacks.stream().flatMap(stack -> entityTypes.stream()
                .filter(entityType -> EntityCaptureUtils.getCapturableStatus(entityType.value()) == EntityCaptureUtils.CapturableStatus.CAPTURABLE)
                .map((entityType -> SoulBoundUtils.tryBind(stack, Soul.of(entityType.value()))))
                .filter(Optional::isPresent)
                .map(stackOpt -> stacks.forStack(stackOpt.get())));
        }

        return Stream.of();
    }

    @Override
    public boolean isEnabled(FeatureFlagSet enabledFeatures) {
        return slotDisplay.isEnabled(enabledFeatures);
    }

    @Override
    public Type<? extends SlotDisplay> type() {
        return TYPE;
    }
}

package com.enderio.core.common.capability;

import com.enderio.api.attachment.StoredEntityData;
import com.enderio.api.filter.EntityFilter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Supplier;

public class EntityFilterCapability implements IFilterCapability<StoredEntityData>, EntityFilter {

    public static final Component EMPTY = new Component(List.of(), false, false);

    protected final Supplier<DataComponentType<Component>> componentType;
    private final ItemStack container;

    public EntityFilterCapability(Supplier<DataComponentType<Component>> componentType, ItemStack container) {
        this.componentType = componentType;
        this.container = container;
    }

    @Override
    public void setNbt(Boolean nbt) {
        container.set(componentType, getComponent().withNBT(nbt));
    }

    @Override
    public boolean isNbt() {
        return getComponent().nbt;
    }

    @Override
    public void setInverted(Boolean inverted) {
        container.set(componentType, getComponent().withInvert(inverted));
    }

    @Override
    public boolean isInvert() {
        return getComponent().invert;
    }

    @Override
    public List<StoredEntityData> getEntries() {
        return getComponent().entities;
    }

    @Override
    public void setEntry(int index, StoredEntityData entry) {
        container.set(componentType, getComponent().withEntities(index, entry));
    }

    @NotNull
    private Component getComponent() {
        return container.getOrDefault(componentType, EMPTY);
    }

    @Override
    public boolean test(Entity entity) {
        boolean typematch = test(entity.getType());
        if (isNbt()) {
            for (StoredEntityData entry : getEntries()) {
                CompoundTag tag = entity.serializeNBT(entity.level().registryAccess());
                boolean test = tag.equals(entry.getEntityTag());
                if (test) {
                    return !isInvert() && typematch;
                }
            }
        }

        return typematch;
    }

    @Override
    public boolean test(EntityType<?> entity) {
        for (StoredEntityData entry : getEntries()) {
            ResourceLocation key = BuiltInRegistries.ENTITY_TYPE.getKey(entity);
            if (entry.hasEntity() && entry.entityType().get().equals(key)) {
                return !isInvert();
            }
        }
        return isInvert();
    }

    public record Component(List<StoredEntityData> entities, boolean nbt, boolean invert) {
        public static Codec<Component> CODEC = RecordCodecBuilder.create(componentInstance -> componentInstance
            .group(Component.Slot.CODEC.sizeLimitedListOf(256).fieldOf("entities").xmap(Component::fromList, Component::fromEntities).forGetter(Component::entities),
                Codec.BOOL.fieldOf("nbt").forGetter(Component::nbt), Codec.BOOL.fieldOf("nbt").forGetter(Component::invert))
            .apply(componentInstance, Component::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Component> STREAM_CODEC = StreamCodec.composite(
            StoredEntityData.STREAM_CODEC.apply(ByteBufCodecs.list(256)),
            Component::entities,
            ByteBufCodecs.BOOL,
            Component::nbt,
            ByteBufCodecs.BOOL,
            Component::invert,
            Component::new);

        public Component(int size) {
            this(NonNullList.withSize(size, StoredEntityData.EMPTY), false, false);
        }

        private static List<Component.Slot> fromEntities(List<StoredEntityData> fluids) {
            List<Component.Slot> slots = new ArrayList<>();
            for (int i = 0; i < fluids.size(); i++) {
                slots.add(new Component.Slot(i, fluids.get(i)));
            }
            return slots;
        }

        private static List<StoredEntityData> fromList(List<Component.Slot> slots) {
            OptionalInt optionalint = slots.stream().mapToInt(Component.Slot::index).max();
            if (optionalint.isEmpty()) {
                return List.of();
            }
            List<StoredEntityData> entities = NonNullList.withSize(optionalint.getAsInt() + 1, StoredEntityData.EMPTY);
            for (Component.Slot slot : slots) {
                entities.set(slot.index, slot.entity);
            }
            return entities;
        }

        public Component withNBT(Boolean nbt){
            return new Component(entities, nbt, invert);
        }

        public Component withInvert(Boolean invert){
            return new Component(entities, nbt, invert);
        }

        public Component withEntities(int pSlotId, StoredEntityData entry) {
            List<StoredEntityData> newEntities = new ArrayList<>();
            entities.forEach(f -> newEntities.add(new StoredEntityData(f.entityTag(), f.maxHealth())));
            newEntities.set(pSlotId, entry);
            return new Component(newEntities, nbt, invert);
        }

        public record Slot(int index, StoredEntityData entity) {
            public static final Codec<Component.Slot> CODEC = RecordCodecBuilder.create(
                p_331695_ -> p_331695_.group(
                        Codec.intRange(0, 255).fieldOf("slot").forGetter(Component.Slot::index),
                        StoredEntityData.CODEC.fieldOf("entity").forGetter(Component.Slot::entity)
                    )
                    .apply(p_331695_, Component.Slot::new)
            );
        }
    }
}

package com.enderio.base.common.capability;

import com.enderio.base.api.attachment.StoredEntityData;
import com.enderio.base.api.filter.EntityFilter;
import com.enderio.core.common.serialization.OrderedListCodec;
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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
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
    public int size() {
        return getEntries().size();
    }

    @Override
    public List<StoredEntityData> getEntries() {
        return getComponent().entities;
    }

    @Override
    public StoredEntityData getEntry(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException(index);
        }

        var entries = getEntries();
        if (index >= entries.size()) {
            return StoredEntityData.EMPTY;
        }

        return entries.get(index);
    }

    @Override
    public void setEntry(int index, StoredEntityData entry) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException(index);
        }

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

    public record Component(int size, List<StoredEntityData> entities, boolean nbt, boolean invert) {
        public static Codec<Component> NEW_CODEC = RecordCodecBuilder.create(
            componentInstance -> componentInstance
                .group(
                    Codec.INT.fieldOf("size").forGetter(Component::size),
                    OrderedListCodec.create(256, StoredEntityData.CODEC, StoredEntityData.EMPTY)
                        .fieldOf("entities")
                        .forGetter(Component::entities),
                    Codec.BOOL.fieldOf("nbt").forGetter(Component::nbt), Codec.BOOL.fieldOf("isInvert").forGetter(Component::invert))
                .apply(componentInstance, Component::new));

        // TODO: Remove in Ender IO 8
        // The Codec used up to and including v7.0.7-alpha
        public static Codec<Component> LEGACY_CODEC = RecordCodecBuilder.create(componentInstance -> componentInstance
            .group(Slot.CODEC.sizeLimitedListOf(256).fieldOf("entities").xmap(Component::fromList, Component::fromEntities).forGetter(Component::entities),
                Codec.BOOL.fieldOf("nbt").forGetter(Component::nbt), Codec.BOOL.fieldOf("nbt").forGetter(Component::invert))
            .apply(componentInstance, Component::new));

        public static final Codec<Component> CODEC = Codec.withAlternative(NEW_CODEC, LEGACY_CODEC);

        public static final StreamCodec<RegistryFriendlyByteBuf, Component> STREAM_CODEC = StreamCodec.composite(
            StoredEntityData.STREAM_CODEC.apply(ByteBufCodecs.list(256)),
            Component::entities,
            ByteBufCodecs.BOOL,
            Component::nbt,
            ByteBufCodecs.BOOL,
            Component::invert,
            Component::new);

        public Component(int size) {
            this(size, NonNullList.withSize(size, StoredEntityData.EMPTY), false, false);
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

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }

            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            Component component = (Component) o;
            for (int i = 0; i < entities.size(); i++) {
                if (!entities.get(i).equals(component.entities.get(i))) {
                    return false;
                }
            }
            return nbt == component.nbt && invert == component.invert;
        }

        @Override
        public int hashCode() {
            return Objects.hash(hashList(entities), nbt, invert);
        }

        public static int hashList(List<StoredEntityData> list) {
            int i = 0;

            StoredEntityData stack;
            for(Iterator<StoredEntityData> var2 = list.iterator(); var2.hasNext(); i = i * 31 + stack.hashCode()) {
                stack = var2.next();
            }

            return i;
        }

        // TODO: Remove in Ender IO 8
        // region Legacy Serialization

        // Used for legacy loading only.
        @Deprecated(since = "7.0.8-alpha")
        private Component(List<StoredEntityData> entities, boolean nbt, boolean invert) {
            this(entities.size(), entities, nbt, invert);
        }

        private static List<Slot> fromEntities(List<StoredEntityData> fluids) {
            List<Slot> slots = new ArrayList<>();
            for (int i = 0; i < fluids.size(); i++) {
                slots.add(new Slot(i, fluids.get(i)));
            }
            return slots;
        }

        private static List<StoredEntityData> fromList(List<Slot> slots) {
            OptionalInt optionalint = slots.stream().mapToInt(Slot::index).max();
            if (optionalint.isEmpty()) {
                return List.of();
            }
            List<StoredEntityData> entities = NonNullList.withSize(optionalint.getAsInt() + 1, StoredEntityData.EMPTY);
            for (Slot slot : slots) {
                entities.set(slot.index, slot.entity);
            }
            return entities;
        }

        public record Slot(int index, StoredEntityData entity) {
            public static final Codec<Slot> CODEC = RecordCodecBuilder.create(
                p_331695_ -> p_331695_.group(
                        Codec.intRange(0, 255).fieldOf("slot").forGetter(Slot::index),
                        StoredEntityData.CODEC.fieldOf("entity").forGetter(Slot::entity)
                    )
                    .apply(p_331695_, Slot::new)
            );
        }

        // endregion
    }
}

package com.enderio.base.common.capability;

import com.enderio.base.api.filter.ItemStackFilter;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Supplier;

public class ItemFilterCapability implements IFilterCapability<ItemStack>, ItemStackFilter {
    public static final Component EMPTY = new Component(List.of(), false, false);

    protected final Supplier<DataComponentType<Component>> componentType;
    private final ItemStack container;

    public ItemFilterCapability(Supplier<DataComponentType<Component>> componentType, ItemStack container) {
        this.componentType = componentType;
        this.container = container;
    }

    @Override
    public List<ItemStack> getEntries() {
        return getComponent().items().stream().map(ItemStack::copy).toList();
    }

    @NotNull
    private ItemFilterCapability.Component getComponent() {
        return container.getOrDefault(componentType, EMPTY);
    }

    @Override
    public void setEntry(int pSlotId, ItemStack stack) {
        container.set(componentType, getComponent().withItem(pSlotId, stack));
    }

    public void setNbt(Boolean nbt) {
        container.set(componentType, getComponent().withNBT(nbt));
    }

    public boolean isNbt() {
        return getComponent().nbt();
    }

    public void setInverted(Boolean inverted) {
        container.set(componentType, getComponent().withInvert(inverted));
    }

    public boolean isInvert() {
        return getComponent().invert();
    }

    @Override
    public boolean test(ItemStack stack) {
        for (ItemStack testStack : getEntries()) {
            boolean test = isNbt() ? ItemStack.isSameItemSameComponents(testStack, stack) : ItemStack.isSameItem(testStack, stack);
            if (test) {
                return !isInvert();
            }
        }
        return isInvert();
    }

    public record Component(List<ItemStack> items, boolean nbt, boolean invert) {
        public static Codec<Component> CODEC = RecordCodecBuilder.create(componentInstance -> componentInstance
                .group(Slot.CODEC.sizeLimitedListOf(256).fieldOf("items").xmap(Component::fromList, Component::fromitems).forGetter(Component::items),
                        Codec.BOOL.fieldOf("nbt").forGetter(Component::nbt), Codec.BOOL.fieldOf("nbt").forGetter(Component::invert))
                .apply(componentInstance, Component::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Component> STREAM_CODEC = StreamCodec.composite(ItemStack.OPTIONAL_STREAM_CODEC
                        .apply(ByteBufCodecs.list(256)), Component::items, ByteBufCodecs.BOOL, Component::nbt,
                ByteBufCodecs.BOOL, Component::invert, Component::new);

        public Component(int size) {
            this(NonNullList.withSize(size, ItemStack.EMPTY), false, false);
        }

        private static List<Slot> fromitems(List<ItemStack> items) {
            List<Slot> slots = new ArrayList<>();
            for (int i = 0; i < items.size(); i++) {
                slots.add(new Slot(i, items.get(i)));
            }
            return slots;
        }

        private static List<ItemStack> fromList(List<Slot> slots) {
            OptionalInt optionalint = slots.stream().mapToInt(Slot::index).max();
            if (optionalint.isEmpty()) {
                return List.of();
            }
            List<ItemStack> items = NonNullList.withSize(optionalint.getAsInt() + 1, ItemStack.EMPTY);
            for (Slot slot : slots) {
                items.set(slot.index, slot.item);
            }
            return items;
        }

        public Component withNBT(Boolean nbt){
            return new Component(items, nbt, invert);
        }

        public Component withInvert(Boolean invert){
            return new Component(items, nbt, invert);
        }

        public Component withItem(int pSlotId, ItemStack entry) {
            List<ItemStack> newItems = new ArrayList<>();
            items.forEach(f -> newItems.add(f.copy()));
            newItems.set(pSlotId, entry);
            return new Component(newItems, nbt, invert);
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
            for (int i = 0; i < items.size(); i++) {
                if (!ItemStack.matches(items.get(i), component.items.get(i))) {
                    return false;
                }
            }

            return nbt == component.nbt && invert == component.invert;
        }

        @Override
        public int hashCode() {
            return Objects.hash(items.hashCode(), nbt, invert);
        }

        public record Slot(int index, ItemStack item) {
            public static final Codec<Slot> CODEC = RecordCodecBuilder.create(
                    p_331695_ -> p_331695_.group(
                                    Codec.intRange(0, 255).fieldOf("slot").forGetter(Slot::index),
                                    ItemStack.OPTIONAL_CODEC.fieldOf("item").forGetter(Slot::item)
                            )
                            .apply(p_331695_, Slot::new)
            );
        }
    }
}

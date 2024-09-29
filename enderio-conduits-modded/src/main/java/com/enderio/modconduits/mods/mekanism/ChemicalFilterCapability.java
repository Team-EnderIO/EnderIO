package com.enderio.modconduits.mods.mekanism;

import com.enderio.base.common.capability.IFilterCapability;
import com.enderio.core.common.serialization.OrderedListCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Supplier;

public class ChemicalFilterCapability implements IFilterCapability<ChemicalStack>, ChemicalFilter {

    public static final Component EMPTY = new Component(List.of(), false);

    private final Supplier<DataComponentType<Component>> componentType;
    private final ItemStack container;

    public ChemicalFilterCapability(Supplier<DataComponentType<Component>> componentType, ItemStack container) {
        this.componentType = componentType;
        this.container = container;
    }

    public Component getComponent() {
        return this.container.getOrDefault(componentType, EMPTY);
    }

    @Override
    public void setNbt(Boolean nbt) {
    }

    @Override
    public boolean isNbt() {
        return false;
    }

    @Override
    public void setInverted(Boolean inverted) {
        this.container.set(componentType, getComponent().withInvert(inverted));
    }

    @Override
    public boolean isInvert() {
        return getComponent().invert;
    }

    @Override
    public int size() {
        return getComponent().size();
    }

    @Override
    public List<ChemicalStack> getEntries() {
        Component data = getComponent();

        return data.chemicals().stream()
            .map(ChemicalStack::copy)
            .limit(data.size())
            .toList();
    }

    @Override
    public ChemicalStack getEntry(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException(index);
        }

        var entries = getEntries();
        if (index >= entries.size()) {
            return ChemicalStack.EMPTY;
        }

        return entries.get(index);
    }

    @Override
    public void setEntry(int index, ChemicalStack entry) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException(index);
        }

        this.container.set(componentType, getComponent().withChemicals(index, entry));
    }

    @Override
    public boolean test(ChemicalStack stack) {
        List<ChemicalStack> entries = getEntries();

        for (int i = 0; i < entries.size() && i < size(); i++) {
            var testStack = entries.get(i);
            if (ChemicalStack.isSameChemical(testStack, stack)) {
                return !isInvert();
            }
        }

        return isInvert();
    }

    public record Component(int size, List<ChemicalStack> chemicals, boolean invert) {

        private static final Codec<Component> NEW_CODEC = RecordCodecBuilder.create(
            componentInstance -> componentInstance
                .group(
                    Codec.INT.fieldOf("size").forGetter(Component::size),
                    OrderedListCodec.create(256, ChemicalStack.OPTIONAL_CODEC, ChemicalStack.EMPTY)
                        .fieldOf("chemicals")
                        .forGetter(Component::chemicals),
                    Codec.BOOL.fieldOf("isInvert").forGetter(Component::invert))
                .apply(componentInstance, Component::new));

        // TODO: Remove in Ender IO 8
        // The Codec used up to and including v7.0.2-alpha
        private static final Codec<Component> LEGACY_CODEC = RecordCodecBuilder.create(componentInstance -> componentInstance
            .group(Component.Slot.CODEC.sizeLimitedListOf(256).fieldOf("chemicals").xmap(Component::fromList, Component::fromChemicals).forGetter(
                    Component::chemicals),
                Codec.BOOL.fieldOf("nbt").forGetter(Component::invert))
            .apply(componentInstance, Component::new));

        public static final Codec<Component> CODEC = Codec.withAlternative(NEW_CODEC, LEGACY_CODEC);

        public static final StreamCodec<RegistryFriendlyByteBuf, Component> STREAM_CODEC = StreamCodec.composite(
            ChemicalStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(256)),
            Component::chemicals,
            ByteBufCodecs.BOOL,
            Component::invert,
            Component::new);

        public Component(int size) {
            this(size, NonNullList.withSize(size, ChemicalStack.EMPTY), false);
        }

        public Component withInvert(boolean invert) {
            return new Component(size, this.chemicals, invert);
        }

        public Component withChemicals(int pSlotId, ChemicalStack entry) {
            List<ChemicalStack> newChemicals = new ArrayList<>();
            chemicals.forEach(f -> newChemicals.add(f.copy()));
            newChemicals.set(pSlotId, entry);
            return new Component(size, newChemicals, invert);
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
            for (int i = 0; i < chemicals.size(); i++) {
                if (!chemicals.get(i).equals(component.chemicals.get(i))) {
                    return false;
                }
            }
            return invert == component.invert;
        }

        @Override
        public int hashCode() {
            return Objects.hash(hashStackList(chemicals), invert);
        }

        public static int hashStackList(List<ChemicalStack> list) {
            int i = 0;

            ChemicalStack stack;
            for(Iterator<ChemicalStack> var2 = list.iterator(); var2.hasNext(); i = i * 31 + stack.hashCode()) {
                stack = var2.next();
            }

            return i;
        }

        // TODO: Remove in Ender IO 8
        // region Legacy Serialization

        // Used for legacy loading only.
        @Deprecated(since = "7.0.3-alpha")
        private Component(List<ChemicalStack> chemicals, boolean invert) {
            this(chemicals.size(), chemicals, invert);
        }

        private static List<Component.Slot> fromChemicals(List<ChemicalStack> chemicals) {
            List<Component.Slot> slots = new ArrayList<>();
            for (int i = 0; i < chemicals.size(); i++) {
                slots.add(new Component.Slot(i, chemicals.get(i)));
            }
            return slots;
        }

        private static List<ChemicalStack> fromList(List<Component.Slot> slots) {
            OptionalInt optionalint = slots.stream().mapToInt(Component.Slot::index).max();
            if (optionalint.isEmpty()) {
                return List.of();
            }
            List<ChemicalStack> chemicals = NonNullList.withSize(optionalint.getAsInt() + 1, ChemicalStack.EMPTY);
            for (Component.Slot slot : slots) {
                chemicals.set(slot.index, slot.chemical);
            }
            return chemicals;
        }

        public record Slot(int index, ChemicalStack chemical) {
            public static final Codec<Component.Slot> CODEC = RecordCodecBuilder.create(
                p_331695_ -> p_331695_.group(
                        Codec.intRange(0, 255).fieldOf("slot").forGetter(Component.Slot::index),
                        ChemicalStack.OPTIONAL_CODEC.fieldOf("fluid").forGetter(Component.Slot::chemical)
                    )
                    .apply(p_331695_, Component.Slot::new)
            );
        }

        // endregion
    }
}

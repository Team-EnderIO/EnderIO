package com.enderio.base.common.capability;

import com.enderio.base.api.filter.FluidStackFilter;
import com.enderio.core.common.serialization.OrderedListCodec;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Supplier;

public class FluidFilterCapability implements IFilterCapability<FluidStack>, FluidStackFilter {

    public static final Component EMPTY = new Component(0, List.of(), false, false);

    protected final Supplier<DataComponentType<Component>> componentType;
    private final ItemStack container;

    public FluidFilterCapability(Supplier<DataComponentType<Component>> componentType, ItemStack container) {
        this.componentType = componentType;
        this.container = container;
    }

    @NotNull
    private Component getComponent() {
        return container.getOrDefault(componentType, EMPTY);
    }

    @Override
    public void setNbt(Boolean nbt) {
        container.set(componentType, this.getComponent().withNBT(nbt));
    }

    @Override
    public boolean isNbt() {
        return getComponent().nbt();
    }

    @Override
    public void setInverted(Boolean inverted) {
        container.set(componentType, this.getComponent().withInvert(inverted));
    }

    @Override
    public boolean isInvert() {
        return getComponent().invert();
    }

    @Override
    public int size() {
        return getComponent().size();
    }

    @Override
    public List<FluidStack> getEntries() {
        Component data = getComponent();

        return data.fluids().stream()
            .map(FluidStack::copy)
            .limit(data.size())
            .toList();
    }

    @Override
    public FluidStack getEntry(int index) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException(index);
        }

        var entries = getEntries();
        if (index >= entries.size()) {
            return FluidStack.EMPTY;
        }

        return entries.get(index);
    }

    @Override
    public void setEntry(int index, FluidStack entry) {
        if (index < 0 || index >= size()) {
            throw new IndexOutOfBoundsException(index);
        }

        container.set(componentType, this.getComponent().withFluid(index, entry));
    }

    @Override
    public boolean test(FluidStack stack) {
        List<FluidStack> entries = getEntries();

        for (int i = 0; i < entries.size() && i < size(); i++) {
            var testStack = entries.get(i);
            boolean isMatch = isNbt()
                ? FluidStack.isSameFluidSameComponents(testStack, stack)
                : testStack.is(stack.getFluid());

            if (isMatch) {
                return !isInvert();
            }
        }

        return isInvert();
    }

    /**
     * @apiNote Direct accessors of this Component must check both the {@link Component#size()} and the size of {@link Component#fluids()} as they may differ.
     * @param size
     * @param fluids
     * @param nbt
     * @param invert
     */
    public record Component(int size, List<FluidStack> fluids, boolean nbt, boolean invert) {
        private static final Codec<Component> NEW_CODEC = RecordCodecBuilder.create(
            componentInstance -> componentInstance
                .group(
                    Codec.INT.fieldOf("size").forGetter(Component::size),
                    OrderedListCodec.create(256, FluidStack.OPTIONAL_CODEC, FluidStack.EMPTY)
                        .fieldOf("fluids")
                        .forGetter(Component::fluids),
                    Codec.BOOL.fieldOf("isNbt").forGetter(Component::nbt),
                    Codec.BOOL.fieldOf("isInvert").forGetter(Component::invert))
                .apply(componentInstance, Component::new));

        // TODO: Remove in Ender IO 8
        // The Codec used up to and including v7.0.2-alpha
        private static final Codec<Component> LEGACY_CODEC = RecordCodecBuilder.create(
            componentInstance -> componentInstance
                .group(
                    Slot.CODEC.sizeLimitedListOf(256)
                        .fieldOf("fluids")
                        .xmap(Component::fromList, Component::fromFluids)
                        .forGetter(Component::fluids),
                    Codec.BOOL.fieldOf("nbt").forGetter(Component::nbt),
                    Codec.BOOL.fieldOf("nbt").forGetter(Component::invert))
                .apply(componentInstance, Component::new));

        public static final Codec<Component> CODEC = Codec.withAlternative(NEW_CODEC, LEGACY_CODEC);

        public static final StreamCodec<RegistryFriendlyByteBuf, Component> STREAM_CODEC = StreamCodec.composite(
            FluidStack.OPTIONAL_STREAM_CODEC.apply(ByteBufCodecs.list(256)),
            Component::fluids,
            ByteBufCodecs.BOOL,
            Component::nbt,
            ByteBufCodecs.BOOL,
            Component::invert,
            Component::new);

        public Component(int maxSize) {
            this(maxSize, NonNullList.withSize(maxSize, FluidStack.EMPTY), false, false);
        }

        public Component withNBT(Boolean nbt){
            return new Component(size, fluids, nbt, invert);
        }

        public Component withInvert(Boolean invert){
            return new Component(size, fluids, nbt, invert);
        }

        public Component withFluid(int index, FluidStack entry) {
            List<FluidStack> newFluids = new ArrayList<>(size());
            fluids.forEach(f -> newFluids.add(f.copy()));
            newFluids.set(index, entry);
            return new Component(size, newFluids, nbt, invert);
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

            if (nbt != component.nbt() || invert != component.invert()) {
                return false;
            }

            for (int i = 0; i < size() && i < fluids.size(); i++) {
                if (!FluidStack.matches(fluids.get(i), component.fluids.get(i))) {
                    return false;
                }
            }

            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(hashStackList(fluids), nbt, invert);
        }

        public static int hashStackList(List<FluidStack> list) {
            int i = 0;

            FluidStack stack;
            for(Iterator<FluidStack> var2 = list.iterator(); var2.hasNext(); i = i * 31 + FluidStack.hashFluidAndComponents(stack)) {
                stack = var2.next();
            }

            return i;
        }

        // TODO: Remove in Ender IO 8
        // region Legacy Serialization

        // Used for legacy loading only.
        @Deprecated(since = "7.0.3-alpha")
        private Component(List<FluidStack> fluids, boolean nbt, boolean invert) {
            this(fluids.size(), fluids, nbt, invert);
        }

        @Deprecated(since = "7.0.3-alpha")
        private static List<Slot> fromFluids(List<FluidStack> fluids) {
            List<Slot> slots = new ArrayList<>();
            for (int i = 0; i < fluids.size(); i++) {
                slots.add(new Slot(i, fluids.get(i)));
            }
            return slots;
        }

        @Deprecated(since = "7.0.3-alpha")
        private static List<FluidStack> fromList(List<Slot> slots) {
            OptionalInt optionalint = slots.stream().mapToInt(Slot::index).max();
            if (optionalint.isEmpty()) {
                return List.of();
            }
            List<FluidStack> fluids = NonNullList.withSize(optionalint.getAsInt() + 1, FluidStack.EMPTY);
            for (Slot slot : slots) {
                fluids.set(slot.index, slot.fluid);
            }
            return fluids;
        }

        @Deprecated(since = "7.0.3-alpha")
        public record Slot(int index, FluidStack fluid) {
            public static final Codec<Slot> CODEC = RecordCodecBuilder.create(
                p_331695_ -> p_331695_.group(
                        Codec.intRange(0, 255).fieldOf("slot").forGetter(Slot::index),
                        FluidStack.OPTIONAL_CODEC.fieldOf("fluid").forGetter(Slot::fluid)
                    )
                    .apply(p_331695_, Slot::new)
            );
        }

        // endregion
    }
}

package com.enderio.core.common.capability;

import com.enderio.core.common.menu.FluidFilterSlot;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.function.Supplier;

public class FluidFilterCapability implements IFilterCapability<FluidStack> {

    public static final Component EMPTY = new Component(List.of(), false, false);

    protected final Supplier<DataComponentType<Component>> componentType;
    private final ItemStack container;

    public FluidFilterCapability(Supplier<DataComponentType<Component>> componentType, ItemStack container) {
        this.componentType = componentType;
        this.container = container;
    }

    @Override
    public void setNbt(Boolean nbt) {
        container.set(componentType, this.getComponent().withNBT(nbt));
    }

    @Override
    public boolean isNbt() {
        return getComponent().nbt();
    }

    @NotNull
    private Component getComponent() {
        return container.getOrDefault(componentType, EMPTY);
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
    public List<FluidStack> getEntries() {
        return getComponent().fluids.stream().map(FluidStack::copy).toList();
    }

    @Override
    public void setEntry(int pSlotId, FluidStack entry) {
        container.set(componentType, this.getComponent().withFluid(pSlotId, entry));
    }

    @Override
    public Slot getSlot(int pSlot, int pX, int pY) {
        return new FluidFilterSlot(fluidStack -> setEntry(pSlot, fluidStack), pSlot, pX, pY);
    }

    @Override
    public boolean test(FluidStack stack) {
        for (FluidStack testStack : getEntries()) {
            boolean test = isNbt() ? FluidStack.isSameFluidSameComponents(testStack, stack) : testStack.is(stack.getFluid());
            if (test) {
                return !isInvert();
            }
        }
        return isInvert();
    }

    public record Component(List<FluidStack> fluids, boolean nbt, boolean invert) {
        public static Codec<Component> CODEC = RecordCodecBuilder.create(componentInstance -> componentInstance
            .group(Slot.CODEC.sizeLimitedListOf(256).fieldOf("fluids").xmap(Component::fromList, Component::fromFluids).forGetter(Component::fluids),
                Codec.BOOL.fieldOf("nbt").forGetter(Component::nbt), Codec.BOOL.fieldOf("nbt").forGetter(Component::invert))
            .apply(componentInstance, Component::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Component> STREAM_CODEC = StreamCodec.composite(FluidStack.OPTIONAL_STREAM_CODEC
                .apply(ByteBufCodecs.list(256)), Component::fluids, ByteBufCodecs.BOOL, Component::nbt,
                ByteBufCodecs.BOOL, Component::invert, Component::new);

        public Component(int size) {
            this(NonNullList.withSize(size, FluidStack.EMPTY), false, false);
        }

        private static List<Slot> fromFluids(List<FluidStack> fluids) {
            List<Slot> slots = new ArrayList<>();
            for (int i = 0; i < fluids.size(); i++) {
                slots.add(new Component.Slot(i, fluids.get(i)));
            }
            return slots;
        }

        private static List<FluidStack> fromList(List<Component.Slot> slots) {
            OptionalInt optionalint = slots.stream().mapToInt(Component.Slot::index).max();
            if (optionalint.isEmpty()) {
                return List.of();
            }
            List<FluidStack> fluids = NonNullList.withSize(optionalint.getAsInt() + 1, FluidStack.EMPTY);
            for (Slot slot : slots) {
                fluids.set(slot.index, slot.fluid);
            }
            return fluids;
        }

        public Component withNBT(Boolean nbt){
            return new Component(fluids, nbt, invert);
        }

        public Component withInvert(Boolean invert){
            return new Component(fluids, nbt, invert);
        }

        public Component withFluid(int pSlotId, FluidStack entry) {
            List<FluidStack> newFluids = new ArrayList<>();
            fluids.forEach(f -> newFluids.add(f.copy()));
            newFluids.set(pSlotId, entry);
            return new Component(newFluids, nbt, invert);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            FluidFilterCapability.Component component = (FluidFilterCapability.Component) o;
            for (int i = 0; i < fluids.size(); i++) {
                if (!FluidStack.matches(fluids.get(i), component.fluids.get(i))) {
                    return false;
                }
            }
            return nbt == component.nbt && invert == component.invert;
        }

        @Override
        public int hashCode() {
            return Objects.hash(fluids.hashCode(), nbt, invert);
        }

        public record Slot(int index, FluidStack fluid) {
            public static final Codec<Slot> CODEC = RecordCodecBuilder.create(
                p_331695_ -> p_331695_.group(
                        Codec.intRange(0, 255).fieldOf("slot").forGetter(Slot::index),
                        FluidStack.OPTIONAL_CODEC.fieldOf("fluid").forGetter(Slot::fluid)
                    )
                    .apply(p_331695_, Slot::new)
            );
        }
    }
}

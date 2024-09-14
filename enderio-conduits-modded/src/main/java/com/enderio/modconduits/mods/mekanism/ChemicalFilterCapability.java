package com.enderio.modconduits.mods.mekanism;

import com.enderio.base.common.capability.IFilterCapability;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.fluids.FluidStack;

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
    public List<ChemicalStack> getEntries() {
        return getComponent().chemicals;
    }

    @Override
    public void setEntry(int index, ChemicalStack entry) {
        this.container.set(componentType, getComponent().withChemicals(index, entry));
    }


    @Override
    public boolean test(ChemicalStack boxedChemicalStack) {
        for (ChemicalStack stack : getEntries()) {
            if (ChemicalStack.isSameChemical(stack, boxedChemicalStack)) {
                return !isInvert();
            }
        }
        return isInvert();
    }

    public record Component(List<ChemicalStack> chemicals, boolean invert) {
        public static Codec<Component> CODEC = RecordCodecBuilder.create(componentInstance -> componentInstance
            .group(Component.Slot.CODEC.sizeLimitedListOf(256).fieldOf("chemicals").xmap(Component::fromList, Component::fromChemicals).forGetter(
                    Component::chemicals),
                Codec.BOOL.fieldOf("nbt").forGetter(Component::invert))
            .apply(componentInstance, Component::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, Component> STREAM_CODEC = StreamCodec.composite(ChemicalStack.OPTIONAL_STREAM_CODEC
                .apply(ByteBufCodecs.list(256)), Component::chemicals,
            ByteBufCodecs.BOOL, Component::invert, Component::new);

        public Component(int size) {
            this(NonNullList.withSize(size, ChemicalStack.EMPTY), false);
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

        public Component withInvert(boolean invert) {
            return new Component(this.chemicals, invert);
        }

        public Component withChemicals(int pSlotId, ChemicalStack entry) {
            List<ChemicalStack> newChemicals = new ArrayList<>();
            chemicals.forEach(f -> newChemicals.add(f.copy()));
            newChemicals.set(pSlotId, entry);
            return new Component(newChemicals, invert);
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

        public record Slot(int index, ChemicalStack chemical) {
            public static final Codec<Component.Slot> CODEC = RecordCodecBuilder.create(
                p_331695_ -> p_331695_.group(
                        Codec.intRange(0, 255).fieldOf("slot").forGetter(Component.Slot::index),
                        ChemicalStack.OPTIONAL_CODEC.fieldOf("fluid").forGetter(Component.Slot::chemical)
                    )
                    .apply(p_331695_, Component.Slot::new)
            );
        }
    }

}

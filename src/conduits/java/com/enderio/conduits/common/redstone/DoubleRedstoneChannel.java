package com.enderio.conduits.common.redstone;

import com.enderio.api.misc.ColorControl;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class DoubleRedstoneChannel {

    public static final Component INSTANCE = new Component(ColorControl.GREEN, ColorControl.BROWN);

    private final Supplier<DataComponentType<Component>> componentType;
    private final ItemStack stack;

    public DoubleRedstoneChannel(ItemStack stack, Supplier<DataComponentType<Component>> componentType) {
        this.stack = stack;
        this.componentType = componentType;
    }

    public ColorControl getFirstChannel() {
        return stack.get(componentType.get()).channel1();
    }

    public ColorControl getSecondChannel() {
        return stack.get(componentType.get()).channel2();
    }

    public void setFirstChannel(ColorControl channel1) {
        stack.set(componentType.get(), new Component(channel1, getSecondChannel()));
    }

    public void setSecondChannel(ColorControl channel2) {
        stack.set(componentType.get(), new Component(getFirstChannel(), channel2));
    }

    public void setChannels(ColorControl channel1, ColorControl channel2) {
        stack.set(componentType.get(), new Component(channel1, channel2));
    }

    public record Component(ColorControl channel1, ColorControl channel2) {

        public static final Codec<Component> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(ColorControl.CODEC.fieldOf("channel1").forGetter(Component::channel1),
                    ColorControl.CODEC.fieldOf("channel2").forGetter(Component::channel2))
                .apply(instance, Component::new)
        );

        public static final StreamCodec<ByteBuf, Component> STREAM_CODEC = StreamCodec.composite(
            ColorControl.STREAM_CODEC,
            Component::channel1,
            ColorControl.STREAM_CODEC,
            Component::channel2,
            Component::new
        );

    }
}

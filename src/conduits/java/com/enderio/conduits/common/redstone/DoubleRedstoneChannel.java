package com.enderio.conduits.common.redstone;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class DoubleRedstoneChannel {

    public static final Component INSTANCE = new Component(DyeColor.GREEN, DyeColor.BROWN);

    private final Supplier<DataComponentType<Component>> componentType;
    private final ItemStack stack;

    public DoubleRedstoneChannel(ItemStack stack, Supplier<DataComponentType<Component>> componentType) {
        this.stack = stack;
        this.componentType = componentType;
    }

    public DyeColor getFirstChannel() {
        return stack.get(componentType.get()).channel1();
    }

    public DyeColor getSecondChannel() {
        return stack.get(componentType.get()).channel2();
    }

    public void setFirstChannel(DyeColor channel1) {
        stack.set(componentType.get(), new Component(channel1, getSecondChannel()));
    }

    public void setSecondChannel(DyeColor channel2) {
        stack.set(componentType.get(), new Component(getFirstChannel(), channel2));
    }

    public void setChannels(DyeColor channel1, DyeColor channel2) {
        stack.set(componentType.get(), new Component(channel1, channel2));
    }

    public record Component(DyeColor channel1, DyeColor channel2) {

        public static final Codec<Component> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(DyeColor.CODEC.fieldOf("channel1").forGetter(Component::channel1),
                    DyeColor.CODEC.fieldOf("channel2").forGetter(Component::channel2))
                .apply(instance, Component::new)
        );

        public static final StreamCodec<ByteBuf, Component> STREAM_CODEC = StreamCodec.composite(
            DyeColor.STREAM_CODEC,
            Component::channel1,
            DyeColor.STREAM_CODEC,
            Component::channel2,
            Component::new
        );

    }
}

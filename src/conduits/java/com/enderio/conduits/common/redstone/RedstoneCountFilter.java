package com.enderio.conduits.common.redstone;

import com.enderio.api.misc.ColorControl;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneExtendedData;
import com.enderio.conduits.common.init.ConduitComponents;
import com.enderio.conduits.common.network.CountFilterPacket;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;

public class RedstoneCountFilter implements RedstoneInsertFilter {

    public static Component INSTANCE = new Component(ColorControl.GREEN, 8, 0, false);
    private final ItemStack stack;

    public RedstoneCountFilter(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getOutputSignal(RedstoneExtendedData data, ColorControl control) {
        ColorControl channel = getChannel();
        int maxCount = getMaxCount();
        boolean lastActive = isLastActive();
        int count = getCount();
        if (lastActive !=  data.isActive(channel)) {
            lastActive = !lastActive;
            setLastActive(lastActive);
            if (data.isActive(channel)) {
                count = (count + 1) % maxCount;
                setCount(count);
            }
        }
        return count == maxCount ? 15 : 0;
    }

    public ColorControl getChannel() {
        return stack.get(ConduitComponents.REDSTONE_COUNT_FILTER).channel1();
    }

    public int getMaxCount() {
        return stack.get(ConduitComponents.REDSTONE_COUNT_FILTER).maxCount();
    }

    public int getCount() {
        return stack.get(ConduitComponents.REDSTONE_COUNT_FILTER).count();
    }

    public void setCount(int count) {
        var component = stack.get(ConduitComponents.REDSTONE_COUNT_FILTER);
        stack.set(ConduitComponents.REDSTONE_COUNT_FILTER, new Component(component.channel1, component.maxCount, count, component.active));
    }

    public boolean isLastActive() {
        return stack.get(ConduitComponents.REDSTONE_COUNT_FILTER).active();
    }

    public void setLastActive(boolean lastActive) {
        var component = stack.get(ConduitComponents.REDSTONE_COUNT_FILTER);
        stack.set(ConduitComponents.REDSTONE_COUNT_FILTER, new Component(component.channel1, component.maxCount, component.count, lastActive));
    }

    public void setState(CountFilterPacket packet) {
        stack.set(ConduitComponents.REDSTONE_COUNT_FILTER, new Component(packet.channel1(), packet.maxCount(), packet.count(), packet.active()));
    }

    public record Component(ColorControl channel1, int maxCount, int count, boolean active) {
        public static final Codec<Component> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(ColorControl.CODEC.fieldOf("channel1").forGetter(Component::channel1),
                    ExtraCodecs.POSITIVE_INT.fieldOf("maxCount").forGetter(Component::maxCount),
                    ExtraCodecs.POSITIVE_INT.fieldOf("ticks").forGetter(Component::count),
                    Codec.BOOL.fieldOf("active").forGetter(Component::active))
                .apply(instance, Component::new)
        );
        public static final StreamCodec<ByteBuf, Component> STREAM_CODEC = StreamCodec.composite(
            ColorControl.STREAM_CODEC,
            Component::channel1,
            ByteBufCodecs.VAR_INT,
            Component::maxCount,
            ByteBufCodecs.VAR_INT,
            Component::count,
            ByteBufCodecs.BOOL,
            Component::active,
            Component::new
        );
    }
}

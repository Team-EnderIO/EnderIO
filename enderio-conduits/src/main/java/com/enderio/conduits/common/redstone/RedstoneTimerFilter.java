package com.enderio.conduits.common.redstone;

import com.enderio.conduits.common.init.ConduitComponents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class RedstoneTimerFilter implements RedstoneExtractFilter{

    public static final Component INSTANCE = new Component(0, 20);
    private final ItemStack stack;

    public RedstoneTimerFilter(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getInputSignal(Level level, BlockPos pos, Direction direction) {
        int ticks = getTicks();
        ticks += 2; //TODO Conduits tick every 2 ticks, so make this clear in the gui
        int maxTicks = getMaxTicks();
        if (ticks >= maxTicks) {
            ticks = 0;
            setTimer(ticks, maxTicks);
            return 15;
        }
        setTimer(ticks, maxTicks);
        return 0;
    }

    public int getMaxTicks() {
        return stack.get(ConduitComponents.REDSTONE_TIMER_FILTER).maxTicks();
    }

    public int getTicks() {
        return stack.get(ConduitComponents.REDSTONE_TIMER_FILTER).ticks();
    }

    public void setTimer(int ticks, int maxTicks) {
        stack.set(ConduitComponents.REDSTONE_TIMER_FILTER, new Component(ticks, maxTicks));
    }

    public void setMaxTicks(int maxTicks) {
        stack.set(ConduitComponents.REDSTONE_TIMER_FILTER, new Component(0, maxTicks));
    }

    public record Component(int ticks, int maxTicks) {
        public static final Codec<Component> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(ExtraCodecs.POSITIVE_INT.fieldOf("ticks").forGetter(r -> r.maxTicks),
                    ExtraCodecs.POSITIVE_INT.fieldOf("maxTicks").forGetter(r -> r.maxTicks))
                .apply(instance, Component::new)
        );

        public static final StreamCodec<ByteBuf, Component> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            Component::ticks,
            ByteBufCodecs.VAR_INT,
            Component::maxTicks,
            Component::new
        );
    }
}

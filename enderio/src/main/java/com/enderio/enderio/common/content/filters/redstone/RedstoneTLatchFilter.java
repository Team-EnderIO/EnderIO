package com.enderio.enderio.common.content.filters.redstone;

import com.enderio.enderio.api.filter.RedstoneOutputFilter;
import com.enderio.enderio.api.filter.RedstoneOutputFilterContext;
import com.enderio.enderio.common.init.ConduitComponents;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class RedstoneTLatchFilter implements RedstoneOutputFilter {

    public static final Component INSTANCE = new Component(false, true);
    private final ItemStack stack;

    public RedstoneTLatchFilter(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public int getOutputSignal(RedstoneOutputFilterContext context, DyeColor control) {
        boolean output = isActive();
        if (context.isActive(control) && isDeactivated()) {
            output = !output;
            setState(output, false);
        }

        if (!context.isActive(control) && !isDeactivated()) {
            setState(output, true);
        }

        return output ? 15 : 0;
    }

    public boolean isActive() {
        return stack.get(ConduitComponents.REDSTONE_TLATCH_FILTER).active();
    }

    public boolean isDeactivated() {
        return stack.get(ConduitComponents.REDSTONE_TLATCH_FILTER).deactivated();
    }

    public void setState(boolean active, boolean deactivated) {
        stack.set(ConduitComponents.REDSTONE_TLATCH_FILTER, new Component(active, deactivated));
    }

    public record Component(boolean active, boolean deactivated) {
        public static final Codec<Component> CODEC = RecordCodecBuilder.create(instance -> instance
                .group(Codec.BOOL.fieldOf("deactivated").forGetter(Component::active),
                        Codec.BOOL.fieldOf("deactivated").forGetter(Component::deactivated))
                .apply(instance, Component::new));

        public static final StreamCodec<ByteBuf, Component> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.BOOL,
                Component::active, ByteBufCodecs.BOOL, Component::deactivated, Component::new);
    }
}

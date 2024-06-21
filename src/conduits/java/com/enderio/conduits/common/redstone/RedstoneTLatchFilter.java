//package com.enderio.conduits.common.redstone;
//
//import com.enderio.api.misc.ColorControl;
//import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduitData;
//import com.enderio.conduits.common.init.ConduitComponents;
//import com.mojang.serialization.Codec;
//import com.mojang.serialization.codecs.RecordCodecBuilder;
//import io.netty.buffer.ByteBuf;
//import net.minecraft.network.codec.ByteBufCodecs;
//import net.minecraft.network.codec.StreamCodec;
//import net.minecraft.world.item.ItemStack;
//
//public class RedstoneTLatchFilter implements RedstoneInsertFilter {
//
//    public static Component INSTANCE = new Component(false, true);
//    private final ItemStack stack;
//
//    public RedstoneTLatchFilter(ItemStack stack) {
//        this.stack = stack;
//    }
//
//    @Override
//    public int getOutputSignal(RedstoneConduitData data, ColorControl control) {
//        boolean active1 = isActive();
//        if (data.isActive(control) && isDeactivated()) {
//            active1 = !active1;
//            setState(active1, false);
//        }
//        if (!data.isActive(control)) {
//            setState(active1, true);
//        }
//        return active1 ? 15 : 0;
//    }
//
//    public boolean isActive() {
//        return stack.get(ConduitComponents.REDSTONE_TLATCH_FILTER).active();
//    }
//
//    public boolean isDeactivated() {
//        return stack.get(ConduitComponents.REDSTONE_TLATCH_FILTER).deactivated();
//    }
//
//    public void setState(boolean active, boolean deactivated) {
//        stack.set(ConduitComponents.REDSTONE_TLATCH_FILTER, new Component(active, deactivated));
//    }
//
//    public record Component(boolean active, boolean deactivated) {
//        public static final Codec<Component> CODEC = RecordCodecBuilder.create(instance ->
//            instance.group(Codec.BOOL.fieldOf("deactivated").forGetter(Component::active),
//                    Codec.BOOL.fieldOf("deactivated").forGetter(Component::deactivated))
//                .apply(instance, Component::new)
//        );
//
//        public static final StreamCodec<ByteBuf, Component> STREAM_CODEC = StreamCodec.composite(
//            ByteBufCodecs.BOOL,
//            Component::active,
//            ByteBufCodecs.BOOL,
//            Component::deactivated,
//            Component::new
//        );
//    }
//}

package com.enderio.machines.common.transceiver;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Set;

public class ChannelList extends EnumMap<ChannelType, LinkedHashSet<Channel>> {

    public static final Codec<ChannelList> CODEC = Codec.unboundedMap(ChannelType.CODEC, Channel.CODEC.listOf()
        .xmap(LinkedHashSet::new, ArrayList::new))
        .xmap(
        map -> {
            ChannelList cl = new ChannelList();
            cl.putAll(map);
            return cl;
        }, HashMap::new
    );

    public static final StreamCodec<ByteBuf, ChannelList> STREAM_CODEC = StreamCodec.of(
        (buf, channels) -> {
            buf.writeInt(channels.size());
            channels.forEach((type, list) -> {
                buf.writeInt(type.id);
                buf.writeInt(list.size());
                list.forEach(c -> Channel.STREAM_CODEC.encode(buf, c));
            });
        },
        buf -> {
            ChannelList channels = new ChannelList();
            int size = buf.readInt();
            for (int i = 0; i < size; i++) {
                ChannelType type = ChannelType.BY_ID.apply(buf.readInt());
                int listSize = buf.readInt();
                LinkedHashSet<Channel> list = new LinkedHashSet<>();
                for (int j = 0; j < listSize; j++) {
                    list.add(Channel.STREAM_CODEC.decode(buf));
                }
                channels.put(type, list);
            }
            return channels;
        }
    );


    public ChannelList() {
        super(ChannelType.class);
        for (ChannelType type : ChannelType.values()) {
            put(type, new LinkedHashSet<>());
        }
    }

    public Tag save(HolderLookup.Provider lookupProvider) {
        return CODEC.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow();
    }

    public static ChannelList parse(HolderLookup.Provider lookupProvider, Tag tag) {
        return CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow();
    }

    public boolean addChannel(Channel channel) {
        ChannelType type = channel.type();
        return this.get(type).add(channel);
    }

    public boolean removeChannel(Channel channel) {
        ChannelType type = channel.type();
        return this.get(type).remove(channel);
    }

    public boolean containsChannel(Channel channel) {
        ChannelType type = channel.type();
        return this.get(type).contains(channel);
    }

    public Set<Channel> getChannels(ChannelType type) {
        return this.get(type);
    }
}

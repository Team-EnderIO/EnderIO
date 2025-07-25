package com.enderio.machines.common.transceiver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

public record Channel(String name, String owner, ChannelType type, boolean isPrivate) {

    public static final Codec<Channel> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.STRING.fieldOf("name").forGetter(Channel::name),
        Codec.STRING.fieldOf("owner").forGetter(Channel::owner),
        ChannelType.CODEC.fieldOf("type").forGetter(Channel::type),
        Codec.BOOL.fieldOf("is_private").forGetter(Channel::isPrivate)
    ).apply(instance, Channel::new));

    public static final StreamCodec<ByteBuf, Channel> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.STRING_UTF8,
        Channel::name,
        ByteBufCodecs.STRING_UTF8,
        Channel::owner,
        ChannelType.STREAM_CODEC,
        Channel::type,
        ByteBufCodecs.BOOL,
        Channel::isPrivate,
        Channel::new
    );

    public boolean isPrivate() {
        return isPrivate;
    }

    @Override
    public String toString() {
        return "Channel[name=%s, owner=%s, type=%s, private=%s]".formatted(name, owner, type, isPrivate);
    }

    public Tag save(HolderLookup.Provider lookupProvider) {
        return CODEC.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), this).getOrThrow();
    }

    public static Channel parse(HolderLookup.Provider lookupProvider, Tag tag) {
        return CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow();
    }

    public boolean canDisplay(String playerName) {
        return !isPrivate || owner.equals(playerName);
    }
}

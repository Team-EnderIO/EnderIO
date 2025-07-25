package com.enderio.machines.common.transceiver;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public class ChannelSavedData extends SavedData {

    public static final String DATA_NAME = "channel_registry";
    public static final String NBT_KEY = "channels";

    private final ChannelList channelList = new ChannelList();

    private ChannelSavedData() {

    }

    private ChannelSavedData(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        this.loadNBT(lookupProvider, tag);
    }

    public static ChannelSavedData get(Level level) {
        MinecraftServer server = level.getServer();
        if (server == null) {
            return new ChannelSavedData();
        }

        // The data is the same across all dimensions.
        // To persist a SavedData across levels, a SD should be attached to the Overworld
        ServerLevel overworld = server.overworld();
        return overworld.getDataStorage().computeIfAbsent(
            new Factory<>(ChannelSavedData::new, ChannelSavedData::new),
            DATA_NAME
        );
    }


    public void addChannel(Channel channel) {
        channelList.get(channel.type()).add(channel);
        setDirty();
    }

    public void removeChannel(Channel channel) {
        channelList.get(channel.type()).remove(channel);
        setDirty();
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider lookupProvider) {
        Tag encoded = ChannelList.CODEC.encodeStart(lookupProvider.createSerializationContext(NbtOps.INSTANCE), channelList)
            .getOrThrow();

        tag.put(NBT_KEY, encoded);
        return tag;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    public void loadNBT(HolderLookup.Provider lookupProvider, CompoundTag tag) {
        this.channelList.clear();
        ChannelList parsed = ChannelList.CODEC.parse(lookupProvider.createSerializationContext(NbtOps.INSTANCE), tag.get(NBT_KEY))
            .getOrThrow();

        this.channelList.putAll(parsed);
    }

    public ChannelList getChannelList() {
        return channelList;
    }
}



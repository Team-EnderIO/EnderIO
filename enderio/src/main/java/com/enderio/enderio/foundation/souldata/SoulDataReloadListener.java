/*
The MIT License (MIT)
Copyright (c) 2020 Joseph Bettendorff a.k.a. "Commoble"
Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:
The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
 */

package com.enderio.enderio.foundation.souldata;

import com.google.gson.Gson;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Codec-based data manager for loading data.
 * This works best if initialized during your mod's construction.
 * After creating the manager, subscribeAsSyncable can optionally be called on it to subscribe the manager
 * to the forge events necessary for syncing datapack data to clients.
 * @param <T> The type of the objects that the codec is parsing jsons as
 */
// TODO: 1.21.4: Might make sense for soul datum to be in their own registry(ies). Then the codecs can be registry sensitive.
public class SoulDataReloadListener<T extends SoulData> extends SimpleJsonResourceReloadListener<T> {
    private static final Gson GSON = new Gson();
    public Map<Identifier, T> map = new HashMap<>();
    private final String folderName;
    private static final Map<String, SoulDataReloadListener<? extends SoulData>> LOADED_SOUL_DATA = new HashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();

    // TODO: 1.21.4: Rearrange order of parameters
    protected SoulDataReloadListener(String folder, Codec codec) {
        super(codec, FileToIdConverter.json("eio_soul/" + folder));
        this.folderName = "eio_soul/" + folder;
        LOADED_SOUL_DATA.put(folder, this);
    }

    @Override
    protected void apply(Map<Identifier, T> soulData, ResourceManager resourceManager, ProfilerFiller profiler) {
        this.map = new HashMap<>(soulData);
        LOGGER.info("Data loader for {} loaded {} jsons", this.folderName, this.map.size());
    }

    /**
     * This should be called at most once, during construction of your mod (static init of your main mod class is fine)
     * Calling this method automatically subscribes a packet-sender to {@link OnDatapackSyncEvent}.
     * @param <P> the packet type that will be sent on the given channel
     * @param packetFactory  A packet constructor or factory method that converts the given map to a packet object to send on the given channel
     * @return this manager object
     */
    public <P extends CustomPacketPayload> SoulDataReloadListener<T> subscribeAsSyncable(
            final Function<Map<Identifier, T>, P> packetFactory) {
        NeoForge.EVENT_BUS.addListener(this.getDatapackSyncListener(packetFactory));
        return this;
    }

    /** Generate an event listener function for the on-datapack-sync event **/
    private <P extends CustomPacketPayload> Consumer<OnDatapackSyncEvent> getDatapackSyncListener(
            final Function<Map<Identifier, T>, P> packetFactory) {
        return event -> {
            ServerPlayer player = event.getPlayer();
            P packet = packetFactory.apply(this.map);

            if (player == null) {
                PacketDistributor.sendToAllPlayers(packet);
            } else {
                PacketDistributor.sendToPlayer(player, packet);
            }
        };
    }

    /**
     * Returns an optional ISoulData implementation.
     */
    public Optional<T> matches(Identifier entitytype) {
        if (map.containsKey(entitytype)) {
            return Optional.of(map.get(entitytype));
        }
        return Optional.empty();
    }

    /**
     * Returns an optional ISoulData implementation.
     */
    public Optional<T> matches(EntityType<?> entitytype) {
        var id = BuiltInRegistries.ENTITY_TYPE.getKeyOrNull(entitytype);
        if (id != null) {
            return matches(id);
        }

        return Optional.empty();
    }

    public static SoulDataReloadListener<? extends SoulData> fromString(String name) {
        return LOADED_SOUL_DATA.get(name);
    }
}

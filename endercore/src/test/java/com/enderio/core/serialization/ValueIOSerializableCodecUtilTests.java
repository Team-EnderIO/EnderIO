package com.enderio.core.serialization;

import com.enderio.core.common.serialization.ValueIOSerializableCodecs;
import com.mojang.datafixers.util.Either;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.TagValueInput;
import net.neoforged.neoforge.transfer.item.ItemResource;
import net.neoforged.neoforge.transfer.item.ItemStacksResourceHandler;
import net.neoforged.testframework.junit.EphemeralTestServerProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(EphemeralTestServerProvider.class)
public class ValueIOSerializableCodecUtilTests {
    @Test
    public void deferredCodec_SavesAndLoadsSuccessfully(MinecraftServer server) {
        // Arrange.
        var itemStackStorage = new ItemStacksResourceHandler(1);
        itemStackStorage.set(0, ItemResource.of(Items.PORKCHOP), 2);

        var ops = server.registryAccess().createSerializationContext(NbtOps.INSTANCE);

        // Act & Assert.
        var encoded = ValueIOSerializableCodecs.DEFERRED_CODEC.encode(Either.right(itemStackStorage), ops, ops.empty());
        Assertions.assertTrue(encoded.isSuccess());

        var decoded = ValueIOSerializableCodecs.DEFERRED_CODEC.decode(ops, encoded.getOrThrow());
        Assertions.assertTrue(decoded.isSuccess());

        var decodedResourceHandler = new ItemStacksResourceHandler(1);
        var valueInput = TagValueInput.create(ProblemReporter.DISCARDING, server.registryAccess(), decoded.getOrThrow().getFirst().left().orElseThrow());
        decodedResourceHandler.deserialize(valueInput);

        Assertions.assertEquals(itemStackStorage.getResource(0), decodedResourceHandler.getResource(0));
        Assertions.assertEquals(itemStackStorage.getAmountAsInt(0), decodedResourceHandler.getAmountAsInt(0));
    }

    @Test
    public void codec_SavesAndLoadsSuccessfully(MinecraftServer server) {
        // Arrange.
        var codec = ValueIOSerializableCodecs.createCodec(() -> new ItemStacksResourceHandler(1));

        var itemStackStorage = new ItemStacksResourceHandler(1);
        itemStackStorage.set(0, ItemResource.of(Items.PORKCHOP), 2);

        var ops = server.registryAccess().createSerializationContext(NbtOps.INSTANCE);

        // Act & Assert.
        var encoded = codec.encode(itemStackStorage, ops, ops.empty());
        Assertions.assertTrue(encoded.isSuccess());

        var decoded = codec.decode(ops, encoded.getOrThrow());
        Assertions.assertTrue(decoded.isSuccess());

        var decodedResourceHandler = decoded.getOrThrow().getFirst();

        Assertions.assertEquals(itemStackStorage.getResource(0), decodedResourceHandler.getResource(0));
        Assertions.assertEquals(itemStackStorage.getAmountAsInt(0), decodedResourceHandler.getAmountAsInt(0));
    }
}

package com.enderio.core.serialization;

import com.enderio.core.common.serialization.ValueIOSerializableHolder;
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
public class ValueIOSerializableHolderTests {
    @Test
    public void holder_encodeAndDecodeSuccessfully(MinecraftServer server) {
        // Arrange.
        var itemStackStorage = new ItemStacksResourceHandler(1);
        itemStackStorage.set(0, ItemResource.of(Items.PORKCHOP), 2);

        var codec = ValueIOSerializableHolder.<ItemStacksResourceHandler>codec();
        var holder = new ValueIOSerializableHolder<>(itemStackStorage);

        var ops = server.registryAccess().createSerializationContext(NbtOps.INSTANCE);

        // Act & Assert.
        var encoded = codec.encode(holder, ops, ops.empty());
        Assertions.assertTrue(encoded.isSuccess());

        var decoded = codec.decode(ops, encoded.getOrThrow());
        Assertions.assertTrue(decoded.isSuccess());

        var decodedHolder = decoded.getOrThrow().getFirst();
        Assertions.assertFalse(decodedHolder.isPresent());

        decodedHolder.inflate(new ItemStacksResourceHandler(1), server.registryAccess(), ProblemReporter.DISCARDING);
        Assertions.assertTrue(decodedHolder.isPresent());

        Assertions.assertEquals(itemStackStorage.getResource(0), decodedHolder.get().getResource(0));
        Assertions.assertEquals(itemStackStorage.getAmountAsInt(0), decodedHolder.get().getAmountAsInt(0));
    }

    @Test
    public void holder_encodeAndDecodeAndInflateSuccessfully(MinecraftServer server) {
        // Arrange.
        var itemStackStorage = new ItemStacksResourceHandler(1);
        itemStackStorage.set(0, ItemResource.of(Items.PORKCHOP), 2);

        var codec = ValueIOSerializableHolder.<ItemStacksResourceHandler>codec();
        var holder = new ValueIOSerializableHolder<>(itemStackStorage);

        var ops = server.registryAccess().createSerializationContext(NbtOps.INSTANCE);

        // Act & Assert.
        var encoded = codec.encode(holder, ops, ops.empty());
        Assertions.assertTrue(encoded.isSuccess());

        var decoded = codec.decode(ops, encoded.getOrThrow());
        Assertions.assertTrue(decoded.isSuccess());

        var decodedHolder = decoded.getOrThrow().getFirst();
        Assertions.assertFalse(decodedHolder.isPresent());

        decodedHolder.inflate(new ItemStacksResourceHandler(1), server.registryAccess(), ProblemReporter.DISCARDING);
        Assertions.assertTrue(decodedHolder.isPresent());

        Assertions.assertEquals(itemStackStorage.getResource(0), decodedHolder.get().getResource(0));
        Assertions.assertEquals(itemStackStorage.getAmountAsInt(0), decodedHolder.get().getAmountAsInt(0));
    }

    @Test
    public void holder_deflate_encodeAndDecode_Reinflates(MinecraftServer server) {
        // Arrange.
        var itemStackStorage = new ItemStacksResourceHandler(1);
        itemStackStorage.set(0, ItemResource.of(Items.PORKCHOP), 2);

        var codec = ValueIOSerializableHolder.<ItemStacksResourceHandler>codec();
        var holder = new ValueIOSerializableHolder<>(itemStackStorage);

        var ops = server.registryAccess().createSerializationContext(NbtOps.INSTANCE);

        // Act & Assert.
        holder.deflate(server.registryAccess(), ProblemReporter.DISCARDING);
        Assertions.assertFalse(holder.isPresent());

        var encoded = codec.encode(holder, ops, ops.empty());
        Assertions.assertTrue(encoded.isSuccess());

        var decoded = codec.decode(ops, encoded.getOrThrow());
        Assertions.assertTrue(decoded.isSuccess());

        var decodedHolder = decoded.getOrThrow().getFirst();
        Assertions.assertFalse(decodedHolder.isPresent());

        decodedHolder.inflate(new ItemStacksResourceHandler(1), server.registryAccess(), ProblemReporter.DISCARDING);
        Assertions.assertTrue(decodedHolder.isPresent());

        Assertions.assertEquals(itemStackStorage.getResource(0), decodedHolder.get().getResource(0));
        Assertions.assertEquals(itemStackStorage.getAmountAsInt(0), decodedHolder.get().getAmountAsInt(0));
    }
}

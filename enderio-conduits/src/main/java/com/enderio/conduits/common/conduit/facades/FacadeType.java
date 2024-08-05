package com.enderio.conduits.common.conduit.facades;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.function.IntFunction;
import java.util.function.Supplier;

/**
 * Additional options for a conduit facade.
 * These are stored separately so that it can be an item component alongside the paint.
 */
public enum FacadeType implements StringRepresentable {
    BASIC(0, "basic", false, false, () -> Items.AIR),
    HARDENED(1, "hardened", false, true, () -> Items.AIR),
    TRANSPARENT(2, "transparent", true, false, () -> Items.AIR),
    TRANSPARENT_HARDENED(3, "transparent_hardened", true, true, () -> Items.AIR);

    public static final Codec<FacadeType> CODEC = StringRepresentable.fromEnum(FacadeType::values);
    public static final IntFunction<FacadeType> BY_ID = ByIdMap.continuous(key -> key.id, values(), ByIdMap.OutOfBoundsStrategy.ZERO);
    public static final StreamCodec<ByteBuf, FacadeType> STREAM_CODEC = ByteBufCodecs.idMapper(BY_ID, v -> v.id);

    private final int id;
    private final String serializedName;

    private final boolean doesHideConduits;
    private final boolean isBlastResistant;
    private final Supplier<Item> facadeItemSupplier;

    FacadeType(int id, String serializedName, boolean doesHideConduits, boolean isBlastResistant, Supplier<Item> facadeItemSupplier) {
        this.id = id;
        this.serializedName = serializedName;
        this.doesHideConduits = doesHideConduits;
        this.isBlastResistant = isBlastResistant;
        this.facadeItemSupplier = facadeItemSupplier;
    }

    public boolean doesHideConduits() {
        return doesHideConduits;
    }

    public boolean isBlastResistant() {
        return isBlastResistant;
    }

    public Item facadeItem() {
        return facadeItemSupplier.get();
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }
}

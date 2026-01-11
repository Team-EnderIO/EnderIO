package com.enderio.api.conduit.facade;

import com.mojang.serialization.Codec;
import net.minecraft.util.ByIdMap;
import net.minecraft.util.StringRepresentable;
import net.minecraft.network.FriendlyByteBuf;

import java.util.function.IntFunction;

/**
 * Additional options for a conduit facade.
 * These are stored separately so that it can be a capability property alongside the paint.
 */
public enum FacadeType implements StringRepresentable {
    BASIC(0, "basic", false, false), 
    HARDENED(1, "hardened", false, true), 
    TRANSPARENT(2, "transparent", true, false),
    TRANSPARENT_HARDENED(3, "transparent_hardened", true, true);

    public static final Codec<FacadeType> CODEC = StringRepresentable.fromEnum(FacadeType::values);
    public static final IntFunction<FacadeType> BY_ID = ByIdMap.continuous(key -> key.id, values(),
            ByIdMap.OutOfBoundsStrategy.ZERO);

    private final int id;
    private final String serializedName;

    private final boolean doesHideConduits;
    private final boolean isBlastResistant;

    FacadeType(int id, String serializedName, boolean doesHideConduits, boolean isBlastResistant) {
        this.id = id;
        this.serializedName = serializedName;
        this.doesHideConduits = doesHideConduits;
        this.isBlastResistant = isBlastResistant;
    }

    /**
     * @return Whether conduits should be rendered behind the facade.
     */
    public boolean doesHideConduits() {
        return doesHideConduits;
    }

    /**
     * @return Whether the conduit block should have increased resistance.
     */
    public boolean isBlastResistant() {
        return isBlastResistant;
    }

    @Override
    public String getSerializedName() {
        return serializedName;
    }
    
    public void toNetwork(FriendlyByteBuf buf) {
        buf.writeVarInt(id);
    }
    
    public static FacadeType fromNetwork(FriendlyByteBuf buf) {
        return BY_ID.apply(buf.readVarInt());
    }
}

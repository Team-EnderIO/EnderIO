package com.enderio.enderio.api.soul.storage;

import com.enderio.enderio.api.soul.Soul;
import com.mojang.serialization.Codec;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public class SimpleSoulContent {
    public static final SimpleSoulContent EMPTY = new SimpleSoulContent(Soul.EMPTY);

    public static final Codec<SimpleSoulContent> CODEC = Soul.OPTIONAL_CODEC
        .xmap(SimpleSoulContent::new, content -> content.soul);
    public static final StreamCodec<RegistryFriendlyByteBuf, SimpleSoulContent> STREAM_CODEC = Soul.OPTIONAL_STREAM_CODEC
        .map(SimpleSoulContent::new, content -> content.soul);

    private final Soul soul;

    private SimpleSoulContent(Soul soul) {
        this.soul = soul;
    }

    public static SimpleSoulContent copyOf(Soul soul) {
        return soul.isEmpty() ? EMPTY : new SimpleSoulContent(soul.copy());
    }

    public Soul copy() {
        return soul.copy();
    }

    public boolean isEmpty() {
        return soul.isEmpty();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (!(obj instanceof SimpleSoulContent o)) {
            return false;
        } else {
            return Soul.isSameEntitySameTag(soul, o.soul);
        }
    }

    @Override
    public int hashCode() {
        return Soul.hashEntityAndTag(soul);
    }
}

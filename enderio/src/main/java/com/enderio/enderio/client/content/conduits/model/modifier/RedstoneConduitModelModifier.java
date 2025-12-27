package com.enderio.enderio.client.content.conduits.model.modifier;

import com.enderio.enderio.api.conduits.Conduit;
import com.enderio.enderio.api.conduits.model.ConduitModelModifier;
import com.enderio.enderio.content.conduits.type.redstone.RedstoneConduit;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

public class RedstoneConduitModelModifier implements ConduitModelModifier {
    @Override
    public Identifier getTexture(Holder<Conduit<?, ?>> conduit, @Nullable CompoundTag extraWorldData) {
        RedstoneConduit redstoneConduit = (RedstoneConduit) conduit.value();

        if (extraWorldData != null) {
            return extraWorldData.contains("IsActive") && extraWorldData.getBooleanOr("IsActive", false)
                    ? redstoneConduit.activeTexture()
                    : redstoneConduit.texture();
        }

        return redstoneConduit.texture();
    }
}

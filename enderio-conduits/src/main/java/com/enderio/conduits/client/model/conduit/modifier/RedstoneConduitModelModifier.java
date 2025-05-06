package com.enderio.conduits.client.model.conduit.modifier;

import com.enderio.conduits.api.Conduit;
import com.enderio.conduits.api.model.ConduitModelModifier;
import com.enderio.conduits.common.conduit.type.redstone.RedstoneConduit;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class RedstoneConduitModelModifier implements ConduitModelModifier {
    @Override
    public ResourceLocation getTexture(Holder<Conduit<?, ?>> conduit, @Nullable CompoundTag extraWorldData) {
        RedstoneConduit redstoneConduit = (RedstoneConduit) conduit.value();

        if (extraWorldData != null) {
            return extraWorldData.contains("IsActive") && extraWorldData.getBoolean("IsActive")
                    ? redstoneConduit.activeTexture()
                    : redstoneConduit.texture();
        }

        return redstoneConduit.texture();
    }
}

package com.enderio.conduits.common.network;

import com.enderio.EnderIO;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.blockentity.SimpleConduitType;
import com.enderio.conduits.common.init.ConduitItems;
import com.enderio.conduits.common.init.EnderConduitTypes;
import net.minecraft.resources.ResourceLocation;

public class RedstoneConduitType extends SimpleConduitType<RedstoneExtraData> {

    private static final ResourceLocation active = EnderIO.loc("block/conduit/redstone_active");
    private static final ResourceLocation inactive = EnderIO.loc("block/conduit/redstone");

    public RedstoneConduitType() {
        super(inactive, new RedstoneConduitTicker(), RedstoneExtraData::new, EnderConduitTypes.ICON_TEXTURE, Vector2i.ZERO);
    }

    @Override
    public ResourceLocation getTexture(RedstoneExtraData extendedData) {
        return extendedData.isActive() ? active : inactive;
    }

    @Override
    public ResourceLocation[] getTextures() {
        return new ResourceLocation[] {active, inactive};
    }
}

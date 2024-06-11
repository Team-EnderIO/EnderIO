package com.enderio.conduits.common.types.redstone;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.init.ConduitTypes;
import com.enderio.conduits.common.types.SimpleConduitType;
import net.minecraft.resources.ResourceLocation;

public class RedstoneConduitType extends SimpleConduitType<RedstoneExtendedData> {

    private static final ResourceLocation ACTIVE = EnderIO.loc("block/conduit/redstone_active");
    private static final ResourceLocation INACTIVE = EnderIO.loc("block/conduit/redstone");

    public RedstoneConduitType() {
        super(INACTIVE, new RedstoneConduitTicker(), RedstoneExtendedData::new, ConduitTypes.ICON_TEXTURE, Vector2i.ZERO, ConduitMenuData.REDSTONE);
    }

    @Override
    public ResourceLocation getTexture(RedstoneExtendedData extendedData) {
        return extendedData.isActive() ? ACTIVE : INACTIVE;
    }
}

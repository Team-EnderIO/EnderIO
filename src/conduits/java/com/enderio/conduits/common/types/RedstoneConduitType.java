package com.enderio.conduits.common.types;

import com.enderio.EnderIO;
import com.enderio.api.conduit.IConduitMenuData;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.init.EnderConduitTypes;
import net.minecraft.resources.ResourceLocation;

public class RedstoneConduitType extends SimpleConduitType<RedstoneExtendedData> {

    private static final ResourceLocation active = EnderIO.loc("block/conduit/redstone_active");
    private static final ResourceLocation inactive = EnderIO.loc("block/conduit/redstone");

    public RedstoneConduitType() {
        super(inactive, new RedstoneConduitTicker(), RedstoneExtendedData::new, EnderConduitTypes.ICON_TEXTURE, Vector2i.ZERO, IConduitMenuData.REDSTONE);
    }

    @Override
    public ResourceLocation getTexture(RedstoneExtendedData extendedData) {
        return extendedData.isActive() ? active : inactive;
    }
}

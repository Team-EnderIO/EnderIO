package com.enderio.conduits.common.conduit.type.redstone;

import com.enderio.EnderIO;
import com.enderio.api.conduit.ConduitMenuData;
import com.enderio.conduits.common.conduit.type.SimpleConduitType;
import net.minecraft.resources.ResourceLocation;

public class RedstoneConduitType extends SimpleConduitType<RedstoneConduitData> {

    private static final ResourceLocation ACTIVE = EnderIO.loc("block/conduit/redstone_active");
    private static final ResourceLocation INACTIVE = EnderIO.loc("block/conduit/redstone");

    private static final ConduitMenuData MENU_DATA = new ConduitMenuData.Simple(true, true, false, true, true, false);

    public RedstoneConduitType() {
        super(INACTIVE, new RedstoneConduitTicker(), RedstoneConduitData::new, MENU_DATA);
    }

    @Override
    public ResourceLocation getTexture(RedstoneConduitData extendedData) {
        return extendedData.isActive() ? ACTIVE : INACTIVE;
    }
}

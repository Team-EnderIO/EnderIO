package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.api.conduit.IConduitMenuData;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.init.EnderConduitTypes;
import com.enderio.conduits.common.types.SimpleConduitType;
import net.minecraft.resources.ResourceLocation;

public class HeatConduitType extends SimpleConduitType<HeatExtendedData> {

    public static final IConduitMenuData MENU_DATA = new IConduitMenuData.Simple(false, false, false, false, false, true);

    public HeatConduitType(ResourceLocation texture, Vector2i iconTexturePos) {
        super(texture, new HeatTicker(), HeatExtendedData::new, EnderConduitTypes.ICON_TEXTURE, iconTexturePos, MENU_DATA);
    }

}

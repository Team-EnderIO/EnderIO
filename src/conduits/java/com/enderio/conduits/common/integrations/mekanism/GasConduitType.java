package com.enderio.conduits.common.integrations.mekanism;

import com.enderio.EnderIO;
import com.enderio.api.conduit.IConduitMenuData;
import com.enderio.api.misc.Vector2i;
import com.enderio.conduits.common.init.EnderConduitTypes;
import com.enderio.conduits.common.types.SimpleConduitType;
import mekanism.common.capabilities.Capabilities;

public class GasConduitType extends SimpleConduitType<GasExtendedData> {
    public static final IConduitMenuData MENU_DATA = new IConduitMenuData.Simple(false, false, false, false, false, true);

    public GasConduitType() {
        super(EnderIO.loc("block/conduit/gas"), new ChemicalTicker(1000, Capabilities.GAS.block(), Capabilities.SLURRY.block()), () -> new GasExtendedData(false),
            EnderConduitTypes.ICON_TEXTURE, new Vector2i(0, 48), MENU_DATA);
    }
}
